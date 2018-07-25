package uca.ruiz.antonio.tfgapp.data.api.model;

import java.io.Serializable;


public class Centro implements Serializable {
    private long id;
    private String nombre;
    private String direccion;
    private String telefono;

    public Centro() {
    }

    public Centro(String nombre) {
        this.nombre = nombre;
    }

    public Centro(String nombre, String direccion, String telefono) {
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
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

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    @Override
    public String toString() {
        return nombre;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Centro centro = (Centro) o;

        if (id != centro.id) return false;
        if (!nombre.equals(centro.nombre)) return false;
        if (!direccion.equals(centro.direccion)) return false;
        return telefono != null ? telefono.equals(centro.telefono) : centro.telefono == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + nombre.hashCode();
        result = 31 * result + direccion.hashCode();
        result = 31 * result + (telefono != null ? telefono.hashCode() : 0);
        return result;
    }
}
