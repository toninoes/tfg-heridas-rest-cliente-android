package uca.ruiz.antonio.tfgapp.data.api.model;

import java.util.Date;

/**
 * Created by toni on 09/07/2018.
 */

public class Cita {
    private long id;
    private Paciente paciente;
    private Sala sala;
    private Date fecha;

    public Cita() {
    }

    public Cita(Paciente paciente, Sala sala, Date fecha) {
        this.paciente = paciente;
        this.sala = sala;
        this.fecha = fecha;
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
}
