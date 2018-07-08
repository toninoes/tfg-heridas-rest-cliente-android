package uca.ruiz.antonio.tfgapp.ui.activity.admin;

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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uca.ruiz.antonio.tfgapp.R;
import uca.ruiz.antonio.tfgapp.data.api.io.MyApiAdapter;
import uca.ruiz.antonio.tfgapp.data.api.mapping.ApiError;
import uca.ruiz.antonio.tfgapp.data.api.model.Centro;
import uca.ruiz.antonio.tfgapp.utils.Pref;
import uca.ruiz.antonio.tfgapp.utils.Validacion;


public class CentroNewEditActivity extends AppCompatActivity {

    private static final String TAG = CentroNewEditActivity.class.getSimpleName();
    private EditText et_nombre, et_direccion, et_telefono;
    private Centro centro;
    private Boolean editando = false;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_centro_new_edit);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        et_nombre = (EditText) findViewById(R.id.et_nombre);
        et_direccion = (EditText) findViewById(R.id.et_direccion);
        et_telefono = (EditText) findViewById(R.id.et_telefono);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.guardando));

        try { // editar
            centro = (Centro) getIntent().getExtras().getSerializable("centro");
            et_nombre.setText(centro.getNombre());
            et_direccion.setText(centro.getDireccion());
            et_telefono.setText(centro.getTelefono());
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
                startActivity(new Intent(this, CentrosActivity.class));
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
        et_direccion.setError(null);
        et_telefono.setError(null);

        //tomo el contenido de los campos
        String nombre = et_nombre.getText().toString();
        String direccion = et_direccion.getText().toString();
        String telefono = et_telefono.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Valida campo Dirección
        if(!Validacion.tamDireccion(direccion)) {
            et_direccion.setError(getString(R.string.error_tam_direccion_invalido));
            focusView = et_direccion;
            cancel = true;
        }

        // Valida campo Nombre
        if(!Validacion.tamNombre(nombre)) {
            et_nombre.setError(getString(R.string.error_tam_nombre_invalido));
            focusView = et_nombre;
            cancel = true;
        }

        if (cancel) {
            // Se ha producido un error: no se intenta el registro y se focaliza en el
            // primer campo del formulario con error.
            focusView.requestFocus();
        } else {
            // ha ido bien, luego se procede a crear o editar.
            Centro c = new Centro(nombre, direccion, telefono);
            if(editando)
                editar(c);
            else
                nuevo(c);
        }

    }

    private void nuevo(Centro c) {
        progressDialog.show();
        Call<Centro> call = MyApiAdapter.getApiService().crearCentro(c, Pref.getToken());
        call.enqueue(new Callback<Centro>() {
            @Override
            public void onResponse(Call<Centro> call, Response<Centro> response) {
                if(response.isSuccessful()) {
                    progressDialog.cancel();
                    Toast.makeText(CentroNewEditActivity.this, getString(R.string.creado_registro),
                            Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(CentroNewEditActivity.this, CentrosActivity.class));
                } else {
                    progressDialog.cancel();
                    if (response.errorBody().contentType().subtype().equals("json")) {
                        ApiError apiError = ApiError.fromResponseBody(response.errorBody());
                        Toast.makeText(CentroNewEditActivity.this, apiError.getMessage(),
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
            public void onFailure(Call<Centro> call, Throwable t) {
                progressDialog.cancel();
                Toast.makeText(CentroNewEditActivity.this, "error :(", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void editar(Centro c) {
        progressDialog.show();
        Call<Centro> call = MyApiAdapter.getApiService().editarCentro(centro.getId(), c, Pref.getToken());
        call.enqueue(new Callback<Centro>() {
            @Override
            public void onResponse(Call<Centro> call, Response<Centro> response) {
                if(response.isSuccessful()) {
                    progressDialog.cancel();
                    Toast.makeText(CentroNewEditActivity.this, getString(R.string.editado_registro), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(CentroNewEditActivity.this, CentrosActivity.class));
                } else {
                    progressDialog.cancel();
                    if (response.errorBody().contentType().subtype().equals("json")) {
                        ApiError apiError = ApiError.fromResponseBody(response.errorBody());
                        Toast.makeText(CentroNewEditActivity.this, apiError.getMessage(),
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
            public void onFailure(Call<Centro> call, Throwable t) {
                progressDialog.cancel();
                Toast.makeText(CentroNewEditActivity.this, "error :(", Toast.LENGTH_LONG).show();
            }
        });
    }


}
