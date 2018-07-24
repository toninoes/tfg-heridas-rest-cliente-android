package uca.ruiz.antonio.tfgapp.data.api.model;

import java.io.Serializable;

/**
 * Created by toni on 24/07/2018.
 */

public class ValoracionesResults implements Serializable {
    private Double notaMedia;
    private Sanitario sanitario;
    private Long totalNotas;

    public ValoracionesResults(Double notaMedia, Sanitario sanitario, Long totalNotas) {
        this.notaMedia = notaMedia;
        this.sanitario = sanitario;
        this.totalNotas = totalNotas;
    }

    public Double getNotaMedia() {
        return notaMedia;
    }

    public Sanitario getSanitario() {
        return sanitario;
    }

    public Long getTotalNotas() {
        return totalNotas;
    }

    public void setNotaMedia(Double notaMedia) {
        this.notaMedia = notaMedia;
    }

    public void setSanitario(Sanitario sanitario) {
        this.sanitario = sanitario;
    }

    public void setTotalNotas(Long totalNotas) {
        this.totalNotas = totalNotas;
    }
}
