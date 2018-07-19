package uca.ruiz.antonio.tfgapp.ui.activity.admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uca.ruiz.antonio.tfgapp.R;
import uca.ruiz.antonio.tfgapp.data.api.io.MyApiAdapter;
import uca.ruiz.antonio.tfgapp.data.api.mapping.ApiError;
import uca.ruiz.antonio.tfgapp.data.api.model.Administrador;
import uca.ruiz.antonio.tfgapp.utils.Pref;
import uca.ruiz.antonio.tfgapp.utils.Validacion;

import static uca.ruiz.antonio.tfgapp.R.id.et_apellidos;
import static uca.ruiz.antonio.tfgapp.R.id.et_direccion;
import static uca.ruiz.antonio.tfgapp.R.id.et_telefono;


public class AdministradorNewEditActivity extends AppCompatActivity {

    private static final String TAG = AdministradorNewEditActivity.class.getSimpleName();
    private EditText et_nombre, et_apellidos, et_email;
    private Administrador administrador;
    private Boolean editando = false;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrador_new_edit);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        et_nombre = (EditText) findViewById(R.id.et_nombre);
        et_apellidos = (EditText) findViewById(R.id.et_apellidos);
        et_email = (EditText) findViewById(R.id.et_email);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.guardando));

        try { // editar
            administrador = (Administrador) getIntent().getExtras().getSerializable("administrador");
            et_nombre.setText(administrador.getFirstname());
            et_apellidos.setText(administrador.getLastname());
            et_email.setText(administrador.getEmail());
            editando = true;
        } catch (Exception e) {
            Log.d(TAG, getString(R.string.creando_nuevo_registro));
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_guardar_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                startActivity(new Intent(this, AdministradoresActivity.class));
                return true;
            case R.id.action_guardar:
                intentarGuardar();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Intenta guardar: Si hay errores de formulario (campo no válido, campos faltantes, etc.), se
     * presentan los errores y no se guarda nada, porque ni siquiera se envía nada al servidor.
     */
    private void intentarGuardar() {


    }

    private void nuevo(Administrador a) {

    }

    private void editar(Administrador a) {

    }


}
