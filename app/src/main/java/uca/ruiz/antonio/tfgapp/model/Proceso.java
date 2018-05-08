package uca.ruiz.antonio.tfgapp.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


public class Proceso implements Serializable {
    private long id;
    private String anamnesis;
    private String diagnostico;
    private String tipo;
    private List<Cura> curas;
    private String observaciones;
    private Date creacion;

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

    public String getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
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
}
