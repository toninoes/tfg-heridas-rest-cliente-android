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

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uca.ruiz.antonio.tfgapp.R;
import uca.ruiz.antonio.tfgapp.data.api.io.MyApiAdapter;
import uca.ruiz.antonio.tfgapp.data.api.mapping.Errores;
import uca.ruiz.antonio.tfgapp.data.api.mapping.Error;
import uca.ruiz.antonio.tfgapp.data.api.model.Cura;
import uca.ruiz.antonio.tfgapp.data.api.model.Proceso;
import uca.ruiz.antonio.tfgapp.utils.Pref;


public class CuraNuevaActivity extends AppCompatActivity {

    private static final String TAG = CuraNuevaActivity.class.getSimpleName();
    private EditText et_evolucion, et_tratamiento, et_recomendaciones;

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

        proceso = (Proceso) getIntent().getExtras().getSerializable("proceso");
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
                Intent intentBack = new Intent(this, ProcesosActivity.class);
                startActivity(intentBack);
                return true;
            case R.id.action_guardar:
                crearCura();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void crearCura() {
        Cura cura = new Cura(et_evolucion.getText().toString(), et_tratamiento.getText().toString(),
                et_recomendaciones.getText().toString(), proceso);

        MyApiAdapter.getApiService().crearCura(cura, Pref.getToken()).enqueue(
                new Callback<Cura>() {
                    @Override
                    public void onResponse(Call<Cura> call, Response<Cura> response) {
                        if (response.isSuccessful()) {
                            Cura cura = response.body();
                            Log.d(TAG, cura.getTratamiento());
                            volverCurasActivity(proceso);
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
                    public void onFailure(Call<Cura> call, Throwable t) {
                        mostrarApiError(t.getMessage());
                    }
                }
        );
    }

    private void volverCurasActivity(Proceso proceso) {
        Intent intent = new Intent(this, CurasActivity.class);
        intent.putExtra("proceso", proceso);
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
        /*Toast toast = Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();*/
        Toasty.error(getApplicationContext(), error, Toast.LENGTH_LONG, true).show();
    }
}
