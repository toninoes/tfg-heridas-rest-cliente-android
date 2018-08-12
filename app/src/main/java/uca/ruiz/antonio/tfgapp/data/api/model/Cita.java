package uca.ruiz.antonio.tfgapp.data.api.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by toni on 09/07/2018.
 */

public class Cita implements Serializable {
    private long id;
    private Paciente paciente;
    private Sala sala;
    private Date fecha;
    private Long orden;

    public Cita() {
    }

    public Cita(Sala sala, Date fecha) {
        this.sala = sala;
        this.fecha = fecha;
    }

    public Cita(Paciente paciente, Sala sala, Date fecha) {
        this.paciente = paciente;
        this.sala = sala;
        this.fecha = fecha;
    }

    public Cita(Paciente paciente, Sala sala, Date fecha, Long orden) {
        this.paciente = paciente;
        this.sala = sala;
        this.fecha = fecha;
        this.orden = orden;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public Sala getSala() {
        return sala;
    }

    public void setSala(Sala sala) {
        this.sala = sala;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Long getOrden() {
        return orden;
    }

    public void setOrden(Long orden) {
        this.orden = orden;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Cita cita = (Cita) o;

        if (id != cita.id) return false;
        if (paciente != null ? !paciente.equals(cita.paciente) : cita.paciente != null)
            return false;
        if (sala != null ? !sala.equals(cita.sala) : cita.sala != null) return false;
        if (fecha != null ? !fecha.equals(cita.fecha) : cita.fecha != null) return false;
        return orden != null ? orden.equals(cita.orden) : cita.orden == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (paciente != null ? paciente.hashCode() : 0);
        result = 31 * result + (sala != null ? sala.hashCode() : 0);
        result = 31 * result + (fecha != null ? fecha.hashCode() : 0);
        result = 31 * result + (orden != null ? orden.hashCode() : 0);
        return result;
    }
}
