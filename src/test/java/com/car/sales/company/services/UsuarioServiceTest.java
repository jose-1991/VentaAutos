package com.car.sales.company.services;

import com.car.sales.company.exceptions.DatoInvalidoException;
import com.car.sales.company.exceptions.UsuarioNoEncontradoException;
import com.car.sales.company.models.NombreNotificacion;
import com.car.sales.company.models.TipoNotificacion;
import com.car.sales.company.models.Usuario;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;

import static com.car.sales.company.models.Accion.*;
import static com.car.sales.company.models.NombreNotificacion.*;
import static com.car.sales.company.models.TipoNotificacion.EMAIL;
import static com.car.sales.company.models.TipoNotificacion.SMS;
import static com.car.sales.company.models.TipoUsuario.COMPRADOR;
import static com.car.sales.company.models.TipoUsuario.VENDEDOR;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class UsuarioServiceTest {


    TipoNotificacion tipoNotificacion;
    NombreNotificacion nombreNotificacion;
    String identificacionEsperado;
    Usuario usuarioEsperado;
    Usuario usuario1;
    Usuario usuario2;
    Usuario usuario3;
    Usuario usuario4;

    @InjectMocks
    private UsuarioService usuarioService;

    @Before
    public void setUp() {

        identificacionEsperado = "A3278129";
        usuarioEsperado = new Usuario("Jorge", "Foronda", "ci", "5203717",
                "jorgito-122@gmail.com", VENDEDOR, null);

        usuario1 = new Usuario("Javier", "Rodriguez", "licencia", "490123984",
                "javi.31_82@hotmail.com", VENDEDOR, null);
        usuario2 = new Usuario("Pablo", "Valencia", "pasaporte", "A3278129",
                "pa_val.1985@gmail.com", COMPRADOR, "60782023");
        usuario3 = new Usuario("Lucy", "Pardo", "ci", "52082393B",
                "lucy.luz023@hotmail.com", COMPRADOR, "76437428");
        usuario4 = new Usuario("Christian", "Ledezma", "licencia", "12323984",
                "cris_lu.21412@hotmail.com", COMPRADOR, null);

        usuarioService.usuarios.add(usuario1);
        usuarioService.usuarios.add(usuario2);
        usuarioService.usuarios.add(usuario3);
        usuarioService.usuarios.add(usuario4);
    }

    @Test
    public void testRegistrarUsuario() {
        usuarioEsperado.setCelular(null);
        Usuario usuarioActual = usuarioService.registrarUsuario(usuarioEsperado);
        Assert.assertNotNull(usuarioActual);
        Assert.assertEquals(usuarioEsperado.getNombre(), usuarioActual.getNombre());
        Assert.assertNull(usuarioActual.getCelular());
        Assert.assertFalse(usuarioActual.isAceptaNotificacionSms());
    }

    @Test
    public void testRegistrarUsuarioCuandoCelularEsVacioNoActualizaElConsentimiento() {

        usuarioEsperado.setCelular("  ");
        Usuario usuarioActual = usuarioService.registrarUsuario(usuarioEsperado);
        Assert.assertNotNull(usuarioActual);
        Assert.assertEquals(usuarioEsperado.getNombre(), usuarioActual.getNombre());
        Assert.assertNull(usuarioActual.getCelular());
        Assert.assertFalse(usuarioActual.isAceptaNotificacionSms());
    }

    @Test(expected = DatoInvalidoException.class)
    public void testRegistrarUsuarioBotaExceptionCuandoNoSeIngresaTipoUsuario() {
        usuarioEsperado.setTipoUsuario(null);
        usuarioService.registrarUsuario(usuarioEsperado);
    }

    @Test
    public void testRegistrarUsuarioVerificaTodosLosDatosObligatoriosHanSidoIngresados() {
        Usuario usuarioActual = usuarioService.registrarUsuario(usuarioEsperado);
        Assert.assertEquals(usuarioEsperado.getNombre(), usuarioActual.getNombre());
        Assert.assertEquals(usuarioEsperado.getApellido(), usuarioActual.getApellido());
        Assert.assertEquals(usuarioEsperado.getIdentificacion(), usuarioActual.getIdentificacion());
        Assert.assertEquals(usuarioEsperado.getEmail(), usuarioActual.getEmail());
    }

    @Test(expected = DatoInvalidoException.class)
    public void testRegistrarUsuarioBotaExceptionCuandoUnDatoObligatorioNoEsIngresado() {
        usuarioEsperado.setEmail(null);
        usuarioService.registrarUsuario(usuarioEsperado);
    }

    @Test
    public void testRegistrarUsuarioActualizaConsentimientoSiElUsuarioIngresaCelular() {
        usuarioEsperado.setCelular("76980426");

        Usuario usuarioActual = usuarioService.registrarUsuario(usuarioEsperado);
        Assert.assertNotNull(usuarioActual.getCelular());
        Assert.assertTrue(usuarioActual.isAceptaNotificacionSms());
    }

    @Test
    public void testEliminarUsuario() {
        Usuario usuarioActual = usuarioService.eliminarUsuario(identificacionEsperado);

        Assert.assertNotNull(identificacionEsperado);
        Assert.assertEquals("A3278129", usuarioActual.getIdentificacion());
    }

    @Test(expected = UsuarioNoEncontradoException.class)
    public void testEliminarUsuarioBotaExceptionCuandoLaIdentificacionNoExisteEnLaLista() {
        identificacionEsperado = "A3278129123";

        usuarioService.eliminarUsuario(identificacionEsperado);
    }

    @Test
    public void testModificarUsuario() {
        identificacionEsperado = "490123984";
        String celularEsperado = "76898123";

        Usuario usuarioActual = usuarioService.modificarUsuario(identificacionEsperado, celularEsperado);

        Assert.assertEquals(celularEsperado, usuarioActual.getCelular());
    }

    @Test
    public void testActualizarSuscripcionCaseUnsuscribirEmail() {
        nombreNotificacion = VENDEDOR_ACEPTA_OFERTA;

        Usuario usuarioEsperado = usuarioService.actualizarSuscripcion(usuario2, nombreNotificacion, UNSUSCRIBIR, EMAIL);

        assertNotNull(usuarioEsperado);
        assertTrue(usuarioEsperado.getUnsuscripcionesEmail().contains(nombreNotificacion));

    }

    @Test
    public void testActualizarSuscripcionSuscribirSms() {
        nombreNotificacion = NUEVO_VEHICULO_EN_VENTA;

        Usuario usuarioEsperado = usuarioService.actualizarSuscripcion(usuario2, nombreNotificacion, UNSUSCRIBIR, EMAIL);

        assertNotNull(usuarioEsperado);
        assertFalse(usuarioEsperado.getUnsuscripcionesSms().contains(nombreNotificacion));

    }

    @Test(expected = DatoInvalidoException.class)
    public void testActualizarBotaExceptionCuandoNoExisteNotificacionSms() {
        nombreNotificacion = VENDEDOR_CONTRAOFERTA;

        usuarioService.actualizarSuscripcion(usuario2, nombreNotificacion, SUSCRIBIR, SMS);
    }

    @Test
    public void testActualizarSuscripcionCaseUnsuscribirTodoEmail() {

        Usuario usuarioEsperado = usuarioService.actualizarSuscripcion(usuario1, null, UNSUSCRIBIR_TODO, EMAIL);

        assertNotNull(usuarioEsperado);
        assertTrue(usuarioEsperado.getUnsuscripcionesEmail().containsAll(Arrays.asList(NombreNotificacion.values())));
    }

    @Test
    public void testActualizarSuscripcionCaseSuscribirTodoSms() {

        Usuario usuarioEsperado = usuarioService.actualizarSuscripcion(usuario1, null, SUSCRIBIR_TODO, SMS);
        assertTrue(usuarioEsperado.getUnsuscripcionesSms().isEmpty());
    }
}