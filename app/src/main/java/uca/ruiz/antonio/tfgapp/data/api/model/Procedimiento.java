package uca.ruiz.antonio.tfgapp.data.api.model;

import java.io.Serializable;
import java.util.List;


public class Procedimiento implements Serializable {
    private long id;
    private String codigo;
    private String nombre;
    private List<Proceso> procesos;

    public Procedimiento() {
    }

    public Procedimiento(String codigo, String nombre) {
        this.codigo = codigo;
        this.nombre = nombre;
    }

    public Procedimiento(String nombre) {
        this.nombre = nombre;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<Proceso> getProcesos() {
        return procesos;
    }

    public void setProcesos(List<Proceso> procesos) {
        this.procesos = procesos;
    }

    @Override
    public String toString() {
        return nombre;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Procedimiento that = (Procedimiento) o;

        if (id != that.id) return false;
        if (!codigo.equals(that.codigo)) return false;
        if (!nombre.equals(that.nombre)) return false;
        return procesos != null ? procesos.equals(that.procesos) : that.procesos == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + codigo.hashCode();
        result = 31 * result + nombre.hashCode();
        result = 31 * result + (procesos != null ? procesos.hashCode() : 0);
        return result;
    }
}
