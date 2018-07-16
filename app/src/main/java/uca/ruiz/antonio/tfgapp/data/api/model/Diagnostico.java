package uca.ruiz.antonio.tfgapp.data.api.model;

import java.io.Serializable;
import java.util.List;


public class Diagnostico implements Serializable {
    private long id;
    private String codigo;
    private String nombre;
    private List<Proceso> procesos;
    private Grupodiagnostico grupodiagnostico;

    public Diagnostico() {
    }

    public Diagnostico(String codigo, String nombre) {
        this.codigo = codigo;
        this.nombre = nombre;
    }

    public Diagnostico(String codigo, String nombre, Grupodiagnostico grupodiagnostico) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.grupodiagnostico = grupodiagnostico;
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

    public Grupodiagnostico getGrupodiagnostico() {
        return grupodiagnostico;
    }

    public void setGrupodiagnostico(Grupodiagnostico grupodiagnostico) {
        this.grupodiagnostico = grupodiagnostico;
    }

    @Override
    public String toString() {
        return nombre;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Diagnostico that = (Diagnostico) o;

        if (id != that.id) return false;
        if (!codigo.equals(that.codigo)) return false;
        if (!nombre.equals(that.nombre)) return false;
        if (procesos != null ? !procesos.equals(that.procesos) : that.procesos != null)
            return false;
        return grupodiagnostico != null ? grupodiagnostico.equals(that.grupodiagnostico) : that.grupodiagnostico == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + codigo.hashCode();
        result = 31 * result + nombre.hashCode();
        result = 31 * result + (procesos != null ? procesos.hashCode() : 0);
        result = 31 * result + (grupodiagnostico != null ? grupodiagnostico.hashCode() : 0);
        return result;
    }
}
