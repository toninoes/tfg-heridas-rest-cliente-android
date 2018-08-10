package uca.ruiz.antonio.tfgapp.ui.activity.admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import uca.ruiz.antonio.tfgapp.data.api.io.MyApiAdapter;
import uca.ruiz.antonio.tfgapp.data.api.mapping.ApiError;
import uca.ruiz.antonio.tfgapp.data.api.model.Sala;
import uca.ruiz.antonio.tfgapp.data.api.model.SalaConfig;
import uca.ruiz.antonio.tfgapp.utils.Pref;

import static uca.ruiz.antonio.tfgapp.R.id.et_nombre;
import static uca.ruiz.antonio.tfgapp.R.string.centro;
import static uca.ruiz.antonio.tfgapp.R.string.nombre;

public class SalaConfigActivity extends AppCompatActivity {

    private static final String TAG = SalaConfigActivity.class.getSimpleName();
    private Sala sala;
    private Boolean editando = false;
    private ProgressDialog progressDialog;

    private TextView tv_sala, tv_centro;
    private EditText et_horaini, et_minini, et_cupo;
    private CheckBox chk_lunes, chk_martes, chk_miercoles, chk_jueves;
    private CheckBox chk_viernes, chk_sabado, chk_domingo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sala_config);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        tv_sala = (TextView) findViewById(R.id.tv_sala);
        tv_centro = (TextView) findViewById(R.id.tv_centro);

        et_horaini = (EditText) findViewById(R.id.et_horaini);
        et_minini = (EditText) findViewById(R.id.et_minini);
        et_cupo = (EditText) findViewById(R.id.et_cupo);

        chk_lunes = (CheckBox) findViewById(R.id.chk_lunes);
        chk_martes = (CheckBox) findViewById(R.id.chk_martes);
        chk_miercoles = (CheckBox) findViewById(R.id.chk_miercoles);
        chk_jueves = (CheckBox) findViewById(R.id.chk_jueves);
        chk_viernes = (CheckBox) findViewById(R.id.chk_viernes);
        chk_sabado = (CheckBox) findViewById(R.id.chk_sabado);
        chk_domingo = (CheckBox) findViewById(R.id.chk_domingo);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.guardando));

        sala = (Sala) getIntent().getExtras().getSerializable("sala");
        tv_sala.setText(sala.getNombre());
        tv_centro.setText(sala.getCentro().getNombre());


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
                startActivity(new Intent(this, SalasActivity.class));
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
        et_horaini.setError(null);
        et_minini.setError(null);
        et_cupo.setError(null);

        //tomo el contenido de los campos
        if(et_horaini.getText().toString().isEmpty())
            et_horaini.setText("-1");
        if(et_minini.getText().toString().isEmpty())
            et_minini.setText("-1");
        if(et_cupo.getText().toString().isEmpty())
            et_cupo.setText("-1");

        Integer horaini = Integer.valueOf(et_horaini.getText().toString());
        Integer minini = Integer.valueOf(et_minini.getText().toString());
        Integer cupo = Integer.valueOf(et_cupo.getText().toString());


        boolean cancel = false;
        View focusView = null;

        if(cupo < 0) {
            et_cupo.setError(getString(R.string.valor_mayor_cero));
            focusView = et_cupo;
            cancel = true;
        }

        if(minini < 0 || minini > 59) {
            et_minini.setError(getString(R.string.valor_0_59));
            focusView = et_minini;
            cancel = true;
        }

        if(horaini < 0 || horaini > 23) {
            et_horaini.setError(getString(R.string.valor_0_23));
            focusView = et_horaini;
            cancel = true;
        }

        if (cancel) {
            // Se ha producido un error: no se intenta el registro y se focaliza en el
            // primer campo del formulario con error.
            focusView.requestFocus();
        } else {
            // ha ido bien, luego se procede a crear o editar.
            SalaConfig sC = new SalaConfig(cupo, horaini, minini, chk_lunes.isChecked(),
                    chk_martes.isChecked(), chk_miercoles.isChecked(), chk_jueves.isChecked(),
                    chk_viernes.isChecked(), chk_sabado.isChecked(), chk_domingo.isChecked(),
                    sala);

            /*if(editando)
                editar(s);
            else*/
                nuevo(sC);
        }
    }

    private void nuevo(SalaConfig sC) {
        progressDialog.show();
        Call<SalaConfig> call = MyApiAdapter.getApiService().crearSalaConfig(sala.getId(), sC, Pref.getToken());
        call.enqueue(new Callback<SalaConfig>() {
            @Override
            public void onResponse(Call<SalaConfig> call, Response<SalaConfig> response) {
                progressDialog.cancel();
                if(response.isSuccessful()) {
                    Toasty.success(SalaConfigActivity.this, getString(R.string.creado_registro),
                            Toast.LENGTH_SHORT, true).show();

                    startActivity(new Intent(SalaConfigActivity.this, SalasActivity.class));
                } else {
                    if (response.errorBody().contentType().subtype().equals("json")) {
                        ApiError apiError = ApiError.fromResponseBody(response.errorBody());
                        Toasty.error(SalaConfigActivity.this, apiError.getMessage(),
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
            public void onFailure(Call<SalaConfig> call, Throwable t) {
                progressDialog.cancel();

                if (t instanceof IOException) {
                    Toasty.warning(SalaConfigActivity.this, getString(R.string.error_conexion_red),
                            Toast.LENGTH_LONG, true).show();
                } else {
                    Toasty.error(SalaConfigActivity.this, getString(R.string.error_conversion),
                            Toast.LENGTH_LONG, true).show();
                    Log.d(TAG, getString(R.string.error_conversion));
                }
            }
        });
    }
}
