package uca.ruiz.antonio.tfgapp.ui.activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uca.ruiz.antonio.tfgapp.R;
import uca.ruiz.antonio.tfgapp.data.Preferencias;
import uca.ruiz.antonio.tfgapp.data.api.io.MyApiAdapter;
import uca.ruiz.antonio.tfgapp.data.api.mapping.ApiError;
import uca.ruiz.antonio.tfgapp.data.api.model.Cita;
import uca.ruiz.antonio.tfgapp.data.api.model.Sala;
import uca.ruiz.antonio.tfgapp.ui.adapter.CitaAdapter;
import uca.ruiz.antonio.tfgapp.utils.FechaHoraUtils;
import uca.ruiz.antonio.tfgapp.utils.Pref;
import uca.ruiz.antonio.tfgapp.utils.Validacion;

public class CitacionesActivity extends AppCompatActivity {

    private static final String TAG = CitacionesActivity.class.getSimpleName();
    private RecyclerView rv_listado;
    private LinearLayoutManager mLayoutManager;
    private CitaAdapter mAdapter;
    private ProgressDialog progressDialog;
    private EditText et_fecha;
    private DatePickerDialog dpd_fecha;
    private Spinner sp_salas;
    private TextView sp_salas_text;
    private Button bt_buscar;
    private Sala sala;
    private LinearLayout ll_busqueda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_citaciones);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        rv_listado = (RecyclerView) findViewById(R.id.rv_listado);
        rv_listado.setHasFixedSize(true); // la altura de los elementos será la misma

        // El RecyclerView usará un LinearLayoutManager
        mLayoutManager = new LinearLayoutManager(this);
        rv_listado.setLayoutManager(mLayoutManager);

        // añade línea divisoria entre cada elemento
        rv_listado.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        // Asociamos un adapter. Define cómo se renderizará la información que tenemos
        mAdapter = new CitaAdapter(this);
        rv_listado.setAdapter(mAdapter);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.cargando));

        if(Preferencias.get(this).getBoolean("ROLE_PACIENTE", false)) {
            ll_busqueda = (LinearLayout) findViewById(R.id.ll_busqueda);
            ll_busqueda.setVisibility(View.GONE);
            cargarCitacionesByPaciente();
        } else if(Preferencias.get(this).getBoolean("ROLE_SANITARIO", false)) {
            et_fecha = (EditText) findViewById(R.id.et_fecha);
            sp_salas = (Spinner) findViewById(R.id.sp_salas);
            sp_salas_text = (TextView) findViewById(R.id.sp_salas_error);

            bt_buscar = (Button) findViewById(R.id.bt_buscar);

            cargarSalas(sp_salas);

            et_fecha.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar cal;
                    int day, month, year;
                    cal = Calendar.getInstance();

                    day = cal.get(Calendar.DAY_OF_MONTH);
                    month = cal.get(Calendar.MONTH);
                    year = cal.get(Calendar.YEAR);

                    // date picker dialog
                    dpd_fecha = new DatePickerDialog(CitacionesActivity.this,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int month, int day) {
                                    // +1 porque enero es cero
                                    final String fechaElegida = FechaHoraUtils.dosDigitos(day) + "/" +
                                            FechaHoraUtils.dosDigitos(month+1) + "/" + year;
                                    et_fecha.setText(fechaElegida);
                                }
                            }, year, month, day);

                    dpd_fecha.show();
                }
            });

            sp_salas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    sala = (Sala) adapterView.getAdapter().getItem(i);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            bt_buscar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    intentarCargarCitaciones();
                }
            });
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(Preferencias.get(this).getBoolean("ROLE_PACIENTE", false))
            getMenuInflater().inflate(R.menu.menu_nuevo_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                volverAtras();
                return true;
            case R.id.add_item:
                //startActivity(new Intent(this, CitaNewEditActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void volverAtras() {
        if(Preferencias.get(this).getBoolean("ROLE_PACIENTE", false))
            startActivity(new Intent(this, MainPacienteActivity.class));
        else if(Preferencias.get(this).getBoolean("ROLE_SANITARIO", false))
            startActivity(new Intent(this, MainSanitarioActivity.class));
    }

    @Override
    public void onBackPressed() {
        volverAtras();
    }

    private void intentarCargarCitaciones() {
        // Resetear errores
        et_fecha.setError(null);
        sp_salas_text.setError(null);

        //tomo el contenido de los campos
        String fecha = et_fecha.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        // Validar listado de Salas
        if(sala.getId() == 0 || sala == null) {
            sp_salas_text.setError(getString(R.string.debes_seleccionar_sala));
            focusView = sp_salas_text;
            cancel = true;
        }

        // Valida campo Fecha
        if(Validacion.vacio(fecha)) {
            et_fecha.setError(getString(R.string.campo_no_vacio));
            focusView = et_fecha;
            cancel = true;
        }

        if (cancel) {
            // Se ha producido un error: no se intenta el registro y se focaliza en el
            // primer campo del formulario con error.
            focusView.requestFocus();
        } else {
            Cita c = new Cita(sala, FechaHoraUtils.getFechaFromString(fecha));
            cargarCitacionesBySanitario(c);
        }
    }

    private void cargarCitacionesByPaciente() {
        progressDialog.show();
        Call<ArrayList<Cita>> call = MyApiAdapter.getApiService().getCitasByPacienteId(Pref.getUserId(),
                Pref.getToken());
        call.enqueue(new Callback<ArrayList<Cita>>() {
            @Override
            public void onResponse(Call<ArrayList<Cita>> call, Response<ArrayList<Cita>> response) {
                progressDialog.cancel();
                if(response.isSuccessful()) {
                    ArrayList<Cita> citas = response.body();

                    if(citas != null)
                        Log.d("CITAS", "Tamaño ==> " + citas.size());

                    Toasty.success(CitacionesActivity.this, getString(R.string.numero_citas) + ": " +
                            citas.size(), Toast.LENGTH_SHORT, true).show();
                    mAdapter.setDataSet(citas);
                } else {
                    if (response.errorBody().contentType().subtype().equals("json")) {
                        ApiError apiError = ApiError.fromResponseBody(response.errorBody());
                        Toasty.error(CitacionesActivity.this, apiError.getMessage(),
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
            public void onFailure(Call<ArrayList<Cita>> call, Throwable t) {
                progressDialog.cancel();

                if (t instanceof IOException) {
                    Toasty.warning(CitacionesActivity.this, getString(R.string.error_conexion_red),
                            Toast.LENGTH_LONG, true).show();
                } else {
                    Toasty.error(CitacionesActivity.this, getString(R.string.error_conversion),
                            Toast.LENGTH_LONG, true).show();
                    Log.d(TAG, getString(R.string.error_conversion));
                }
            }
        });
    }

    private void cargarCitacionesBySanitario(Cita c) {
        progressDialog.show();
        Call<ArrayList<Cita>> call = MyApiAdapter.getApiService().getCitasBySanitarioId(Pref.getUserId(),
                c, Pref.getToken());
        call.enqueue(new Callback<ArrayList<Cita>>() {
            @Override
            public void onResponse(Call<ArrayList<Cita>> call, Response<ArrayList<Cita>> response) {
                progressDialog.cancel();
                if(response.isSuccessful()) {
                    ArrayList<Cita> citas = response.body();

                    if(citas != null)
                        Log.d("CITAS", "Tamaño ==> " + citas.size());

                    Toasty.success(CitacionesActivity.this, getString(R.string.numero_citas) + ": " +
                            citas.size(), Toast.LENGTH_SHORT, true).show();
                    mAdapter.setDataSet(citas);
                } else {
                    if (response.errorBody().contentType().subtype().equals("json")) {
                        ApiError apiError = ApiError.fromResponseBody(response.errorBody());
                        Toasty.error(CitacionesActivity.this, apiError.getMessage(),
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
            public void onFailure(Call<ArrayList<Cita>> call, Throwable t) {
                progressDialog.cancel();

                if (t instanceof IOException) {
                    Toasty.warning(CitacionesActivity.this, getString(R.string.error_conexion_red),
                            Toast.LENGTH_LONG, true).show();
                } else {
                    Toasty.error(CitacionesActivity.this, getString(R.string.error_conversion),
                            Toast.LENGTH_LONG, true).show();
                    Log.d(TAG, getString(R.string.error_conversion));
                }
            }
        });
    }

    private void cargarSalas(final Spinner sp_salas) {
        progressDialog.show();
        Call<ArrayList<Sala>> call = MyApiAdapter.getApiService().getSalasByUserId(Pref.getUserId(),
                Pref.getToken());
        call.enqueue(new Callback<ArrayList<Sala>>() {
            @Override
            public void onResponse(Call<ArrayList<Sala>> call, Response<ArrayList<Sala>> response) {
                progressDialog.cancel();
                if(response.isSuccessful()) {
                    ArrayList<Sala> salas = response.body();
                    if(salas != null) {
                        salas.add(0, new Sala(getString(R.string.seleccione_sala)));
                        ArrayAdapter<Sala> arrayAdapter = new ArrayAdapter<Sala>(CitacionesActivity.this,
                                android.R.layout.simple_spinner_dropdown_item, salas);
                        Log.d("SALAS", "Tamaño ==> " + salas.size());
                        sp_salas.setAdapter(arrayAdapter);
                    }

                } else {
                    if (response.errorBody().contentType().subtype().equals("json")) {
                        ApiError apiError = ApiError.fromResponseBody(response.errorBody());
                        Toasty.error(CitacionesActivity.this, apiError.getMessage(),
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
            public void onFailure(Call<ArrayList<Sala>> call, Throwable t) {
                progressDialog.cancel();

                if (t instanceof IOException) {
                    Toasty.warning(CitacionesActivity.this, getString(R.string.error_conexion_red),
                            Toast.LENGTH_LONG, true).show();
                } else {
                    Toasty.error(CitacionesActivity.this, getString(R.string.error_conversion),
                            Toast.LENGTH_LONG, true).show();
                    Log.d(TAG, getString(R.string.error_conversion));
                }
            }
        });
    }

}
