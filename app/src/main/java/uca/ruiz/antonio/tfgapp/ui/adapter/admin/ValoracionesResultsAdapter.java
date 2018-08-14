package uca.ruiz.antonio.tfgapp.ui.adapter.admin;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import uca.ruiz.antonio.tfgapp.R;

import uca.ruiz.antonio.tfgapp.data.api.model.ValoracionesResults;

import static uca.ruiz.antonio.tfgapp.R.id.tv_texto;


public class ValoracionesResultsAdapter extends RecyclerView.Adapter<ValoracionesResultsAdapter.ViewHolder> {

    private ArrayList<ValoracionesResults> mDataSet;
    private Context context;

    // Obtener referencias de los componentes visuales para cada elemento
    // es decir, referencias de los EditText, TextView, Button...
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_titulo;
        private TextView tv_subtitulo;
        private ImageButton ib_delete;
        private ImageButton ib_edit;

        public ViewHolder(View v) {
            super(v);
            tv_titulo = (TextView) v.findViewById(R.id.tv_titulo);
            tv_subtitulo = (TextView) v.findViewById(R.id.tv_subtitulo);
            ib_delete = (ImageButton) v.findViewById(R.id.ib_delete);
            ib_delete.setVisibility(View.GONE); // no se pueden borrar valoraciones medias
            ib_edit = (ImageButton) v.findViewById(R.id.ib_edit);
            ib_edit.setVisibility(View.GONE); // tampoco editarlas
        }
    }

    public ValoracionesResultsAdapter(Context context) {
        this.context = context;
        mDataSet = new ArrayList<>();
    }

    public void setDataSet(ArrayList<ValoracionesResults> dataSet) {
        mDataSet = dataSet;
        notifyDataSetChanged();
    }

    // Encargado de crear los nuevos objetos ViewHolder necesarios para los elementos de la
    // colección. El LayoutManager invoca este metodo para renderizar cada elemento del RecyclerView
    @Override
    public ValoracionesResultsAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

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
        ValoracionesResults valoracionesResults = mDataSet.get(position);
        holder.tv_titulo.setText((position+1) + " "  + valoracionesResults.getSanitario().getFullName());
        holder.tv_subtitulo.setText(context.getString(R.string.valoracion_media) + ": " +
                String.format(Locale.getDefault(), "%.2f", valoracionesResults.getNotaMedia()));


    }

    // Indica el número de elementos de la colección de datos.
    @Override
    public int getItemCount() {
        // si es despues de filtrar, aqui hay que hacer cosas diferentes,
        // calculando entonces el tamaño del dataset tras el filtrado.
        return mDataSet.size();
    }

}
