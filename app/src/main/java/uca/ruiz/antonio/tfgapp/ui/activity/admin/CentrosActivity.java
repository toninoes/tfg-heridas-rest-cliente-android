package uca.ruiz.antonio.tfgapp.ui.activity.admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uca.ruiz.antonio.tfgapp.R;
import uca.ruiz.antonio.tfgapp.data.api.io.MyApiAdapter;
import uca.ruiz.antonio.tfgapp.data.api.model.Centro;
import uca.ruiz.antonio.tfgapp.data.api.model.Proceso;
import uca.ruiz.antonio.tfgapp.ui.activity.ProcesoEditarActivity;
import uca.ruiz.antonio.tfgapp.ui.adapter.CentroAdapter;
import uca.ruiz.antonio.tfgapp.utils.Pref;

import static uca.ruiz.antonio.tfgapp.R.string.proceso;

public class CentrosActivity extends AppCompatActivity {

    private RecyclerView rv_listado;
    private LinearLayoutManager mLayoutManager;
    private CentroAdapter mAdapter;
    private SwipeRefreshLayout srl_listado;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_crud);

        rv_listado = (RecyclerView) findViewById(R.id.rv_listado);
        rv_listado.setHasFixedSize(true); // la altura de los elementos será la misma

        // El RecyclerView usará un LinearLayoutManager
        mLayoutManager = new LinearLayoutManager(this);
        rv_listado.setLayoutManager(mLayoutManager);

        // añade línea divisoria entre cada elemento
        rv_listado.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        // Asociamos un adapter. Define cómo se renderizará la información que tenemos
        mAdapter = new CentroAdapter(this);
        rv_listado.setAdapter(mAdapter);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.cargando));

        cargarCentros();

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
        getMenuInflater().inflate(R.menu.menu_add_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.add_item:
                startActivity(new Intent(this, CentroNewEditActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void cargarCentros() {
        progressDialog.show();
        Call<ArrayList<Centro>> call = MyApiAdapter.getApiService().getCentros(Pref.getToken());
        call.enqueue(new Callback<ArrayList<Centro>>() {
            @Override
            public void onResponse(Call<ArrayList<Centro>> call, Response<ArrayList<Centro>> response) {
                if(response.isSuccessful()) {
                    progressDialog.cancel();
                    ArrayList<Centro> centros = response.body();
                    if(centros != null) {
                        Log.d("CENTROS", "Tamaño ==> " + centros.size());

                    }
                    mAdapter.setDataSet(centros);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Centro>> call, Throwable t) {
                progressDialog.cancel();
                Toast.makeText(CentrosActivity.this, "error :(", Toast.LENGTH_SHORT).show();
            }
        });

    }

}
