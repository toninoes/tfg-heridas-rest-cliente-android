package uca.ruiz.antonio.tfgapp.ui.activity.admin;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import uca.ruiz.antonio.tfgapp.data.api.model.Centro;
import uca.ruiz.antonio.tfgapp.data.api.model.Proceso;
import uca.ruiz.antonio.tfgapp.ui.adapter.CentroAdapter;
import uca.ruiz.antonio.tfgapp.ui.adapter.ProcesoAdapter;
import uca.ruiz.antonio.tfgapp.utils.Pref;

import static uca.ruiz.antonio.tfgapp.R.id.tv_listado_titulo;

public class CentrosActivity extends AppCompatActivity implements Callback<ArrayList<Centro>> {

    private FloatingActionButton fab_add_elemento;
    private RecyclerView rv_listado;
    private LinearLayoutManager mLayoutManager;
    private CentroAdapter mAdapter;
    private SwipeRefreshLayout srl_listado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_centros);

        fab_add_elemento = (FloatingActionButton) findViewById(R.id.fab_add_elemento);
        fab_add_elemento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //irProcesoNuevoActivity();
            }
        });

        rv_listado = (RecyclerView) findViewById(R.id.rv_listado);
        rv_listado.setHasFixedSize(true); // la altura de los elementos será la misma

        // El RecyclerView usará un LinearLayoutManager
        mLayoutManager = new LinearLayoutManager(this);
        rv_listado.setLayoutManager(mLayoutManager);

        // Asociamos un adapter. Define cómo se renderizará la información que tenemos
        mAdapter = new CentroAdapter(this);
        rv_listado.setAdapter(mAdapter);

        Call<ArrayList<Centro>> call = MyApiAdapter.getApiService().getCentros(Pref.getToken());
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
    public void onResponse(Call<ArrayList<Centro>> call, Response<ArrayList<Centro>> response) {
        if(response.isSuccessful()) {
            ArrayList<Centro> centros = response.body();
            if(centros != null) {
                Log.d("CENTROS", "Tamaño ==> " + centros.size());

            }
            mAdapter.setDataSet(centros);
        }
    }

    @Override
    public void onFailure(Call<ArrayList<Centro>> call, Throwable t) {
    }


}
