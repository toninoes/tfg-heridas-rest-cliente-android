package uca.ruiz.antonio.tfgapp.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uca.ruiz.antonio.tfgapp.R;
import uca.ruiz.antonio.tfgapp.data.Preferencias;
import uca.ruiz.antonio.tfgapp.data.api.io.MyApiAdapter;
import uca.ruiz.antonio.tfgapp.data.api.mapping.ApiError;
import uca.ruiz.antonio.tfgapp.data.api.mapping.Authority;
import uca.ruiz.antonio.tfgapp.data.api.mapping.CambiarPassword;
import uca.ruiz.antonio.tfgapp.data.api.mapping.Login;
import uca.ruiz.antonio.tfgapp.data.api.mapping.TokenResponse;
import uca.ruiz.antonio.tfgapp.data.api.mapping.UserResponse;
import uca.ruiz.antonio.tfgapp.data.api.model.Centro;
import uca.ruiz.antonio.tfgapp.data.api.model.Cuidado;
import uca.ruiz.antonio.tfgapp.data.api.model.User;
import uca.ruiz.antonio.tfgapp.ui.activity.admin.CentroNewEditActivity;
import uca.ruiz.antonio.tfgapp.ui.activity.admin.CentrosActivity;
import uca.ruiz.antonio.tfgapp.ui.activity.admin.MainAdminActivity;
import uca.ruiz.antonio.tfgapp.utils.Pref;
import uca.ruiz.antonio.tfgapp.utils.Validacion;

import static uca.ruiz.antonio.tfgapp.R.id.et_direccion;
import static uca.ruiz.antonio.tfgapp.R.id.et_nombre;
import static uca.ruiz.antonio.tfgapp.R.id.et_telefono;


public class CambioPassActivity extends AppCompatActivity {

    private static final String TAG = CambioPassActivity.class.getSimpleName();
    private EditText et_password_actual, et_password_nueva, et_password_nueva_repetida;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambiar_password);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        et_password_actual = (EditText) findViewById(R.id.et_password_actual);
        et_password_nueva = (EditText) findViewById(R.id.et_password_nueva);
        et_password_nueva_repetida = (EditText) findViewById(R.id.et_password_nueva_repetida);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.guardando));

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
        et_password_actual.setError(null);
        et_password_nueva.setError(null);
        et_password_nueva_repetida.setError(null);

        //tomo el contenido de los campos
        String password_actual = et_password_actual.getText().toString();
        String password_nueva = et_password_nueva.getText().toString();
        String password_nueva_repetida = et_password_nueva_repetida.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Valida la repetición de la nueva contraseña
        if(!password_nueva_repetida.equals(password_nueva)) {
            et_password_nueva_repetida.setError(getString(R.string.error_repeticion_password));
            focusView = et_password_nueva_repetida;
            cancel = true;
        }

        // Valida campo password_nueva_repetida
        if(!Validacion.tamPassword(password_nueva_repetida)) {
            et_password_nueva_repetida.setError(getString(R.string.error_tam_password_invalida));
            focusView = et_password_nueva_repetida;
            cancel = true;
        }

        // Valida campo password_nueva
        if(!Validacion.tamPassword(password_nueva)) {
            et_password_nueva.setError(getString(R.string.error_tam_password_invalida));
            focusView = et_password_nueva;
            cancel = true;
        }

        // Valida campo password_actual
        if(!Validacion.tamPassword(password_actual)) {
            et_password_actual.setError(getString(R.string.error_tam_password_invalida));
            focusView = et_password_actual;
            cancel = true;
        }

        if (cancel) {
            // Se ha producido un error: no se intenta el registro y se focaliza en el
            // primer campo del formulario con error.
            focusView.requestFocus();
        } else {
            // ha ido bien, luego se procede a solicitar la modificación de contraseña
            CambiarPassword cp = new CambiarPassword(password_actual, password_nueva);
            solicitarCambiarPassword(cp);

        }

    }

    private void solicitarCambiarPassword(CambiarPassword cp) {
        progressDialog.show();
        Call<User> call = MyApiAdapter.getApiService().solicitarCambiarPassword(Pref.getUserId(),
                cp, Pref.getToken());
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                progressDialog.cancel();
                if(response.isSuccessful()) {
                    Toasty.success(CambioPassActivity.this, getString(R.string.password_cambiada),
                            Toast.LENGTH_SHORT, true).show();

                    Intent intent = new Intent(CambioPassActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    if (response.errorBody().contentType().subtype().equals("json")) {
                        ApiError apiError = ApiError.fromResponseBody(response.errorBody());
                        Toasty.error(CambioPassActivity.this, apiError.getMessage(),
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
            public void onFailure(Call<User> call, Throwable t) {
                progressDialog.cancel();

                if (t instanceof IOException) {
                    Toasty.warning(CambioPassActivity.this, getString(R.string.error_conexion_red),
                            Toast.LENGTH_LONG, true).show();
                } else {
                    Toasty.error(CambioPassActivity.this, getString(R.string.error_conversion),
                            Toast.LENGTH_LONG, true).show();
                    Log.d(TAG, getString(R.string.error_conversion));
                }
            }
        });
    }


}