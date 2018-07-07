package uca.ruiz.antonio.tfgapp.ui.activity.admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import uca.ruiz.antonio.tfgapp.R;

public class MainAdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_admin);
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
