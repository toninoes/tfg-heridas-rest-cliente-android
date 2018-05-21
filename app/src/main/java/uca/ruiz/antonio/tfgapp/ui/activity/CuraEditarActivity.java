package uca.ruiz.antonio.tfgapp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import uca.ruiz.antonio.tfgapp.data.api.mapping.ApiError;
import uca.ruiz.antonio.tfgapp.data.api.mapping.Error;
import uca.ruiz.antonio.tfgapp.data.api.model.Cura;
import uca.ruiz.antonio.tfgapp.data.api.model.Proceso;

public class CuraEditarActivity extends AppCompatActivity {

    private static final String TAG = CuraEditarActivity.class.getSimpleName();
    private EditText et_evolucion, et_tratamiento, et_recomendaciones;
    private Cura cura;
    private Proceso proceso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cura_new_edit);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        et_evolucion = (EditText) findViewById(R.id.et_evolucion);
        et_tratamiento = (EditText) findViewById(R.id.et_tratamiento);
        et_recomendaciones = (EditText) findViewById(R.id.et_recomendaciones);

        cura = (Cura) getIntent().getExtras().getSerializable("cura");
        proceso = (Proceso) getIntent().getExtras().getSerializable("proceso");
        if(cura != null) {// para editar un proceso existente
            setTitle(R.string.editcura);
            et_evolucion.setText(cura.getEvolucion());
            et_tratamiento.setText(cura.getTratamiento());
            et_recomendaciones.setText(cura.getRecomendaciones());
        }
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
                if(cura == null) {
                    crearCura();
                }else
                    editarCura(cura);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void crearCura() {

        Cura cura = new Cura(et_evolucion.getText().toString(), et_tratamiento.getText().toString(),
                et_recomendaciones.getText().toString(), proceso);

        MyApiAdapter.getApiService().crearCura(cura).enqueue(
                new Callback<Cura>() {
                    @Override
                    public void onResponse(Call<Cura> call, Response<Cura> response) {
                        if (response.isSuccessful()) {
                            Cura cura = response.body();
                            Log.d(TAG, cura.getTratamiento());
                            volverCurasActivity(cura);
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

                            mostrarApiErrores(errores);
                        }

                    }

                    @Override
                    public void onFailure(Call<Cura> call, Throwable t) {
                        mostrarApiError(t.getMessage());
                    }
                }
        );
    }

    private void editarCura(Cura cura) {
        cura.setEvolucion(et_evolucion.getText().toString());
        cura.setTratamiento(et_tratamiento.getText().toString());
        cura.setEvolucion(et_evolucion.getText().toString());

        MyApiAdapter.getApiService().editarCura(cura.getId(), cura).enqueue(
                new Callback<Cura>() {
                    @Override
                    public void onResponse(Call<Cura> call, Response<Cura> response) {
                        if (response.isSuccessful()) {
                            Cura cura = response.body();
                            Log.d(TAG, cura.getTratamiento());
                            volverCuraTrasEditar(cura);
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

                            mostrarApiErrores(errores);
                        }

                    }

                    @Override
                    public void onFailure(Call<Cura> call, Throwable t) {
                        mostrarApiError(t.getMessage());
                    }
                }
        );
    }

    private void volverCuraTrasEditar(Cura cura) {
        Intent intent = new Intent(this, ImagenesActivity.class);
        intent.putExtra("cura", cura);
        startActivity(intent);
    }

    private void volverCurasActivity(Cura cura) {
        Intent intent = new Intent(this, CurasActivity.class);
        intent.putExtra("cura", cura);
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
