package uca.ruiz.antonio.tfgapp.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uca.ruiz.antonio.tfgapp.R;
import uca.ruiz.antonio.tfgapp.data.api.io.MyApiAdapter;
import uca.ruiz.antonio.tfgapp.data.api.mapping.ApiError;
import uca.ruiz.antonio.tfgapp.data.api.model.Paciente;
import uca.ruiz.antonio.tfgapp.data.api.model.Proceso;
import uca.ruiz.antonio.tfgapp.ui.activity.admin.ValoracionesResultsActivity;
import uca.ruiz.antonio.tfgapp.utils.Pref;
import uca.ruiz.antonio.tfgapp.utils.Utils;

import static uca.ruiz.antonio.tfgapp.R.string.procesos;

public class MainPacienteActivity extends AppCompatActivity  {

    private static final String TAG = MainPacienteActivity.class.getSimpleName();
    private ProgressDialog progressDialog;
    private LinearLayout ll_asistencias, ll_cuidados, ll_valoraciones;
    private LinearLayout ll_citas;
    private Paciente paciente;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_paciente);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        ll_asistencias = (LinearLayout) findViewById(R.id.ll_asistencias);
        ll_cuidados = (LinearLayout) findViewById(R.id.ll_cuidados);
        ll_valoraciones = (LinearLayout) findViewById(R.id.ll_valoraciones);
        ll_citas = (LinearLayout) findViewById(R.id.ll_citas);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.cargando));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                Utils.preguntarQuiereSalir(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        Utils.preguntarQuiereSalir(this);
    }

    public  void asistencias(View view) {
        ll_asistencias.setBackgroundResource(R.color.grisFondoLL);
        progressDialog.show();

        Call<Paciente> call = MyApiAdapter.getApiService().getPaciente(Pref.getUserId(), Pref.getToken());
        call.enqueue(new Callback<Paciente>() {
            @Override
            public void onResponse(Call<Paciente> call, Response<Paciente> response) {
                progressDialog.cancel();
                if(response.isSuccessful()) {
                    paciente = response.body();
                    if(paciente != null) {
                        Log.d("PACIENTE", "Tamaño ==> " + paciente);
                        Intent intent = new Intent(MainPacienteActivity.this, ProcesosActivity.class);
                        intent.putExtra("paciente", paciente);
                        startActivity(intent);
                    }
                } else {
                    if (response.errorBody().contentType().subtype().equals("json")) {
                        ApiError apiError = ApiError.fromResponseBody(response.errorBody());
                        Toasty.error(MainPacienteActivity.this, apiError.getMessage(),
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
            public void onFailure(Call<Paciente> call, Throwable t) {
                progressDialog.cancel();

                if (t instanceof IOException) {
                    Toasty.warning(MainPacienteActivity.this, getString(R.string.error_conexion_red),
                            Toast.LENGTH_LONG, true).show();
                } else {
                    Toasty.error(MainPacienteActivity.this, getString(R.string.error_conversion),
                            Toast.LENGTH_LONG, true).show();
                    Log.d(TAG, getString(R.string.error_conversion));
                }
            }
        });

    }

    public  void cuidados(View view) {
        ll_cuidados.setBackgroundResource(R.color.grisFondoLL);
        progressDialog.show();

        Intent intent = new Intent(this, CuidadosActivity.class);
        startActivity(intent);
    }


    public void valoraciones(View view) {
        ll_valoraciones.setBackgroundResource(R.color.grisFondoLL);
        progressDialog.show();

        Intent intent = new Intent(this, CurasSinValorarActivity.class);
        startActivity(intent);
    }

    public  void agenda(View view) {
        ll_citas.setBackgroundResource(R.color.grisFondoLL);
        progressDialog.show();

        Intent intent = new Intent(this, CitacionesActivity.class);
        startActivity(intent);
    }





}