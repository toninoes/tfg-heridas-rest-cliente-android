package uca.ruiz.antonio.tfgapp.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
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
import uca.ruiz.antonio.tfgapp.data.api.model.Cura;
import uca.ruiz.antonio.tfgapp.data.api.model.Paciente;
import uca.ruiz.antonio.tfgapp.data.api.model.Proceso;
import uca.ruiz.antonio.tfgapp.ui.adapter.CuraAdapter;
import uca.ruiz.antonio.tfgapp.utils.FechaHoraUtils;
import uca.ruiz.antonio.tfgapp.utils.Pref;


public class CurasActivity extends AppCompatActivity {

    private static final String TAG = CurasActivity.class.getSimpleName();
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
    private ProgressDialog progressDialog;

    private Proceso proceso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_curas);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        tv_listado_titulo = (TextView) findViewById(R.id.tv_listado_titulo);
        tv_diagnostico_tit = (TextView) findViewById(R.id.tv_diagnostico_tit);
        tv_diagnostico = (TextView) findViewById(R.id.tv_diagnostico);
        tv_fecha = (TextView) findViewById(R.id.tv_fecha);
        tv_anamnesis = (TextView) findViewById(R.id.tv_anamnesis);
        tv_anamnesis_tit = (TextView) findViewById(R.id.tv_anamnesis_tit);
        tv_observaciones_tit = (TextView) findViewById(R.id.tv_observaciones_tit);
        tv_observaciones = (TextView) findViewById(R.id.tv_observaciones);
        rv_listado = (RecyclerView) findViewById(R.id.rv_listado);
        fab_add_elemento = (FloatingActionButton) findViewById(R.id.fab_add_elemento);
        srl_listado = (SwipeRefreshLayout) findViewById(R.id.srl_listado);

        tv_listado_titulo.setText(R.string.curas);
        rv_listado.setHasFixedSize(true); //la altura de los elmtos es la misma

        //El RecyclerView usará un LinearLayoutManager
        mLayoutManager = new LinearLayoutManager(this);
        rv_listado.setLayoutManager(mLayoutManager);

        //Asociamos un adapter. Define cómo se renderizará la informacion que tenemos
        mAdapter = new CuraAdapter(this);
        rv_listado.setAdapter(mAdapter);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.cargando));

        proceso = (Proceso) getIntent().getExtras().getSerializable("proceso");
        if (proceso != null) {
            tv_fecha.setText(FechaHoraUtils.formatoFechaHoraUI(proceso.getCreacion()));
            tv_diagnostico_tit.setText(getText(R.string.diagnostico));
            tv_diagnostico.setText(proceso.getDiagnostico().getNombre());
            tv_anamnesis_tit.setText(getText(R.string.anamnesis));
            tv_anamnesis.setText(proceso.getAnamnesis());
            tv_observaciones_tit.setText(getText(R.string.observaciones));
            tv_observaciones.setText(proceso.getObservaciones());

            fab_add_elemento.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    irCuraNuevaActivity();
                }
            });

            cargarCuras();
        }

        srl_listado.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                recreate();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_proceso, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                volverAtras();
                return true;
            case R.id.action_editar_proceso:
                Intent intentEditar = new Intent(this, ProcesoNewEditActivity.class);
                intentEditar.putExtra("proceso", proceso);
                startActivity(intentEditar);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void volverAtras() {
        Paciente paciente = proceso.getPaciente();
        Intent i = new Intent(this, ProcesosActivity.class);
        i.putExtra("paciente", paciente);
        startActivity(i);
    }


    @Override
    public void onBackPressed() {
        volverAtras();
    }

    private void cargarCuras() {
        progressDialog.show();
        Call<ArrayList<Cura>> call = MyApiAdapter.getApiService().getCurasByProcesoId(proceso.getId(),
                Pref.getToken());
        call.enqueue(new Callback<ArrayList<Cura>>() {
            @Override
            public void onResponse(Call<ArrayList<Cura>> call, Response<ArrayList<Cura>> response) {
                if(response.isSuccessful()) {
                    progressDialog.cancel();
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
                progressDialog.cancel();

                if (t instanceof IOException) {
                    Toasty.warning(CurasActivity.this, getString(R.string.error_conexion_red),
                            Toast.LENGTH_LONG, true).show();
                } else {
                    Toasty.error(CurasActivity.this, getString(R.string.error_conversion),
                            Toast.LENGTH_LONG, true).show();
                    Log.d(TAG, getString(R.string.error_conversion));
                }
            }
        });
    }

    private void irCuraNuevaActivity() {
        Intent intent = new Intent(this, CuraNuevaActivity.class);
        intent.putExtra("proceso", proceso);
        startActivity(intent);
    }
}