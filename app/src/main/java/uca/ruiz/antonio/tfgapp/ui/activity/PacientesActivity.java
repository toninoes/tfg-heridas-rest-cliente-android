package uca.ruiz.antonio.tfgapp.ui.activity;

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
import android.widget.EditText;
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
import uca.ruiz.antonio.tfgapp.data.Preferencias;
import uca.ruiz.antonio.tfgapp.data.api.io.MyApiAdapter;
import uca.ruiz.antonio.tfgapp.data.api.mapping.ApiError;
import uca.ruiz.antonio.tfgapp.data.api.model.Paciente;
import uca.ruiz.antonio.tfgapp.data.api.model.Proceso;
import uca.ruiz.antonio.tfgapp.ui.activity.admin.MainAdminActivity;
import uca.ruiz.antonio.tfgapp.ui.adapter.PacienteAdapter;
import uca.ruiz.antonio.tfgapp.utils.Pref;


public class PacientesActivity extends AppCompatActivity {

    private static final String TAG = PacientesActivity.class.getSimpleName();
    private RecyclerView rv_listado;
    private LinearLayoutManager mLayoutManager;
    private PacienteAdapter mAdapter;
    private SwipeRefreshLayout srl_listado;
    private ProgressDialog progressDialog;
    private EditText et_buscar;
    private Button bt_buscar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_crud);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        et_buscar = (EditText) findViewById(R.id.et_buscar);
        bt_buscar = (Button) findViewById(R.id.bt_buscar);

        rv_listado = (RecyclerView) findViewById(R.id.rv_listado);
        rv_listado.setHasFixedSize(true); // la altura de los elementos será la misma

        // El RecyclerView usará un LinearLayoutManager
        mLayoutManager = new LinearLayoutManager(this);
        rv_listado.setLayoutManager(mLayoutManager);

        // añade línea divisoria entre cada elemento
        rv_listado.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        // Asociamos un adapter. Define cómo se renderizará la información que tenemos
        mAdapter = new PacienteAdapter(this);
        rv_listado.setAdapter(mAdapter);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.cargando));

        cargarPacientes();

        srl_listado = (SwipeRefreshLayout) findViewById(R.id.srl_listado);
        srl_listado.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                recreate();
            }
        });

        bt_buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cargarPacientes(et_buscar.getText().toString());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
                startActivity(new Intent(this, PacienteNewEditActivity.class));
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

    private void cargarPacientes() {
        progressDialog.show();
        Call<ArrayList<Paciente>> call = MyApiAdapter.getApiService().getPacientesRecientes(Pref.getToken());
        call.enqueue(new Callback<ArrayList<Paciente>>() {
            @Override
            public void onResponse(Call<ArrayList<Paciente>> call, Response<ArrayList<Paciente>> response) {
                progressDialog.cancel();
                if(response.isSuccessful()) {
                    ArrayList<Paciente> pacientes = response.body();
                    if(pacientes != null) {
                        Log.d("PACIENTES", "Tamaño ==> " + pacientes.size());

                        // mas recientes primero
                        Collections.sort(pacientes, new Comparator<Paciente>() {
                            @Override
                            public int compare(Paciente p1, Paciente p2) {
                                return p2.getId().compareTo(p1.getId());
                            }
                        });
                    }
                    mAdapter.setDataSet(pacientes);
                } else {
                    if (response.errorBody().contentType().subtype().equals("json")) {
                        ApiError apiError = ApiError.fromResponseBody(response.errorBody());
                        Toasty.error(PacientesActivity.this, apiError.getMessage(),
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
            public void onFailure(Call<ArrayList<Paciente>> call, Throwable t) {
                progressDialog.cancel();

                if (t instanceof IOException) {
                    Toasty.warning(PacientesActivity.this, getString(R.string.error_conexion_red),
                            Toast.LENGTH_LONG, true).show();
                } else {
                    Toasty.error(PacientesActivity.this, getString(R.string.error_conversion),
                            Toast.LENGTH_LONG, true).show();
                    Log.d(TAG, getString(R.string.error_conversion));
                }
            }
        });
    }

    private void cargarPacientes(String s) {
        progressDialog.show();
        Call<ArrayList<Paciente>> call = MyApiAdapter.getApiService().getPacientesByFiltro(s, Pref.getToken());
        call.enqueue(new Callback<ArrayList<Paciente>>() {
            @Override
            public void onResponse(Call<ArrayList<Paciente>> call, Response<ArrayList<Paciente>> response) {
                progressDialog.cancel();
                if(response.isSuccessful()) {
                    ArrayList<Paciente> pacientes = response.body();
                    if(pacientes != null) {
                        Log.d("PACIENTES", "Tamaño ==> " + pacientes.size());

                        // mas recientes primero
                        Collections.sort(pacientes, new Comparator<Paciente>() {
                            @Override
                            public int compare(Paciente p1, Paciente p2) {
                                return p2.getId().compareTo(p1.getId());
                            }
                        });
                    }
                    mAdapter.setDataSet(pacientes);
                } else {
                    if (response.errorBody().contentType().subtype().equals("json")) {
                        ApiError apiError = ApiError.fromResponseBody(response.errorBody());
                        Toasty.error(PacientesActivity.this, apiError.getMessage(),
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
            public void onFailure(Call<ArrayList<Paciente>> call, Throwable t) {
                progressDialog.cancel();

                if (t instanceof IOException) {
                    Toasty.warning(PacientesActivity.this, getString(R.string.error_conexion_red),
                            Toast.LENGTH_LONG, true).show();
                } else {
                    Toasty.error(PacientesActivity.this, getString(R.string.error_conversion),
                            Toast.LENGTH_LONG, true).show();
                    Log.d(TAG, getString(R.string.error_conversion));
                }
            }
        });
    }

}
