package uca.ruiz.antonio.tfgapp.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import uca.ruiz.antonio.tfgapp.R;
import uca.ruiz.antonio.tfgapp.data.api.model.Proceso;
import uca.ruiz.antonio.tfgapp.ui.activity.CurasActivity;
import uca.ruiz.antonio.tfgapp.utils.FechaHoraUtils;


public class ProcesoAdapter extends RecyclerView.Adapter<ProcesoAdapter.ViewHolder> {

    private ArrayList<Proceso> mDataSet;
    private Context context;

    //obtener referencias de los componentes visuales para cada elemento
    // es decir, referencias de los edittext, textviews, buttons
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // en este ejemplo cada elemento consta solo de un titulo
        private TextView textView;
        private LinearLayout ll_item;
        private ViewHolder(TextView tv) {
            super(tv);
            textView = tv;
        }
    }

    public ProcesoAdapter(Context context) {
        this.context = context;
        mDataSet = new ArrayList<>();
    }

    public  void setDataSet(ArrayList<Proceso> dataSet) {
        mDataSet = dataSet;
        notifyDataSetChanged();
    }

    // el layoutmanager invoca este metodo para renderizar cad
    // elemento del recyclerview
    @Override
    public ProcesoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //Creamos una nueva vista
        TextView tv = (TextView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.elemento_listado_simple, parent, false);

        return new ViewHolder(tv);
    }

    //este método reemplaza el contenido de cada view,
    // para cada elemento de la lista
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // obtenemos un elemento del dataset según su posición
        // reemplazamos el contenido de los views según tales datos
        Proceso proceso = mDataSet.get(position);
        String cad = FechaHoraUtils.formatoFechaUI(proceso.getCreacion()) + " " + proceso.getDiagnostico();
        holder.textView.setText(cad);

        // click sobre cada elemento de los procesos
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.textView.setTextColor(ContextCompat.getColor(context, R.color.azul));
                Intent intent = new Intent(context, CurasActivity.class);
                intent.putExtra("proceso", mDataSet.get(position));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        // si es despues de filtrar aqui hay que hacer cosas diferentes,
        // calculando el tamaño de los objetos tras el filtroo
        return mDataSet.size();
    }




}
