package uca.ruiz.antonio.tfgapp.ui.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uca.ruiz.antonio.tfgapp.R;
import uca.ruiz.antonio.tfgapp.data.api.io.MyApiAdapter;
import uca.ruiz.antonio.tfgapp.data.api.mapping.Errores;
import uca.ruiz.antonio.tfgapp.data.api.mapping.Error;
import uca.ruiz.antonio.tfgapp.data.api.model.Paciente;
import uca.ruiz.antonio.tfgapp.data.api.model.Proceso;
import uca.ruiz.antonio.tfgapp.utils.Pref;


public class ProcesoNuevoActivity extends AppCompatActivity  {

    private static final String TAG = ProcesoNuevoActivity.class.getSimpleName();
    private EditText et_anamnesis, et_diagnostico, et_tipo, et_observaciones;

    private Paciente paciente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proceso_new_edit);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        et_anamnesis = (EditText) findViewById(R.id.et_anamnesis);
        et_diagnostico = (EditText) findViewById(R.id.et_diagnostico);
        et_tipo = (EditText) findViewById(R.id.et_tipo);
        et_observaciones = (EditText) findViewById(R.id.et_observaciones);

        //paciente = (Paciente) getIntent().getExtras().getSerializable("paciente");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_edit_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                Intent intentBack = new Intent(this, ProcesosActivity.class);
                startActivity(intentBack);
                return true;
            case R.id.action_guardar:
                crearProceso();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void crearProceso() {
        Paciente paciente = new Paciente();
        paciente.setId(2);

        Proceso proceso = new Proceso(et_anamnesis.getText().toString(),
                et_diagnostico.getText().toString(), et_tipo.getText().toString(),
                et_observaciones.getText().toString(), paciente);

        MyApiAdapter.getApiService().crearProceso(proceso, Pref.getToken()).enqueue(
                new Callback<Proceso>() {
                    @Override
                    public void onResponse(Call<Proceso> call, Response<Proceso> response) {
                        if (response.isSuccessful()) {
                            Proceso proceso = response.body();
                            Log.d(TAG, proceso.getDiagnostico());
                            volverProcesosActivity();
                        } else {
                            ArrayList<Error> errores = null;

                            if (response.errorBody().contentType().subtype().equals("json")) {
                                Errores apiError = Errores.fromResponseBody(response.errorBody());
                                errores = apiError.getErrors();
                                Log.d(TAG, apiError.getPath());
                            } else {
                                try {
                                    Log.d(TAG, response.errorBody().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            mostrarApiErrores(errores);
                        }

                    }

                    @Override
                    public void onFailure(Call<Proceso> call, Throwable t) {
                        mostrarApiError(t.getMessage());
                    }
                }
        );
    }

    private void volverProcesosActivity() {
        Intent intent = new Intent(this, ProcesosActivity.class);
        intent.putExtra("paciente", paciente);
        startActivity(intent);
    }

    private void mostrarApiErrores(ArrayList<Error> errores) {
        String e = getString(R.string.errores);
        for (Error error: errores) {
            e += "\n" + error.getDefaultMessage();
        }
        mostrarApiError(e);
    }

    private void mostrarApiError(String error) {
        Toast toast = Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }
}
