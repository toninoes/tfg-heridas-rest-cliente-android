package uca.ruiz.antonio.tfgapp.ui.activity.admin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import uca.ruiz.antonio.tfgapp.R;
import uca.ruiz.antonio.tfgapp.utils.Utils;

public class MainAdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_admin);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
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
        Intent intent = new Intent(this, CentrosActivity.class);
        startActivity(intent);
    }

    public void salas(View view) {
    }

    public void gruposdiagnosticos(View view) {
    }

    public void diagnosticos(View view) {
    }

    public void procedimientos(View view) {
    }

    public void cuidados(View view) {
    }

    public void pacientes(View view) {
    }

    public void sanitarios(View view) {
    }

    public void valoraciones(View view) {
    }

}
