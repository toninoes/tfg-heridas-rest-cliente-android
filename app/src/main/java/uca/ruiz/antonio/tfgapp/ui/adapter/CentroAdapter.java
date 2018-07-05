package uca.ruiz.antonio.tfgapp.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import uca.ruiz.antonio.tfgapp.R;
import uca.ruiz.antonio.tfgapp.data.api.model.Centro;
import uca.ruiz.antonio.tfgapp.ui.activity.CurasActivity;
import uca.ruiz.antonio.tfgapp.utils.FechaHoraUtils;


public class CentroAdapter extends RecyclerView.Adapter<CentroAdapter.ViewHolder> {

    private ArrayList<Centro> mDataSet;
    private Context context;

    //obtener referencias de los componentes visuales para cada elemento
    // es decir, referencias de los edittext, textviews, buttons
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // en este ejemplo cada elemento consta solo de un titulo
        private TextView tv_titulo;
        private TextView tv_subtitulo;
        private ImageButton ib_delete;
        public View layout;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            tv_titulo = (TextView) v.findViewById(R.id.tv_titulo);
            tv_subtitulo = (TextView) v.findViewById(R.id.tv_subtitulo);
            ib_delete = (ImageButton) v.findViewById(R.id.ib_delete);
        }
    }

    public void add(int position, Centro centro) {
        mDataSet.add(position, centro);
        notifyItemInserted(position);
    }

    public void remove(int position) {
        mDataSet.remove(position);
        notifyItemRemoved(position);
    }

    public CentroAdapter(Context context) {
        this.context = context;
        mDataSet = new ArrayList<>();
    }

    public  void setDataSet(ArrayList<Centro> dataSet) {
        mDataSet = dataSet;
        notifyDataSetChanged();
    }

    // el layoutmanager invoca este metodo para renderizar cada
    // elemento del recyclerview
    @Override
    public CentroAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //Creamos una nueva vista
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.elemento_crud_listado_view, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    //este método reemplaza el contenido de cada view,
    // para cada elemento de la lista
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // obtenemos un elemento del dataset según su posición
        // reemplazamos el contenido de los views según tales datos
        Centro centro = mDataSet.get(position);
        holder.tv_titulo.setText(centro.getNombre());
        holder.tv_subtitulo.setText(centro.getDireccion());

        // click sobre cada elemento
        holder.tv_titulo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent intent = new Intent(context, CurasActivity.class);
                intent.putExtra("proceso", mDataSet.get(position));
                context.startActivity(intent);*/
            }
        });

        holder.ib_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                remove(position);
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
