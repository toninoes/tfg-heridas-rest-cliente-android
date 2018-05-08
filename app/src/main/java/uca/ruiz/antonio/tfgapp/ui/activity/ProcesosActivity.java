package uca.ruiz.antonio.tfgapp.ui.activity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import uca.ruiz.antonio.tfgapp.model.Proceso;
import uca.ruiz.antonio.tfgapp.ui.adapter.ProcesoAdapter;

public class ProcesosActivity extends AppCompatActivity implements Callback<ArrayList<Proceso>> {

    private ProcesoAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_procesos);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_add_elemento);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isOnline()) {
                    Snackbar.make(view, "Se presionó el FAB", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    Snackbar.make(view, "No hay conexión", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });

        TextView mTextView = (TextView) findViewById(R.id.tv_listado_titulo);
        mTextView.setText(R.string.procesos);

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.rv_listado);
        mRecyclerView.setHasFixedSize(true); // la altura de los elementos será la misma

        // El RecyclerView usará un LinearLayoutManager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // Asociamos un adapter. Define cómo se renderizará la información que tenemos
        mAdapter = new ProcesoAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        Call<ArrayList<Proceso>> call = MyApiAdapter.getApiService().getProcesos();
        call.enqueue(this);

        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.srl_listado);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                recreate();
            }
        });
    }

    @Override
    public void onResponse(Call<ArrayList<Proceso>> call, Response<ArrayList<Proceso>> response) {
        if(response.isSuccessful()) {
            ArrayList<Proceso> procesos = response.body();
            if(procesos != null)
                Log.d("PROCESOS", "Tamaño ==> " + procesos.size());
            mAdapter.setDataSet(procesos);
        }
    }

    @Override
    public void onFailure(Call<ArrayList<Proceso>> call, Throwable t) {

    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnected();
    }

}