package uca.ruiz.antonio.tfgapp.ui.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uca.ruiz.antonio.tfgapp.R;
import uca.ruiz.antonio.tfgapp.data.api.io.MyApiAdapter;
import uca.ruiz.antonio.tfgapp.data.api.mapping.ApiError;
import uca.ruiz.antonio.tfgapp.data.api.mapping.Authority;
import uca.ruiz.antonio.tfgapp.data.api.mapping.User;
import uca.ruiz.antonio.tfgapp.data.api.mapping.UserResponse;
import uca.ruiz.antonio.tfgapp.utils.Pref;
import uca.ruiz.antonio.tfgapp.utils.Validacion;

public class RegistroActivity extends AppCompatActivity {

    private static final String TAG = RegistroActivity.class.getSimpleName();
    Button btn_registrar;
    private EditText et_nombre, et_apellidos, et_email;
    private CheckBox chk_adm, chk_san, chk_pac;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        et_nombre = (EditText) findViewById(R.id.et_nombre);
        et_apellidos = (EditText) findViewById(R.id.et_apellidos);
        et_email = (EditText) findViewById(R.id.et_email);
        chk_adm = (CheckBox) findViewById(R.id.chk_adm);
        chk_san = (CheckBox) findViewById(R.id.chk_san);
        chk_pac = (CheckBox) findViewById(R.id.chk_pac);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.registrando));

        btn_registrar = (Button) findViewById(R.id.btn_registrar);
        btn_registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Authority> roles = new ArrayList<Authority>();
                ArrayList<Boolean> permisos = new ArrayList<>(Arrays.asList(false, false, false));
                if(chk_adm.isChecked())
                    permisos.set(0, true);

                if(chk_san.isChecked())
                    permisos.set(1, true);

                if(chk_pac.isChecked())
                    permisos.set(2, true);

                User user = new User(et_email.getText().toString(), et_nombre.getText().toString(),
                        et_apellidos.getText().toString(), et_email.getText().toString(), roles, permisos);

                intentoRegistro(user);
            }
        });
    }

    /**
     * Intenta registrar usuario mediante el formulario de registro.
     * Si hay errores de formulario (correo electrónico no válido, campos faltantes, etc.), se
     * presentan los errores y no se realiza ningún intento de registro.
     */
    private void intentoRegistro(User user) {
        // Resetear errores
        et_nombre.setError(null);
        et_apellidos.setError(null);
        et_email.setError(null);

        //tomo el contenido de los campos
        String nombre = et_nombre.getText().toString();
        String apellidos = et_apellidos.getText().toString();
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

        // Valida campo Apellidos
        if(!Validacion.tamNombre(apellidos)) {
            et_apellidos.setError(getString(R.string.error_tam_apellidos_invalido));
            focusView = et_apellidos;
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
            registrar(user);
        }

    }

    private void registrar(User user) {
        progressDialog.show();
        Call<UserResponse> call = MyApiAdapter.getApiService().registro(user, Pref.getToken());

        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if(response.isSuccessful()) {
                    progressDialog.cancel();
                    Toast.makeText(RegistroActivity.this, "Creado usuario " + response.body().getEmail(),
                            Toast.LENGTH_LONG).show();
                } else {
                    progressDialog.cancel();
                    if (response.errorBody().contentType().subtype().equals("json")) {
                        ApiError apiError = ApiError.fromResponseBody(response.errorBody());
                        Toast.makeText(RegistroActivity.this, apiError.getMessage(), Toast.LENGTH_LONG).show();
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
            public void onFailure(Call<UserResponse> call, Throwable t) {
                progressDialog.cancel();
                Toast.makeText(RegistroActivity.this, "error :(", Toast.LENGTH_SHORT).show();
            }
        });
    }

    
}
