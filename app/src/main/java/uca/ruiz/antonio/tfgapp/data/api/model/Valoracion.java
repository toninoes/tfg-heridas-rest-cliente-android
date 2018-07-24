package uca.ruiz.antonio.tfgapp.data.api.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 * Created by toni on 24/07/2018.
 */

public class Valoracion implements Serializable {
    private long id;
    private Date fecha;
    private Sanitario sanitario;
    private Double nota;
    private String observaciones;

    public Valoracion() {
        super();
    }

    public Valoracion(Sanitario sanitario, Double nota, String observaciones) {
        super();
        this.sanitario = sanitario;
        this.nota = nota;
        this.observaciones = observaciones;
    }

    public Sanitario getSanitario() {
        return sanitario;
    }

    public void setSanitario(Sanitario sanitario) {
        this.sanitario = sanitario;
    }

    public Double getNota() {
        return nota;
    }

    public void setNota(Double nota) {
        this.nota = nota;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public long getId() {
        return id;
    }

    public Date getFecha() {
        return fecha;
    }

}
