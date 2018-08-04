package uca.ruiz.antonio.tfgapp.data.api.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


public class Proceso implements Serializable {
    private long id;
    private String anamnesis;
    private Diagnostico diagnostico;
    private Procedimiento procedimiento;
    private Paciente paciente;
    private List<Cura> curas;
    private String observaciones;
    private Date creacion;


    public Proceso() {
    }

    public Proceso(String anamnesis, Diagnostico diagnostico, Procedimiento procedimiento,
                   String observaciones, Paciente paciente) {
        this.anamnesis = anamnesis;
        this.diagnostico = diagnostico;
        this.procedimiento = procedimiento;
        this.observaciones = observaciones;
        this.paciente = paciente;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAnamnesis() {
        return anamnesis;
    }

    public void setAnamnesis(String anamnesis) {
        this.anamnesis = anamnesis;
    }

    public Diagnostico getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(Diagnostico diagnostico) {
        this.diagnostico = diagnostico;
    }

    public List<Cura> getCuras() {
        return curas;
    }

    public void setCuras(List<Cura> curas) {
        this.curas = curas;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Date getCreacion() {
        return creacion;
    }

    public void setCreacion(Date creacion) {
        this.creacion = creacion;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public Procedimiento getProcedimiento() {
        return procedimiento;
    }

    public void setProcedimiento(Procedimiento procedimiento) {
        this.procedimiento = procedimiento;
    }

    @Override
    public String toString() {
        return "Proceso{" +
                "paciente=" + paciente +
                ", creacion=" + creacion +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Proceso proceso = (Proceso) o;

        if (id != proceso.id) return false;
        if (anamnesis != null ? !anamnesis.equals(proceso.anamnesis) : proceso.anamnesis != null)
            return false;
        if (diagnostico != null ? !diagnostico.equals(proceso.diagnostico) : proceso.diagnostico != null)
            return false;
        if (procedimiento != null ? !procedimiento.equals(proceso.procedimiento) : proceso.procedimiento != null)
            return false;
        if (paciente != null ? !paciente.equals(proceso.paciente) : proceso.paciente != null)
            return false;
        if (curas != null ? !curas.equals(proceso.curas) : proceso.curas != null) return false;
        if (observaciones != null ? !observaciones.equals(proceso.observaciones) : proceso.observaciones != null)
            return false;
        return creacion != null ? creacion.equals(proceso.creacion) : proceso.creacion == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (anamnesis != null ? anamnesis.hashCode() : 0);
        result = 31 * result + (diagnostico != null ? diagnostico.hashCode() : 0);
        result = 31 * result + (procedimiento != null ? procedimiento.hashCode() : 0);
        result = 31 * result + (paciente != null ? paciente.hashCode() : 0);
        result = 31 * result + (curas != null ? curas.hashCode() : 0);
        result = 31 * result + (observaciones != null ? observaciones.hashCode() : 0);
        result = 31 * result + (creacion != null ? creacion.hashCode() : 0);
        return result;
    }
}
