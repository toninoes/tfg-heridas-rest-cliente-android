package uca.ruiz.antonio.tfgapp.ui.activity.admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uca.ruiz.antonio.tfgapp.R;
import uca.ruiz.antonio.tfgapp.data.api.io.MyApiAdapter;
import uca.ruiz.antonio.tfgapp.data.api.mapping.ApiError;
import uca.ruiz.antonio.tfgapp.data.api.model.Grupodiagnostico;
import uca.ruiz.antonio.tfgapp.ui.activity.LoginActivity;
import uca.ruiz.antonio.tfgapp.utils.Pref;
import uca.ruiz.antonio.tfgapp.utils.Validacion;


public class GrupodiagnosticoNewEditActivity extends AppCompatActivity {

    private static final String TAG = GrupodiagnosticoNewEditActivity.class.getSimpleName();
    private EditText et_nombre;
    private Grupodiagnostico grupodiagnostico;
    private Boolean editando = false;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grupodiagnostico_new_edit);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        et_nombre = (EditText) findViewById(R.id.et_nombre);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.guardando));

        try { // editar
            grupodiagnostico = (Grupodiagnostico) getIntent().getExtras().getSerializable("grupodiagnostico");
            et_nombre.setText(grupodiagnostico.getNombre());
            editando = true;
        } catch (Exception e) {
            Log.d(TAG, getString(R.string.creando_nuevo_registro));
        }

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
                startActivity(new Intent(this, GruposdiagnosticosActivity.class));
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

        //tomo el contenido de los campos
        String nombre = et_nombre.getText().toString();

        boolean cancel = false;
        View focusView = null;

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
            Grupodiagnostico gd = new Grupodiagnostico(nombre);
            if(editando)
                editar(gd);
            else
                nuevo(gd);
        }

    }

    private void nuevo(Grupodiagnostico gd) {
        progressDialog.show();
        Call<Grupodiagnostico> call = MyApiAdapter.getApiService().crearGrupodiagnostico(gd, Pref.getToken());
        call.enqueue(new Callback<Grupodiagnostico>() {
            @Override
            public void onResponse(Call<Grupodiagnostico> call, Response<Grupodiagnostico> response) {
                if(response.isSuccessful()) {
                    progressDialog.cancel();
                    Toasty.success(GrupodiagnosticoNewEditActivity.this, getString(R.string.creado_registro),
                            Toast.LENGTH_SHORT, true).show();
                    startActivity(new Intent(GrupodiagnosticoNewEditActivity.this, GruposdiagnosticosActivity.class));
                } else {
                    progressDialog.cancel();
                    if (response.errorBody().contentType().subtype().equals("json")) {
                        ApiError apiError = ApiError.fromResponseBody(response.errorBody());
                        Toasty.error(GrupodiagnosticoNewEditActivity.this, apiError.getMessage(),
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
            public void onFailure(Call<Grupodiagnostico> call, Throwable t) {
                progressDialog.cancel();if (t instanceof IOException) {
                    Toasty.warning(GrupodiagnosticoNewEditActivity.this, getString(R.string.error_conexion_red),
                            Toast.LENGTH_LONG, true).show();
                } else {
                    Toasty.error(GrupodiagnosticoNewEditActivity.this, getString(R.string.error_conversion),
                            Toast.LENGTH_LONG, true).show();
                    Log.d(TAG, getString(R.string.error_conversion));
                }
            }
        });

    }

    private void editar(Grupodiagnostico gd) {
        progressDialog.show();
        Call<Grupodiagnostico> call = MyApiAdapter.getApiService().editarGrupodiagnostico(grupodiagnostico.getId(),
                gd, Pref.getToken());
        call.enqueue(new Callback<Grupodiagnostico>() {
            @Override
            public void onResponse(Call<Grupodiagnostico> call, Response<Grupodiagnostico> response) {
                if(response.isSuccessful()) {
                    progressDialog.cancel();
                    Toasty.success(GrupodiagnosticoNewEditActivity.this, getString(R.string.editado_registro),
                            Toast.LENGTH_SHORT, true).show();
                    startActivity(new Intent(GrupodiagnosticoNewEditActivity.this, GruposdiagnosticosActivity.class));
                } else {
                    progressDialog.cancel();
                    if (response.errorBody().contentType().subtype().equals("json")) {
                        ApiError apiError = ApiError.fromResponseBody(response.errorBody());
                        Toasty.error(GrupodiagnosticoNewEditActivity.this, apiError.getMessage(),
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
            public void onFailure(Call<Grupodiagnostico> call, Throwable t) {
                progressDialog.cancel();

                if (t instanceof IOException) {
                    Toasty.warning(GrupodiagnosticoNewEditActivity.this, getString(R.string.error_conexion_red),
                            Toast.LENGTH_LONG, true).show();
                } else {
                    Toasty.error(GrupodiagnosticoNewEditActivity.this, getString(R.string.error_conversion),
                            Toast.LENGTH_LONG, true).show();
                    Log.d(TAG, getString(R.string.error_conversion));
                }
            }
        });
    }


}
