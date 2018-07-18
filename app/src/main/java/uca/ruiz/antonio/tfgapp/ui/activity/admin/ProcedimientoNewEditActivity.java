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
import uca.ruiz.antonio.tfgapp.data.api.model.Procedimiento;
import uca.ruiz.antonio.tfgapp.utils.Pref;
import uca.ruiz.antonio.tfgapp.utils.Validacion;



public class ProcedimientoNewEditActivity extends AppCompatActivity {

    private static final String TAG = ProcedimientoNewEditActivity.class.getSimpleName();
    private EditText et_nombre, et_codigo;
    private Procedimiento procedimiento;
    private Boolean editando = false;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_procedimiento_new_edit);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        et_nombre = (EditText) findViewById(R.id.et_nombre);
        et_codigo = (EditText) findViewById(R.id.et_codigo);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.guardando));

        try { // editar
            procedimiento = (Procedimiento) getIntent().getExtras().getSerializable("procedimiento");
            et_codigo.setText(procedimiento.getCodigo());
            et_nombre.setText(procedimiento.getNombre());
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
                startActivity(new Intent(this, ProcedimientosActivity.class));
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

        //tomo el contenido de los campos
        String nombre = et_nombre.getText().toString();
        String codigo = et_codigo.getText().toString();

        boolean cancel = false;
        View focusView = null;

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
            Procedimiento p = new Procedimiento(codigo, nombre);
            if(editando)
                editar(p);
            else
                nuevo(p);
        }

    }

    private void nuevo(Procedimiento p) {
        progressDialog.show();
        Call<Procedimiento> call = MyApiAdapter.getApiService().crearProcedimiento(p, Pref.getToken());
        call.enqueue(new Callback<Procedimiento>() {
            @Override
            public void onResponse(Call<Procedimiento> call, Response<Procedimiento> response) {
                if(response.isSuccessful()) {
                    progressDialog.cancel();
                    Toasty.success(ProcedimientoNewEditActivity.this, getString(R.string.creado_registro),
                            Toast.LENGTH_SHORT, true).show();
                    startActivity(new Intent(ProcedimientoNewEditActivity.this, ProcedimientosActivity.class));
                } else {
                    progressDialog.cancel();
                    if (response.errorBody().contentType().subtype().equals("json")) {
                        ApiError apiError = ApiError.fromResponseBody(response.errorBody());
                        Toasty.error(ProcedimientoNewEditActivity.this, apiError.getMessage(),
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
            public void onFailure(Call<Procedimiento> call, Throwable t) {
                progressDialog.cancel();

                if (t instanceof IOException) {
                    Toasty.warning(ProcedimientoNewEditActivity.this, getString(R.string.error_conexion_red),
                            Toast.LENGTH_LONG, true).show();
                } else {
                    Toasty.error(ProcedimientoNewEditActivity.this, getString(R.string.error_conversion),
                            Toast.LENGTH_LONG, true).show();
                    Log.d(TAG, getString(R.string.error_conversion));
                }
            }
        });

    }

    private void editar(Procedimiento p) {
        progressDialog.show();
        Call<Procedimiento> call = MyApiAdapter.getApiService().editarProcedimiento(procedimiento.getId(), p, Pref.getToken());
        call.enqueue(new Callback<Procedimiento>() {
            @Override
            public void onResponse(Call<Procedimiento> call, Response<Procedimiento> response) {
                if(response.isSuccessful()) {
                    progressDialog.cancel();
                    Toasty.success(ProcedimientoNewEditActivity.this, getString(R.string.editado_registro),
                            Toast.LENGTH_SHORT, true).show();
                    startActivity(new Intent(ProcedimientoNewEditActivity.this, ProcedimientosActivity.class));
                } else {
                    progressDialog.cancel();
                    if (response.errorBody().contentType().subtype().equals("json")) {
                        ApiError apiError = ApiError.fromResponseBody(response.errorBody());
                        Toasty.error(ProcedimientoNewEditActivity.this, apiError.getMessage(),
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
            public void onFailure(Call<Procedimiento> call, Throwable t) {
                progressDialog.cancel();

                if (t instanceof IOException) {
                    Toasty.warning(ProcedimientoNewEditActivity.this, getString(R.string.error_conexion_red),
                            Toast.LENGTH_LONG, true).show();
                } else {
                    Toasty.error(ProcedimientoNewEditActivity.this, getString(R.string.error_conversion),
                            Toast.LENGTH_LONG, true).show();
                    Log.d(TAG, getString(R.string.error_conversion));
                }
            }
        });
    }


}
