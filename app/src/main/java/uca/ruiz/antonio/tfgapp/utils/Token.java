package uca.ruiz.antonio.tfgapp.utils;

import android.content.Context;
import android.support.annotation.NonNull;

import uca.ruiz.antonio.tfgapp.data.Preferencias;

/**
 * Created by toni on 21/06/2018.
 */

public class Token {

    private static String token;
    private static Context ctx;

    public Token(@NonNull Context ctx) {
        this.ctx = ctx;
    }

    public static String get() {
        return Preferencias.get(ctx).getString("token", "token");
    }
}
