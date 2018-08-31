package uca.ruiz.antonio.tfgapp;

import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import uca.ruiz.antonio.tfgapp.ui.activity.LoginActivity;
import uca.ruiz.antonio.tfgapp.ui.activity.admin.CentroNewEditActivity;
import uca.ruiz.antonio.tfgapp.ui.activity.admin.CentrosActivity;
import uca.ruiz.antonio.tfgapp.ui.activity.admin.GrupodiagnosticoNewEditActivity;
import uca.ruiz.antonio.tfgapp.ui.activity.admin.GruposdiagnosticosActivity;
import uca.ruiz.antonio.tfgapp.ui.activity.admin.MainAdminActivity;

import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class GruposDiagnosticoActivityTest {

    private static final String EMAIL_ADMIN = "admin@user.es";
    private static final String PASSWORD = "admin";

    private static final String NOMBRE = "Grupo Diagnóstico de prueba";
    private static final String NOMBRE2 = "Otra cosa";

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

        solo.typeText(0, EMAIL_ADMIN);
        solo.typeText(1, PASSWORD);
        solo.clickOnView(solo.getView(R.id.btn_entrar));

        // Aseguramos que vamos al menú principal del adminstrador
        solo.assertCurrentActivity("Se espera el Activity: ", MainAdminActivity.class);

        solo.clickOnView(solo.getView(R.id.ib_gruposdiagnosticos));
    }

    @After
    public void tearDown() throws Exception {
        //tearDown() se ejecuta cuando termina un test
        //finishOpenedActivities() finalizará todos los activities abiertos durante el test.
        solo.finishOpenedActivities();
    }

    /**
     * Test para crear un Grupodiagnostico
     * @throws Exception
     */
    @Test
    public void test1() throws Exception {
        //Desbloquea la pantalla
        solo.unlockScreen();

        solo.assertCurrentActivity("Se espera el Activity: ", GruposdiagnosticosActivity.class);

        solo.clickOnView(solo.getView(R.id.add_item));
        solo.assertCurrentActivity("Se espera el Activity: ", GrupodiagnosticoNewEditActivity.class);

        solo.enterText(0, NOMBRE);
        solo.clickOnView(solo.getView(R.id.action_guardar));
        assertTrue("No se ha creado.", solo.waitForText(solo.getString(R.string.creado_registro)));

        solo.assertCurrentActivity("Se espera el Activity: ", GruposdiagnosticosActivity.class);
    }

    /**
     * Test para editar el anterior Grupodiagnostico
     * @throws Exception
     */
    @Test
    public void test2() throws Exception {
        //Desbloquea la pantalla
        solo.unlockScreen();

        solo.assertCurrentActivity("Se espera el Activity: ", GruposdiagnosticosActivity.class);

        RecyclerView listado = (RecyclerView) solo.getView(R.id.rv_listado);

        for (int i = 0; i < listado.getChildCount(); i++) {
            View v = listado.getLayoutManager().findViewByPosition(i);
            TextView tv_titulo = (TextView) v.findViewById(R.id.tv_titulo);

            if(tv_titulo.getText().equals(NOMBRE)) {
                solo.clickOnView(v.findViewById(R.id.ib_edit));
                solo.assertCurrentActivity("Se espera el Activity: ", GrupodiagnosticoNewEditActivity.class);
                solo.clearEditText(0);
                solo.enterText(0, NOMBRE2);
                solo.clickOnView(solo.getView(R.id.action_guardar));
                assertTrue("No se ha editado.", solo.waitForText(solo.getString(R.string.editado_registro)));
            }
        }

        solo.assertCurrentActivity("Se espera el Activity: ", GruposdiagnosticosActivity.class);
    }

    /**
     * Test para finalmente borrar el Grupodiagnostico
     * @throws Exception
     */
    @Test
    public void test3() throws Exception {
        //Desbloquea la pantalla
        solo.unlockScreen();
        boolean seHaBorrado = false;

        solo.assertCurrentActivity("Se espera el Activity: ", GruposdiagnosticosActivity.class);

        RecyclerView listado = (RecyclerView) solo.getView(R.id.rv_listado);

        for (int i = 0; i < listado.getChildCount(); i++) {
            View v = listado.getLayoutManager().findViewByPosition(i);
            TextView tv_titulo = (TextView) v.findViewById(R.id.tv_titulo);

            if(tv_titulo.getText().equals(NOMBRE2)) {
                solo.clickOnView(v.findViewById(R.id.ib_delete));
                solo.clickOnView(solo.getView(android.R.id.button1));
                assertTrue("No se ha borrado.", solo.waitForText(solo.getString(R.string.borrado_registro)));
                seHaBorrado = true;
            }
        }

        assertTrue("No se ha borrado el registro de prueba.", seHaBorrado);
        solo.assertCurrentActivity("Se espera el Activity: ", GruposdiagnosticosActivity.class);
    }

}
