package uca.ruiz.antonio.tfgapp.ui.activity.admin;

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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uca.ruiz.antonio.tfgapp.R;
import uca.ruiz.antonio.tfgapp.data.api.io.MyApiAdapter;
import uca.ruiz.antonio.tfgapp.data.api.mapping.ApiError;
import uca.ruiz.antonio.tfgapp.data.api.model.Diagnostico;
import uca.ruiz.antonio.tfgapp.data.api.model.Grupodiagnostico;
import uca.ruiz.antonio.tfgapp.utils.Pref;
import uca.ruiz.antonio.tfgapp.utils.Validacion;


public class DiagnosticoNewEditActivity extends AppCompatActivity {

    private static final String TAG = DiagnosticoNewEditActivity.class.getSimpleName();
    private EditText et_nombre, et_codigo;
    private Spinner sp_gruposdiagnosticos;
    private  TextView sp_gruposdiagnosticos_text;
    private Diagnostico diagnostico;
    private Grupodiagnostico grupodiagnostico;
    private Boolean editando = false;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagnostico_new_edit);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        et_nombre = (EditText) findViewById(R.id.et_nombre);
        et_codigo = (EditText) findViewById(R.id.et_codigo);
        sp_gruposdiagnosticos = (Spinner) findViewById(R.id.sp_gruposdiagnosticos);
        sp_gruposdiagnosticos_text = (TextView) findViewById(R.id.sp_gruposdiagnosticos_error);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.guardando));

        try { // editar
            diagnostico = (Diagnostico) getIntent().getExtras().getSerializable("diagnostico");
            et_codigo.setText(diagnostico.getCodigo());
            et_nombre.setText(diagnostico.getNombre());
            cargarGruposdiagnosticos(sp_gruposdiagnosticos, diagnostico);
            editando = true;
        } catch (Exception e) {
            Log.d(TAG, getString(R.string.creando_nuevo_registro));
            cargarGruposdiagnosticos(sp_gruposdiagnosticos, diagnostico); // diagnostico.id = 0
        }

        sp_gruposdiagnosticos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                grupodiagnostico = (Grupodiagnostico) adapterView.getAdapter().getItem(i);
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
                startActivity(new Intent(this, DiagnosticosActivity.class));
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
        et_codigo.setError(null);
        sp_gruposdiagnosticos_text.setError(null);

        //tomo el contenido de los campos
        String nombre = et_nombre.getText().toString();
        String codigo = et_codigo.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Validar listado de Grupos Diagnosticos
        if(grupodiagnostico.getId() == 0 || grupodiagnostico == null) {
            sp_gruposdiagnosticos_text.setError(getString(R.string.debes_seleccionar_grupo_diagnosticos));
            focusView = sp_gruposdiagnosticos_text;
            cancel = true;
        }

        // Valida código
        if(Validacion.vacio(codigo)) {
            et_codigo.setError(getString(R.string.campo_no_vacio));
            focusView = et_codigo;
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
            Diagnostico d = new Diagnostico(codigo, nombre, grupodiagnostico);
            if(editando)
                editar(d);
            else
                nuevo(d);
        }

    }

    private void nuevo(Diagnostico c) {
        progressDialog.show();
        Call<Diagnostico> call = MyApiAdapter.getApiService().crearDiagnostico(c, Pref.getToken());
        call.enqueue(new Callback<Diagnostico>() {
            @Override
            public void onResponse(Call<Diagnostico> call, Response<Diagnostico> response) {
                if(response.isSuccessful()) {
                    progressDialog.cancel();
                    Toast.makeText(DiagnosticoNewEditActivity.this, getString(R.string.creado_registro),
                            Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(DiagnosticoNewEditActivity.this, DiagnosticosActivity.class));
                } else {
                    progressDialog.cancel();
                    if (response.errorBody().contentType().subtype().equals("json")) {
                        ApiError apiError = ApiError.fromResponseBody(response.errorBody());
                        Toast.makeText(DiagnosticoNewEditActivity.this, apiError.getMessage(),
                                Toast.LENGTH_LONG).show();
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
            public void onFailure(Call<Diagnostico> call, Throwable t) {
                progressDialog.cancel();
                Toast.makeText(DiagnosticoNewEditActivity.this, "error :(", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void editar(Diagnostico d) {
        progressDialog.show();
        Call<Diagnostico> call = MyApiAdapter.getApiService().editarDiagnostico(diagnostico.getId(), d, Pref.getToken());
        call.enqueue(new Callback<Diagnostico>() {
            @Override
            public void onResponse(Call<Diagnostico> call, Response<Diagnostico> response) {
                if(response.isSuccessful()) {
                    progressDialog.cancel();
                    Toast.makeText(DiagnosticoNewEditActivity.this, getString(R.string.editado_registro), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(DiagnosticoNewEditActivity.this, DiagnosticosActivity.class));
                } else {
                    progressDialog.cancel();
                    if (response.errorBody().contentType().subtype().equals("json")) {
                        ApiError apiError = ApiError.fromResponseBody(response.errorBody());
                        Toast.makeText(DiagnosticoNewEditActivity.this, apiError.getMessage(),
                                Toast.LENGTH_LONG).show();
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
            public void onFailure(Call<Diagnostico> call, Throwable t) {
                progressDialog.cancel();
                Toast.makeText(DiagnosticoNewEditActivity.this, "error :(", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void cargarGruposdiagnosticos(final Spinner sp_gruposdiagnosticos, final Diagnostico diagnostico) {
        Call<ArrayList<Grupodiagnostico>> call = MyApiAdapter.getApiService().getGruposdiagnosticos(Pref.getToken());
        call.enqueue(new Callback<ArrayList<Grupodiagnostico>>() {
            @Override
            public void onResponse(Call<ArrayList<Grupodiagnostico>> call, Response<ArrayList<Grupodiagnostico>> response) {
                if(response.isSuccessful()) {
                    ArrayList<Grupodiagnostico> gruposdiagnosticos = response.body();

                    if(gruposdiagnosticos != null) {
                        gruposdiagnosticos.add(0, new Grupodiagnostico(getString(R.string.seleccione_grupo_diagnostico)));
                        ArrayAdapter<Grupodiagnostico> arrayAdapter = new ArrayAdapter<Grupodiagnostico>(DiagnosticoNewEditActivity.this,
                                android.R.layout.simple_spinner_dropdown_item, gruposdiagnosticos);

                        Log.d("GRUPOS DIAGNOSTICOS", "Tamaño ==> " + gruposdiagnosticos.size());
                        sp_gruposdiagnosticos.setAdapter(arrayAdapter);
                        if(editando) {
                            sp_gruposdiagnosticos.setSelection(gruposdiagnosticos.indexOf(diagnostico.getGrupodiagnostico()));
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Grupodiagnostico>> call, Throwable t) {
                progressDialog.cancel();
                Toast.makeText(DiagnosticoNewEditActivity.this, "error :(", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
