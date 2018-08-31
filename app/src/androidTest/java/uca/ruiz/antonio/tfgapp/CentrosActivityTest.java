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
import uca.ruiz.antonio.tfgapp.ui.activity.admin.MainAdminActivity;


import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class CentrosActivityTest {

    private static final String EMAIL_ADMIN = "admin@user.es";
    private static final String PASSWORD = "admin";

    private static final String NOMBRE = "Escuela Superior de Ingeniería";
    private static final String DIRECCION = "Avda. Universidad de Cádiz, nº 10, " +
            "11519 Puerto Real, Cádiz";
    private static final String TELEFONO = "956483200";

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

        solo.clickOnView(solo.getView(R.id.ib_centros));
    }

    @After
    public void tearDown() throws Exception {
        //tearDown() se ejecuta cuando termina un test
        //finishOpenedActivities() finalizará todos los activities abiertos durante el test.
        solo.finishOpenedActivities();
    }

    /**
     * Test para crear un Centro
     * @throws Exception
     */
    @Test
    public void test1() throws Exception {
        //Desbloquea la pantalla
        solo.unlockScreen();

        solo.assertCurrentActivity("Se espera el Activity: ", CentrosActivity.class);

        solo.clickOnView(solo.getView(R.id.add_item));
        solo.assertCurrentActivity("Se espera el Activity: ", CentroNewEditActivity.class);

        solo.enterText(0, NOMBRE);
        solo.enterText(1, DIRECCION);
        solo.enterText(2, TELEFONO);
        solo.clickOnView(solo.getView(R.id.action_guardar));
        assertTrue("No se ha creado.", solo.waitForText(solo.getString(R.string.creado_registro)));

        solo.assertCurrentActivity("Se espera el Activity: ", CentrosActivity.class);
    }

    /**
     * Test para editar el anterior Centro
     * @throws Exception
     */
    @Test
    public void test2() throws Exception {
        //Desbloquea la pantalla
        solo.unlockScreen();

        solo.assertCurrentActivity("Se espera el Activity: ", CentrosActivity.class);

        RecyclerView listado = (RecyclerView) solo.getView(R.id.rv_listado);

        for (int i = 0; i < listado.getChildCount(); i++) {
            View v = listado.getLayoutManager().findViewByPosition(i);
            TextView tv_titulo = (TextView) v.findViewById(R.id.tv_titulo);

            if(tv_titulo.getText().equals(NOMBRE)) {
                solo.clickOnView(v.findViewById(R.id.ib_edit));
                solo.assertCurrentActivity("Se espera el Activity: ", CentroNewEditActivity.class);
                solo.clearEditText(1);
                solo.clearEditText(2);
                solo.enterText(1, "Otra dirección");
                solo.enterText(2, "123456789");
                solo.clickOnView(solo.getView(R.id.action_guardar));
                assertTrue("No se ha editado.", solo.waitForText(solo.getString(R.string.editado_registro)));
            }
        }

        solo.assertCurrentActivity("Se espera el Activity: ", CentrosActivity.class);
    }

    /**
     * Test para finalmente borrar el Centro
     * @throws Exception
     */
    @Test
    public void test3() throws Exception {
        //Desbloquea la pantalla
        solo.unlockScreen();
        boolean seHaBorrado = false;

        solo.assertCurrentActivity("Se espera el Activity: ", CentrosActivity.class);

        RecyclerView listado = (RecyclerView) solo.getView(R.id.rv_listado);

        for (int i = 0; i < listado.getChildCount(); i++) {
            View v = listado.getLayoutManager().findViewByPosition(i);
            TextView tv_titulo = (TextView) v.findViewById(R.id.tv_titulo);

            if(tv_titulo.getText().equals(NOMBRE)) {
                solo.clickOnView(v.findViewById(R.id.ib_delete));
                solo.clickOnView(solo.getView(android.R.id.button1));
                assertTrue("No se ha borrado.", solo.waitForText(solo.getString(R.string.borrado_registro)));
                seHaBorrado = true;
            }
        }

        assertTrue("No se ha borrado el registro de prueba.", seHaBorrado);
        solo.assertCurrentActivity("Se espera el Activity: ", CentrosActivity.class);
    }

}
