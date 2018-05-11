package uca.ruiz.antonio.tfgapp.ui.activity;

import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uca.ruiz.antonio.tfgapp.R;
import uca.ruiz.antonio.tfgapp.data.api.io.MyApiAdapter;
import uca.ruiz.antonio.tfgapp.data.api.mapping.ApiError;
import uca.ruiz.antonio.tfgapp.data.api.mapping.Error;
import uca.ruiz.antonio.tfgapp.data.api.model.Paciente;
import uca.ruiz.antonio.tfgapp.data.api.model.Proceso;


public class AddProcesoActivity extends AppCompatActivity  {

    private static final String TAG = AddProcesoActivity.class.getSimpleName();
    private EditText et_anamnesis, et_diagnostico, et_tipo, et_observaciones;
    private Button btn_guardar_proceso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_proceso);

        et_anamnesis = (EditText) findViewById(R.id.et_anamnesis);
        et_diagnostico = (EditText) findViewById(R.id.et_diagnostico);
        et_tipo = (EditText) findViewById(R.id.et_tipo);
        et_observaciones = (EditText) findViewById(R.id.et_observaciones);

        btn_guardar_proceso = (Button) findViewById(R.id.btn_guardar_proceso);
        btn_guardar_proceso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                crearProceso();
            }
        });
    }

    private void crearProceso() {
        Paciente paciente = new Paciente();
        paciente.setId(2);

        Proceso proceso = new Proceso(et_anamnesis.getText().toString(),
                et_diagnostico.getText().toString(), et_tipo.getText().toString(),
                et_observaciones.getText().toString(), paciente);

        MyApiAdapter.getApiService().crearProceso(proceso).enqueue(
                new Callback<Proceso>() {
                    @Override
                    public void onResponse(Call<Proceso> call, Response<Proceso> response) {
                        if (response.isSuccessful()) {
                            Proceso res = response.body();
                            Log.d(TAG, res.getDiagnostico());
                            showProcesosUi();

                        } else {
                            ArrayList<Error> error = null;

                            if (response.errorBody().contentType().subtype().equals("json")) {
                                ApiError apiError = ApiError.fromResponseBody(response.errorBody());
                                error = apiError.getErrors();
                                Log.d(TAG, apiError.getPath());
                            }/* else {
                                try {
                                    Log.d(TAG, response.errorBody().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }*/

                            showApiErrores(error);
                        }

                    }

                    @Override
                    public void onFailure(Call<Proceso> call, Throwable t) {
                        showApiError(t.getMessage());
                    }
                }
        );
    }

    private void showProcesosUi() {
        setResult(Activity.RESULT_OK);
        finish();
    }

    private void showApiErrores(ArrayList<Error> errores) {
        String e = "ERRORES";
        for (Error error: errores) {
            e += "\n" + error.getDefaultMessage();
        }
        showApiError(e);
    }

    private void showApiError(String error) {
       // Snackbar.make(findViewById(android.R.id.content), error, Snackbar.LENGTH_LONG).show();
        Toast toast =
                Toast.makeText(getApplicationContext(),
                        error, Toast.LENGTH_LONG);

        toast.setGravity(Gravity.CENTER,0,0);

        toast.show();
    }
}
