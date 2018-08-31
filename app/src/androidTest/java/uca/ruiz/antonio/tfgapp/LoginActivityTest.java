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
import uca.ruiz.antonio.tfgapp.ui.activity.admin.MainAdminActivity;

import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {

    private static final String EMAIL_ADMIN = "admin@user.es";
    private static final String EMAIL_SANITARIO = "sanitario@user.es";
    private static final String EMAIL_PACIENTE = "enfermo@user.es";
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

    }

    @After
    public void tearDown() throws Exception {
        //tearDown() se ejecuta cuando termina un test
        //finishOpenedActivities() finalizará todos los activities abiertos durante el test.
        solo.finishOpenedActivities();
    }

    @Test
    public void loginCorrectoAdmin() throws Exception {
        //Desbloquea la pantalla
        solo.unlockScreen();

        //Confirma de que el Activity a probar se abre
        solo.assertCurrentActivity("Se espera el Activity: ", LoginActivity.class);

        solo.clearEditText(0);
        solo.clearEditText(1);

        solo.typeText(0, EMAIL_ADMIN);
        solo.typeText(1, PASSWORD);
        solo.clickOnView(solo.getView(R.id.btn_entrar));

        // Aseguramos que vamos al menú principal del adminstrador
        solo.assertCurrentActivity("Se espera el Activity: ", MainAdminActivity.class);
    }

    @Test
    public void loginInCorrectoAdmin() throws Exception {
        //Desbloquea la pantalla
        solo.unlockScreen();

        //Confirma de que el Activity a probar se abre
        solo.assertCurrentActivity("Se espera el Activity: ", LoginActivity.class);

        solo.clearEditText(0);
        solo.clearEditText(1);

        solo.typeText(0, EMAIL_ADMIN.split("@")[0]); // introduce email incorrecto.
        solo.typeText(1, PASSWORD);
        solo.clickOnView(solo.getView(R.id.btn_entrar));

        assertTrue(solo.waitForText(solo.getString(R.string.error_formato_email_invalido)));
    }

    @Test
    public void loginCorrectoSanitario() throws Exception {
        //Desbloquea la pantalla
        solo.unlockScreen();

        //Confirma de que el Activity a probar este abierto
        solo.assertCurrentActivity("Se espera el Activity: ", LoginActivity.class);

        //Comprobar que funciona bien el checkbox de recordar email
        if (solo.isCheckBoxChecked(0)) {
            boolean estaMailAnterior = solo.searchText(EMAIL_ADMIN);
            assertTrue("No ha funcionado el checkbox de guardar email.", estaMailAnterior);
        }

        solo.clearEditText(0);
        solo.clearEditText(1);

        solo.typeText(0, EMAIL_SANITARIO);
        solo.typeText(1, PASSWORD);
        solo.clickOnView(solo.getView(R.id.btn_entrar));

        // Aseguramos que vamos al menú principal del adminstrador
        solo.assertCurrentActivity("Se espera el Activity: ", MainSanitarioActivity.class);
    }

    @Test
    public void loginCorrectoPaciente() throws Exception {
        //Desbloquea la pantalla
        solo.unlockScreen();

        //Confirma de que el Activity a probar este abierto
        solo.assertCurrentActivity("LoginActivity esperado...", LoginActivity.class);

        //Comprobar que funciona bien el checkbox de recordar email
        if (solo.isCheckBoxChecked(0)) {
            boolean estaMailAnterior = solo.searchText(EMAIL_SANITARIO);
            assertTrue("No ha funcionado el checkbox de guardar email.", estaMailAnterior);
        }

        solo.clearEditText(0);
        solo.clearEditText(1);

        solo.typeText(0, EMAIL_PACIENTE);
        solo.typeText(1, PASSWORD);
        solo.clickOnView(solo.getView(R.id.btn_entrar));

        // Aseguramos que vamos al menú principal del adminstrador
        solo.assertCurrentActivity("Se espera el Activity: ", MainPacienteActivity.class);
    }
}
