package uca.ruiz.antonio.tfgapp.data.api.model;

import java.io.Serializable;

import static uca.ruiz.antonio.tfgapp.R.string.direccion;
import static uca.ruiz.antonio.tfgapp.R.string.telefono;


public class Grupodiagnostico implements Serializable {
    private long id;
    private String nombre;

    public Grupodiagnostico() {
    }

    public Grupodiagnostico(String nombre) {
        this.nombre = nombre;
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

    @Override
    public String toString() {
        return nombre;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Grupodiagnostico that = (Grupodiagnostico) o;

        if (id != that.id) return false;
        return nombre.equals(that.nombre);

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + nombre.hashCode();
        return result;
    }
}
