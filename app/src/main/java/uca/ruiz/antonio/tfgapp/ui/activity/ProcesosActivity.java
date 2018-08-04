package uca.ruiz.antonio.tfgapp.ui.activity;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uca.ruiz.antonio.tfgapp.R;
import uca.ruiz.antonio.tfgapp.data.api.io.MyApiAdapter;
import uca.ruiz.antonio.tfgapp.data.api.model.Paciente;
import uca.ruiz.antonio.tfgapp.data.api.model.Proceso;
import uca.ruiz.antonio.tfgapp.ui.adapter.ProcesoAdapter;
import uca.ruiz.antonio.tfgapp.utils.FechaHoraUtils;
import uca.ruiz.antonio.tfgapp.utils.Pref;

public class ProcesosActivity extends AppCompatActivity implements Callback<ArrayList<Proceso>> {

    private FloatingActionButton fab_add_elemento;
    private TextView tv_listado_titulo;
    private RecyclerView rv_listado;
    private LinearLayoutManager mLayoutManager;
    private ProcesoAdapter mAdapter;
    private SwipeRefreshLayout srl_listado;

    private Paciente paciente;
    private TextView tv_nombre_completo, tv_dni, tv_historia, tv_edad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_procesos);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        paciente = (Paciente) getIntent().getExtras().getSerializable("paciente");
        tv_nombre_completo = (TextView) findViewById(R.id.tv_nombre_completo);
        tv_dni = (TextView) findViewById(R.id.tv_dni);
        tv_historia = (TextView) findViewById(R.id.tv_historia);
        tv_edad = (TextView) findViewById(R.id.tv_edad);

        tv_nombre_completo.setText(paciente.getFullName());
        tv_dni.setText(getString(R.string.dni).toUpperCase() + ": " + paciente.getDni());
        tv_historia.setText(getString(R.string.nhc) + ": " +
                paciente.getHistoria().toString());
        tv_edad.setText(getString(R.string.edad).toUpperCase() + ": " +
                FechaHoraUtils.getEdad(paciente.getNacimiento()).toString());


        fab_add_elemento = (FloatingActionButton) findViewById(R.id.fab_add_elemento);
        fab_add_elemento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irProcesoNuevoActivity();
            }
        });

        tv_listado_titulo = (TextView) findViewById(R.id.tv_listado_titulo);
        tv_listado_titulo.setText(R.string.procesos);

        rv_listado = (RecyclerView) findViewById(R.id.rv_listado);
        rv_listado.setHasFixedSize(true); // la altura de los elementos será la misma

        // El RecyclerView usará un LinearLayoutManager
        mLayoutManager = new LinearLayoutManager(this);
        rv_listado.setLayoutManager(mLayoutManager);

        // Asociamos un adapter. Define cómo se renderizará la información que tenemos
        mAdapter = new ProcesoAdapter(this);
        rv_listado.setAdapter(mAdapter);

        Call<ArrayList<Proceso>> call = MyApiAdapter.getApiService().getProcesosByPacienteId(paciente.getId(),
                Pref.getToken());
        call.enqueue(this);

        srl_listado = (SwipeRefreshLayout) findViewById(R.id.srl_listado);
        srl_listado.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                recreate();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu_proceso, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                startActivity(new Intent(this, PacientesActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, PacientesActivity.class));
    }

    @Override
    public void onResponse(Call<ArrayList<Proceso>> call, Response<ArrayList<Proceso>> response) {
        if(response.isSuccessful()) {
            ArrayList<Proceso> procesos = response.body();
            if(procesos != null) {
                Log.d("PROCESOS", "Tamaño ==> " + procesos.size());

                // Ordenar los procesos según fecha en orden descendiente
                // Prefiero hacerlo en cliente para descargar al servidor
                Collections.sort(procesos, new Comparator<Proceso>() {
                    @Override
                    public int compare(Proceso p1, Proceso p2) {
                        return p2.getCreacion().compareTo(p1.getCreacion());
                    }
                });
            }
            mAdapter.setDataSet(procesos);
        }
    }

    @Override
    public void onFailure(Call<ArrayList<Proceso>> call, Throwable t) {
    }


    private void irProcesoNuevoActivity() {
        Intent intent = new Intent(this, ProcesoNewEditActivity.class);
        intent.putExtra("paciente", paciente);
        startActivity(intent);
    }


}