package uca.ruiz.antonio.tfgapp.ui.activity.admin;

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
import uca.ruiz.antonio.tfgapp.data.api.io.MyApiAdapter;
import uca.ruiz.antonio.tfgapp.data.api.mapping.ApiError;
import uca.ruiz.antonio.tfgapp.data.api.model.Administrador;
import uca.ruiz.antonio.tfgapp.data.api.model.Sanitario;
import uca.ruiz.antonio.tfgapp.ui.activity.CuraNewEditActivity;
import uca.ruiz.antonio.tfgapp.ui.adapter.admin.SanitarioAdapter;
import uca.ruiz.antonio.tfgapp.utils.Pref;

public class SanitariosActivity extends AppCompatActivity {

    private static final String TAG = SanitariosActivity.class.getSimpleName();
    private RecyclerView rv_listado;
    private LinearLayoutManager mLayoutManager;
    private SanitarioAdapter mAdapter;
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
        mAdapter = new SanitarioAdapter(this);
        rv_listado.setAdapter(mAdapter);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.cargando));

        cargarSanitarios();

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
                cargarSanitarios(et_buscar.getText().toString());
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
                startActivity(new Intent(this, MainAdminActivity.class));
                return true;
            case R.id.add_item:
                startActivity(new Intent(this, SanitarioNewEditActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainAdminActivity.class));
    }

    private void cargarSanitarios() {
        progressDialog.show();
        Call<ArrayList<Sanitario>> call = MyApiAdapter.getApiService().getSanitariosRecientes(Pref.getToken());
        call.enqueue(new Callback<ArrayList<Sanitario>>() {
            @Override
            public void onResponse(Call<ArrayList<Sanitario>> call, Response<ArrayList<Sanitario>> response) {
                progressDialog.cancel();
                if(response.isSuccessful()) {
                    ArrayList<Sanitario> sanitarios = response.body();
                    if(sanitarios != null) {
                        Log.d("SANITARIOS", "Tamaño ==> " + sanitarios.size());

                        // mas recientes primero
                        Collections.sort(sanitarios, new Comparator<Sanitario>() {
                            @Override
                            public int compare(Sanitario s1, Sanitario s2) {
                                return s2.getId().compareTo(s1.getId());
                            }
                        });
                    }
                    mAdapter.setDataSet(sanitarios);
                } else {
                    if (response.errorBody().contentType().subtype().equals("json")) {
                        ApiError apiError = ApiError.fromResponseBody(response.errorBody());
                        Toasty.error(SanitariosActivity.this, apiError.getMessage(),
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
            public void onFailure(Call<ArrayList<Sanitario>> call, Throwable t) {
                progressDialog.cancel();

                if (t instanceof IOException) {
                    Toasty.warning(SanitariosActivity.this, getString(R.string.error_conexion_red),
                            Toast.LENGTH_LONG, true).show();
                } else {
                    Toasty.error(SanitariosActivity.this, getString(R.string.error_conversion),
                            Toast.LENGTH_LONG, true).show();
                    Log.d(TAG, getString(R.string.error_conversion));
                }
            }
        });
    }

    private void cargarSanitarios(String s) {
        progressDialog.show();
        Call<ArrayList<Sanitario>> call = MyApiAdapter.getApiService().getSanitariosByFiltro(s, Pref.getToken());
        call.enqueue(new Callback<ArrayList<Sanitario>>() {
            @Override
            public void onResponse(Call<ArrayList<Sanitario>> call, Response<ArrayList<Sanitario>> response) {
                if(response.isSuccessful()) {
                    progressDialog.cancel();
                    ArrayList<Sanitario> sanitarios = response.body();
                    if(sanitarios != null) {
                        Log.d("SANITARIOS", "Tamaño ==> " + sanitarios.size());

                        // mas recientes primero
                        Collections.sort(sanitarios, new Comparator<Sanitario>() {
                            @Override
                            public int compare(Sanitario s1, Sanitario s2) {
                                return s2.getId().compareTo(s1.getId());
                            }
                        });
                    }
                    mAdapter.setDataSet(sanitarios);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Sanitario>> call, Throwable t) {
                progressDialog.cancel();

                if (t instanceof IOException) {
                    Toasty.warning(SanitariosActivity.this, getString(R.string.error_conexion_red),
                            Toast.LENGTH_LONG, true).show();
                } else {
                    Toasty.error(SanitariosActivity.this, getString(R.string.error_conversion),
                            Toast.LENGTH_LONG, true).show();
                    Log.d(TAG, getString(R.string.error_conversion));
                }
            }
        });
    }

}
