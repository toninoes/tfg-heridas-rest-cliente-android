package uca.ruiz.antonio.tfgapp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
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
import uca.ruiz.antonio.tfgapp.data.api.model.Cura;
import uca.ruiz.antonio.tfgapp.data.api.model.Proceso;
import uca.ruiz.antonio.tfgapp.ui.adapter.CuraAdapter;
import uca.ruiz.antonio.tfgapp.utils.FechaHoraUtils;


public class CurasActivity extends AppCompatActivity implements Callback<ArrayList<Cura>> {

    private FloatingActionButton fab_add_elemento;
    private TextView tv_listado_titulo;
    private RecyclerView rv_listado;
    private LinearLayoutManager mLayoutManager;
    private CuraAdapter mAdapter;

    private TextView tv_diagnostico, tv_diagnostico_tit;
    private TextView tv_fecha;
    private TextView tv_anamnesis, tv_anamnesis_tit;
    private TextView tv_observaciones, tv_observaciones_tit;

    private SwipeRefreshLayout srl_listado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_curas);

        fab_add_elemento = (FloatingActionButton) findViewById(R.id.fab_add_elemento);
        fab_add_elemento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        tv_listado_titulo = (TextView) findViewById(R.id.tv_listado_titulo);
        tv_listado_titulo.setText(R.string.curas);

        rv_listado = (RecyclerView) findViewById(R.id.rv_listado);
        rv_listado.setHasFixedSize(true); //la altura de los elmtos es la misma

        //El RecyclerView usará un LinearLayoutManager
        mLayoutManager = new LinearLayoutManager(this);
        rv_listado.setLayoutManager(mLayoutManager);

        //Asociamos un adapter. Define cómo se renderizará la informacion que tenemos
        mAdapter = new CuraAdapter();
        rv_listado.setAdapter(mAdapter);

        Proceso proceso = (Proceso) getIntent().getExtras().getSerializable("proceso");
        Call<ArrayList<Cura>> curasByProcesoId;

        tv_diagnostico_tit = (TextView) findViewById(R.id.tv_diagnostico_tit);
        tv_diagnostico = (TextView) findViewById(R.id.tv_diagnostico);
        tv_fecha = (TextView) findViewById(R.id.tv_fecha);
        tv_anamnesis = (TextView) findViewById(R.id.tv_anamnesis);
        tv_anamnesis_tit = (TextView) findViewById(R.id.tv_anamnesis_tit);
        tv_observaciones_tit = (TextView) findViewById(R.id.tv_observaciones_tit);
        tv_observaciones = (TextView) findViewById(R.id.tv_observaciones);

        if (proceso != null) {
            tv_diagnostico_tit.setText(getText(R.string.diagnostico));
            tv_diagnostico.setText(proceso.getDiagnostico());
            tv_fecha.setText(FechaHoraUtils.formatoFechaUI(proceso.getCreacion()));
            tv_anamnesis_tit.setText(getText(R.string.anamnesis));
            tv_anamnesis.setText(proceso.getAnamnesis());
            tv_observaciones_tit.setText(getText(R.string.observaciones));
            tv_observaciones.setText(proceso.getObservaciones());

            curasByProcesoId = MyApiAdapter.getApiService().getCurasByProcesoId(proceso.getId());
            curasByProcesoId.enqueue(this);
        }

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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_proceso, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_editar_proceso:
                //Intent intentComite = new Intent(MainActivity.this, ComiteActivity.class);
                //startActivity(intentComite);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResponse(Call<ArrayList<Cura>> call, Response<ArrayList<Cura>> response) {
        if(response.isSuccessful()) {
            ArrayList<Cura> curas = response.body();

            if(curas != null) {
                Log.d("CURAS", "Tamaño ==> " + curas.size());

                // Ordenar las curas según fecha en orden descendiente
                // Prefiero hacerlo en cliente para descargar al servidor
                Collections.sort(curas, new Comparator<Cura>() {
                    @Override
                    public int compare(Cura cura1, Cura cura2) {
                        return cura2.getCreacion().compareTo(cura1.getCreacion());
                    }
                });
            }

            mAdapter.setDataSet(curas);
        }
    }

    @Override
    public void onFailure(Call<ArrayList<Cura>> call, Throwable t) {

    }

}
