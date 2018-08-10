package uca.ruiz.antonio.tfgapp.data.api.model;

import java.io.Serializable;

public class SalaConfig implements Serializable
{

    private Integer id;
    private Integer cupo;
    private Integer horaini;
    private Integer minini;
    private Boolean lunes;
    private Boolean martes;
    private Boolean miercoles;
    private Boolean jueves;
    private Boolean viernes;
    private Boolean sabado;
    private Boolean domingo;
    private Sala sala;

    public SalaConfig() {
    }

    public SalaConfig(Integer cupo, Integer horaini, Integer minini, Boolean lunes, Boolean martes,
                      Boolean miercoles, Boolean jueves, Boolean viernes, Boolean sabado,
                      Boolean domingo) {
        this.cupo = cupo;
        this.horaini = horaini;
        this.minini = minini;
        this.lunes = lunes;
        this.martes = martes;
        this.miercoles = miercoles;
        this.jueves = jueves;
        this.viernes = viernes;
        this.sabado = sabado;
        this.domingo = domingo;
    }

    public SalaConfig(Integer cupo, Integer horaini, Integer minini, Boolean lunes, Boolean martes,
                      Boolean miercoles, Boolean jueves, Boolean viernes, Boolean sabado,
                      Boolean domingo, Sala sala) {
        this.cupo = cupo;
        this.horaini = horaini;
        this.minini = minini;
        this.lunes = lunes;
        this.martes = martes;
        this.miercoles = miercoles;
        this.jueves = jueves;
        this.viernes = viernes;
        this.sabado = sabado;
        this.domingo = domingo;
        this.sala = sala;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCupo() {
        return cupo;
    }

    public void setCupo(Integer cupo) {
        this.cupo = cupo;
    }

    public Integer getHoraini() {
        return horaini;
    }

    public void setHoraini(Integer horaini) {
        this.horaini = horaini;
    }

    public Integer getMinini() {
        return minini;
    }

    public void setMinini(Integer minini) {
        this.minini = minini;
    }

    public Boolean getLunes() {
        return lunes;
    }

    public void setLunes(Boolean lunes) {
        this.lunes = lunes;
    }

    public Boolean getMartes() {
        return martes;
    }

    public void setMartes(Boolean martes) {
        this.martes = martes;
    }

    public Boolean getMiercoles() {
        return miercoles;
    }

    public void setMiercoles(Boolean miercoles) {
        this.miercoles = miercoles;
    }

    public Boolean getJueves() {
        return jueves;
    }

    public void setJueves(Boolean jueves) {
        this.jueves = jueves;
    }

    public Boolean getViernes() {
        return viernes;
    }

    public void setViernes(Boolean viernes) {
        this.viernes = viernes;
    }

    public Boolean getSabado() {
        return sabado;
    }

    public void setSabado(Boolean sabado) {
        this.sabado = sabado;
    }

    public Boolean getDomingo() {
        return domingo;
    }

    public void setDomingo(Boolean domingo) {
        this.domingo = domingo;
    }

    public Sala getSala() {
        return sala;
    }

    public void setSala(Sala sala) {
        this.sala = sala;
    }
}
