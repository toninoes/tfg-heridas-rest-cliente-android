package uca.ruiz.antonio.tfgapp.data.api.mapping;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ImgResponse implements Serializable
{

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("nombre")
    @Expose
    private String nombre;
    @SerializedName("cura")
    @Expose
    private Object cura;
    private final static long serialVersionUID = -4351617595121234763L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Object getCura() {
        return cura;
    }

    public void setCura(Object cura) {
        this.cura = cura;
    }

}
