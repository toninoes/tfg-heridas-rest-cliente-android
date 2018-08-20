package uca.ruiz.antonio.tfgapp.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
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
import uca.ruiz.antonio.tfgapp.data.api.mapping.Login;
import uca.ruiz.antonio.tfgapp.data.api.mapping.TokenResponse;
import uca.ruiz.antonio.tfgapp.data.api.mapping.UserResponse;
import uca.ruiz.antonio.tfgapp.data.api.model.User;
import uca.ruiz.antonio.tfgapp.ui.activity.admin.MainAdminActivity;
import uca.ruiz.antonio.tfgapp.utils.Pref;
import uca.ruiz.antonio.tfgapp.utils.Validacion;

import static android.R.attr.password;
import static uca.ruiz.antonio.tfgapp.R.id.chk_recordar;
import static uca.ruiz.antonio.tfgapp.R.id.et_password;


public class OlvidoPasswordActivity extends AppCompatActivity {

    private static final String TAG = OlvidoPasswordActivity.class.getSimpleName();
    private EditText et_email;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_olvido_password);

        et_email = (EditText) findViewById(R.id.et_email);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.entrando));

        Button btn_entrar = (Button) findViewById(R.id.btn_entrar);
        btn_entrar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                intentoRestablecerPassword();
            }
        });


    }


    /**
     * Intenta iniciar sesión mediante el formulario de inicio de sesión.
     * Si hay errores de formulario (correo electrónico no válido, campos faltantes, etc.), se
     * presentan los errores y no se realiza ningún intento de inicio de sesión.
     */
    private void intentoRestablecerPassword() {
        // Resetear errores
        et_email.setError(null);

        String email = et_email.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Valida campo Email.
        if (!Validacion.tamEmail(email)) {
            et_email.setError(getString(R.string.error_tam_email_invalido));
            focusView = et_email;
            cancel = true;
        } else if (!Validacion.formatoEmail(email)) {
            et_email.setError(getString(R.string.error_formato_email_invalido));
            focusView = et_email;
            cancel = true;
        }

        if (cancel) {
            // Se ha producido un error: no se intenta el login y se focaliza en el
            // primer campo del formulario con error.
            focusView.requestFocus();
        } else {
            User u = new User(email);
            restablecerPassword(u );
        }
    }

    /**
     * Aquí es donde hacemos la llamada al servidor para restablecer contraseña
     */
    private void restablecerPassword(User u) {
        progressDialog.show();


        Call<String> call = MyApiAdapter.getApiService().resetPassword(u);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                progressDialog.cancel();
                if(response.isSuccessful()) {
                    Toasty.success(OlvidoPasswordActivity.this, getString(R.string.solicitud_correcta),
                            Toast.LENGTH_LONG, true).show();
                    startActivity(new Intent(OlvidoPasswordActivity.this, LoginActivity.class));
                } else {
                    if (response.errorBody().contentType().subtype().equals("json")) {
                        ApiError apiError = ApiError.fromResponseBody(response.errorBody());
                        Toasty.error(OlvidoPasswordActivity.this, apiError.getMessage(),
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
            public void onFailure(Call<String> call, Throwable t) {
                progressDialog.cancel();

                if (t instanceof IOException) {
                    Toasty.warning(OlvidoPasswordActivity.this, getString(R.string.error_conexion_red),
                            Toast.LENGTH_LONG, true).show();
                } else {
                    Toasty.error(OlvidoPasswordActivity.this, getString(R.string.error_conversion),
                            Toast.LENGTH_LONG, true).show();
                    Log.d(TAG, getString(R.string.error_conversion));
                }
            }
        });
    }

}

