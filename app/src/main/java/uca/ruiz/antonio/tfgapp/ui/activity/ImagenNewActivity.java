package uca.ruiz.antonio.tfgapp.ui.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import es.dmoral.toasty.Toasty;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import uca.ruiz.antonio.tfgapp.R;
import uca.ruiz.antonio.tfgapp.data.api.io.MyApiAdapter;
import uca.ruiz.antonio.tfgapp.data.api.mapping.ImgErrorResponse;
import uca.ruiz.antonio.tfgapp.data.api.mapping.ImgResponse;
import uca.ruiz.antonio.tfgapp.data.api.model.Cura;
import uca.ruiz.antonio.tfgapp.utils.Pref;


public class ImagenNewActivity extends AppCompatActivity {

    private static final String TAG = ImagenNewActivity.class.getSimpleName();
    private Button button_camera, button_gallery, button_upload;
    private ImageView iv_imagen;
    private Uri fileImg;
    String mediaPath = null;
    ProgressDialog progressDialog;
    private Boolean seHaceFoto = false;
    private Cura cura;
    private EditText et_descripcion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagen_new);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.subiendo_imagen));

        cura = (Cura) getIntent().getExtras().getSerializable("cura");

        button_camera = (Button) findViewById(R.id.button_camera);
        button_gallery = (Button) findViewById(R.id.button_gallery);
        button_upload = (Button) findViewById(R.id.button_upload);
        iv_imagen = (ImageView) findViewById(R.id.iv_imagen);
        et_descripcion = (EditText) findViewById(R.id.et_descripcion);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            button_camera.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[] {
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }

        button_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(debesPedirPermiso()) {
                    pedirPermisos();
                }
                hacerFotografia();
            }
        });

        button_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(debesPedirPermiso()) {
                    pedirPermisos();
                }
                irGaleria();

            }
        });

        button_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPath == null) {
                    msgWarning(getString(R.string.no_seleccion_imagen));
                } else {
                    SubirImagen();
                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu_principal, menu);
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
        i.putExtra("cura", cura);
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        volverAtras();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                button_camera.setEnabled(true);
                button_gallery.setEnabled(true);
                button_upload.setEnabled(true);
            }
        }
    }

    public void hacerFotografia() {
        Intent fotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileImg = Uri.fromFile(getOutputMediaFile());
        mediaPath = fileImg.getPath() ;
        fotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileImg);
        seHaceFoto = true;
        startActivityForResult(fotoIntent, 100);
    }

    public void irGaleria() {
        Intent galeriaIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        seHaceFoto = false;
        startActivityForResult(galeriaIntent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == 100 && resultCode == RESULT_OK) { //se hace foto
                iv_imagen.setPadding(0,0,0,0);
                iv_imagen.setImageURI(fileImg);
            } else if (requestCode == 0 && resultCode == RESULT_OK && null != data) { //desde galería
                // Toma la imagen desde 'data'
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                assert cursor != null;
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                mediaPath = cursor.getString(columnIndex);
                // Coloca la imagen en ImageView para previsualizar.
                iv_imagen.setPadding(0,0,0,0);
                iv_imagen.setImageBitmap(BitmapFactory.decodeFile(mediaPath));
                cursor.close();
            } else {
                msgWarning(getString(R.string.no_seleccion_imagen));
            }
        } catch (Exception e) {
            msgError(getString(R.string.error_desconocido));
        }
    }

    private void SubirImagen() {
        progressDialog.show();

        final File fichero = new File(mediaPath);

        RequestBody requestBody = RequestBody.create(MediaType.parse(obtenerTipoMime(fichero.getPath())), fichero);
        MultipartBody.Part ficheroParaSubir = MultipartBody.Part.createFormData("imagen", fichero.getName(), requestBody);
        RequestBody nombreFichero = RequestBody.create(MediaType.parse("text/plain"), fichero.getName());
        String descripcion = et_descripcion.getText().toString();

        //Call<ImgResponse> call = MyApiAdapter.getApiService().subirImagen(cura.getId(), ficheroParaSubir, nombreFichero);
        Call<ImgResponse> call = MyApiAdapter.getApiService().subirImagenToken(cura.getId(),
                ficheroParaSubir, nombreFichero, descripcion, Pref.getToken());
        call.enqueue(new Callback<ImgResponse>() {
            @Override
            public void onResponse(Call<ImgResponse> call, Response<ImgResponse> response) {
                progressDialog.cancel();
                if(response.isSuccessful()) {
                    ImgResponse serverResponse = response.body();
                    if (seHaceFoto) {
                        if(fichero.delete()) //eliminamos las fotografias hechas desde la App
                            msgExito(getString(R.string.ok_foto_borrada_de_aqui));
                        else
                            msgExito(getString(R.string.ok_foto));
                    } else
                        msgExito(getString(R.string.ok_imagen));

                    Intent intent = new Intent(ImagenNewActivity.this, ImagenesActivity.class);
                    intent.putExtra("cura", cura);
                    startActivity(intent);
                } else {
                    if (response.errorBody().contentType().subtype().equals("json")) {
                        ImgErrorResponse apiError = ImgErrorResponse.fromResponseBody(response.errorBody());
                        msgError(apiError.getMessage());
                    } else {
                        try {
                            Log.d(getString(R.string.error), response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ImgResponse> call, Throwable t) {
                progressDialog.cancel();

                if (t instanceof IOException) {
                    msgWarning(getString(R.string.error_conexion_red));
                } else {
                    msgError(getString(R.string.error_conversion));
                    Log.d(TAG, getString(R.string.error_conversion));
                }
            }
        });
    }

    public static String obtenerTipoMime(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    protected boolean debesPedirPermiso() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @TargetApi(23)
    protected void pedirPermisos() {
        String[] permissions = {
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE",
                "android.permission.CAMERA"
        };
        int requestCode = 200;
        requestPermissions(permissions, requestCode);
    }

    private File getOutputMediaFile(){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), getString(R.string.app_name));

        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                Log.d(getString(R.string.app_name), getString(R.string.error_crear_directorio));
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator + timeStamp + ".jpg");
    }

    private void msgExito(String msg) {
        Toast t = Toasty.success(ImagenNewActivity.this, msg, Toast.LENGTH_SHORT, true);
        t.setGravity(Gravity.CENTER,0,0);
        t.show();
    }

    private void msgWarning(String msg) {
        Toast t = Toasty.warning(ImagenNewActivity.this, msg, Toast.LENGTH_LONG, true);
        t.setGravity(Gravity.CENTER,0,0);
        t.show();
    }

    private void msgError(String msg) {
        Toast t = Toasty.error(ImagenNewActivity.this, msg, Toast.LENGTH_LONG, true);
        t.setGravity(Gravity.CENTER,0,0);
        t.show();
    }

}
