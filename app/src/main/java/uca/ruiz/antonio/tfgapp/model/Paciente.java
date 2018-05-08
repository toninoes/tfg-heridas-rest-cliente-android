package uca.ruiz.antonio.tfgapp.model;

import java.io.Serializable;
import java.util.Date;

public class Paciente implements Serializable {
    private long id;
    private String dni;
    private String nombre;
    private String apellidos;
    private Date nacimiento;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public Date getNacimiento() {
        return nacimiento;
    }

    public void setNacimiento(Date nacimiento) {
        this.nacimiento = nacimiento;
    }

    public String getFullName () {
        return this.nombre + " " + this.apellidos;
    }
}
