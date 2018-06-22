package uca.ruiz.antonio.tfgapp.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import uca.ruiz.antonio.tfgapp.R;

/**
 * Created by toni on 07/06/2018.
 */

public class Preferencias {

    private static SharedPreferences PREFERENCIAS;

    /**
     * Devuelve un objeto SharedPreferences
     * @param ctx Contexto desde donde se llama
     * @return SharedPreferences
     */
    public static SharedPreferences get(@NonNull Context ctx) {
        instanciar(ctx);
        return PREFERENCIAS;
    }

    /**
     * Duevuelve un editor SharedPreferences
     * @param ctx Contexto desde donde se llama
     * @return SharedPreferences.Editor
     */
    public static SharedPreferences.Editor getEditor(@NonNull Context ctx) {
        instanciar(ctx);
        return PREFERENCIAS.edit();
    }

    /**
     * Instancia un objeto SharedPreferences
     * @param ctx Contexto desde donde se llama
     */
    private static void instanciar (@NonNull Context ctx) {
        if(PREFERENCIAS == null) // patrón singleton
            PREFERENCIAS = ctx.getSharedPreferences(definirNombre(ctx), Context.MODE_PRIVATE);
    }

    /**
     * Para definir el fichero de preferencias igual que el nombre de la App
     * @param ctx Contexto desde donde se llama
     * @return String
     */
    private static String definirNombre(@NonNull Context ctx) {
        return ctx.getString(R.string.app_name);
    }


}
