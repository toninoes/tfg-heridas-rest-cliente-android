package uca.ruiz.antonio.tfgapp.ui.activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import android.view.WindowManager;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uca.ruiz.antonio.tfgapp.R;
import uca.ruiz.antonio.tfgapp.data.api.io.MyApiSplashAdapter;

public class SplashActivity extends AppCompatActivity {
    private final int DURACION_SPLASH = 3000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        setContentView(R.layout.activity_splash);

        comprobarServidor();
    }

    private void comprobarServidor() {
        String protocolo, url, puerto;
        SharedPreferences prefs = this.getSharedPreferences(getString(R.string.app_name),
                Context.MODE_PRIVATE);
        protocolo = prefs.getString("protocolo", "http");
        url = prefs.getString("url", "10.0.2.2");
        puerto = prefs.getString("puerto", "8080");
        Call<String> call = MyApiSplashAdapter.getApiService(protocolo, url, puerto).comprobarServidor();
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful()) {
                    new Handler().postDelayed(new Runnable(){
                        public void run(){
                            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        };
                    }, DURACION_SPLASH);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toasty.warning(SplashActivity.this, getString(R.string.error_conexion_servidor),
                        Toast.LENGTH_LONG, true).show();
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}