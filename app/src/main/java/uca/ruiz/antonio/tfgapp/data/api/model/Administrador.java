package uca.ruiz.antonio.tfgapp.data.api.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import uca.ruiz.antonio.tfgapp.data.api.mapping.Authority;

/**
 * Created by toni on 19/07/2018.
 */

public class Administrador extends User implements Serializable {
    private long id;
    private String dni;
    private Date nacimiento;

    public Administrador() {
    }

    public Administrador(String username, String firstname, String lastname, String email,
                         List<Authority> authorities, String dni, Date nacimiento) {
        super(username, firstname, lastname, email, authorities);
        this.dni = dni;
        this.nacimiento = nacimiento;
    }

    public Administrador(String username, String firstname, String lastname, String email,
                         List<Authority> authorities, ArrayList<Boolean> permisos, String dni,
                         Date nacimiento) {
        super(username, firstname, lastname, email, authorities, permisos);
        this.dni = dni;
        this.nacimiento = nacimiento;
    }

    public long getId() {
        return id;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public Date getNacimiento() {
        return nacimiento;
    }

    public void setNacimiento(Date nacimiento) {
        this.nacimiento = nacimiento;
    }
}
