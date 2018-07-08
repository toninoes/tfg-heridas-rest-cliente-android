package uca.ruiz.antonio.tfgapp.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import uca.ruiz.antonio.tfgapp.R;


/**
 * Created by toni on 08/07/2018.
 */

public class Utils {




    public static void preguntarQuiereSalir(final Context ctx) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);

        // Establecer un título para el diálogo de alerta
        builder.setTitle(R.string.seleccione_opcion);

        // Preguntar si desea realizar la acción
        builder.setMessage(R.string.quiere_salir);

        // Establecer listener para el click de los botones de diálogo de alerta
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch(which){
                    case DialogInterface.BUTTON_POSITIVE:
                        // Usuario confirma la acción y sale de la aplicación
                        Activity activity = (Activity) ctx;
                        activity.moveTaskToBack(true);
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(1);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        // Usuario no confirma la acción
                        dialog.cancel();
                        break;
                }
            }
        };

        // Establecer el mensaje sobre el botón BUTTON_POSITIVE
        builder.setPositiveButton(R.string.si, dialogClickListener);

        // Establecer el mensaje sobre el botón BUTTON_NEGATIVE
        builder.setNegativeButton(R.string.no, dialogClickListener);

        AlertDialog dialog = builder.create();
        // Mostrar el diálogo de alerta
        dialog.show();
    }
}
