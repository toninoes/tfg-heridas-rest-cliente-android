package uca.ruiz.antonio.tfgapp.data.api.model;

import java.io.Serializable;
import java.util.Date;


public class Imagen implements Serializable {
    private long id;
    private String nombre;
    private String descripcion;
    private Cura cura;

    private Date creacion;

    public Imagen() {
    }

    public Imagen(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Date getCreacion() {
        return creacion;
    }

    public void setCreacion(Date creacion) {
        this.creacion = creacion;
    }

    public Cura getCura() {
        return cura;
    }

    public void setCura(Cura cura) {
        this.cura = cura;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Imagen imagen = (Imagen) o;

        if (id != imagen.id) return false;
        if (nombre != null ? !nombre.equals(imagen.nombre) : imagen.nombre != null) return false;
        return descripcion != null ? descripcion.equals(imagen.descripcion) : imagen.descripcion == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (nombre != null ? nombre.hashCode() : 0);
        result = 31 * result + (descripcion != null ? descripcion.hashCode() : 0);
        return result;
    }
}
