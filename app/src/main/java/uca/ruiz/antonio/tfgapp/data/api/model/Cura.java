package uca.ruiz.antonio.tfgapp.data.api.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


public class Cura implements Serializable {
    private long id;
    private String evolucion;
    private String tratamiento;
    private String recomendaciones;
    private List<Imagen> imagenes;
    private Date creacion;
    private Proceso proceso;
    private Sanitario sanitario;

    public Cura(String evolucion, String tratamiento, String recomendaciones, Proceso proceso) {
        this.evolucion = evolucion;
        this.tratamiento = tratamiento;
        this.recomendaciones = recomendaciones;
        this.proceso = proceso;
    }

    public Cura(String evolucion, String tratamiento, String recomendaciones, Proceso proceso, Sanitario sanitario) {
        this.evolucion = evolucion;
        this.tratamiento = tratamiento;
        this.recomendaciones = recomendaciones;
        this.proceso = proceso;
        this.sanitario = sanitario;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEvolucion() {
        return evolucion;
    }

    public void setEvolucion(String evolucion) {
        this.evolucion = evolucion;
    }

    public String getTratamiento() {
        return tratamiento;
    }

    public void setTratamiento(String tratamiento) {
        this.tratamiento = tratamiento;
    }

    public String getRecomendaciones() {
        return recomendaciones;
    }

    public void setRecomendaciones(String recomendaciones) {
        this.recomendaciones = recomendaciones;
    }

    public List<Imagen> getImagenes() {
        return imagenes;
    }

    public void setImagenes(List<Imagen> imagenes) {
        this.imagenes = imagenes;
    }

    public Date getCreacion() {
        return creacion;
    }

    public void setCreacion(Date creacion) {
        this.creacion = creacion;
    }

    public Proceso getProceso() {
        return proceso;
    }

    public void setProceso(Proceso proceso) {
        this.proceso = proceso;
    }

    public Sanitario getSanitario() {
        return sanitario;
    }

    public void setSanitario(Sanitario sanitario) {
        this.sanitario = sanitario;
    }
}
