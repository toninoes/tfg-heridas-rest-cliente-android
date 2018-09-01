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
import uca.ruiz.antonio.tfgapp.data.api.mapping.Login;
import uca.ruiz.antonio.tfgapp.data.api.io.MyApiAdapter;
import uca.ruiz.antonio.tfgapp.data.api.mapping.Authority;
import uca.ruiz.antonio.tfgapp.data.api.mapping.TokenResponse;
import uca.ruiz.antonio.tfgapp.data.api.mapping.UserResponse;
import uca.ruiz.antonio.tfgapp.ui.activity.admin.MainAdminActivity;
import uca.ruiz.antonio.tfgapp.utils.Pref;
import uca.ruiz.antonio.tfgapp.utils.Validacion;


public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private EditText et_email;
    private EditText et_password;
    private ProgressDialog progressDialog;
    private static String token;
    private CheckBox chk_recordar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        et_email = (EditText) findViewById(R.id.et_email);
        et_password = (EditText) findViewById(R.id.et_password);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.entrando));

        et_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    intentoLogin();
                    return true;
                }
                return false;
            }
        });

        Button btn_entrar = (Button) findViewById(R.id.btn_entrar);
        btn_entrar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                intentoLogin();
            }
        });

        chk_recordar = (CheckBox) findViewById(R.id.chk_recordar);
        Boolean recordarMail = Preferencias.get(this).getBoolean("recordar", false);
        chk_recordar.setChecked(recordarMail);

        if(recordarMail) {
            et_email.setText(Preferencias.get(this).getString("email", "email"));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_configuracion, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.configuracion:
                startActivity(new Intent(this, LoginConfigActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Intenta iniciar sesión mediante el formulario de inicio de sesión.
     * Si hay errores de formulario (correo electrónico no válido, campos faltantes, etc.), se
     * presentan los errores y no se realiza ningún intento de inicio de sesión.
     */
    private void intentoLogin() {
        // Resetear errores
        et_email.setError(null);
        et_password.setError(null);

        String email = et_email.getText().toString();
        String password = et_password.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Valida campo Password
        if (!Validacion.tamPassword(password)) {
            et_password.setError(getString(R.string.error_tam_password_invalida));
            focusView = et_password;
            cancel = true;
        }

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
            login(email, password);
        }
    }

    /**
     * Aquí es donde hacemos la llamada al servidor para hacer login gracias a Retrofit.
     * @param username
     * @param password
     */
    private void login(String username, String password) {
        progressDialog.show();

        Login login = new Login(username, password);

        Call<TokenResponse> call = MyApiAdapter.getApiService().login(login);
        call.enqueue(new Callback<TokenResponse>() {
            @Override
            public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                progressDialog.cancel();
                if(response.isSuccessful()) {
                    token = "Bearer " + response.body().getToken();
                    definirPreferencias(token);
                    obtenerUsuario(token);
                } else {
                    Toasty.error(LoginActivity.this, getString(R.string.error_acceso),
                            Toast.LENGTH_LONG, true).show();
                }
            }

            @Override
            public void onFailure(Call<TokenResponse> call, Throwable t) {
                progressDialog.cancel();

                if (t instanceof IOException) {
                    Toasty.warning(LoginActivity.this, getString(R.string.error_conexion_red),
                            Toast.LENGTH_LONG, true).show();
                } else {
                    Toasty.error(LoginActivity.this, getString(R.string.error_conversion),
                            Toast.LENGTH_LONG, true).show();
                    Log.d(TAG, getString(R.string.error_conversion));
                }
            }
        });
    }

    private void definirPreferencias(String token) {
        Preferencias.getEditor(this).putString("token", token).commit();
        Preferencias.getEditor(this).putString("email", et_email.getText().toString()).commit();
        Preferencias.getEditor(this).putBoolean("recordar", chk_recordar.isChecked()).commit();
    }

    private void obtenerUsuario(String token) {
        Call<UserResponse> call = MyApiAdapter.getApiService().getUser(token);
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if(response.isSuccessful()) {
                    UserResponse user = response.body();
                    definirUsuarioAndMain(user);
                    Toasty.success(LoginActivity.this, getString(R.string.bienvenido) + ", " +
                            user.getFirstname(), Toast.LENGTH_SHORT, true).show();
                } else {
                    Toasty.error(LoginActivity.this, getString(R.string.error_token_incorrecto),
                            Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                progressDialog.cancel();

                if (t instanceof IOException) {
                    Toasty.warning(LoginActivity.this, getString(R.string.error_conexion_red),
                            Toast.LENGTH_LONG, true).show();
                } else {
                    Toasty.error(LoginActivity.this, getString(R.string.error_conversion),
                            Toast.LENGTH_LONG, true).show();
                    Log.d(TAG, getString(R.string.error_conversion));
                }
            }
        });
    }

    private void definirUsuarioAndMain(UserResponse user) {

        Preferencias.getEditor(this).putString("nombre", user.getFirstname()).commit();
        Preferencias.getEditor(this).putString("apellidos", user.getLastname()).commit();
        Preferencias.getEditor(this).putBoolean("activo", user.getEnabled()).commit();
        Preferencias.getEditor(this).putLong("id", user.getId()).commit();

        // Reiniciamos los roles a falso todos
        Preferencias.getEditor(this).putBoolean("ROLE_ADMIN", false).commit();
        Preferencias.getEditor(this).putBoolean("ROLE_SANITARIO", false).commit();
        Preferencias.getEditor(this).putBoolean("ROLE_PACIENTE", false).commit();

        // establecemos los roles que nos llega del servidor
        for(Authority rol: user.getAuthorities()) {
            Preferencias.getEditor(this).putBoolean(rol.getName().toString(), true).commit();
        }

        irMain();
    }

    private void irMain() {
        Intent intent;

        if(Pref.esAdmin())
            intent = new Intent(this, MainAdminActivity.class);
        else if(Pref.esSanitario())
            intent = new Intent(this, MainSanitarioActivity.class);
        else
            intent = new Intent(this, MainPacienteActivity.class);

        startActivity(intent);
    }

    public void resetPassword(View view) {
        startActivity(new Intent(this, OlvidoPasswordActivity.class));
    }

}

