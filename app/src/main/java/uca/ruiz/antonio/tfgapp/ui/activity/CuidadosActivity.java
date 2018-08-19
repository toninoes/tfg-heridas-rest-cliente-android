package uca.ruiz.antonio.tfgapp.ui.activity;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import es.dmoral.toasty.Toasty;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uca.ruiz.antonio.tfgapp.R;
import uca.ruiz.antonio.tfgapp.data.Preferencias;
import uca.ruiz.antonio.tfgapp.data.api.io.MyApiAdapter;
import uca.ruiz.antonio.tfgapp.data.api.mapping.ApiError;
import uca.ruiz.antonio.tfgapp.data.api.model.Cuidado;
import uca.ruiz.antonio.tfgapp.data.api.model.Grupodiagnostico;
import uca.ruiz.antonio.tfgapp.ui.adapter.CuidadoAdapter;
import uca.ruiz.antonio.tfgapp.utils.Pref;

public class CuidadosActivity extends AppCompatActivity {

    private static final String TAG = CuidadosActivity.class.getSimpleName();
    private RecyclerView rv_listado;
    private LinearLayoutManager mLayoutManager;
    private CuidadoAdapter mAdapter;
    private ProgressDialog progressDialog;
    private Spinner sp_Gdiagnosticos;
    private TextView sp_Gdiagnosticos_text;
    private Button bt_buscar;
    private Grupodiagnostico gDiagnostico;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuidados);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        rv_listado = (RecyclerView) findViewById(R.id.rv_listado);
        rv_listado.setHasFixedSize(true); // la altura de los elementos será la misma

        // El RecyclerView usará un LinearLayoutManager
        mLayoutManager = new LinearLayoutManager(this);
        rv_listado.setLayoutManager(mLayoutManager);

        // añade línea divisoria entre cada elemento
        rv_listado.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        // Asociamos un adapter. Define cómo se renderizará la información que tenemos
        mAdapter = new CuidadoAdapter(this);
        rv_listado.setAdapter(mAdapter);

        sp_Gdiagnosticos = (Spinner) findViewById(R.id.sp_Gdiagnosticos);
        sp_Gdiagnosticos_text = (TextView) findViewById(R.id.sp_Gdiagnosticos_error);
        bt_buscar = (Button) findViewById(R.id.bt_buscar);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.cargando));


        if(Preferencias.get(this).getBoolean("ROLE_SANITARIO", false))
            cargarGDiagnosticos(sp_Gdiagnosticos);
        else if(Preferencias.get(this).getBoolean("ROLE_PACIENTE", false)) {
            cargarMisGDiagnosticos(sp_Gdiagnosticos);
        }

        sp_Gdiagnosticos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                gDiagnostico = (Grupodiagnostico) adapterView.getAdapter().getItem(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        bt_buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intentarCargarCuidados();
            }
        });

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(Preferencias.get(this).getBoolean("ROLE_SANITARIO", false))
            getMenuInflater().inflate(R.menu.menu_nuevo_item, menu);
        else if(Preferencias.get(this).getBoolean("ROLE_PACIENTE", false)) {
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
            case R.id.add_item:
                startActivity(new Intent(this, CuidadoNewEditActivity.class));
                return true;
            case R.id.action_descargar_pdf:
                if(gDiagnostico.getId() == 0 || gDiagnostico == null) {
                    Toasty.warning(CuidadosActivity.this, getString(R.string.seleccione_Gdiagnostico),
                            Toast.LENGTH_LONG, true).show();
                } else {
                    descargarCuidadosPDF();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void volverAtras() {
        if(Preferencias.get(this).getBoolean("ROLE_PACIENTE", false))
            startActivity(new Intent(this, MainPacienteActivity.class));
        else if(Preferencias.get(this).getBoolean("ROLE_SANITARIO", false))
            startActivity(new Intent(this, MainSanitarioActivity.class));
    }

    @Override
    public void onBackPressed() {
        volverAtras();
    }

    private void intentarCargarCuidados() {
        // Resetear errores
        sp_Gdiagnosticos_text.setError(null);

        boolean cancel = false;
        View focusView = null;

        // Validar listado de Diagnósticos
        if(gDiagnostico.getId() == 0 || gDiagnostico == null) {
            sp_Gdiagnosticos_text.setError(getString(R.string.debes_seleccionar_grupo_diagnosticos));
            focusView = sp_Gdiagnosticos_text;
            cancel = true;
        }

        if (cancel) {
            // Se ha producido un error: no se intenta el registro y se focaliza en el
            // primer campo del formulario con error.
            focusView.requestFocus();
        } else {
            // ha ido bien, luego se procede a solicitar el listado de cuidados del diagnóstico
            // seleccionado
            cargarCuidados();

        }
    }

    private void cargarCuidados() {
        progressDialog.show();
        Call<ArrayList<Cuidado>> call = MyApiAdapter.getApiService().getCuidadosByGDiagnosticoId(
                gDiagnostico.getId(), Pref.getToken());
        call.enqueue(new Callback<ArrayList<Cuidado>>() {
            @Override
            public void onResponse(Call<ArrayList<Cuidado>> call, Response<ArrayList<Cuidado>> response) {
                progressDialog.cancel();
                if(response.isSuccessful()) {
                    ArrayList<Cuidado> cuidados = response.body();

                    if(cuidados != null)
                        Log.d("CUIDADOS", "Tamaño ==> " + cuidados.size());

                    mAdapter.setDataSet(cuidados);
                } else {
                    if (response.errorBody().contentType().subtype().equals("json")) {
                        ApiError apiError = ApiError.fromResponseBody(response.errorBody());
                        Toasty.error(CuidadosActivity.this, apiError.getMessage(),
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
            public void onFailure(Call<ArrayList<Cuidado>> call, Throwable t) {
                progressDialog.cancel();

                if (t instanceof IOException) {
                    Toasty.warning(CuidadosActivity.this, getString(R.string.error_conexion_red),
                            Toast.LENGTH_LONG, true).show();
                } else {
                    Toasty.error(CuidadosActivity.this, getString(R.string.error_conversion),
                            Toast.LENGTH_LONG, true).show();
                    Log.d(TAG, getString(R.string.error_conversion));
                }
            }
        });
    }

    private void cargarGDiagnosticos(final Spinner sp_Gdiagnosticos) {
        Call<ArrayList<Grupodiagnostico>> call = MyApiAdapter.getApiService().getGruposdiagnosticos(Pref.getToken());
        call.enqueue(new Callback<ArrayList<Grupodiagnostico>>() {
            @Override
            public void onResponse(Call<ArrayList<Grupodiagnostico>> call, Response<ArrayList<Grupodiagnostico>> response) {
                progressDialog.cancel();
                if(response.isSuccessful()) {
                    ArrayList<Grupodiagnostico> gDiagnosticos = response.body();
                    if(gDiagnosticos != null) {
                        gDiagnosticos.add(0, new Grupodiagnostico(getString(R.string.seleccione_Gdiagnostico)));
                        ArrayAdapter<Grupodiagnostico> arrayAdapter = new ArrayAdapter<Grupodiagnostico>(CuidadosActivity.this,
                                android.R.layout.simple_spinner_dropdown_item, gDiagnosticos);

                        Log.d("GRUPOS DIAGNOSTICOS", "Tamaño ==> " + gDiagnosticos.size());
                        sp_Gdiagnosticos.setAdapter(arrayAdapter);
                    }
                } else {
                    if (response.errorBody().contentType().subtype().equals("json")) {
                        ApiError apiError = ApiError.fromResponseBody(response.errorBody());
                        Toasty.error(CuidadosActivity.this, apiError.getMessage(),
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
            public void onFailure(Call<ArrayList<Grupodiagnostico>> call, Throwable t) {
                progressDialog.cancel();

                if (t instanceof IOException) {
                    Toasty.warning(CuidadosActivity.this, getString(R.string.error_conexion_red),
                            Toast.LENGTH_LONG, true).show();
                } else {
                    Toasty.error(CuidadosActivity.this, getString(R.string.error_conversion),
                            Toast.LENGTH_LONG, true).show();
                    Log.d(TAG, getString(R.string.error_conversion));
                }
            }
        });
    }

    private void cargarMisGDiagnosticos(final Spinner sp_Gdiagnosticos) {
        Call<ArrayList<Grupodiagnostico>> call = MyApiAdapter.getApiService().getMisGruposdiagnosticos(
                Pref.getUserId(), Pref.getToken());
        call.enqueue(new Callback<ArrayList<Grupodiagnostico>>() {
            @Override
            public void onResponse(Call<ArrayList<Grupodiagnostico>> call, Response<ArrayList<Grupodiagnostico>> response) {
                progressDialog.cancel();
                if(response.isSuccessful()) {
                    ArrayList<Grupodiagnostico> gDiagnosticos = response.body();
                    if(gDiagnosticos != null) {
                        gDiagnosticos.add(0, new Grupodiagnostico(getString(R.string.seleccione_Gdiagnostico)));
                        ArrayAdapter<Grupodiagnostico> arrayAdapter = new ArrayAdapter<Grupodiagnostico>(CuidadosActivity.this,
                                android.R.layout.simple_spinner_dropdown_item, gDiagnosticos);

                        Log.d("GRUPOS DIAGNOSTICOS", "Tamaño ==> " + gDiagnosticos.size());
                        sp_Gdiagnosticos.setAdapter(arrayAdapter);
                    }
                } else {
                    if (response.errorBody().contentType().subtype().equals("json")) {
                        ApiError apiError = ApiError.fromResponseBody(response.errorBody());
                        Toasty.error(CuidadosActivity.this, apiError.getMessage(),
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
            public void onFailure(Call<ArrayList<Grupodiagnostico>> call, Throwable t) {
                progressDialog.cancel();

                if (t instanceof IOException) {
                    Toasty.warning(CuidadosActivity.this, getString(R.string.error_conexion_red),
                            Toast.LENGTH_LONG, true).show();
                } else {
                    Toasty.error(CuidadosActivity.this, getString(R.string.error_conversion),
                            Toast.LENGTH_LONG, true).show();
                    Log.d(TAG, getString(R.string.error_conversion));
                }
            }
        });
    }

    private void descargarCuidadosPDF() {
        progressDialog.show();
        Call<ResponseBody> call = MyApiAdapter.getApiService().getPdfCuidadosByGrupoDiagnostico(
                gDiagnostico.getId(), Pref.getToken());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressDialog.cancel();
                if(response.isSuccessful()) {
                    try {
                        // Lo guardamos en la carpeta Downloads
                        File ruta = Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_DOWNLOADS);
                        String fecha = new SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(new Date());
                        String nombreFichero = "cuidados_" + fecha + ".pdf";

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
                        Toasty.warning(CuidadosActivity.this, getString(R.string.error_lector_pdf),
                                Toast.LENGTH_LONG, true).show();
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                } else {
                    if (response.errorBody().contentType().subtype().equals("json")) {
                        ApiError apiError = ApiError.fromResponseBody(response.errorBody());
                        Toasty.error(CuidadosActivity.this, apiError.getMessage(),
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
                    Toasty.warning(CuidadosActivity.this, getString(R.string.error_conexion_red),
                            Toast.LENGTH_LONG, true).show();
                } else {
                    Toasty.error(CuidadosActivity.this, getString(R.string.error_conversion),
                            Toast.LENGTH_LONG, true).show();
                    Log.d(TAG, getString(R.string.error_conversion));
                }
            }
        });
    }


}
