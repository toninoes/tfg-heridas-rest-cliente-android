package uca.ruiz.antonio.tfgapp.ui.activity;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

import es.dmoral.toasty.Toasty;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import uca.ruiz.antonio.tfgapp.R;
import uca.ruiz.antonio.tfgapp.data.Preferencias;
import uca.ruiz.antonio.tfgapp.data.api.io.MyApiAdapter;
import uca.ruiz.antonio.tfgapp.data.api.mapping.ApiError;
import uca.ruiz.antonio.tfgapp.data.api.model.Cura;
import uca.ruiz.antonio.tfgapp.data.api.model.Paciente;
import uca.ruiz.antonio.tfgapp.data.api.model.Proceso;
import uca.ruiz.antonio.tfgapp.ui.adapter.CuraAdapter;
import uca.ruiz.antonio.tfgapp.utils.FechaHoraUtils;
import uca.ruiz.antonio.tfgapp.utils.Pref;

import java.util.Date;


public class CurasActivity extends AppCompatActivity {

    private static final String TAG = CurasActivity.class.getSimpleName();
    private FloatingActionButton fab_add_elemento;
    private TextView tv_listado_titulo;
    private RecyclerView rv_listado;
    private LinearLayoutManager mLayoutManager;
    private CuraAdapter mAdapter;

    private TextView tv_fecha;
    private TextView tv_diagnostico, tv_diagnostico_tit;
    private TextView tv_anamnesis, tv_anamnesis_tit;
    private TextView tv_observaciones, tv_observaciones_tit;

    private SwipeRefreshLayout srl_listado;
    private ProgressDialog progressDialog;

    private Proceso proceso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_curas);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        tv_fecha = (TextView) findViewById(R.id.tv_fecha);
        tv_listado_titulo = (TextView) findViewById(R.id.tv_listado_titulo);
        tv_diagnostico_tit = (TextView) findViewById(R.id.tv_diagnostico_tit);
        tv_diagnostico = (TextView) findViewById(R.id.tv_diagnostico);
        tv_anamnesis = (TextView) findViewById(R.id.tv_anamnesis);
        tv_anamnesis_tit = (TextView) findViewById(R.id.tv_anamnesis_tit);
        tv_observaciones_tit = (TextView) findViewById(R.id.tv_observaciones_tit);
        tv_observaciones = (TextView) findViewById(R.id.tv_observaciones);
        rv_listado = (RecyclerView) findViewById(R.id.rv_listado);
        fab_add_elemento = (FloatingActionButton) findViewById(R.id.fab_add_elemento);
        srl_listado = (SwipeRefreshLayout) findViewById(R.id.srl_listado);

        tv_listado_titulo.setText(R.string.curas);
        rv_listado.setHasFixedSize(true); //la altura de los elmtos es la misma

        //El RecyclerView usará un LinearLayoutManager
        mLayoutManager = new LinearLayoutManager(this);
        rv_listado.setLayoutManager(mLayoutManager);

        //Asociamos un adapter. Define cómo se renderizará la informacion que tenemos
        mAdapter = new CuraAdapter(this);
        rv_listado.setAdapter(mAdapter);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.cargando));

        proceso = (Proceso) getIntent().getExtras().getSerializable("proceso");
        if (proceso != null) {
            tv_fecha.setText(FechaHoraUtils.formatoFechaHoraUI(proceso.getCreacion()));
            tv_diagnostico_tit.setText(getText(R.string.diagnostico));
            tv_diagnostico.setText(proceso.getDiagnostico().getNombre());
            tv_anamnesis_tit.setText(getText(R.string.anamnesis));
            tv_anamnesis.setText(proceso.getAnamnesis());
            tv_observaciones_tit.setText(getText(R.string.observaciones));
            tv_observaciones.setText(proceso.getObservaciones());

            if(Preferencias.get(this).getBoolean("ROLE_SANITARIO", false)) {
                fab_add_elemento.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        irCuraNuevaActivity();
                    }
                });
            } else if(Preferencias.get(this).getBoolean("ROLE_PACIENTE", false)) {
                fab_add_elemento.setVisibility(View.GONE);
            }

            cargarCuras();
        }

        srl_listado.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                recreate();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if(Preferencias.get(this).getBoolean("ROLE_SANITARIO", false)) {
            getMenuInflater().inflate(R.menu.menu_editar_item, menu);
        } else if(Preferencias.get(this).getBoolean("ROLE_PACIENTE", false)) {
            getMenuInflater().inflate(R.menu.menu_descargar_pdf, menu);
        }


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                volverAtras();
                return true;
            case R.id.action_editar:
                Intent intentEditar = new Intent(this, ProcesoNewEditActivity.class);
                intentEditar.putExtra("proceso", proceso);
                startActivity(intentEditar);
                return true;
            case R.id.action_descargar_pdf:
                descargarPDF();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void volverAtras() {
        Paciente paciente = proceso.getPaciente();
        Intent i = new Intent(this, ProcesosActivity.class);
        i.putExtra("paciente", paciente);
        startActivity(i);
    }


    @Override
    public void onBackPressed() {
        volverAtras();
    }

    private void cargarCuras() {
        progressDialog.show();
        Call<ArrayList<Cura>> call = MyApiAdapter.getApiService().getCurasByProcesoId(proceso.getId(),
                Pref.getToken());
        call.enqueue(new Callback<ArrayList<Cura>>() {
            @Override
            public void onResponse(Call<ArrayList<Cura>> call, Response<ArrayList<Cura>> response) {
                progressDialog.cancel();
                if(response.isSuccessful()) {
                    ArrayList<Cura> curas = response.body();
                    if(curas != null) {
                        Log.d("CURAS", "Tamaño ==> " + curas.size());

                        // Ordenar las curas según fecha en orden descendiente
                        // Prefiero hacerlo en cliente para descargar al servidor
                        Collections.sort(curas, new Comparator<Cura>() {
                            @Override
                            public int compare(Cura cura1, Cura cura2) {
                                return cura2.getCreacion().compareTo(cura1.getCreacion());
                            }
                        });
                    }
                    mAdapter.setDataSet(curas);
                } else {
                    if (response.errorBody().contentType().subtype().equals("json")) {
                        ApiError apiError = ApiError.fromResponseBody(response.errorBody());
                        Toasty.error(CurasActivity.this, apiError.getMessage(),
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
            public void onFailure(Call<ArrayList<Cura>> call, Throwable t) {
                progressDialog.cancel();

                if (t instanceof IOException) {
                    Toasty.warning(CurasActivity.this, getString(R.string.error_conexion_red),
                            Toast.LENGTH_LONG, true).show();
                } else {
                    Toasty.error(CurasActivity.this, getString(R.string.error_conversion),
                            Toast.LENGTH_LONG, true).show();
                    Log.d(TAG, getString(R.string.error_conversion));
                }
            }
        });
    }

    private void irCuraNuevaActivity() {
        Intent intent = new Intent(this, CuraNewEditActivity.class);
        intent.putExtra("proceso", proceso);
        startActivity(intent);
    }

    private void descargarPDF() {
        Call<ResponseBody> call = MyApiAdapter.getApiService().getPdfProceso(proceso.getId(), Pref.getToken());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()) {
                    try {
                        // Lo guardamos en la carpeta Downloads
                        File ruta = Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_DOWNLOADS);
                        String fecha = new SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(new Date());
                        String nombreFichero = "informe_" + fecha + ".pdf";

                        File fichero = new File(ruta, nombreFichero);
                        FileOutputStream fileOutputStream = new FileOutputStream(fichero);
                        IOUtils.write(response.body().bytes(), fileOutputStream);

                        // Y ahora lo abrimos
                        Intent leerPDF = new Intent(Intent.ACTION_VIEW);
                        leerPDF.setDataAndType(Uri.fromFile(fichero), "application/pdf");
                        leerPDF.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                        // Este intent te permite elegir entre las diferentes Apps instaladas en
                        // el dispositivos que son capaces de leer PDFs
                        Intent intent = Intent.createChooser(leerPDF, "Open File");
                        startActivity(intent);

                    } catch (ActivityNotFoundException e) {
                        // si no existe ninguna App capaz de leer PDFs muestra mensaje indiándolo.
                        Toasty.warning(CurasActivity.this, getString(R.string.error_lector_pdf),
                                Toast.LENGTH_LONG, true).show();
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                } else {
                    if (response.errorBody().contentType().subtype().equals("json")) {
                        ApiError apiError = ApiError.fromResponseBody(response.errorBody());
                        Toasty.error(CurasActivity.this, apiError.getMessage(),
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
                    Toasty.warning(CurasActivity.this, getString(R.string.error_conexion_red),
                            Toast.LENGTH_LONG, true).show();
                } else {
                    Toasty.error(CurasActivity.this, getString(R.string.error_conversion),
                            Toast.LENGTH_LONG, true).show();
                    Log.d(TAG, getString(R.string.error_conversion));
                }
            }
        });
    }

    private boolean writeResponseBodyToDisk(ResponseBody body) {
        try {
            File mediaStorageDir = new File(
                    Environment
                            .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    "PDF");

            // Create the storage directory if it does not exist
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.d("door tracker", "Oops! Failed create "
                            + "door tracker" + " directory");
                }
            }

            // Create a media file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                    Locale.getDefault()).format(new Date());
            File mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "informe "+ timeStamp + ".pdf");

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(mediaFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    Log.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
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
}