package uca.ruiz.antonio.tfgapp.data.api.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


public class Cuidado implements Serializable {
    private long id;
    private String nombre;
    private String descripcion;
    private Grupodiagnostico grupodiagnostico;
    private Sanitario sanitario;
    private Date creacion;

    public Cuidado() {
        super();
    }

    public Cuidado(String nombre, String descripcion, Grupodiagnostico grupodiagnostico) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.grupodiagnostico = grupodiagnostico;
    }

    public long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Grupodiagnostico getGrupodiagnostico() {
        return grupodiagnostico;
    }

    public void setGrupodiagnostico(Grupodiagnostico grupodiagnostico) {
        this.grupodiagnostico = grupodiagnostico;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Sanitario getSanitario() {
        return sanitario;
    }

    public void setSanitario(Sanitario sanitario) {
        this.sanitario = sanitario;
    }

    public Date getCreacion() {
        return creacion;
    }

    public void setCreacion(Date creacion) {
        this.creacion = creacion;
    }

    @Override
    public String toString() {
        return "Cuidado{" +
                "nombre='" + nombre + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Cuidado cuidado = (Cuidado) o;

        if (id != cuidado.id) return false;
        if (nombre != null ? !nombre.equals(cuidado.nombre) : cuidado.nombre != null) return false;
        if (descripcion != null ? !descripcion.equals(cuidado.descripcion) : cuidado.descripcion != null)
            return false;
        if (grupodiagnostico != null ? !grupodiagnostico.equals(cuidado.grupodiagnostico) : cuidado.grupodiagnostico != null)
            return false;
        if (sanitario != null ? !sanitario.equals(cuidado.sanitario) : cuidado.sanitario != null)
            return false;
        return creacion != null ? creacion.equals(cuidado.creacion) : cuidado.creacion == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (nombre != null ? nombre.hashCode() : 0);
        result = 31 * result + (descripcion != null ? descripcion.hashCode() : 0);
        result = 31 * result + (grupodiagnostico != null ? grupodiagnostico.hashCode() : 0);
        result = 31 * result + (sanitario != null ? sanitario.hashCode() : 0);
        result = 31 * result + (creacion != null ? creacion.hashCode() : 0);
        return result;
    }
}
