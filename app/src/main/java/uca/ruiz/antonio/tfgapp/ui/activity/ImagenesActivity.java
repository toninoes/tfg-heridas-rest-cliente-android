package uca.ruiz.antonio.tfgapp.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import uca.ruiz.antonio.tfgapp.R;
import uca.ruiz.antonio.tfgapp.data.api.model.Cura;
import uca.ruiz.antonio.tfgapp.data.api.model.Paciente;
import uca.ruiz.antonio.tfgapp.data.api.model.Proceso;
import uca.ruiz.antonio.tfgapp.ui.adapter.CuraAdapter;
import uca.ruiz.antonio.tfgapp.ui.adapter.ImagenAdapter;
import uca.ruiz.antonio.tfgapp.utils.FechaHoraUtils;

import static uca.ruiz.antonio.tfgapp.R.id.tv_anamnesis;
import static uca.ruiz.antonio.tfgapp.R.id.tv_anamnesis_tit;
import static uca.ruiz.antonio.tfgapp.R.id.tv_diagnostico;
import static uca.ruiz.antonio.tfgapp.R.id.tv_diagnostico_tit;
import static uca.ruiz.antonio.tfgapp.R.id.tv_observaciones;
import static uca.ruiz.antonio.tfgapp.R.id.tv_observaciones_tit;
import static uca.ruiz.antonio.tfgapp.R.string.paciente;
import static uca.ruiz.antonio.tfgapp.R.string.proceso;

public class ImagenesActivity extends AppCompatActivity {

    private static final String TAG = ImagenesActivity.class.getSimpleName();
    private FloatingActionButton fab_add_elemento;
    private TextView tv_listado_titulo;
    private RecyclerView rv_listado;
    private LinearLayoutManager mLayoutManager;
    private ImagenAdapter mAdapter;

    private TextView tv_fecha;
    private TextView tv_evolucion, tv_evolucion_tit;
    private TextView tv_tratamiento, tv_tratamiento_tit;
    private TextView tv_recomendaciones, tv_recomendaciones_tit;
    private TextView tv_sanitario, tv_sanitario_tit;

    private SwipeRefreshLayout srl_listado;
    private ProgressDialog progressDialog;

    private Cura cura;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagenes);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        tv_fecha = (TextView) findViewById(R.id.tv_fecha);
        tv_listado_titulo = (TextView) findViewById(R.id.tv_listado_titulo);
        tv_evolucion_tit = (TextView) findViewById(R.id.tv_evolucion_tit);
        tv_evolucion = (TextView) findViewById(R.id.tv_evolucion);
        tv_tratamiento_tit = (TextView) findViewById(R.id.tv_tratamiento_tit);
        tv_tratamiento = (TextView) findViewById(R.id.tv_tratamiento);
        tv_recomendaciones_tit = (TextView) findViewById(R.id.tv_recomendaciones_tit);
        tv_recomendaciones = (TextView) findViewById(R.id.tv_recomendaciones);
        tv_sanitario_tit = (TextView) findViewById(R.id.tv_sanitario_tit);
        tv_sanitario = (TextView) findViewById(R.id.tv_sanitario);
        rv_listado = (RecyclerView) findViewById(R.id.rv_listado);
        fab_add_elemento = (FloatingActionButton) findViewById(R.id.fab_add_elemento);
        srl_listado = (SwipeRefreshLayout) findViewById(R.id.srl_listado);

        tv_listado_titulo.setText(R.string.fotos);
        rv_listado.setHasFixedSize(true); //la altura de los elmtos es la misma

        //El RecyclerView usará un LinearLayoutManager
        mLayoutManager = new LinearLayoutManager(this);
        rv_listado.setLayoutManager(mLayoutManager);

        //Asociamos un adapter. Define cómo se renderizará la informacion que tenemos
        mAdapter = new ImagenAdapter(this);
        rv_listado.setAdapter(mAdapter);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.cargando));

        cura = (Cura) getIntent().getExtras().getSerializable("cura");
        if (cura != null) {
            tv_fecha.setText(FechaHoraUtils.formatoFechaHoraUI(cura.getCreacion()));
            tv_evolucion_tit.setText(getText(R.string.evolucion));
            tv_evolucion.setText(cura.getEvolucion());
            tv_tratamiento_tit.setText(getText(R.string.tratamiento));
            tv_tratamiento.setText(cura.getTratamiento());
            tv_recomendaciones_tit.setText(getText(R.string.recomendaciones));
            tv_recomendaciones.setText(cura.getRecomendaciones());
            tv_sanitario_tit.setText(getText(R.string.sanitario));
            tv_sanitario.setText(cura.getSanitario().getFullName());

            fab_add_elemento.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    irImagenNuevaActivity();
                }
            });

            cargarImagenes();
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
        getMenuInflater().inflate(R.menu.menu_editar_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                volverAtras();
                return true;
            case R.id.action_editar:
                Intent intentEditar = new Intent(this, CuraNewEditActivity.class);
                intentEditar.putExtra("cura", cura);
                startActivity(intentEditar);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void volverAtras() {
        Proceso proceso = cura.getProceso();
        Intent i = new Intent(this, CurasActivity.class);
        i.putExtra("proceso", proceso);
        startActivity(i);
    }


    @Override
    public void onBackPressed() {
        volverAtras();
    }

    private void cargarImagenes() {
        //TODO
    }

    private void irImagenNuevaActivity() {
        //TODO
    }
}
