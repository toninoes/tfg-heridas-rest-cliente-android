package uca.ruiz.antonio.tfgapp.utils;

/**
 * Created by toni on 14/06/2018.
 */

public class Validacion {

    private static final int minPass = 4;
    private static final int maxPass = 100;

    private static final int minEmail = 4;
    private static final int maxEmail = 100;

    private static final int minNombre = 3;
    private static final int maxNombre = 50;

    private static final int minApellidos = 3;
    private static final int maxApellidos = 100;

    public static boolean formatoEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean tamPassword(String password) {
        return password.length() >= minPass && password.length() <= maxPass;
    }

    public static boolean tamEmail(String email) {
        return email.length() >= minEmail && email.length() <= maxEmail;
    }

    public static boolean tamNombre(String nombre) {
        return nombre.length() >= minNombre && nombre.length() <= maxNombre;
    }

    public static boolean tamApellidos(String apellidos) {
        return apellidos.length() >= minApellidos && apellidos.length() <= maxApellidos;
    }

}
