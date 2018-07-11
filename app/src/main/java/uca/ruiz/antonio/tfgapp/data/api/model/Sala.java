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

    public Sala(String nombre, Centro centro) {
        this.nombre = nombre;
        this.centro = centro;
    }

    public Sala() {

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
}
