package uca.ruiz.antonio.tfgapp.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uca.ruiz.antonio.tfgapp.R;
import uca.ruiz.antonio.tfgapp.data.api.io.MyApiAdapter;
import uca.ruiz.antonio.tfgapp.data.api.mapping.ApiError;
import uca.ruiz.antonio.tfgapp.data.api.model.Cuidado;
import uca.ruiz.antonio.tfgapp.data.api.model.Grupodiagnostico;
import uca.ruiz.antonio.tfgapp.data.api.model.Sala;
import uca.ruiz.antonio.tfgapp.ui.activity.admin.SalaConfigActivity;
import uca.ruiz.antonio.tfgapp.ui.activity.admin.SalaNewEditActivity;
import uca.ruiz.antonio.tfgapp.utils.Pref;
import uca.ruiz.antonio.tfgapp.utils.Validacion;

import static uca.ruiz.antonio.tfgapp.R.id.sp_centros;
import static uca.ruiz.antonio.tfgapp.R.string.centros;
import static uca.ruiz.antonio.tfgapp.R.string.cuidado;
import static uca.ruiz.antonio.tfgapp.R.string.sala;


public class CuidadoNewEditActivity extends AppCompatActivity  {

    private static final String TAG = CuidadoNewEditActivity.class.getSimpleName();

    private Spinner sp_gruposdiagnosticos;
    private TextView sp_gruposdiagnosticos_error;
    private EditText et_nombre, et_descripcion;
    private Cuidado cuidado;
    private Grupodiagnostico gDiagnostico;
    private ProgressDialog progressDialog;

    private Boolean editando = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuidado_new_edit);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        sp_gruposdiagnosticos = (Spinner) findViewById(R.id.sp_gruposdiagnosticos);
        sp_gruposdiagnosticos_error = (TextView) findViewById(R.id.sp_gruposdiagnosticos_error);

        et_nombre = (EditText) findViewById(R.id.et_nombre);
        et_descripcion = (EditText) findViewById(R.id.et_descripcion);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.cargando));

        try { // editar
            cuidado = (Cuidado) getIntent().getExtras().getSerializable("cuidado");
            et_nombre.setText(cuidado.getNombre());
            et_descripcion.setText(cuidado.getDescripcion());
            cargarGDiagnosticos(sp_gruposdiagnosticos, cuidado);
            editando = true;
        } catch (Exception e) { // nuevo
            Log.d(TAG, getString(R.string.creando_nuevo_registro));
            cargarGDiagnosticos(sp_gruposdiagnosticos, cuidado); // cuidado tiene id=0.
        }

        sp_gruposdiagnosticos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                gDiagnostico = (Grupodiagnostico) adapterView.getAdapter().getItem(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_guardar_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_guardar:
                intentarGuardar();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /**
     * Intenta guardar: Si hay errores de formulario (campo no válido, campos faltantes, etc.), se
     * presentan los errores y no se guarda nada, porque ni siquiera se envía nada al servidor.
     */
    private void intentarGuardar() {
        // Resetear errores
        et_nombre.setError(null);
        et_descripcion.setError(null);
        sp_gruposdiagnosticos_error.setError(null);

        //tomo el contenido de los campos
        String nombre = et_nombre.getText().toString();
        String descripcion = et_descripcion.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Validar listado de Diagnósticos
        if(gDiagnostico.getId() == 0 || gDiagnostico == null) {
            sp_gruposdiagnosticos_error.setError(getString(R.string.debes_seleccionar_grupo_diagnosticos));
            focusView = sp_gruposdiagnosticos_error;
            cancel = true;
        }

        // Valida campo Descripción
        if(Validacion.vacio(descripcion)) {
            et_descripcion.setError(getString(R.string.campo_no_vacio));
            focusView = et_descripcion;
            cancel = true;
        }

        // Valida campo Nombre
        if(Validacion.vacio(nombre)) {
            et_nombre.setError(getString(R.string.campo_no_vacio));
            focusView = et_nombre;
            cancel = true;
        }

        if (cancel) {
            // Se ha producido un error: no se intenta el registro y se focaliza en el
            // primer campo del formulario con error.
            focusView.requestFocus();
        } else {
            // ha ido bien, luego se procede a crear o editar.
            Cuidado c = new Cuidado(nombre, descripcion, gDiagnostico);

            if(editando) {
                cuidado.setNombre(nombre);
                cuidado.setDescripcion(descripcion);
                cuidado.setGrupodiagnostico(gDiagnostico);
                editar(cuidado);
            } else {
                nuevo(c);
            }

        }
    }

    private void nuevo(Cuidado c) {
        progressDialog.show();
        Call<Cuidado> call = MyApiAdapter.getApiService().crearCuidado(Pref.getUserId(), c, Pref.getToken());
        call.enqueue(new Callback<Cuidado>() {
            @Override
            public void onResponse(Call<Cuidado> call, Response<Cuidado> response) {
                progressDialog.cancel();
                if(response.isSuccessful()) {
                    Toasty.success(CuidadoNewEditActivity.this, getString(R.string.creado_registro),
                            Toast.LENGTH_SHORT, true).show();

                    Intent intent = new Intent(CuidadoNewEditActivity.this, CuidadosActivity.class);
                    startActivity(intent);
                } else {
                    if (response.errorBody().contentType().subtype().equals("json")) {
                        ApiError apiError = ApiError.fromResponseBody(response.errorBody());
                        Toasty.error(CuidadoNewEditActivity.this, apiError.getMessage(),
                                Toast.LENGTH_LONG, true).show();
                        Log.d(TAG, apiError.getPath() + " " + apiError.getMessage());
                    } else {
                        try {
                            Log.d(TAG, response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<Cuidado> call, Throwable t) {
                progressDialog.cancel();

                if (t instanceof IOException) {
                    Toasty.warning(CuidadoNewEditActivity.this, getString(R.string.error_conexion_red),
                            Toast.LENGTH_LONG, true).show();
                } else {
                    Toasty.error(CuidadoNewEditActivity.this, getString(R.string.error_conversion),
                            Toast.LENGTH_LONG, true).show();
                    Log.d(TAG, getString(R.string.error_conversion));
                }
            }
        });
    }


    private void editar(Cuidado c) {
        progressDialog.show();
        Call<Cuidado> call = MyApiAdapter.getApiService().editarCuidado(cuidado.getId(), c,
                Pref.getToken());
        call.enqueue(new Callback<Cuidado>() {
            @Override
            public void onResponse(Call<Cuidado> call, Response<Cuidado> response) {
                progressDialog.cancel();
                if(response.isSuccessful()) {
                    Toasty.success(CuidadoNewEditActivity.this, getString(R.string.editado_registro),
                            Toast.LENGTH_SHORT, true).show();
                    Intent intent = new Intent(CuidadoNewEditActivity.this, CuidadosActivity.class);
                    startActivity(intent);
                } else {
                    if (response.errorBody().contentType().subtype().equals("json")) {
                        ApiError apiError = ApiError.fromResponseBody(response.errorBody());
                        Toasty.error(CuidadoNewEditActivity.this, apiError.getMessage(),
                                Toast.LENGTH_LONG, true).show();
                        Log.d(TAG, apiError.getPath() + " " + apiError.getMessage());
                    } else {
                        try {
                            Log.d(TAG, response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<Cuidado> call, Throwable t) {
                progressDialog.cancel();

                if (t instanceof IOException) {
                    Toasty.warning(CuidadoNewEditActivity.this, getString(R.string.error_conexion_red),
                            Toast.LENGTH_LONG, true).show();
                } else {
                    Toasty.error(CuidadoNewEditActivity.this, getString(R.string.error_conversion),
                            Toast.LENGTH_LONG, true).show();
                    Log.d(TAG, getString(R.string.error_conversion));
                }
            }
        });
    }


    private void cargarGDiagnosticos(final Spinner sp_Gdiagnosticos, final Cuidado cuidado) {
        Call<ArrayList<Grupodiagnostico>> call = MyApiAdapter.getApiService().getGruposdiagnosticos(Pref.getToken());
        call.enqueue(new Callback<ArrayList<Grupodiagnostico>>() {
            @Override
            public void onResponse(Call<ArrayList<Grupodiagnostico>> call, Response<ArrayList<Grupodiagnostico>> response) {
                progressDialog.cancel();
                if(response.isSuccessful()) {
                    ArrayList<Grupodiagnostico> gDiagnosticos = response.body();
                    if(gDiagnosticos != null) {
                        gDiagnosticos.add(0, new Grupodiagnostico(getString(R.string.seleccione_Gdiagnostico)));
                        ArrayAdapter<Grupodiagnostico> arrayAdapter = new ArrayAdapter<Grupodiagnostico>(CuidadoNewEditActivity.this,
                                android.R.layout.simple_spinner_dropdown_item, gDiagnosticos);

                        Log.d("GRUPOS DIAGNOSTICOS", "Tamaño ==> " + gDiagnosticos.size());
                        sp_Gdiagnosticos.setAdapter(arrayAdapter);
                        if(editando) {
                            sp_Gdiagnosticos.setSelection(gDiagnosticos.indexOf(cuidado.getGrupodiagnostico()));
                        }
                    }
                } else {
                    if (response.errorBody().contentType().subtype().equals("json")) {
                        ApiError apiError = ApiError.fromResponseBody(response.errorBody());
                        Toasty.error(CuidadoNewEditActivity.this, apiError.getMessage(),
                                Toast.LENGTH_LONG, true).show();
                        Log.d(TAG, apiError.getPath() + " " + apiError.getMessage());
                    } else {
                        try {
                            Log.d(TAG, response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Grupodiagnostico>> call, Throwable t) {
                progressDialog.cancel();

                if (t instanceof IOException) {
                    Toasty.warning(CuidadoNewEditActivity.this, getString(R.string.error_conexion_red),
                            Toast.LENGTH_LONG, true).show();
                } else {
                    Toasty.error(CuidadoNewEditActivity.this, getString(R.string.error_conversion),
                            Toast.LENGTH_LONG, true).show();
                    Log.d(TAG, getString(R.string.error_conversion));
                }
            }
        });
    }


}
