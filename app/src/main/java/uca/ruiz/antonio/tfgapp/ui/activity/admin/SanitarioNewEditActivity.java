package uca.ruiz.antonio.tfgapp.ui.activity.admin;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uca.ruiz.antonio.tfgapp.R;
import uca.ruiz.antonio.tfgapp.data.api.io.MyApiAdapter;
import uca.ruiz.antonio.tfgapp.data.api.mapping.ApiError;
import uca.ruiz.antonio.tfgapp.data.api.mapping.Authority;
import uca.ruiz.antonio.tfgapp.data.api.mapping.UserResponse;
import uca.ruiz.antonio.tfgapp.data.api.model.Centro;
import uca.ruiz.antonio.tfgapp.data.api.model.Sanitario;
import uca.ruiz.antonio.tfgapp.data.api.model.User;
import uca.ruiz.antonio.tfgapp.ui.activity.CuraNewEditActivity;
import uca.ruiz.antonio.tfgapp.utils.FechaHoraUtils;
import uca.ruiz.antonio.tfgapp.utils.Pref;
import uca.ruiz.antonio.tfgapp.utils.Validacion;


public class SanitarioNewEditActivity extends AppCompatActivity {

    private static final String TAG = SanitarioNewEditActivity.class.getSimpleName();
    private EditText et_nombre, et_apellidos, et_dni, et_email, et_fnac, et_colegiado;
    private DatePickerDialog dpd_fnac;
    private CheckBox chk_activo;
    private Sanitario sanitario;
    private Centro centro;
    private Boolean editando = false;
    private ProgressDialog progressDialog;
    private Spinner sp_centros;
    private TextView sp_centros_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sanitario_new_edit);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        et_nombre = (EditText) findViewById(R.id.et_nombre);
        et_apellidos = (EditText) findViewById(R.id.et_apellidos);
        et_dni = (EditText) findViewById(R.id.et_dni);
        et_email = (EditText) findViewById(R.id.et_email);

        et_fnac = (EditText) findViewById(R.id.et_fnac);
        et_fnac.setInputType(InputType.TYPE_NULL);

        et_colegiado = (EditText) findViewById(R.id.et_colegiado);

        chk_activo = (CheckBox) findViewById(R.id.chk_activo);

        sp_centros = (Spinner) findViewById(R.id.sp_centros);
        sp_centros_text = (TextView) findViewById(R.id.sp_centros_error);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.guardando));

        try { // editar
            sanitario = (Sanitario) getIntent().getExtras().getSerializable("sanitario");
            centro = sanitario.getCentroActual();
            et_nombre.setText(sanitario.getFirstname());
            et_apellidos.setText(sanitario.getLastname());
            et_dni.setText(sanitario.getDni());
            et_email.setText(sanitario.getEmail());
            et_email.setEnabled(false);
            et_fnac.setText(FechaHoraUtils.formatoFechaUI(sanitario.getNacimiento()));
            et_colegiado.setText(sanitario.getColegiado().toString());

            if(sanitario.getEnabled())
                chk_activo.setChecked(true);
            else
                chk_activo.setChecked(false);

            cargarCentros(sp_centros, centro);
            editando = true;
        } catch (Exception e) {
            chk_activo.setVisibility(View.GONE);
            Log.d(TAG, getString(R.string.creando_nuevo_registro));
            cargarCentros(sp_centros, centro);
        }

        sp_centros.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                centro = (Centro) adapterView.getAdapter().getItem(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        et_fnac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal;
                int day, month, year;
                try {
                    cal = FechaHoraUtils.DateToCalendar(sanitario.getNacimiento());
                } catch (Exception e) {
                    cal = Calendar.getInstance();
                }

                day = cal.get(Calendar.DAY_OF_MONTH);
                month = cal.get(Calendar.MONTH);
                year = cal.get(Calendar.YEAR);

                // date picker dialog
                dpd_fnac = new DatePickerDialog(SanitarioNewEditActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int day) {
                                // +1 porque enero es cero
                                final String fechaElegida = FechaHoraUtils.dosDigitos(day) + "/" +
                                        FechaHoraUtils.dosDigitos(month+1) + "/" + year;
                                et_fnac.setText(fechaElegida);
                            }
                        }, year, month, day);
                dpd_fnac.show();
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
                startActivity(new Intent(this, SanitariosActivity.class));
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
        // Resetear errores
        et_nombre.setError(null);
        et_apellidos.setError(null);
        et_dni.setError(null);
        et_email.setError(null);
        et_fnac.setError(null);
        et_colegiado.setError(null);
        sp_centros_text.setError(null);

        //tomo el contenido de los campos
        String nombre = et_nombre.getText().toString().trim().toUpperCase();
        String apellidos = et_apellidos.getText().toString().trim().toUpperCase();
        String dni = et_dni.getText().toString().trim().toUpperCase();
        String email = et_email.getText().toString().trim().toLowerCase();
        String fnac = et_fnac.getText().toString().trim();
        String colegiado = et_colegiado.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        // Validar listado de Centros
        if(centro.getId() == 0 || centro == null) {
            sp_centros_text.setError(getString(R.string.debes_seleccionar_centro));
            focusView = sp_centros_text;
            cancel = true;
        }

        // Valida campo Colegiado
        if(Validacion.vacio(colegiado)) {
            et_colegiado.setError(getString(R.string.campo_no_vacio));
            focusView = et_colegiado;
            cancel = true;
        }

        // Valida campo Fnac
        if(Validacion.vacio(fnac)) {
            et_fnac.setError(getString(R.string.campo_no_vacio));
            focusView = et_fnac;
            cancel = true;
        }

        // Valida campo Email.
        if (!Validacion.tamEmail(email)) {
            et_email.setError(getString(R.string.error_tam_email_invalido));
            focusView = et_email;
            cancel = true;
        } else if (!Validacion.formatoEmail(email)) {
            et_email.setError(getString(R.string.error_formato_email_invalido));
            focusView = et_email;
            cancel = true;
        }

        // Valida campo Dni
        if(!Validacion.tamDni(dni)) {
            et_dni.setError(getString(R.string.error_tam_dni));
            focusView = et_dni;
            cancel = true;
        }

        // Valida campo Apellidos
        if(Validacion.vacio(apellidos)) {
            et_apellidos.setError(getString(R.string.campo_no_vacio));
            focusView = et_apellidos;
            cancel = true;
        }

        // Valida campo Nombre
        if(Validacion.vacio(nombre)) {
            et_nombre.setError(getString(R.string.campo_no_vacio));
            focusView = et_nombre;
            cancel = true;
        }

        if (cancel) {
            // Se ha producido un error: no se intenta el registro y se focaliza en el
            // primer campo del formulario con error.
            focusView.requestFocus();
        } else {
            // ha ido bien, luego se procede a crear o editar.
            List<Authority> roles = new ArrayList<Authority>();

            // permisos(0): admin; permisos(1): sanitario; permisos(2): paciente
            ArrayList<Boolean> permisos = new ArrayList<>(Arrays.asList(false, true, false));

            if(editando) {
                Sanitario sanitarioEditado = new Sanitario(email, nombre, apellidos, email, permisos, dni,
                        FechaHoraUtils.getFechaFromString(fnac), colegiado, chk_activo.isChecked());
                sanitarioEditado.setId(sanitario.getId());
                editar(sanitarioEditado);
            } else {
                Sanitario sanitarioNuevo = new Sanitario(email, nombre, apellidos, email, permisos, dni,
                        FechaHoraUtils.getFechaFromString(fnac), colegiado);
                nuevo(sanitarioNuevo);
            }
        }

    }

    private void nuevo(User sanit) {
        progressDialog.show();
        Call<UserResponse> call = MyApiAdapter.getApiService().registro(centro.getId(), sanit, Pref.getToken());
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                progressDialog.cancel();
                if(response.isSuccessful()) {
                    Toasty.success(SanitarioNewEditActivity.this, getString(R.string.creado_registro),
                            Toast.LENGTH_SHORT, true).show();
                    startActivity(new Intent(SanitarioNewEditActivity.this, SanitariosActivity.class));
                } else {
                    if (response.errorBody().contentType().subtype().equals("json")) {
                        ApiError apiError = ApiError.fromResponseBody(response.errorBody());
                        Toasty.error(SanitarioNewEditActivity.this, apiError.getMessage(),
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
            public void onFailure(Call<UserResponse> call, Throwable t) {
                progressDialog.cancel();

                if (t instanceof IOException) {
                    Toasty.warning(SanitarioNewEditActivity.this, getString(R.string.error_conexion_red),
                            Toast.LENGTH_LONG, true).show();
                } else {
                    Toasty.error(SanitarioNewEditActivity.this, getString(R.string.error_conversion),
                            Toast.LENGTH_LONG, true).show();
                    Log.d(TAG, getString(R.string.error_conversion));
                }
            }
        });
    }

    private void editar(User sanit) {
        progressDialog.show();
        Call<User> call = MyApiAdapter.getApiService().editarRegistro(sanitario.getId(),
                centro.getId(), sanit, Pref.getToken());
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                progressDialog.cancel();
                if(response.isSuccessful()) {
                    Toasty.success(SanitarioNewEditActivity.this, getString(R.string.editado_registro),
                            Toast.LENGTH_SHORT, true).show();
                    startActivity(new Intent(SanitarioNewEditActivity.this, SanitariosActivity.class));
                } else {
                    if (response.errorBody().contentType().subtype().equals("json")) {
                        ApiError apiError = ApiError.fromResponseBody(response.errorBody());
                        Toasty.error(SanitarioNewEditActivity.this, apiError.getMessage(),
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
            public void onFailure(Call<User> call, Throwable t) {
                progressDialog.cancel();

                Log.d(TAG, t.getMessage());

                if (t instanceof IOException) {
                    Toasty.warning(SanitarioNewEditActivity.this, getString(R.string.error_conexion_red),
                            Toast.LENGTH_LONG, true).show();
                } else {
                    Toasty.error(SanitarioNewEditActivity.this, getString(R.string.error_conversion),
                            Toast.LENGTH_LONG, true).show();
                    Log.d(TAG, getString(R.string.error_conversion));
                }
            }
        });
    }


    private void cargarCentros(final Spinner sp_centros, final Centro cActual) {
        Call<ArrayList<Centro>> call = MyApiAdapter.getApiService().getCentros(Pref.getToken());
        call.enqueue(new Callback<ArrayList<Centro>>() {
            @Override
            public void onResponse(Call<ArrayList<Centro>> call, Response<ArrayList<Centro>> response) {
                if(response.isSuccessful()) {
                    ArrayList<Centro> centros = response.body();

                    if(centros != null) {
                        centros.add(0, new Centro(getString(R.string.seleccione_centro)));
                        ArrayAdapter<Centro> arrayAdapter = new ArrayAdapter<Centro>(SanitarioNewEditActivity.this,
                                android.R.layout.simple_spinner_dropdown_item, centros);

                        Log.d("CENTROS", "Tamaño ==> " + centros.size());
                        sp_centros.setAdapter(arrayAdapter);
                        if(editando) {
                            try {
                                sp_centros.setSelection(centros.indexOf(cActual));
                            } catch (Exception e) {
                                Log.d("CENTROS_ACTUAL: ", "Ninguno!!");
                            }
                        }
                    }
                } else {
                    if (response.errorBody().contentType().subtype().equals("json")) {
                        ApiError apiError = ApiError.fromResponseBody(response.errorBody());
                        Toasty.error(SanitarioNewEditActivity.this, apiError.getMessage(),
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
            public void onFailure(Call<ArrayList<Centro>> call, Throwable t) {
                progressDialog.cancel();

                if (t instanceof IOException) {
                    Toasty.warning(SanitarioNewEditActivity.this, getString(R.string.error_conexion_red),
                            Toast.LENGTH_LONG, true).show();
                } else {
                    Toasty.error(SanitarioNewEditActivity.this, getString(R.string.error_conversion),
                            Toast.LENGTH_LONG, true).show();
                    Log.d(TAG, getString(R.string.error_conversion));
                }
            }
        });
    }

}
