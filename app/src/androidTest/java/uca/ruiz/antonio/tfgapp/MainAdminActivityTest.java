package uca.ruiz.antonio.tfgapp;

import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import uca.ruiz.antonio.tfgapp.ui.activity.LoginActivity;
import uca.ruiz.antonio.tfgapp.ui.activity.MainPacienteActivity;
import uca.ruiz.antonio.tfgapp.ui.activity.MainSanitarioActivity;
import uca.ruiz.antonio.tfgapp.ui.activity.PacientesActivity;
import uca.ruiz.antonio.tfgapp.ui.activity.admin.AdministradoresActivity;
import uca.ruiz.antonio.tfgapp.ui.activity.admin.CentrosActivity;
import uca.ruiz.antonio.tfgapp.ui.activity.admin.DiagnosticosActivity;
import uca.ruiz.antonio.tfgapp.ui.activity.admin.GruposdiagnosticosActivity;
import uca.ruiz.antonio.tfgapp.ui.activity.admin.MainAdminActivity;
import uca.ruiz.antonio.tfgapp.ui.activity.admin.ProcedimientosActivity;
import uca.ruiz.antonio.tfgapp.ui.activity.admin.SalasActivity;
import uca.ruiz.antonio.tfgapp.ui.activity.admin.SanitariosActivity;
import uca.ruiz.antonio.tfgapp.ui.activity.admin.ValoracionesResultsActivity;

import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class MainAdminActivityTest {

    private static final String EMAIL_ADMIN = "admin@user.es";
    private static final String PASSWORD = "admin";

    @Rule
    public ActivityTestRule<LoginActivity> activityTestRule =
            new ActivityTestRule<>(LoginActivity.class);

    private Solo solo;

    @Before
    public void setUp() throws Exception {
        //setUp() se ejecuta antes de que un test se inicie
        //Aquí se crea el objeto 'solo'
        solo = new Solo(InstrumentationRegistry.getInstrumentation(),
                activityTestRule.getActivity());

        if (!solo.isCheckBoxChecked(0)) //si no está habilitado el checkbox
            solo.clickOnCheckBox(0); // lo pulsa para habilitarlo

        //Confirma de que el Activity a probar se abre
        solo.assertCurrentActivity("Se espera el Activity: ", LoginActivity.class);

        solo.clearEditText(0);
        solo.clearEditText(1);

        //se autentica el administrador
        solo.typeText(0, EMAIL_ADMIN);
        solo.typeText(1, PASSWORD);
        solo.clickOnView(solo.getView(R.id.btn_entrar));

    }

    @After
    public void tearDown() throws Exception {
        //tearDown() se ejecuta cuando termina un test
        //finishOpenedActivities() finalizará todos los activities abiertos durante el test.
        solo.finishOpenedActivities();
    }

    @Test
    public void testAdminHomeButtons() throws Exception {
        //Desbloquea la pantalla
        solo.unlockScreen();

        // Aseguramos que vamos al menú principal del adminstrador
        solo.assertCurrentActivity("Se espera el Activity: ", MainAdminActivity.class);

        solo.clickOnView(solo.getView(R.id.ib_centros));
        solo.assertCurrentActivity("Se espera el Activity: ", CentrosActivity.class);
        volverAtras();

        solo.clickOnView(solo.getView(R.id.ib_salas));
        solo.assertCurrentActivity("Se espera el Activity: ", SalasActivity.class);
        volverAtras();

        solo.clickOnView(solo.getView(R.id.ib_gruposdiagnosticos));
        solo.assertCurrentActivity("Se espera el Activity: ", GruposdiagnosticosActivity.class);
        volverAtras();

        solo.clickOnView(solo.getView(R.id.ib_diagnosticos));
        solo.assertCurrentActivity("Se espera el Activity: ", DiagnosticosActivity.class);
        volverAtras();

        solo.clickOnView(solo.getView(R.id.ib_procedimientos));
        solo.assertCurrentActivity("Se espera el Activity: ", ProcedimientosActivity.class);
        volverAtras();

        solo.clickOnView(solo.getView(R.id.ib_administradores));
        solo.assertCurrentActivity("Se espera el Activity: ", AdministradoresActivity.class);
        volverAtras();

        solo.clickOnView(solo.getView(R.id.ib_sanitarios));
        solo.assertCurrentActivity("Se espera el Activity: ", SanitariosActivity.class);
        volverAtras();

        solo.clickOnView(solo.getView(R.id.ib_pacientes));
        solo.assertCurrentActivity("Se espera el Activity: ", PacientesActivity.class);
        volverAtras();

        solo.clickOnView(solo.getView(R.id.ib_valoraciones));
        solo.assertCurrentActivity("Se espera el Activity: ", ValoracionesResultsActivity.class);
        volverAtras();
    }

    private void volverAtras() {
        solo.goBackToActivity("MainAdminActivity");
        solo.assertCurrentActivity("Se espera el Activity: ", MainAdminActivity.class);
    }

}
