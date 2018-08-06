package uca.ruiz.antonio.tfgapp.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import uca.ruiz.antonio.tfgapp.data.api.model.Cura;
import uca.ruiz.antonio.tfgapp.data.api.model.Proceso;
import uca.ruiz.antonio.tfgapp.utils.Pref;
import uca.ruiz.antonio.tfgapp.utils.Validacion;


public class CuraNewEditActivity extends AppCompatActivity {

    private static final String TAG = CuraNewEditActivity.class.getSimpleName();
    private EditText et_evolucion, et_tratamiento, et_recomendaciones;

    private Proceso proceso;
    private Cura cura;
    private Boolean editando = false;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cura_new_edit);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        proceso = (Proceso) getIntent().getExtras().getSerializable("proceso");

        et_evolucion = (EditText) findViewById(R.id.et_evolucion);
        et_tratamiento = (EditText) findViewById(R.id.et_tratamiento);
        et_recomendaciones = (EditText) findViewById(R.id.et_recomendaciones);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.cargando));

        try { // editar
            cura = (Cura) getIntent().getExtras().getSerializable("cura");
            et_evolucion.setText(cura.getEvolucion());
            et_tratamiento.setText(cura.getTratamiento());
            et_recomendaciones.setText(cura.getRecomendaciones());
            editando = true;
        } catch (Exception e) { // nuevo
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
                volverAtras();
                return true;
            case R.id.action_guardar:
                intentarGuardar();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void volverAtras() {
        if (editando) {
            Intent i = new Intent(this, ImagenesActivity.class);
            i.putExtra("cura", cura);
            startActivity(i);
        } else {
            Intent i = new Intent(this, CurasActivity.class);
            i.putExtra("proceso", proceso);
            startActivity(i);
        }
    }

    @Override
    public void onBackPressed() {
        volverAtras();
    }

    /**
     * Intenta guardar: Si hay errores de formulario (campo no válido, campos faltantes, etc.), se
     * presentan los errores y no se guarda nada, porque ni siquiera se envía nada al servidor.
     */
    private void intentarGuardar() {
        et_evolucion.setError(null);
        et_tratamiento.setError(null);
        et_recomendaciones.setError(null);

        //tomo el contenido de los campos
        String evolucion = et_evolucion.getText().toString();
        String tratamiento = et_tratamiento.getText().toString();
        String recomendaciones = et_recomendaciones.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Valida campo Tratamiento
        if(Validacion.vacio(tratamiento)) {
            et_tratamiento.setError(getString(R.string.campo_no_vacio));
            focusView = et_tratamiento;
            cancel = true;
        }

        if (cancel) {
            // Se ha producido un error: no se intenta el registro y se focaliza en el
            // primer campo del formulario con error.
            focusView.requestFocus();
        } else {
            // ha ido bien, luego se procede a crear o editar.
            Cura c = new Cura(evolucion, tratamiento, recomendaciones, proceso);
            if(editando)
                editar(c);
            else
                nuevo(c);
        }

    }

    private void nuevo(Cura c) {
        progressDialog.show();
        Call<Cura> call = MyApiAdapter.getApiService().crearCura(Pref.getUserId(), c, Pref.getToken());
        call.enqueue(new Callback<Cura>() {
            @Override
            public void onResponse(Call<Cura> call, Response<Cura> response) {
                progressDialog.cancel();
                if(response.isSuccessful()) {
                    Toasty.success(CuraNewEditActivity.this, getString(R.string.creado_registro),
                            Toast.LENGTH_SHORT, true).show();
                    Intent intent = new Intent(CuraNewEditActivity.this, CurasActivity.class);
                    intent.putExtra("proceso", proceso);
                    startActivity(intent);
                } else {
                    if (response.errorBody().contentType().subtype().equals("json")) {
                        ApiError apiError = ApiError.fromResponseBody(response.errorBody());
                        Toasty.error(CuraNewEditActivity.this, apiError.getMessage(),
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
            public void onFailure(Call<Cura> call, Throwable t) {
                progressDialog.cancel();

                if (t instanceof IOException) {
                    Toasty.warning(CuraNewEditActivity.this, getString(R.string.error_conexion_red),
                            Toast.LENGTH_LONG, true).show();
                } else {
                    Toasty.error(CuraNewEditActivity.this, getString(R.string.error_conversion),
                            Toast.LENGTH_LONG, true).show();
                    Log.d(TAG, getString(R.string.error_conversion));
                }
            }
        });

    }

    private void editar(Cura c) {
        progressDialog.show();
        Call<Cura> call = MyApiAdapter.getApiService().editarCura(cura.getId(), c, Pref.getToken());
        call.enqueue(new Callback<Cura>() {
            @Override
            public void onResponse(Call<Cura> call, Response<Cura> response) {
                progressDialog.cancel();
                if(response.isSuccessful()) {
                    cura = response.body();
                    Toasty.success(CuraNewEditActivity.this, getString(R.string.editado_registro),
                            Toast.LENGTH_SHORT, true).show();
                    Intent intent = new Intent(CuraNewEditActivity.this, ImagenesActivity.class);
                    intent.putExtra("cura", cura);
                    startActivity(intent);
                } else {
                    if (response.errorBody().contentType().subtype().equals("json")) {
                        ApiError apiError = ApiError.fromResponseBody(response.errorBody());
                        Toasty.error(CuraNewEditActivity.this, apiError.getMessage(),
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
            public void onFailure(Call<Cura> call, Throwable t) {
                progressDialog.cancel();

                if (t instanceof IOException) {
                    Toasty.warning(CuraNewEditActivity.this, getString(R.string.error_conexion_red),
                            Toast.LENGTH_LONG, true).show();
                } else {
                    Toasty.error(CuraNewEditActivity.this, getString(R.string.error_conversion),
                            Toast.LENGTH_LONG, true).show();
                    Log.d(TAG, getString(R.string.error_conversion));
                }
            }
        });
    }
}
