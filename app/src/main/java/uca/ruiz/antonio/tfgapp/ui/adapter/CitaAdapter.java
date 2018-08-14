package uca.ruiz.antonio.tfgapp.ui.adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uca.ruiz.antonio.tfgapp.R;
import uca.ruiz.antonio.tfgapp.data.Preferencias;
import uca.ruiz.antonio.tfgapp.data.api.io.MyApiAdapter;
import uca.ruiz.antonio.tfgapp.data.api.mapping.ApiError;
import uca.ruiz.antonio.tfgapp.data.api.model.Cita;
import uca.ruiz.antonio.tfgapp.data.api.model.SalaConfig;
import uca.ruiz.antonio.tfgapp.ui.activity.CitaActivity;
import uca.ruiz.antonio.tfgapp.ui.activity.CitacionesActivity;
import uca.ruiz.antonio.tfgapp.ui.activity.CitacionesEditActivity;
import uca.ruiz.antonio.tfgapp.utils.FechaHoraUtils;
import uca.ruiz.antonio.tfgapp.utils.Pref;

import static uca.ruiz.antonio.tfgapp.R.string.cita;

public class CitaAdapter extends RecyclerView.Adapter<CitaAdapter.ViewHolder> {

    private static final String TAG = CitaAdapter.class.getSimpleName();
    private ArrayList<Cita> mDataSet;
    private Context context;
    private Boolean nuevo = false;
    private Boolean editar = false;
    private Long citaId;
    private Date hoy = FechaHoraUtils.getHoySinTiempo();
    private ProgressDialog progressDialog;

    // Obtener referencias de los componentes visuales para cada elemento
    // es decir, referencias de los EditText, TextView, Button...
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout ll_item;
        private TextView tv_titulo;
        private TextView tv_subtitulo;
        private ImageButton ib_delete;
        private ImageButton ib_edit;

        public ViewHolder(View v) {
            super(v);
            ll_item = (LinearLayout) v.findViewById(R.id.ll_item);
            tv_titulo = (TextView) v.findViewById(R.id.tv_titulo);
            tv_subtitulo = (TextView) v.findViewById(R.id.tv_subtitulo);
            ib_delete = (ImageButton) v.findViewById(R.id.ib_delete);
            ib_edit = (ImageButton) v.findViewById(R.id.ib_edit);
            if(Preferencias.get(v.getContext()).getBoolean("ROLE_SANITARIO", false)){
                ib_delete.setVisibility(View.GONE); // las citas sólo las borran los pacientes
                ib_edit.setVisibility(View.GONE); // ...y la edición lo mismo.
            }

        }
    }

    public CitaAdapter(Context context) {
        this.context = context;
        mDataSet = new ArrayList<>();
    }

    public CitaAdapter(Context context, Boolean nuevo, Boolean editar, Long citaId) {
        this.context = context;
        mDataSet = new ArrayList<>();
        this.nuevo = nuevo;
        this.editar = editar;
        this.citaId = citaId;
    }

    public void setDataSet(ArrayList<Cita> dataSet) {
        mDataSet = dataSet;
        notifyDataSetChanged();
    }

    // Encargado de crear los nuevos objetos ViewHolder necesarios para los elementos de la
    // colección. El LayoutManager invoca este metodo para renderizar cada elemento del RecyclerView
    @Override
    public CitaAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View elemento = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.elemento_listado_crud, viewGroup, false);

        return new ViewHolder(elemento);
    }

    // Encargado de actualizar los datos de un ViewHolder ya existente. Reemplaza el contenido de
    // cada view, para cada elemento de la lista.
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        // obtenemos un elemento del dataset según su posición
        // reemplazamos el contenido de los views según tales datos
        Cita cita = mDataSet.get(position);

        SalaConfig sC = cita.getSala().getSalaConfig();
        Date diaCita = cita.getFecha();
        Long orden = cita.getOrden();
        Integer horaini = sC.getHoraini();
        Integer minini = sC.getMinini();
        Long minutos_paciente = sC.getMinutosPaciente();

        if(diaCita.before(hoy)) {
            holder.ll_item.setBackgroundResource(R.color.grisFondoLL);
            holder.ib_edit.setVisibility(View.GONE);
            holder.ib_delete.setVisibility(View.GONE);
        } else if (nuevo || editar) {
            holder.ib_edit.setVisibility(View.GONE);
            holder.ib_delete.setVisibility(View.GONE);
        } else {
            holder.ll_item.setBackgroundResource(R.color.blanco);
        }

        Date fechaHoraCita = FechaHoraUtils.calcularFechaHoraCita(diaCita, horaini, minini, orden,
                minutos_paciente);

        final String titulo, subtitulo;
        if(Preferencias.get(context).getBoolean("ROLE_PACIENTE", false)){
            titulo = FechaHoraUtils.formatoFechaHoraUI(fechaHoraCita) + " (" + orden + ")";
            subtitulo = cita.getSala().getCentro().getNombre() + " (" + cita.getSala().getNombre() + ")";
        } else {
            titulo = cita.getPaciente().getFullName();
            subtitulo = context.getString(R.string.orden) + ": " + orden + " - " +
                    context.getString(R.string.hora) + ": " + FechaHoraUtils.formatoHoraUI(fechaHoraCita);
        }

        holder.tv_titulo.setText(titulo);
        holder.tv_subtitulo.setText(subtitulo);

        // click sobre cada elemento
        holder.ll_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = holder.getAdapterPosition();
                final Cita cita = mDataSet.get(pos);

                if((nuevo || editar) && Preferencias.get(context).getBoolean("ROLE_PACIENTE", false)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);

                    // Establecer un título para el diálogo de alerta
                    builder.setTitle(R.string.seleccione_opcion);
                    // Preguntar si desea realizar la acción
                    builder.setMessage(context.getString(R.string.quiere_reservar_cita) +
                            "\n\n" + titulo);

                    // Establecer listener para el click de los botones de diálogo de alerta
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch(which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    // Usuario confirma la acción
                                    if(nuevo)
                                        intentarReservarCita(cita);
                                    else if (editar)
                                        intentarEditarCita(cita);
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    // Usuario no confirma la acción
                                    dialog.cancel();
                                    break;
                            }
                        }
                    };

                    // Establecer el mensaje sobre el botón BUTTON_POSITIVE
                    builder.setPositiveButton(R.string.si, dialogClickListener);

                    // Establecer el mensaje sobre el botón BUTTON_NEGATIVE
                    builder.setNegativeButton(R.string.no,dialogClickListener);

                    AlertDialog dialog = builder.create();
                    // Mostrar el diálogo de alerta
                    dialog.show();

                } else {
                    if(!cita.getFecha().before(hoy)) {
                        Intent intent = new Intent(context, CitaActivity.class);
                        intent.putExtra("cita", cita);
                        context.startActivity(intent);
                    }
                }
            }
        });

        // para editar la cita
        holder.ib_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = holder.getAdapterPosition();
                final Cita cita = mDataSet.get(pos);
                Intent intent = new Intent(context, CitacionesEditActivity.class);
                intent.putExtra("cita", cita);
                context.startActivity(intent);
            }
        });

        // para borrar la cita
        holder.ib_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                // Establecer un título para el diálogo de alerta
                builder.setTitle(R.string.seleccione_opcion);

                // Preguntar si desea realizar la acción
                builder.setMessage(context.getString(R.string.quiere_borrar_cita) +
                        "\n\n" + titulo);

                // Establecer listener para el click de los botones de diálogo de alerta
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch(which){
                            case DialogInterface.BUTTON_POSITIVE:
                                // Usuario confirma la acción
                                int pos = holder.getAdapterPosition();
                                Cita cita = mDataSet.get(pos);
                                borrarCita(cita.getId(), pos);
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                // Usuario no confirma la acción
                                dialog.cancel();
                                break;
                        }
                    }
                };

                // Establecer el mensaje sobre el botón BUTTON_POSITIVE
                builder.setPositiveButton(R.string.si, dialogClickListener);

                // Establecer el mensaje sobre el botón BUTTON_NEGATIVE
                builder.setNegativeButton(R.string.no,dialogClickListener);

                AlertDialog dialog = builder.create();
                // Mostrar el diálogo de alerta
                dialog.show();
            }
        });

    }

    private void intentarReservarCita(Cita cita) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(context.getString(R.string.cargando));
        progressDialog.show();

        Call<Cita> call = MyApiAdapter.getApiService().crearCita(cita, Pref.getToken());
        call.enqueue(new Callback<Cita>() {
            @Override
            public void onResponse(Call<Cita> call, Response<Cita> response) {
                progressDialog.cancel();
                if(response.isSuccessful()) {
                    Toasty.success(context, context.getString(R.string.cita_reservada),
                            Toast.LENGTH_SHORT, true).show();
                    Intent intent = new Intent(context, CitacionesActivity.class);
                    context.startActivity(intent);
                } else {
                    if (response.errorBody().contentType().subtype().equals("json")) {
                        ApiError apiError = ApiError.fromResponseBody(response.errorBody());
                        Toasty.error(context, apiError.getMessage(),
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
            public void onFailure(Call<Cita> call, Throwable t) {
                progressDialog.cancel();

                if (t instanceof IOException) {
                    Toasty.warning(context, context.getString(R.string.error_conexion_red),
                            Toast.LENGTH_LONG, true).show();
                } else {
                    Toasty.error(context, context.getString(R.string.error_conversion),
                            Toast.LENGTH_LONG, true).show();
                    Log.d(TAG, context.getString(R.string.error_conversion));
                }
            }
        });

    }

    private void intentarEditarCita(Cita c) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(context.getString(R.string.cargando));
        progressDialog.show();

        Call<Cita> call = MyApiAdapter.getApiService().editarCita(citaId, c, Pref.getToken());
        call.enqueue(new Callback<Cita>() {
            @Override
            public void onResponse(Call<Cita> call, Response<Cita> response) {
                progressDialog.cancel();
                if(response.isSuccessful()) {
                    Toasty.success(context, context.getString(R.string.cita_editada),
                            Toast.LENGTH_SHORT, true).show();
                    Intent intent = new Intent(context, CitacionesActivity.class);
                    context.startActivity(intent);
                } else {
                    if (response.errorBody().contentType().subtype().equals("json")) {
                        ApiError apiError = ApiError.fromResponseBody(response.errorBody());
                        Toasty.error(context, apiError.getMessage(),
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
            public void onFailure(Call<Cita> call, Throwable t) {
                progressDialog.cancel();

                if (t instanceof IOException) {
                    Toasty.warning(context, context.getString(R.string.error_conexion_red),
                            Toast.LENGTH_LONG, true).show();
                } else {
                    Toasty.error(context, context.getString(R.string.error_conversion),
                            Toast.LENGTH_LONG, true).show();
                    Log.d(TAG, context.getString(R.string.error_conversion));
                }
            }
        });
    }

    // Indica el número de elementos de la colección de datos.
    @Override
    public int getItemCount() {
        // si es despues de filtrar, aqui hay que hacer cosas diferentes,
        // calculando entonces el tamaño del dataset tras el filtrado.
        return mDataSet.size();
    }


    private void borrarCita(final long id, final int pos) {
        Call<String> call = MyApiAdapter.getApiService().borrarCita(id, Pref.getToken());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if(response.isSuccessful()) {
                    mDataSet.remove(pos);
                    notifyItemRemoved(pos);
                    Toasty.success(context, context.getString(R.string.borrado_registro),
                            Toast.LENGTH_SHORT, true).show();
                } else {
                    if (response.errorBody().contentType().subtype().equals("json")) {
                        ApiError apiError = ApiError.fromResponseBody(response.errorBody());
                        if (apiError != null) {
                            Toasty.error(context, apiError.getMessage(), Toast.LENGTH_LONG, true).show();
                            Log.d(TAG, apiError.getPath() + " " + apiError.getMessage());
                        }

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
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                if (t instanceof IOException) {
                    Toasty.warning(context, context.getString(R.string.error_conexion_red),
                            Toast.LENGTH_LONG, true).show();
                } else {
                    Toasty.error(context, context.getString(R.string.error_conversion),
                            Toast.LENGTH_LONG, true).show();
                    Log.d(TAG, context.getString(R.string.error_conversion));
                }
            }
        });
    }


}
