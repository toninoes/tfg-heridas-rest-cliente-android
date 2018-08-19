package uca.ruiz.antonio.tfgapp.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import uca.ruiz.antonio.tfgapp.R;
import uca.ruiz.antonio.tfgapp.ui.activity.admin.ValoracionesResultsActivity;
import uca.ruiz.antonio.tfgapp.utils.Utils;

public class MainSanitarioActivity extends AppCompatActivity  {

    private ProgressDialog progressDialog;
    private LinearLayout ll_asistencias, ll_cuidados, ll_valoraciones;
    private LinearLayout ll_citas;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_sanitario);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        ll_asistencias = (LinearLayout) findViewById(R.id.ll_asistencias);
        ll_cuidados = (LinearLayout) findViewById(R.id.ll_cuidados);
        ll_valoraciones = (LinearLayout) findViewById(R.id.ll_valoraciones);
        ll_citas = (LinearLayout) findViewById(R.id.ll_citas);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.cargando));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                Utils.preguntarQuiereSalir(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        Utils.preguntarQuiereSalir(this);
    }

    public  void asistencias(View view) {
        ll_asistencias.setBackgroundResource(R.color.grisFondoLL);
        progressDialog.show();

        Intent intent = new Intent(this, PacientesActivity.class);
        startActivity(intent);
    }

    public  void cuidados(View view) {
        ll_cuidados.setBackgroundResource(R.color.grisFondoLL);
        progressDialog.show();

        Intent intent = new Intent(this, CuidadosActivity.class);
        startActivity(intent);
    }


    public void valoraciones(View view) {
        ll_valoraciones.setBackgroundResource(R.color.grisFondoLL);
        progressDialog.show();

        Intent intent = new Intent(this, ValoracionesResultsActivity.class);
        startActivity(intent);
    }

    public  void agenda(View view) {
        ll_citas.setBackgroundResource(R.color.grisFondoLL);
        progressDialog.show();

        Intent intent = new Intent(this, CitacionesActivity.class);
        startActivity(intent);
    }





}