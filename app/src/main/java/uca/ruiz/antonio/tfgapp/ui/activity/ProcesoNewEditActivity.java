package uca.ruiz.antonio.tfgapp.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uca.ruiz.antonio.tfgapp.R;
import uca.ruiz.antonio.tfgapp.data.api.io.MyApiAdapter;
import uca.ruiz.antonio.tfgapp.data.api.mapping.ApiError;
import uca.ruiz.antonio.tfgapp.data.api.model.Diagnostico;
import uca.ruiz.antonio.tfgapp.data.api.model.Grupodiagnostico;
import uca.ruiz.antonio.tfgapp.data.api.model.Paciente;
import uca.ruiz.antonio.tfgapp.data.api.model.Procedimiento;
import uca.ruiz.antonio.tfgapp.data.api.model.Proceso;
import uca.ruiz.antonio.tfgapp.data.api.model.User;
import uca.ruiz.antonio.tfgapp.utils.Pref;

import static android.R.attr.id;
import static android.R.attr.x;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;


public class ProcesoNewEditActivity extends AppCompatActivity  {

    private static final String TAG = ProcesoNewEditActivity.class.getSimpleName();
    private EditText et_anamnesis, et_observaciones;
    private Spinner sp_Gdiagnosticos, sp_diagnosticos, sp_procedimientos;
    private TextView sp_Gdiagnosticos_text, sp_diagnosticos_text, sp_procedimientos_text;

    private Paciente paciente;
    private Grupodiagnostico gDiagnostico;
    private Diagnostico diagnostico;
    private Procedimiento procedimiento;
    private Proceso proceso;
    private Boolean editando = false;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proceso_new_edit);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        paciente = (Paciente) getIntent().getExtras().getSerializable("paciente");

        et_anamnesis = (EditText) findViewById(R.id.et_anamnesis);
        et_observaciones = (EditText) findViewById(R.id.et_observaciones);
        sp_Gdiagnosticos = (Spinner) findViewById(R.id.sp_Gdiagnosticos);
        sp_Gdiagnosticos_text = (TextView) findViewById(R.id.sp_Gdiagnosticos_error);
        sp_diagnosticos = (Spinner) findViewById(R.id.sp_diagnosticos);
        sp_diagnosticos_text = (TextView) findViewById(R.id.sp_diagnosticos_error);
        sp_procedimientos = (Spinner) findViewById(R.id.sp_procedimientos);
        sp_procedimientos_text = (TextView) findViewById(R.id.sp_procedimientos_error);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.cargando));

        try { // editar
            proceso = (Proceso) getIntent().getExtras().getSerializable("proceso");
            et_anamnesis.setText(proceso.getAnamnesis());
            et_observaciones.setText(proceso.getObservaciones());
            cargarDiagnosticos(sp_diagnosticos, proceso);
            cargarProcedimientos(sp_procedimientos, proceso);
            cargarGDiagnosticos(sp_Gdiagnosticos);
            editando = true;
        } catch (Exception e) { // nuevo
            Log.d(TAG, getString(R.string.creando_nuevo_registro));
            cargarDiagnosticos(sp_diagnosticos, proceso); // proceso tiene id=0.
            cargarProcedimientos(sp_procedimientos, proceso);
            cargarGDiagnosticos(sp_Gdiagnosticos);
        }

        sp_diagnosticos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                diagnostico = (Diagnostico) adapterView.getAdapter().getItem(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        sp_procedimientos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                procedimiento = (Procedimiento) adapterView.getAdapter().getItem(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        sp_Gdiagnosticos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                gDiagnostico = (Grupodiagnostico) adapterView.getAdapter().getItem(i);
                if(gDiagnostico.getId() != 0)
                    cargarDiagnosticosByGDiagnostico(sp_diagnosticos, proceso);
                else
                    cargarDiagnosticos(sp_diagnosticos, proceso);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
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
        if (editando) {
            Intent i = new Intent(this, CurasActivity.class);
            i.putExtra("proceso", proceso);
            startActivity(i);
        } else {
            Intent i = new Intent(this, ProcesosActivity.class);
            i.putExtra("paciente", paciente);
            startActivity(i);
        }
    }

    @Override
    public void onBackPressed() {
        volverAtras();
    }

    /**
     * Intenta guardar: Si hay errores de formulario (campo no válido, campos faltantes, etc.), se
     * presentan los errores y no se guarda nada, porque ni siquiera se envía nada al servidor.
     */
    private void intentarGuardar() {
        et_anamnesis.setError(null);
        et_observaciones.setError(null);
        sp_diagnosticos_text.setError(null);
        sp_procedimientos_text.setError(null);

        //tomo el contenido de los campos
        String anamnesis = et_anamnesis.getText().toString();
        String observaciones = et_observaciones.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Validar listado de Procedimientos
        if(procedimiento.getId() == 0 || procedimiento == null) {
            sp_procedimientos_text.setError(getString(R.string.debes_seleccionar_procedimiento));
            focusView = sp_procedimientos_text;
            cancel = true;
        }

        // Validar listado de Diagnósticos
        if(diagnostico.getId() == 0 || diagnostico == null) {
            sp_diagnosticos_text.setError(getString(R.string.debes_seleccionar_diagnostico));
            focusView = sp_diagnosticos_text;
            cancel = true;
        }

        if (cancel) {
            // Se ha producido un error: no se intenta el registro y se focaliza en el
            // primer campo del formulario con error.
            focusView.requestFocus();
        } else {
            // ha ido bien, luego se procede a crear o editar.
            Proceso p = new Proceso(anamnesis, diagnostico, procedimiento, observaciones, paciente);
            if(editando)
                editar(p);
            else
                nuevo(p);
        }
    }

    private void editar(Proceso p) {
        progressDialog.show();
        Call<Proceso> call = MyApiAdapter.getApiService().editarProceso(proceso.getId(), p, Pref.getToken());
        call.enqueue(new Callback<Proceso>() {
            @Override
            public void onResponse(Call<Proceso> call, Response<Proceso> response) {
                progressDialog.cancel();
                if(response.isSuccessful()) {
                    proceso = response.body();
                    Toasty.success(ProcesoNewEditActivity.this, getString(R.string.editado_registro),
                            Toast.LENGTH_SHORT, true).show();
                    Intent intent = new Intent(ProcesoNewEditActivity.this, CurasActivity.class);
                    intent.putExtra("proceso", proceso);
                    startActivity(intent);
                } else {
                    if (response.errorBody().contentType().subtype().equals("json")) {
                        ApiError apiError = ApiError.fromResponseBody(response.errorBody());
                        Toasty.error(ProcesoNewEditActivity.this, apiError.getMessage(),
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
            public void onFailure(Call<Proceso> call, Throwable t) {
                progressDialog.cancel();

                if (t instanceof IOException) {
                    Toasty.warning(ProcesoNewEditActivity.this, getString(R.string.error_conexion_red),
                            Toast.LENGTH_LONG, true).show();
                } else {
                    Toasty.error(ProcesoNewEditActivity.this, getString(R.string.error_conversion),
                            Toast.LENGTH_LONG, true).show();
                    Log.d(TAG, getString(R.string.error_conversion));
                }
            }
        });
    }

    private void nuevo(Proceso p) {
        progressDialog.show();
        Call<Proceso> call = MyApiAdapter.getApiService().crearProceso(Pref.getUserId(), p, Pref.getToken());
        call.enqueue(new Callback<Proceso>() {
            @Override
            public void onResponse(Call<Proceso> call, Response<Proceso> response) {
                progressDialog.cancel();
                if(response.isSuccessful()) {
                    Toasty.success(ProcesoNewEditActivity.this, getString(R.string.creado_registro),
                            Toast.LENGTH_SHORT, true).show();
                    Intent intent = new Intent(ProcesoNewEditActivity.this, ProcesosActivity.class);
                    intent.putExtra("paciente", paciente);
                    startActivity(intent);
                } else {
                    if (response.errorBody().contentType().subtype().equals("json")) {
                        ApiError apiError = ApiError.fromResponseBody(response.errorBody());
                        Toasty.error(ProcesoNewEditActivity.this, apiError.getMessage(),
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
            public void onFailure(Call<Proceso> call, Throwable t) {
                progressDialog.cancel();

                if (t instanceof IOException) {
                    Toasty.warning(ProcesoNewEditActivity.this, getString(R.string.error_conexion_red),
                            Toast.LENGTH_LONG, true).show();
                } else {
                    Toasty.error(ProcesoNewEditActivity.this, getString(R.string.error_conversion),
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
                        ArrayAdapter<Grupodiagnostico> arrayAdapter = new ArrayAdapter<Grupodiagnostico>(ProcesoNewEditActivity.this,
                                android.R.layout.simple_spinner_dropdown_item, gDiagnosticos);

                        Log.d("GRUPOS DIAGNOSTICOS", "Tamaño ==> " + gDiagnosticos.size());
                        sp_Gdiagnosticos.setAdapter(arrayAdapter);
                    }
                } else {
                    if (response.errorBody().contentType().subtype().equals("json")) {
                        ApiError apiError = ApiError.fromResponseBody(response.errorBody());
                        Toasty.error(ProcesoNewEditActivity.this, apiError.getMessage(),
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
                    Toasty.warning(ProcesoNewEditActivity.this, getString(R.string.error_conexion_red),
                            Toast.LENGTH_LONG, true).show();
                } else {
                    Toasty.error(ProcesoNewEditActivity.this, getString(R.string.error_conversion),
                            Toast.LENGTH_LONG, true).show();
                    Log.d(TAG, getString(R.string.error_conversion));
                }
            }
        });
    }

    private void cargarDiagnosticosByGDiagnostico(final Spinner sp_diagnosticos, final Proceso proceso) {
        Call<ArrayList<Diagnostico>> call = MyApiAdapter.getApiService().getDiagnosticosByGId(gDiagnostico.getId(),
                Pref.getToken());
        call.enqueue(new Callback<ArrayList<Diagnostico>>() {
            @Override
            public void onResponse(Call<ArrayList<Diagnostico>> call, Response<ArrayList<Diagnostico>> response) {
                progressDialog.cancel();
                if(response.isSuccessful()) {
                    ArrayList<Diagnostico> diagnosticos = response.body();

                    if(diagnosticos != null) {
                        diagnosticos.add(0, new Diagnostico(getString(R.string.seleccione_diagnostico)));
                        ArrayAdapter<Diagnostico> arrayAdapter = new ArrayAdapter<Diagnostico>(ProcesoNewEditActivity.this,
                                android.R.layout.simple_spinner_dropdown_item, diagnosticos);

                        Log.d("DIAGNOSTICOS", "Tamaño ==> " + diagnosticos.size());
                        sp_diagnosticos.setAdapter(arrayAdapter);
                        if(editando) {
                            sp_diagnosticos.setSelection(diagnosticos.indexOf(proceso.getDiagnostico()));
                        }
                    }
                } else {
                    if (response.errorBody().contentType().subtype().equals("json")) {
                        ApiError apiError = ApiError.fromResponseBody(response.errorBody());
                        Toasty.error(ProcesoNewEditActivity.this, apiError.getMessage(),
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
            public void onFailure(Call<ArrayList<Diagnostico>> call, Throwable t) {
                progressDialog.cancel();

                if (t instanceof IOException) {
                    Toasty.warning(ProcesoNewEditActivity.this, getString(R.string.error_conexion_red),
                            Toast.LENGTH_LONG, true).show();
                } else {
                    Toasty.error(ProcesoNewEditActivity.this, getString(R.string.error_conversion),
                            Toast.LENGTH_LONG, true).show();
                    Log.d(TAG, getString(R.string.error_conversion));
                }
            }
        });
    }

    private void cargarDiagnosticos(final Spinner sp_diagnosticos, final Proceso proceso) {
        progressDialog.show();
        Call<ArrayList<Diagnostico>> call = MyApiAdapter.getApiService().getDiagnosticos(Pref.getToken());
        call.enqueue(new Callback<ArrayList<Diagnostico>>() {
            @Override
            public void onResponse(Call<ArrayList<Diagnostico>> call, Response<ArrayList<Diagnostico>> response) {
                progressDialog.cancel();
                if(response.isSuccessful()) {
                    ArrayList<Diagnostico> diagnosticos = response.body();

                    if(diagnosticos != null) {
                        diagnosticos.add(0, new Diagnostico(getString(R.string.seleccione_diagnostico)));
                        ArrayAdapter<Diagnostico> arrayAdapter = new ArrayAdapter<Diagnostico>(ProcesoNewEditActivity.this,
                                android.R.layout.simple_spinner_dropdown_item, diagnosticos);

                        Log.d("DIAGNOSTICOS", "Tamaño ==> " + diagnosticos.size());
                        sp_diagnosticos.setAdapter(arrayAdapter);
                        if(editando) {
                            sp_diagnosticos.setSelection(diagnosticos.indexOf(proceso.getDiagnostico()));
                        }
                    }
                } else {
                    if (response.errorBody().contentType().subtype().equals("json")) {
                        ApiError apiError = ApiError.fromResponseBody(response.errorBody());
                        Toasty.error(ProcesoNewEditActivity.this, apiError.getMessage(),
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
            public void onFailure(Call<ArrayList<Diagnostico>> call, Throwable t) {
                progressDialog.cancel();

                if (t instanceof IOException) {
                    Toasty.warning(ProcesoNewEditActivity.this, getString(R.string.error_conexion_red),
                            Toast.LENGTH_LONG, true).show();
                } else {
                    Toasty.error(ProcesoNewEditActivity.this, getString(R.string.error_conversion),
                            Toast.LENGTH_LONG, true).show();
                    Log.d(TAG, getString(R.string.error_conversion));
                }
            }
        });
    }

    private void cargarProcedimientos(final Spinner sp_procedimientos, final Proceso proceso) {
        Call<ArrayList<Procedimiento>> call = MyApiAdapter.getApiService().getProcedimientos(Pref.getToken());
        call.enqueue(new Callback<ArrayList<Procedimiento>>() {
            @Override
            public void onResponse(Call<ArrayList<Procedimiento>> call, Response<ArrayList<Procedimiento>> response) {
                progressDialog.cancel();
                if(response.isSuccessful()) {
                    ArrayList<Procedimiento> procedimientos = response.body();

                    if(procedimientos != null) {
                        procedimientos.add(0, new Procedimiento(getString(R.string.seleccione_procedimiento)));
                        ArrayAdapter<Procedimiento> arrayAdapter = new ArrayAdapter<Procedimiento>(ProcesoNewEditActivity.this,
                                android.R.layout.simple_spinner_dropdown_item, procedimientos);

                        Log.d("PROCEDIMIENTOS", "Tamaño ==> " + procedimientos.size());
                        sp_procedimientos.setAdapter(arrayAdapter);
                        if(editando) {
                            sp_procedimientos.setSelection(procedimientos.indexOf(proceso.getProcedimiento()));
                        }
                    }
                } else {
                    if (response.errorBody().contentType().subtype().equals("json")) {
                        ApiError apiError = ApiError.fromResponseBody(response.errorBody());
                        Toasty.error(ProcesoNewEditActivity.this, apiError.getMessage(),
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
            public void onFailure(Call<ArrayList<Procedimiento>> call, Throwable t) {
                progressDialog.cancel();

                if (t instanceof IOException) {
                    Toasty.warning(ProcesoNewEditActivity.this, getString(R.string.error_conexion_red),
                            Toast.LENGTH_LONG, true).show();
                } else {
                    Toasty.error(ProcesoNewEditActivity.this, getString(R.string.error_conversion),
                            Toast.LENGTH_LONG, true).show();
                    Log.d(TAG, getString(R.string.error_conversion));
                }
            }
        });
    }
}
