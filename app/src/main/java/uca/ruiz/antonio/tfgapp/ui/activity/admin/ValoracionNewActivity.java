package uca.ruiz.antonio.tfgapp.ui.activity.admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
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
import uca.ruiz.antonio.tfgapp.data.api.model.Cura;
import uca.ruiz.antonio.tfgapp.data.api.model.Valoracion;
import uca.ruiz.antonio.tfgapp.ui.activity.CurasSinValorarActivity;
import uca.ruiz.antonio.tfgapp.utils.Pref;

import static android.widget.Toast.makeText;


public class ValoracionNewActivity extends AppCompatActivity {

    private static final String TAG = ValoracionNewActivity.class.getSimpleName();
    private EditText et_observaciones;
    private Cura cura;
    private Double nota = null;
    private ProgressDialog progressDialog;
    private ImageButton ib_0, ib_2_5, ib_5, ib_7_5, ib_10;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_valoracion_new);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        ib_0 = (ImageButton) findViewById(R.id.ib_0);
        ib_2_5 = (ImageButton) findViewById(R.id.ib_2_5);
        ib_5 = (ImageButton) findViewById(R.id.ib_5);
        ib_7_5 = (ImageButton) findViewById(R.id.ib_7_5);
        ib_10 = (ImageButton) findViewById(R.id.ib_10);

        et_observaciones = (EditText) findViewById(R.id.et_observaciones);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.guardando));

        cura = (Cura) getIntent().getExtras().getSerializable("cura");

        ib_0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ib_0.setImageResource(R.drawable.ic_star_rellena);
                ib_2_5.setImageResource(R.drawable.ic_star);
                ib_5.setImageResource(R.drawable.ic_star);
                ib_7_5.setImageResource(R.drawable.ic_star);
                ib_10.setImageResource(R.drawable.ic_star);
                nota = 0.0;

                mostrarMsg(getString(R.string.val_0));
            }
        });

        ib_2_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ib_0.setImageResource(R.drawable.ic_star_rellena);
                ib_2_5.setImageResource(R.drawable.ic_star_rellena);
                ib_5.setImageResource(R.drawable.ic_star);
                ib_7_5.setImageResource(R.drawable.ic_star);
                ib_10.setImageResource(R.drawable.ic_star);
                nota = 2.5;

                mostrarMsg(getString(R.string.val_2_5));
            }
        });

        ib_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ib_0.setImageResource(R.drawable.ic_star_rellena);
                ib_2_5.setImageResource(R.drawable.ic_star_rellena);
                ib_5.setImageResource(R.drawable.ic_star_rellena);
                ib_7_5.setImageResource(R.drawable.ic_star);
                ib_10.setImageResource(R.drawable.ic_star);
                nota = 5.0;

                mostrarMsg(getString(R.string.val_5));
            }
        });

        ib_7_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ib_0.setImageResource(R.drawable.ic_star_rellena);
                ib_2_5.setImageResource(R.drawable.ic_star_rellena);
                ib_5.setImageResource(R.drawable.ic_star_rellena);
                ib_7_5.setImageResource(R.drawable.ic_star_rellena);
                ib_10.setImageResource(R.drawable.ic_star);
                nota = 7.5;

                mostrarMsg(getString(R.string.val_7_5));
            }
        });

        ib_10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ib_0.setImageResource(R.drawable.ic_star_rellena);
                ib_2_5.setImageResource(R.drawable.ic_star_rellena);
                ib_5.setImageResource(R.drawable.ic_star_rellena);
                ib_7_5.setImageResource(R.drawable.ic_star_rellena);
                ib_10.setImageResource(R.drawable.ic_star_rellena);
                nota = 10.0;

                mostrarMsg(getString(R.string.val_10));
            }
        });

    }

    private void mostrarMsg(String msg) {
        Toast t = Toast.makeText(ValoracionNewActivity.this, msg, Toast.LENGTH_SHORT);
        t.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 200);
        t.show();
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
                startActivity(new Intent(this, CurasSinValorarActivity.class));
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
        et_observaciones.setError(null);


        //tomo el contenido de los campos
        String observaciones = et_observaciones.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Validar selección de nota
        if(nota == null) {
            Toast t = Toasty.warning(ValoracionNewActivity.this, getString(R.string.debes_seleccionar_nota),
                    Toast.LENGTH_SHORT, true);
            t.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 200);
            t.show();
        }

        // Valida campo Observaciones
        if(observaciones.length() > 280) {
            et_observaciones.setError(getString(R.string.maximo_280));
            focusView = et_observaciones;
            cancel = true;
        }

        if (cancel) {
            // Se ha producido un error: no se intenta el registro y se focaliza en el
            // primer campo del formulario con error.
            focusView.requestFocus();
        } else if (nota != null) {
            // ha ido bien, luego se procede a crear la valoración de la cura
            Valoracion v = new Valoracion(cura.getSanitario(), nota, observaciones);

            crearValoracion(v);
        }

    }

    private void crearValoracion(Valoracion v) {
        progressDialog.show();
        Call<Valoracion> call = MyApiAdapter.getApiService().crearValoracion(cura.getId(), v, Pref.getToken());
        call.enqueue(new Callback<Valoracion>() {
            @Override
            public void onResponse(Call<Valoracion> call, Response<Valoracion> response) {
                progressDialog.cancel();
                if(response.isSuccessful()) {
                    Toasty.success(ValoracionNewActivity.this, getString(R.string.valoracion_creada),
                            Toast.LENGTH_SHORT, true).show();
                    startActivity(new Intent(ValoracionNewActivity.this, CurasSinValorarActivity.class));
                } else {
                    if (response.errorBody().contentType().subtype().equals("json")) {
                        ApiError apiError = ApiError.fromResponseBody(response.errorBody());
                        Toasty.error(ValoracionNewActivity.this, apiError.getMessage(),
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
            public void onFailure(Call<Valoracion> call, Throwable t) {
                progressDialog.cancel();

                if (t instanceof IOException) {
                    Toasty.warning(ValoracionNewActivity.this, getString(R.string.error_conexion_red),
                            Toast.LENGTH_LONG, true).show();
                } else {
                    Toasty.error(ValoracionNewActivity.this, getString(R.string.error_conversion),
                            Toast.LENGTH_LONG, true).show();
                    Log.d(TAG, getString(R.string.error_conversion));
                }
            }
        });
    }


}
