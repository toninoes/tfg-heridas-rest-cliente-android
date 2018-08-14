package uca.ruiz.antonio.tfgapp.ui.activity.admin;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uca.ruiz.antonio.tfgapp.R;
import uca.ruiz.antonio.tfgapp.data.Preferencias;
import uca.ruiz.antonio.tfgapp.data.api.io.MyApiAdapter;
import uca.ruiz.antonio.tfgapp.data.api.mapping.ApiError;
import uca.ruiz.antonio.tfgapp.data.api.mapping.DosFechas;
import uca.ruiz.antonio.tfgapp.data.api.model.Cita;
import uca.ruiz.antonio.tfgapp.data.api.model.Paciente;
import uca.ruiz.antonio.tfgapp.data.api.model.ValoracionesResults;
import uca.ruiz.antonio.tfgapp.ui.activity.CitacionesNewActivity;
import uca.ruiz.antonio.tfgapp.ui.activity.CuraNewEditActivity;
import uca.ruiz.antonio.tfgapp.ui.activity.MainSanitarioActivity;
import uca.ruiz.antonio.tfgapp.ui.adapter.admin.ValoracionesResultsAdapter;
import uca.ruiz.antonio.tfgapp.utils.FechaHoraUtils;
import uca.ruiz.antonio.tfgapp.utils.Pref;
import uca.ruiz.antonio.tfgapp.utils.Validacion;

import static uca.ruiz.antonio.tfgapp.R.id.et_buscar;
import static uca.ruiz.antonio.tfgapp.R.id.et_fecha;

public class ValoracionesResultsActivity extends AppCompatActivity {

    private static final String TAG = ValoracionesResultsActivity.class.getSimpleName();
    private RecyclerView rv_listado;
    private LinearLayoutManager mLayoutManager;
    private ValoracionesResultsAdapter mAdapter;
    private ProgressDialog progressDialog;
    private Button bt_buscar;
    private EditText et_fecha1, et_fecha2;
    private DatePickerDialog dpd_fecha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informe_valoraciones_media);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        et_fecha1 = (EditText) findViewById(R.id.et_fecha1);
        et_fecha2 = (EditText) findViewById(R.id.et_fecha2);
        bt_buscar = (Button) findViewById(R.id.bt_buscar);

        rv_listado = (RecyclerView) findViewById(R.id.rv_listado);
        rv_listado.setHasFixedSize(true); // la altura de los elementos será la misma

        // El RecyclerView usará un LinearLayoutManager
        mLayoutManager = new LinearLayoutManager(this);
        rv_listado.setLayoutManager(mLayoutManager);

        // añade línea divisoria entre cada elemento
        rv_listado.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        // Asociamos un adapter. Define cómo se renderizará la información que tenemos
        mAdapter = new ValoracionesResultsAdapter(this);
        rv_listado.setAdapter(mAdapter);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.cargando));

        et_fecha1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal;
                int day, month, year;
                cal = Calendar.getInstance();

                day = cal.get(Calendar.DAY_OF_MONTH);
                month = cal.get(Calendar.MONTH);
                year = cal.get(Calendar.YEAR);

                // date picker dialog
                dpd_fecha = new DatePickerDialog(ValoracionesResultsActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int day) {
                                // +1 porque enero es cero
                                final String fechaElegida = FechaHoraUtils.dosDigitos(day) + "/" +
                                        FechaHoraUtils.dosDigitos(month+1) + "/" + year;
                                et_fecha1.setText(fechaElegida);
                            }
                        }, year, month, day);

                dpd_fecha.show();
            }
        });

        et_fecha2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal;
                int day, month, year;
                cal = Calendar.getInstance();

                day = cal.get(Calendar.DAY_OF_MONTH);
                month = cal.get(Calendar.MONTH);
                year = cal.get(Calendar.YEAR);

                // date picker dialog
                dpd_fecha = new DatePickerDialog(ValoracionesResultsActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int day) {
                                // +1 porque enero es cero
                                final String fechaElegida = FechaHoraUtils.dosDigitos(day) + "/" +
                                        FechaHoraUtils.dosDigitos(month+1) + "/" + year;
                                et_fecha2.setText(fechaElegida);
                            }
                        }, year, month, day);

                dpd_fecha.show();
            }
        });

        bt_buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validarCampos();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_valoraciones, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                volverAtras();
                return true;
            case R.id.todas_valoraciones:
                startActivity(new Intent(this, ValoracionesActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void volverAtras() {
        if(Preferencias.get(this).getBoolean("ROLE_ADMIN", false))
            startActivity(new Intent(this, MainAdminActivity.class));
        else if(Preferencias.get(this).getBoolean("ROLE_SANITARIO", false))
            startActivity(new Intent(this, MainSanitarioActivity.class));
    }

    @Override
    public void onBackPressed() {
        volverAtras();
    }

    private void validarCampos() {
        // Resetear errores
        et_fecha1.setError(null);
        et_fecha2.setError(null);

        //tomo el contenido de los campos
        String fecha1 = et_fecha1.getText().toString().trim();
        String fecha2 = et_fecha2.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        // Valida campo Fecha2
        if(Validacion.vacio(fecha2)) {
            et_fecha2.setError(getString(R.string.campo_no_vacio));
            focusView = et_fecha2;
            cancel = true;
        }

        // Valida campo Fecha1
        if(Validacion.vacio(fecha1)) {
            et_fecha1.setError(getString(R.string.campo_no_vacio));
            focusView = et_fecha1;
            cancel = true;
        }

        if (cancel) {
            // Se ha producido un error: no se intenta el registro y se focaliza en el
            // primer campo del formulario con error.
            focusView.requestFocus();
        } else {
            DosFechas df = new DosFechas(FechaHoraUtils.getFechaFromString(fecha1),
                    FechaHoraUtils.getFechaFromString(fecha2));
            cargarValoracionesMediasPeriodicas(df);
        }
    }

    private void cargarValoracionesMediasPeriodicas(DosFechas df) {
        progressDialog.show();
        Call<ArrayList<ValoracionesResults>> call = MyApiAdapter.getApiService().
                getAVGValoracionesPeriodico(df, Pref.getToken());
        call.enqueue(new Callback<ArrayList<ValoracionesResults>>() {
            @Override
            public void onResponse(Call<ArrayList<ValoracionesResults>> call,
                                   Response<ArrayList<ValoracionesResults>> response) {
                progressDialog.cancel();
                if(response.isSuccessful()) {
                    ArrayList<ValoracionesResults> vr = response.body();
                    if(vr != null) {
                        Log.d("VALORACIONES MEDIA", "Tamaño ==> " + vr.size());
                    }
                    mAdapter.setDataSet(vr);
                } else {
                    if (response.errorBody().contentType().subtype().equals("json")) {
                        ApiError apiError = ApiError.fromResponseBody(response.errorBody());
                        Toasty.error(ValoracionesResultsActivity.this, apiError.getMessage(),
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
            public void onFailure(Call<ArrayList<ValoracionesResults>> call, Throwable t) {
                progressDialog.cancel();

                if (t instanceof IOException) {
                    Toasty.warning(ValoracionesResultsActivity.this, getString(R.string.error_conexion_red),
                            Toast.LENGTH_LONG, true).show();
                } else {
                    Toasty.error(ValoracionesResultsActivity.this, getString(R.string.error_conversion),
                            Toast.LENGTH_LONG, true).show();
                    Log.d(TAG, getString(R.string.error_conversion));
                }
            }
        });
    }


/*
    private void cargarValoracionesResults() {
        progressDialog.show();
        Call<ArrayList<ValoracionesResults>> call = MyApiAdapter.getApiService().
                getAVGValoraciones(Pref.getToken());
        call.enqueue(new Callback<ArrayList<ValoracionesResults>>() {
            @Override
            public void onResponse(Call<ArrayList<ValoracionesResults>> call,
                                   Response<ArrayList<ValoracionesResults>> response) {
                progressDialog.cancel();
                if(response.isSuccessful()) {
                    ArrayList<ValoracionesResults> vr = response.body();
                    if(vr != null) {
                        Log.d("VALORACIONES MEDIA", "Tamaño ==> " + vr.size());
                    }
                    mAdapter.setDataSet(vr);
                } else {
                    if (response.errorBody().contentType().subtype().equals("json")) {
                        ApiError apiError = ApiError.fromResponseBody(response.errorBody());
                        Toasty.error(ValoracionesResultsActivity.this, apiError.getMessage(),
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
            public void onFailure(Call<ArrayList<ValoracionesResults>> call, Throwable t) {
                progressDialog.cancel();

                if (t instanceof IOException) {
                    Toasty.warning(ValoracionesResultsActivity.this, getString(R.string.error_conexion_red),
                            Toast.LENGTH_LONG, true).show();
                } else {
                    Toasty.error(ValoracionesResultsActivity.this, getString(R.string.error_conversion),
                            Toast.LENGTH_LONG, true).show();
                    Log.d(TAG, getString(R.string.error_conversion));
                }
            }
        });
    }

    private void cargarValoracionesResults(String s) {
        progressDialog.show();
        Call<ArrayList<ValoracionesResults>> call = MyApiAdapter.getApiService().
                getValoracionesResultsByFiltro(s, Pref.getToken());
        call.enqueue(new Callback<ArrayList<ValoracionesResults>>() {
            @Override
            public void onResponse(Call<ArrayList<ValoracionesResults>> call,
                                   Response<ArrayList<ValoracionesResults>> response) {
                progressDialog.cancel();
                if(response.isSuccessful()) {
                    ArrayList<ValoracionesResults> vr = response.body();
                    if(vr != null) {
                        Log.d("VALORACIONES MEDIA", "Tamaño ==> " + vr.size());
                    }
                    mAdapter.setDataSet(vr);
                } else {
                    if (response.errorBody().contentType().subtype().equals("json")) {
                        ApiError apiError = ApiError.fromResponseBody(response.errorBody());
                        Toasty.error(ValoracionesResultsActivity.this, apiError.getMessage(),
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
            public void onFailure(Call<ArrayList<ValoracionesResults>> call, Throwable t) {
                progressDialog.cancel();

                if (t instanceof IOException) {
                    Toasty.warning(ValoracionesResultsActivity.this, getString(R.string.error_conexion_red),
                            Toast.LENGTH_LONG, true).show();
                } else {
                    Toasty.error(ValoracionesResultsActivity.this, getString(R.string.error_conversion),
                            Toast.LENGTH_LONG, true).show();
                    Log.d(TAG, getString(R.string.error_conversion));
                }
            }
        });
    }
*/
}
