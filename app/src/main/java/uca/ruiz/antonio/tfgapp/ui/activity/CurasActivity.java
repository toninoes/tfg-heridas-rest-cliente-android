package uca.ruiz.antonio.tfgapp.ui.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uca.ruiz.antonio.tfgapp.R;
import uca.ruiz.antonio.tfgapp.io.MyApiAdapter;
import uca.ruiz.antonio.tfgapp.model.Cura;
import uca.ruiz.antonio.tfgapp.model.Proceso;
import uca.ruiz.antonio.tfgapp.ui.adapter.CuraAdapter;

public class CurasActivity extends AppCompatActivity implements Callback<ArrayList<Cura>> {

    private CuraAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_curas);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_add_elemento);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        TextView mTextView = (TextView) findViewById(R.id.tv_listado_titulo);
        mTextView.setText(R.string.curas);

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.rv_listado);
        mRecyclerView.setHasFixedSize(true); //la altura de los elmtos es la misma

        //El RecyclerView usará un LinearLayoutManager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        //Asociamos un adapter. Define cómo se renderizará la informacion que tenemos
        mAdapter = new CuraAdapter();
        mRecyclerView.setAdapter(mAdapter);

        //Long proceso_id = getIntent().getExtras().getLong("proceso_id");
        Proceso proceso = (Proceso) getIntent().getExtras().getSerializable("proceso");
        Call<ArrayList<Cura>> curasByProcesoId = MyApiAdapter.getApiService().getCurasByProcesoId(proceso.getId());
        curasByProcesoId.enqueue(this);

        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.srl_listado);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                recreate();
            }
        });
    }

    @Override
    public void onResponse(Call<ArrayList<Cura>> call, Response<ArrayList<Cura>> response) {
        if(response.isSuccessful()) {
            ArrayList<Cura> curas = response.body();
            if(curas != null)
                Log.d("CURAS", "Tamaño ==> " + curas.size());
            mAdapter.setDataSet(curas);
        }
    }

    @Override
    public void onFailure(Call<ArrayList<Cura>> call, Throwable t) {

    }

}
