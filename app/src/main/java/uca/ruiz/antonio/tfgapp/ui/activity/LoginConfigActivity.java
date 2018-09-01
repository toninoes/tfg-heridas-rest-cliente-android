package uca.ruiz.antonio.tfgapp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;
import uca.ruiz.antonio.tfgapp.R;
import uca.ruiz.antonio.tfgapp.data.Preferencias;
import uca.ruiz.antonio.tfgapp.utils.Pref;

import static uca.ruiz.antonio.tfgapp.R.id.et_puerto;
import static uca.ruiz.antonio.tfgapp.R.id.et_servicio;
import static uca.ruiz.antonio.tfgapp.R.string.puerto;


public class LoginConfigActivity extends AppCompatActivity {

    private static final String TAG = LoginConfigActivity.class.getSimpleName();
    private EditText et_url, et_puerto, et_servicio;
    private CheckBox chk_https;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_config);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        et_url = (EditText) findViewById(R.id.et_url);
        et_puerto = (EditText) findViewById(R.id.et_puerto);
        et_servicio = (EditText) findViewById(R.id.et_servicio);
        chk_https = (CheckBox) findViewById(R.id.chk_https);

        et_url.setText(Pref.getUrlServidor());
        et_puerto.setText(Pref.getPuertoServidor());
        et_servicio.setText(Pref.getServicioServidor());
        String protocolo = Pref.getProtocoloServidor();
        if (protocolo.equals("https"))
            chk_https.setChecked(true);
        else if (protocolo.equals("http"))
            chk_https.setChecked(false);
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
                volverAtras();
                return true;
            case R.id.action_guardar:
                intentarGuardar();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void volverAtras() {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        volverAtras();
    }

    /**
     * Intenta guardar: Si hay errores de formulario (campo no válido, campos faltantes, etc.), se
     * presentan los errores y no se guarda nada.
     */
    private void intentarGuardar() {
        et_url.setError(null);
        et_puerto.setError(null);

        String servicio = et_servicio.getText().toString();
        String puerto = et_puerto.getText().toString();
        String url = et_url.getText().toString();
        String protocolo;
        if (chk_https.isChecked())
            protocolo = "https";
        else
            protocolo = "http";


        boolean cancel = false;
        View focusView = null;

        if(!puerto.isEmpty()) {
            Long puertoL = Long.valueOf(et_puerto.getText().toString());
            // Validar puerto
            if (puertoL < 0 || puertoL > 65535) {
                et_puerto.setError(getString(R.string.puerto_0_65535));
                focusView = et_puerto;
                cancel = true;
            }
            puerto = puertoL.toString();
        }

        // Validar la URL o IP
        if (url.isEmpty()) {
            et_url.setError(getString(R.string.debes_escribir_ip_url));
            focusView = et_url;
            cancel = true;
        }

        if (cancel) {
            // Se ha producido un error: no se intenta el registro y se focaliza en el
            // primer campo del formulario con error.
            focusView.requestFocus();
        } else {
            // ha ido bien, luego se procede guardar estos datos en las preferencias.
            Preferencias.getEditor(this).putString("protocolo", protocolo).commit();
            Preferencias.getEditor(this).putString("url", url).commit();
            Preferencias.getEditor(this).putString("puerto", puerto).commit();
            Preferencias.getEditor(this).putString("servicio", servicio).commit();

            String msg = getString(R.string.conf_servidor_guardada) + "\n\n" + Pref.getBaseUrl();
            Toasty.success(LoginConfigActivity.this, msg, Toast.LENGTH_LONG, true).show();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }
}

