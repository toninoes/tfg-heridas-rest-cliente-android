package uca.ruiz.antonio.tfgapp.data.api.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by toni on 19/07/2018.
 */

public class Administrador extends User implements Serializable {

    public Administrador() {
    }

    public Administrador(String username, String firstname, String lastname, String email,
                         ArrayList<Boolean> permisos, String dni, Date nacimiento) {
        super(username, firstname, lastname, email, permisos, dni, nacimiento);
    }

    public Administrador(String username, String firstname, String lastname, String email,
                         ArrayList<Boolean> permisos, String dni, Date nacimiento,
                         Boolean enabled) {
        super(username, firstname, lastname, email, permisos, dni, nacimiento, enabled);
    }
}
