package uca.ruiz.antonio.tfgapp.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import uca.ruiz.antonio.tfgapp.R;
import uca.ruiz.antonio.tfgapp.data.Preferencias;
import uca.ruiz.antonio.tfgapp.data.api.model.Cita;
import uca.ruiz.antonio.tfgapp.data.api.model.SalaConfig;
import uca.ruiz.antonio.tfgapp.utils.FechaHoraUtils;

import static uca.ruiz.antonio.tfgapp.R.id.ll_item;

public class CitaAdapter extends RecyclerView.Adapter<CitaAdapter.ViewHolder> {

    private ArrayList<Cita> mDataSet;
    private Context context;

    private Date hoy = FechaHoraUtils.getHoySinTiempo();

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
            ib_delete.setVisibility(View.GONE); // no borrar citas desde aquí
            ib_edit = (ImageButton) v.findViewById(R.id.ib_edit);
            ib_edit.setVisibility(View.GONE); // no editar citas desde aquí
        }
    }

    public CitaAdapter(Context context) {
        this.context = context;
        mDataSet = new ArrayList<>();
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
        Date dia = cita.getFecha();
        Long orden = cita.getOrden();
        Integer horaini = sC.getHoraini();
        Integer minini = sC.getMinini();
        Long minutos_paciente = sC.getMinutosPaciente();


        if(dia.before(hoy)) {
            holder.ll_item.setBackgroundResource(R.color.grisFondoLL);
        } else {
            holder.ll_item.setBackgroundResource(R.color.blanco);
        }

        Date fechaHoraCita = calcularFechaHoraCita(dia, horaini, minini, orden, minutos_paciente);

        String titulo = "";
        String subtitulo = "";
        if(Preferencias.get(context).getBoolean("ROLE_PACIENTE", false)){
            titulo = FechaHoraUtils.formatoFechaHoraUI(fechaHoraCita) + " (" + orden + ")";
            subtitulo = cita.getSala().getCentro().getNombre() + " (" + cita.getSala().getNombre() + ")";
        } else if(Preferencias.get(context).getBoolean("ROLE_SANITARIO", false)) {
            titulo = cita.getPaciente().getLastnameComaAndFirstname();
            subtitulo = context.getString(R.string.orden) + ": " + orden + " - " +
                    context.getString(R.string.hora) + ": " + FechaHoraUtils.formatoHoraUI(fechaHoraCita);
        }

        holder.tv_titulo.setText(titulo);
        holder.tv_subtitulo.setText(subtitulo);

        // click sobre cada elemento para ver más detalles de la cita
        /*holder.ll_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = holder.getAdapterPosition();
                Cita cita = mDataSet.get(pos);
                Intent intent = new Intent(context, PacienteActivity.class);
                intent.putExtra("cita", cita);
                context.startActivity(intent);
            }
        });*/

    }

    private Date calcularFechaHoraCita(Date dia, Integer horaini, Integer minini, Long orden, Long minutos_paciente) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dia);
        cal.set(Calendar.HOUR, horaini);
        cal.set(Calendar.MINUTE, minini);

        Integer sumar = (orden.intValue()-1) * minutos_paciente.intValue();
        cal.add(Calendar.MINUTE, sumar);

        return cal.getTime();
    }

    // Indica el número de elementos de la colección de datos.
    @Override
    public int getItemCount() {
        // si es despues de filtrar, aqui hay que hacer cosas diferentes,
        // calculando entonces el tamaño del dataset tras el filtrado.
        return mDataSet.size();
    }

}
