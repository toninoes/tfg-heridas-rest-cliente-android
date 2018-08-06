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

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uca.ruiz.antonio.tfgapp.R;
import uca.ruiz.antonio.tfgapp.data.api.io.MyApiAdapter;
import uca.ruiz.antonio.tfgapp.data.api.model.Imagen;
import uca.ruiz.antonio.tfgapp.data.api.model.Paciente;
import uca.ruiz.antonio.tfgapp.data.api.model.Proceso;
import uca.ruiz.antonio.tfgapp.utils.Pref;

import static uca.ruiz.antonio.tfgapp.R.string.cura;
import static uca.ruiz.antonio.tfgapp.R.string.paciente;

public class ImagenActivity extends AppCompatActivity {

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
            //Picasso.get().load("http://i.imgur.com/DvpvklR.png").into(iv_imagen);
            //Picasso.with(this).load("http://localhost:8080/api/imagenes/1").into(iv_imagen);
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
                try {

                    Log.d("onResponse", "Response came from server");

                    boolean FileDownloaded = DownloadImage(response.body());

                    Log.d("onResponse", "Image is downloaded and saved ? " + FileDownloaded);

                } catch (Exception e) {
                    Log.d("onResponse", "There is an error");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private boolean DownloadImage(ResponseBody body) {

        try {
            Log.d("DownloadImage", "Reading and writing file");
            InputStream in = null;
            FileOutputStream out = null;

            try {
                in = body.byteStream();
                out = new FileOutputStream(getExternalFilesDir(null) + File.separator + "img.jpg");
                int c;

                while ((c = in.read()) != -1) {
                    out.write(c);
                }
            }
            catch (IOException e) {
                Log.d("DownloadImage",e.toString());
                return false;
            }
            finally {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            }

            //int width, height;
            //ImageView image = (ImageView) findViewById(R.id.iv_imagen);
            Bitmap bMap = BitmapFactory.decodeFile(getExternalFilesDir(null) + File.separator + "img.jpg");
            //width = 2*bMap.getWidth();
            //height = 6*bMap.getHeight();
            //Bitmap bMap2 = Bitmap.createScaledBitmap(bMap, width, height, false);
            //image.setImageBitmap(bMap2);
            iv_imagen.setPadding(0, 0, 0, 0);
            iv_imagen.setImageBitmap(bMap);
            progressDialog.cancel();

            return true;

        } catch (IOException e) {
            Log.d("DownloadImage",e.toString());
            return false;
        }
    }
}
