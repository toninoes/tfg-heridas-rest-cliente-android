package uca.ruiz.antonio.tfgapp.ui.activity.admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uca.ruiz.antonio.tfgapp.R;
import uca.ruiz.antonio.tfgapp.data.api.io.MyApiAdapter;
import uca.ruiz.antonio.tfgapp.data.api.mapping.ApiError;
import uca.ruiz.antonio.tfgapp.data.api.model.Centro;
import uca.ruiz.antonio.tfgapp.data.api.model.Sala;
import uca.ruiz.antonio.tfgapp.data.api.model.SalaConfig;
import uca.ruiz.antonio.tfgapp.utils.Pref;
import uca.ruiz.antonio.tfgapp.utils.Validacion;

public class SalaNewEditActivity extends AppCompatActivity {

    private static final String TAG = SalaNewEditActivity.class.getSimpleName();
    private EditText et_nombre;
    private Spinner sp_centros;
    private TextView sp_centros_text;
    private Sala sala;
    private Centro centro;
    private Boolean editando = false;
    private ProgressDialog progressDialog;

    private EditText et_horaini, et_horafin, et_minini, et_minfin, et_cupo;
    private CheckBox chk_lunes, chk_martes, chk_miercoles, chk_jueves;
    private CheckBox chk_viernes, chk_sabado, chk_domingo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sala_new_edit);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        et_nombre = (EditText) findViewById(R.id.et_nombre);
        sp_centros = (Spinner) findViewById(R.id.sp_centros);
        sp_centros_text = (TextView) findViewById(R.id.sp_centros_error);

        et_horaini = (EditText) findViewById(R.id.et_horaini);
        et_horafin = (EditText) findViewById(R.id.et_horafin);
        et_minini = (EditText) findViewById(R.id.et_minini);
        et_minfin = (EditText) findViewById(R.id.et_minfin);
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

        try { // editar
            sala = (Sala) getIntent().getExtras().getSerializable("sala");
            et_nombre.setText(sala.getNombre());
            cargarCentros(sp_centros, sala);
            editando = true;
        } catch (Exception e) { // nuevo
            Log.d(TAG, getString(R.string.creando_nuevo_registro));
            cargarCentros(sp_centros, sala); // sala tiene id=0.
        }

        sp_centros.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                centro = (Centro) adapterView.getAdapter().getItem(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


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
        et_nombre.setError(null);
        sp_centros_text.setError(null);

        et_horaini.setError(null);
        et_horafin.setError(null);
        et_minini.setError(null);
        et_minfin.setError(null);
        et_cupo.setError(null);

        //tomo el contenido de los campos
        String nombre = et_nombre.getText().toString();

        if(et_horaini.getText().toString().isEmpty())
            et_horaini.setText("-1");
        if(et_horafin.getText().toString().isEmpty())
            et_horafin.setText("-1");
        if(et_minini.getText().toString().isEmpty())
            et_minini.setText("-1");
        if(et_minfin.getText().toString().isEmpty())
            et_minfin.setText("-1");
        if(et_cupo.getText().toString().isEmpty())
            et_cupo.setText("-1");

        Integer horaini = Integer.valueOf(et_horaini.getText().toString());
        Integer horafin = Integer.valueOf(et_horafin.getText().toString());
        Integer minini = Integer.valueOf(et_minini.getText().toString());
        Integer minfin = Integer.valueOf(et_minfin.getText().toString());
        Integer cupo = Integer.valueOf(et_cupo.getText().toString());


        boolean cancel = false;
        View focusView = null;

        if((horafin < horaini) || (horafin == horaini && minfin <= minini)) {
            et_horafin.setError(getString(R.string.hora_fin_mayor_inicio));
            focusView = et_horafin;
            cancel = true;
        }

        if(cupo < 0) {
            et_cupo.setError(getString(R.string.valor_mayor_cero));
            focusView = et_cupo;
            cancel = true;
        }

        if(minfin < 0 || minfin > 59) {
            et_minfin.setError(getString(R.string.valor_0_59));
            focusView = et_minfin;
            cancel = true;
        }

        if(horafin < 0 || horafin > 23) {
            et_horafin.setError(getString(R.string.valor_0_23));
            focusView = et_horafin;
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

        // Validar listado de Centros
        if(centro.getId() == 0 || centro == null) {
            sp_centros_text.setError(getString(R.string.debes_seleccionar_centro));
            focusView = sp_centros_text;
            cancel = true;
        }

        // Valida campo Nombre
        if(Validacion.vacio(nombre)) {
            et_nombre.setError(getString(R.string.campo_no_vacio));
            focusView = et_nombre;
            cancel = true;
        }

        if (cancel) {
            // Se ha producido un error: no se intenta el registro y se focaliza en el
            // primer campo del formulario con error.
            focusView.requestFocus();
        } else {
            // ha ido bien, luego se procede a crear o editar.
            Sala s = new Sala(nombre, centro);
            SalaConfig sC = new SalaConfig(cupo, horaini, minini, horafin, minfin,
                    chk_lunes.isChecked(), chk_martes.isChecked(), chk_miercoles.isChecked(),
                    chk_jueves.isChecked(), chk_viernes.isChecked(), chk_sabado.isChecked(),
                    chk_domingo.isChecked(), sala);

            //SalaConfig sC = new SalaConfig(15, 10, 0, 13, 30, true, false, true, false, false, true, false);
            if(editando)
                editar(s);
            else
                nuevo(s);
        }

    }


    private void nuevo(Sala s) {
        progressDialog.show();
        Call<Sala> call = MyApiAdapter.getApiService().crearSala(s, Pref.getToken());
        call.enqueue(new Callback<Sala>() {
            @Override
            public void onResponse(Call<Sala> call, Response<Sala> response) {
                progressDialog.cancel();
                if(response.isSuccessful()) {
                    Toasty.success(SalaNewEditActivity.this, getString(R.string.creado_registro),
                            Toast.LENGTH_SHORT, true).show();
                    startActivity(new Intent(SalaNewEditActivity.this, SalasActivity.class));
                } else {
                    if (response.errorBody().contentType().subtype().equals("json")) {
                        ApiError apiError = ApiError.fromResponseBody(response.errorBody());
                        Toasty.error(SalaNewEditActivity.this, apiError.getMessage(),
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
            public void onFailure(Call<Sala> call, Throwable t) {
                progressDialog.cancel();

                if (t instanceof IOException) {
                    Toasty.warning(SalaNewEditActivity.this, getString(R.string.error_conexion_red),
                            Toast.LENGTH_LONG, true).show();
                } else {
                    Toasty.error(SalaNewEditActivity.this, getString(R.string.error_conversion),
                            Toast.LENGTH_LONG, true).show();
                    Log.d(TAG, getString(R.string.error_conversion));
                }
            }
        });

    }

    private void editar(Sala s) {
        progressDialog.show();
        Call<Sala> call = MyApiAdapter.getApiService().editarSala(sala.getId(), s, Pref.getToken());
        call.enqueue(new Callback<Sala>() {
            @Override
            public void onResponse(Call<Sala> call, Response<Sala> response) {
                progressDialog.cancel();
                if(response.isSuccessful()) {
                    Toasty.success(SalaNewEditActivity.this, getString(R.string.editado_registro),
                            Toast.LENGTH_SHORT, true).show();
                    startActivity(new Intent(SalaNewEditActivity.this, SalasActivity.class));
                } else {
                    if (response.errorBody().contentType().subtype().equals("json")) {
                        ApiError apiError = ApiError.fromResponseBody(response.errorBody());
                        Toasty.error(SalaNewEditActivity.this, apiError.getMessage(),
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
            public void onFailure(Call<Sala> call, Throwable t) {
                progressDialog.cancel();

                if (t instanceof IOException) {
                    Toasty.warning(SalaNewEditActivity.this, getString(R.string.error_conexion_red),
                            Toast.LENGTH_LONG, true).show();
                } else {
                    Toasty.error(SalaNewEditActivity.this, getString(R.string.error_conversion),
                            Toast.LENGTH_LONG, true).show();
                    Log.d(TAG, getString(R.string.error_conversion));
                }
            }
        });
    }

    private void cargarCentros(final Spinner sp_centros, final Sala sala) {
        Call<ArrayList<Centro>> call = MyApiAdapter.getApiService().getCentros(Pref.getToken());
        call.enqueue(new Callback<ArrayList<Centro>>() {
            @Override
            public void onResponse(Call<ArrayList<Centro>> call, Response<ArrayList<Centro>> response) {
                if(response.isSuccessful()) {
                    ArrayList<Centro> centros = response.body();

                    if(centros != null) {
                        centros.add(0, new Centro(getString(R.string.seleccione_centro)));
                        ArrayAdapter<Centro> arrayAdapter = new ArrayAdapter<Centro>(SalaNewEditActivity.this,
                                android.R.layout.simple_spinner_dropdown_item, centros);

                        Log.d("CENTROS", "Tamaño ==> " + centros.size());
                        sp_centros.setAdapter(arrayAdapter);
                        if(editando) {
                            sp_centros.setSelection(centros.indexOf(sala.getCentro()));
                        }
                    }
                }  else {
                    if (response.errorBody().contentType().subtype().equals("json")) {
                        ApiError apiError = ApiError.fromResponseBody(response.errorBody());
                        Toasty.error(SalaNewEditActivity.this, apiError.getMessage(),
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
            public void onFailure(Call<ArrayList<Centro>> call, Throwable t) {
                progressDialog.cancel();

                if (t instanceof IOException) {
                    Toasty.warning(SalaNewEditActivity.this, getString(R.string.error_conexion_red),
                            Toast.LENGTH_LONG, true).show();
                } else {
                    Toasty.error(SalaNewEditActivity.this, getString(R.string.error_conversion),
                            Toast.LENGTH_LONG, true).show();
                    Log.d(TAG, getString(R.string.error_conversion));
                }
            }
        });

    }


}
