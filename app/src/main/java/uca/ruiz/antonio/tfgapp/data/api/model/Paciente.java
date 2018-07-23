package uca.ruiz.antonio.tfgapp.data.api.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Paciente extends User implements Serializable {

    public Paciente() {
    }

    public Paciente(String username, String firstname, String lastname, String email,
                     ArrayList<Boolean> permisos, String dni, Date nacimiento) {
        super(username, firstname, lastname, email, permisos, dni, nacimiento);
    }

    public Paciente(String username, String firstname, String lastname, String email,
                     ArrayList<Boolean> permisos, String dni, Date nacimiento,
                     Boolean enabled) {
        super(username, firstname, lastname, email, permisos, dni, nacimiento, enabled);
    }
}
