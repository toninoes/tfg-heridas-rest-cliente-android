package uca.ruiz.antonio.tfgapp.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;

import es.dmoral.toasty.Toasty;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uca.ruiz.antonio.tfgapp.R;
import uca.ruiz.antonio.tfgapp.data.api.io.MyApiAdapter;
import uca.ruiz.antonio.tfgapp.data.api.mapping.ApiError;
import uca.ruiz.antonio.tfgapp.data.api.model.Imagen;
import uca.ruiz.antonio.tfgapp.utils.Pref;

public class ImagenActivity extends AppCompatActivity {

    private static final String TAG = ImagenActivity.class.getSimpleName();
    private Imagen imagen;
    private TextView tv_descripcion;
    private ImageView iv_imagen;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagen);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.cargando));

        iv_imagen = (ImageView) findViewById(R.id.iv_imagen);
        tv_descripcion = (TextView) findViewById(R.id.tv_descripcion);

        imagen = (Imagen) getIntent().getExtras().getSerializable("imagen");

        if (imagen != null) {
            tv_descripcion.setText(imagen.getDescripcion());
            //Picasso.with(this).load("http://localhost:8080/api/imagenes/nombre/126.jpg").into(iv_imagen);
            cargarImagen(iv_imagen);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu_editar_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                volverAtras();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void volverAtras() {
        Intent i = new Intent(this, ImagenesActivity.class);
        i.putExtra("cura", imagen.getCura());
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        volverAtras();
    }

    private void cargarImagen(final ImageView iv_imagen) {
        progressDialog.show();
        Call<ResponseBody> call = MyApiAdapter.getApiService().descargarImagenId(imagen.getId(), Pref.getToken());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()) {
                    try {
                        InputStream is = response.body().byteStream();
                        Bitmap bm = BitmapFactory.decodeStream(is);
                        Bitmap resized = Bitmap.createScaledBitmap(bm, 500, 500, true);
                        is.close();
                        iv_imagen.setPadding(0, 0, 0, 0);
                        iv_imagen.setImageBitmap(resized);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    progressDialog.cancel();
                } else {
                    if (response.errorBody().contentType().subtype().equals("json")) {
                        ApiError apiError = ApiError.fromResponseBody(response.errorBody());
                        Toasty.error(ImagenActivity.this, apiError.getMessage(),
                                Toast.LENGTH_LONG, true).show();
                        Log.d(TAG, apiError.getPath() + " " + apiError.getMessage());
                    } else {
                        try {
                            Log.d(TAG, response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.cancel();

                if (t instanceof IOException) {
                    Toasty.warning(ImagenActivity.this, getString(R.string.error_conexion_red),
                            Toast.LENGTH_LONG, true).show();
                } else {
                    Toasty.error(ImagenActivity.this, getString(R.string.error_conversion),
                            Toast.LENGTH_LONG, true).show();
                    Log.d(TAG, getString(R.string.error_conversion));
                }
            }
        });
    }

}
