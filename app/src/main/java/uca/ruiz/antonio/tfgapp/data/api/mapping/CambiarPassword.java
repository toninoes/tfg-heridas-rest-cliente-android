package uca.ruiz.antonio.tfgapp.data.api.mapping;

/**
 * Created by toni on 20/08/2018.
 */

public class CambiarPassword {

    protected String password;
    protected String password_nueva;

    public CambiarPassword() {
        super();
    }

    public CambiarPassword(String password, String password_nueva) {
        super();
        this.password = password;
        this.password_nueva = password_nueva;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword_nueva() {
        return password_nueva;
    }

    public void setPassword_nueva(String password_nueva) {
        this.password_nueva = password_nueva;
    }

}
