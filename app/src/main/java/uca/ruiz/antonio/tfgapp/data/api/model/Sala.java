package uca.ruiz.antonio.tfgapp.data.api.model;

import java.io.Serializable;
import java.util.Set;

/**
 * Created by toni on 09/07/2018.
 */

public class Sala implements Serializable {
    private long id;
    private String nombre;
    private Centro centro;
    private Set<Cita> citas;
    private SalaConfig salaConfig;

    public Sala() {
    }

    public Sala(String nombre) {
        this.nombre = nombre;
    }

    public Sala(String nombre, Centro centro) {
        this.nombre = nombre;
        this.centro = centro;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Centro getCentro() {
        return centro;
    }

    public void setCentro(Centro centro) {
        this.centro = centro;
    }

    public Set<Cita> getCitas() {
        return citas;
    }

    public void setCitas(Set<Cita> citas) {
        this.citas = citas;
    }

    public SalaConfig getSalaConfig() {
        return salaConfig;
    }

    public void setSalaConfig(SalaConfig salaConfig) {
        this.salaConfig = salaConfig;
    }

    @Override
    public String toString() {
        return nombre;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Sala sala = (Sala) o;

        if (id != sala.id) return false;
        if (nombre != null ? !nombre.equals(sala.nombre) : sala.nombre != null) return false;
        if (centro != null ? !centro.equals(sala.centro) : sala.centro != null) return false;
        if (citas != null ? !citas.equals(sala.citas) : sala.citas != null) return false;
        return salaConfig != null ? salaConfig.equals(sala.salaConfig) : sala.salaConfig == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (nombre != null ? nombre.hashCode() : 0);
        result = 31 * result + (centro != null ? centro.hashCode() : 0);
        result = 31 * result + (citas != null ? citas.hashCode() : 0);
        result = 31 * result + (salaConfig != null ? salaConfig.hashCode() : 0);
        return result;
    }
}
