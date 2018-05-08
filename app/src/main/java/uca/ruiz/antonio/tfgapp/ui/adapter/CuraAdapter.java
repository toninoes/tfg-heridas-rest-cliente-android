package uca.ruiz.antonio.tfgapp.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import uca.ruiz.antonio.tfgapp.R;
import uca.ruiz.antonio.tfgapp.model.Cura;

public class CuraAdapter extends RecyclerView.Adapter<CuraAdapter.ViewHolder> {

    private ArrayList<Cura> mDataSet;

    //obtener referencias de los componentes visuales para cada elemento
    // es decir, referencias de los edittext, textviews, buttons
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // en este ejemplo cada elemento consta solo de un titulo
        private TextView textView;
        private ViewHolder(TextView tv) {
            super(tv);
            textView = tv;
        }
    }

    public CuraAdapter() {
        mDataSet = new ArrayList<>();
    }

    public  void setDataSet(ArrayList<Cura> dataSet) {
        mDataSet = dataSet;
        notifyDataSetChanged();
    }

    // el layoutmanager invoca este metodo para renderizar cad
    // elemento del recyclerview
    @Override
    public CuraAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //Creamos una nueva vista
        TextView tv = (TextView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.simple_element_view, parent, false);

        return new ViewHolder(tv);
    }

    //este método reemplaza el contenido de cada view,
    // para cada elemento de la lista
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // obtenemos un elemento del dataset según su posición
        // reemplazamos el contenido de los views según tales datos

        holder.textView.setText(mDataSet.get(position).getTratamiento());

    }

    @Override
    public int getItemCount() {
        //si es despues de filtrar aqui hay que hacer cosas diferentes,
        // calculando el tamaño de los objetos tras el filtro
        return mDataSet.size();
    }


}
