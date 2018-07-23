package uca.ruiz.antonio.tfgapp.data.api.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by toni on 19/07/2018.
 */

public class Sanitario extends User implements Serializable {

    public Sanitario() {
    }

    public Sanitario(String username, String firstname, String lastname, String email,
                     ArrayList<Boolean> permisos, String dni, Date nacimiento, String colegiado) {
        super(username, firstname, lastname, email, permisos, dni, nacimiento, colegiado);
    }

    public Sanitario(String username, String firstname, String lastname, String email,
                     ArrayList<Boolean> permisos, String dni, Date nacimiento, String colegiado,
                     Boolean enabled) {
        super(username, firstname, lastname, email, permisos, dni, nacimiento, colegiado, enabled);
    }
}
