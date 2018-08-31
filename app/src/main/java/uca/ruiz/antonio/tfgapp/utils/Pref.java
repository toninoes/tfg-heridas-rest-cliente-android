package uca.ruiz.antonio.tfgapp.utils;

import android.content.Context;
import android.support.annotation.NonNull;

import uca.ruiz.antonio.tfgapp.data.Preferencias;

/**
 * Created by toni on 21/06/2018.
 */

public class Pref {

    private static Context ctx;

    public Pref(@NonNull Context ctx) {
        this.ctx = ctx;
    }

    public static String getToken() {
        return Preferencias.get(ctx).getString("token", "token");
    }

    public static String getNombre() {
        return Preferencias.get(ctx).getString("nombre", "nombre");
    }

    public static String getProtocoloServidor() {
        return Preferencias.get(ctx).getString("protocolo", "http");
    }

    public static String getUrlServidor() {
        return Preferencias.get(ctx).getString("url", "10.0.2.2");
    }

    public static String getPuertoServidor() {
        return Preferencias.get(ctx).getString("puerto", "8080");
    }

    public static String getServicioServidor() {
        return Preferencias.get(ctx).getString("servicio", "");
    }

    public static String getBaseUrl() {
        String prot = Pref.getProtocoloServidor();
        String url = Pref.getUrlServidor();
        String port = Pref.getPuertoServidor();
        String servicio = Pref.getServicioServidor();
        String baseUrl;

        if(port.isEmpty() && servicio.isEmpty())
            baseUrl = String.format("%s://%s/", prot, url);
        else if (!port.isEmpty() && servicio.isEmpty())
            baseUrl = String.format("%s://%s:%s/", prot, url, port);
        else if (port.isEmpty() && !servicio.isEmpty())
            baseUrl = String.format("%s://%s/%s/", prot, url, servicio);
        else
            baseUrl = String.format("%s://%s:%s/%s/", prot, url, port, servicio);

        /*if(servicio.isEmpty())
            baseUrl = String.format("%s://%s:%s/", prot, url, port);
        else
            baseUrl = String.format("%s://%s:%s/%s/", prot, url, port, servicio);*/

        return baseUrl;
    }

    public static String getApellidos() {
        return Preferencias.get(ctx).getString("apellidos", "apellidos");
    }

    public static String getMail() {
        return Preferencias.get(ctx).getString("email", "email");
    }

    public static Boolean esAdmin() {
        return Preferencias.get(ctx).getBoolean("ROLE_ADMIN", false);
    }

    public static Boolean esSanitario() {
        return Preferencias.get(ctx).getBoolean("ROLE_SANITARIO", false);
    }

    public static Boolean esPaciente() {
        return Preferencias.get(ctx).getBoolean("ROLE_PACIENTE", false);
    }

    public static Long getUserId() {
        return Preferencias.get(ctx).getLong("id", 0);
    }
}
