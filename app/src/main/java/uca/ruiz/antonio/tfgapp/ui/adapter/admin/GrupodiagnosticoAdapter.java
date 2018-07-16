package uca.ruiz.antonio.tfgapp.ui.adapter.admin;

import android.app.AlertDialog;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uca.ruiz.antonio.tfgapp.R;
import uca.ruiz.antonio.tfgapp.data.api.io.MyApiAdapter;
import uca.ruiz.antonio.tfgapp.data.api.mapping.ApiError;
import uca.ruiz.antonio.tfgapp.data.api.model.Grupodiagnostico;
import uca.ruiz.antonio.tfgapp.ui.activity.admin.GrupodiagnosticoActivity;
import uca.ruiz.antonio.tfgapp.ui.activity.admin.GrupodiagnosticoNewEditActivity;
import uca.ruiz.antonio.tfgapp.utils.Pref;

import static android.content.ContentValues.TAG;


public class GrupodiagnosticoAdapter extends RecyclerView.Adapter<GrupodiagnosticoAdapter.ViewHolder> {

    private ArrayList<Grupodiagnostico> mDataSet;
    private Context context;

    // Obtener referencias de los componentes visuales para cada elemento
    // es decir, referencias de los EditText, TextView, Button...
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout ll_item;
        private TextView tv_titulo, tv_subtitulo;
        private ImageButton ib_delete;
        private ImageButton ib_edit;

        public ViewHolder(View v) {
            super(v);
            ll_item = (LinearLayout) v.findViewById(R.id.ll_item);
            tv_titulo = (TextView) v.findViewById(R.id.tv_titulo);
            tv_titulo.setMaxLines(2);
            tv_subtitulo = (TextView) v.findViewById(R.id.tv_subtitulo);
            tv_subtitulo.setVisibility(View.GONE);
            ib_delete = (ImageButton) v.findViewById(R.id.ib_delete);
            ib_edit = (ImageButton) v.findViewById(R.id.ib_edit);
        }
    }

    public GrupodiagnosticoAdapter(Context context) {
        this.context = context;
        mDataSet = new ArrayList<>();
    }

    public void setDataSet(ArrayList<Grupodiagnostico> dataSet) {
        mDataSet = dataSet;
        notifyDataSetChanged();
    }

    // Encargado de crear los nuevos objetos ViewHolder necesarios para los elementos de la
    // colección. El LayoutManager invoca este metodo para renderizar cada elemento del RecyclerView
    @Override
    public GrupodiagnosticoAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

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
        Grupodiagnostico grupodiagnostico = mDataSet.get(position);
        holder.tv_titulo.setText(grupodiagnostico.getNombre());

        // click sobre cada elemento
        holder.ll_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = holder.getAdapterPosition();
                Grupodiagnostico grupodiagnostico = mDataSet.get(pos);
                Intent intent = new Intent(context, GrupodiagnosticoActivity.class);
                intent.putExtra("grupodiagnostico", grupodiagnostico);
                context.startActivity(intent);
            }
        });

        // para borrar el elemento
        holder.ib_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                // Establecer un título para el diálogo de alerta
                builder.setTitle(R.string.seleccione_opcion);

                // Preguntar si desea realizar la acción
                builder.setMessage(R.string.quiere_borrar);

                // Establecer listener para el click de los botones de diálogo de alerta
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch(which){
                            case DialogInterface.BUTTON_POSITIVE:
                                // Usuario confirma la acción
                                int pos = holder.getAdapterPosition();
                                Grupodiagnostico grupodiagnostico = mDataSet.get(pos);
                                borrarGrupodiagnostico(grupodiagnostico.getId(), pos);
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

        // para editar un elemento
        holder.ib_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = holder.getAdapterPosition();
                Grupodiagnostico grupodiagnostico = mDataSet.get(pos);
                Intent intent = new Intent(context, GrupodiagnosticoNewEditActivity.class);
                intent.putExtra("grupodiagnostico", grupodiagnostico);
                context.startActivity(intent);
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

    private void borrarGrupodiagnostico(final long id, final int pos) {
        Call<String> call = MyApiAdapter.getApiService().borrarGrupodiagnostico(id, Pref.getToken());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if(response.isSuccessful()) {
                    mDataSet.remove(pos);
                    notifyItemRemoved(pos);
                    Toast.makeText(context, R.string.borrado_registro, Toast.LENGTH_SHORT).show();
                } else {
                    if (response.errorBody().contentType().subtype().equals("json")) {
                        ApiError apiError = ApiError.fromResponseBody(response.errorBody());
                        if (apiError != null) {
                            Toast.makeText(context, apiError.getMessage(), Toast.LENGTH_LONG).show();
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
                Toast.makeText(context, "error :(", Toast.LENGTH_LONG).show();
            }
        });
    }

}
