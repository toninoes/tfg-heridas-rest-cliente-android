package uca.ruiz.antonio.tfgapp.ui.activity;

import android.app.Activity;
import android.content.Intent;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uca.ruiz.antonio.tfgapp.R;
import uca.ruiz.antonio.tfgapp.data.api.io.MyApiAdapter;
import uca.ruiz.antonio.tfgapp.data.api.mapping.ApiError;
import uca.ruiz.antonio.tfgapp.data.api.mapping.Error;
import uca.ruiz.antonio.tfgapp.data.api.model.Paciente;
import uca.ruiz.antonio.tfgapp.data.api.model.Proceso;


public class ProcesoNuevoEditarActivity extends AppCompatActivity  {

    private static final String TAG = ProcesoNuevoEditarActivity.class.getSimpleName();
    private EditText et_anamnesis, et_diagnostico, et_tipo, et_observaciones;
    private Button btn_guardar_proceso;
    private Proceso proceso = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_proceso);

        et_anamnesis = (EditText) findViewById(R.id.et_anamnesis);
        et_diagnostico = (EditText) findViewById(R.id.et_diagnostico);
        et_tipo = (EditText) findViewById(R.id.et_tipo);
        et_observaciones = (EditText) findViewById(R.id.et_observaciones);

        btn_guardar_proceso = (Button) findViewById(R.id.btn_guardar_proceso);

        try {
            proceso = (Proceso) getIntent().getExtras().getSerializable("proceso");
        } catch (Exception e) {
            proceso = null;
        }

        if(proceso != null) {// para editar un proceso existente
            setTitle(R.string.editproceso);
            et_anamnesis.setText(proceso.getAnamnesis());
            et_diagnostico.setText(proceso.getDiagnostico());
            et_tipo.setText(proceso.getTipo());
            et_observaciones.setText(proceso.getObservaciones());
        }

        btn_guardar_proceso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(proceso == null)
                    crearProceso();
                else
                    editarProceso(proceso);
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
                            Proceso proceso = response.body();
                            Log.d(TAG, proceso.getDiagnostico());
                            showAnteriorUi(proceso);
                        } else {
                            ArrayList<Error> errores = null;

                            if (response.errorBody().contentType().subtype().equals("json")) {
                                ApiError apiError = ApiError.fromResponseBody(response.errorBody());
                                errores = apiError.getErrors();
                                Log.d(TAG, apiError.getPath());
                            } else {
                                try {
                                    Log.d(TAG, response.errorBody().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            showApiErrores(errores);
                        }

                    }

                    @Override
                    public void onFailure(Call<Proceso> call, Throwable t) {
                        showApiError(t.getMessage());
                    }
                }
        );
    }

    private void editarProceso(Proceso proceso) {
        proceso.setAnamnesis(et_anamnesis.getText().toString());
        proceso.setDiagnostico(et_diagnostico.getText().toString());
        proceso.setObservaciones(et_observaciones.getText().toString());
        proceso.setTipo(et_tipo.getText().toString());

        proceso.getPaciente().setNacimiento(proceso.getPaciente().getNacimiento());

        MyApiAdapter.getApiService().editarProceso(proceso.getId(), proceso).enqueue(
                new Callback<Proceso>() {
                    @Override
                    public void onResponse(Call<Proceso> call, Response<Proceso> response) {
                        if (response.isSuccessful()) {
                            Proceso proceso = response.body();
                            Log.d(TAG, proceso.getDiagnostico());
                            volverProcesoTrasEditar(proceso);
                        } else {
                            ArrayList<Error> errores = null;

                            if (response.errorBody().contentType().subtype().equals("json")) {
                                ApiError apiError = ApiError.fromResponseBody(response.errorBody());
                                errores = apiError.getErrors();
                                Log.d(TAG, apiError.getPath());
                            } else {
                                try {
                                    Log.d(TAG, response.errorBody().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            showApiErrores(errores);
                        }

                    }

                    @Override
                    public void onFailure(Call<Proceso> call, Throwable t) {
                        showApiError(t.getMessage());
                    }
                }
        );
    }

    private void volverProcesoTrasEditar(Proceso proceso) {
        Intent intent = new Intent(this, CurasActivity.class);
        intent.putExtra("proceso", proceso);
        startActivity(intent);
    }

    private void showAnteriorUi(Proceso proceso) {
        //setResult(Activity.RESULT_OK);
        Intent resultIntent = new Intent();
        resultIntent.putExtra("proceso", proceso);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    private void showApiErrores(ArrayList<Error> errores) {
        String e = getString(R.string.errores);
        for (Error error: errores) {
            e += "\n" + error.getDefaultMessage();
        }
        showApiError(e);
    }

    private void showApiError(String error) {
        Toast toast = Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }
}
