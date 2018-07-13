package uca.ruiz.antonio.tfgapp.ui.activity.admin;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import uca.ruiz.antonio.tfgapp.R;
import uca.ruiz.antonio.tfgapp.utils.Utils;

public class MainAdminActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private LinearLayout ll_centros, ll_salas, ll_gruposdiagnosticos;
    private LinearLayout ll_diagnosticos, ll_procedimientos, ll_cuidados;
    private LinearLayout ll_pacientes, ll_sanitarios, ll_valoraciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_admin);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        ll_centros = (LinearLayout) findViewById(R.id.ll_centros);
        ll_salas = (LinearLayout) findViewById(R.id.ll_salas);
        ll_gruposdiagnosticos = (LinearLayout) findViewById(R.id.ll_gruposdiagnosticos);
        ll_diagnosticos = (LinearLayout) findViewById(R.id.ll_diagnosticos);
        ll_procedimientos = (LinearLayout) findViewById(R.id.ll_procedimientos);
        ll_cuidados = (LinearLayout) findViewById(R.id.ll_cuidados);
        ll_pacientes = (LinearLayout) findViewById(R.id.ll_pacientes);
        ll_sanitarios = (LinearLayout) findViewById(R.id.ll_sanitarios);
        ll_valoraciones = (LinearLayout) findViewById(R.id.ll_valoraciones);

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

    public void centros(View view) {
        ll_centros.setBackgroundResource(R.color.grisFondoLL);
        progressDialog.show();
        Intent intent = new Intent(this, CentrosActivity.class);
        startActivity(intent);
    }

    public void salas(View view) {
        ll_salas.setBackgroundResource(R.color.grisFondoLL);
        progressDialog.show();
        Intent intent = new Intent(this, SalasActivity.class);
        startActivity(intent);
    }

    public void gruposdiagnosticos(View view) {
        ll_gruposdiagnosticos.setBackgroundResource(R.color.grisFondoLL);
        progressDialog.show();

        // Intent aqui;
    }

    public void diagnosticos(View view) {
        ll_diagnosticos.setBackgroundResource(R.color.grisFondoLL);
        progressDialog.show();

        // Intent aqui;
    }

    public void procedimientos(View view) {
        ll_procedimientos.setBackgroundResource(R.color.grisFondoLL);
        progressDialog.show();

        // Intent aqui;
    }

    public void cuidados(View view) {
        ll_cuidados.setBackgroundResource(R.color.grisFondoLL);
        progressDialog.show();

        // Intent aqui;
    }

    public void pacientes(View view) {
        ll_pacientes.setBackgroundResource(R.color.grisFondoLL);
        progressDialog.show();

        // Intent aqui;
    }

    public void sanitarios(View view) {
        ll_sanitarios.setBackgroundResource(R.color.grisFondoLL);
        progressDialog.show();

        // Intent aqui;
    }

    public void valoraciones(View view) {
        ll_valoraciones.setBackgroundResource(R.color.grisFondoLL);
        progressDialog.show();

        // Intent aqui;
    }

}
