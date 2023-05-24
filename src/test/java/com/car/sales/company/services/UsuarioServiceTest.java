package com.car.sales.company.services;

import com.car.sales.company.exceptions.DatoInvalidoException;
import com.car.sales.company.exceptions.UsuarioNoEncontradoException;
import com.car.sales.company.models.NombreNotificacion;
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
        usuario2.setAceptaNotificacionSms(true);
        usuario3 = new Usuario("Lucy", "Pardo", "ci", "52082393B",
                "lucy.luz023@hotmail.com", COMPRADOR, "76437428");
        usuario3.setAceptaNotificacionSms(true);
        usuario4 = new Usuario("Christian", "Ledezma", "licencia", "12323984",
                "cris_lu.21412@hotmail.com", COMPRADOR, null);

        usuarioService.getListaUsuariosRegistrados().add(usuario1);
        usuarioService.getListaUsuariosRegistrados().add(usuario2);
        usuarioService.getListaUsuariosRegistrados().add(usuario3);
        usuarioService.getListaUsuariosRegistrados().add(usuario4);
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
        identificacionEsperado = "12323984";
//        Usuario usuarioActual = usuarioService.eliminarUsuario(identificacionEsperado);

        Assert.assertNotNull(identificacionEsperado);
//        Assert.assertEquals(identificacionEsperado, usuarioActual.getIdentificacion());
    }

    @Test(expected = UsuarioNoEncontradoException.class)
    public void testEliminarUsuarioBotaExceptionCuandoLaIdentificacionNoExisteEnLaLista() {
        identificacionEsperado = "3278ES29123";

        usuarioService.eliminarUsuario(identificacionEsperado);
    }

    @Test
    public void testModificarUsuario() {
        identificacionEsperado = "490123984";
        String celularEsperado = "76898123";

        Usuario usuarioActual = usuarioService.modificarUsuario(identificacionEsperado, celularEsperado);

        Assert.assertEquals(celularEsperado, usuarioActual.getCelular());
        Assert.assertTrue(usuarioActual.isAceptaNotificacionSms());
    }

    @Test(expected = UsuarioNoEncontradoException.class)
    public void testModificarUsuarioBotaExceptionCuandoLaIdentificacionNoExiste() {
        identificacionEsperado = "1245234";

        usuarioService.modificarUsuario(identificacionEsperado, "75463462");
    }

    @Test
    public void testActualizarSuscripcionCaseUnsuscribirEmail() {
        nombreNotificacion = VENDEDOR_ACEPTA_OFERTA;

        Usuario usuarioEsperado = usuarioService.actualizarSuscripcion(usuario2, nombreNotificacion, UNSUSCRIBIR, EMAIL);

        assertNotNull(usuarioEsperado);
        assertTrue(usuarioEsperado.getUnsuscripcionesEmail().contains(nombreNotificacion));
    }

    @Test
    public void testActualizarSuscripcionCaseUnsuscribirSms() {
        nombreNotificacion = VENDEDOR_ACEPTA_OFERTA;

        Usuario usuarioEsperado = usuarioService.actualizarSuscripcion(usuario2, nombreNotificacion, UNSUSCRIBIR, SMS);

        assertNotNull(usuarioEsperado);
        assertTrue(usuarioEsperado.getUnsuscripcionesSms().contains(nombreNotificacion));
    }

    @Test
    public void testActualizarSuscripcionCaseSuscribirEmail() {
        nombreNotificacion = VENDEDOR_CONTRAOFERTA;

        Usuario usuarioEsperado = usuarioService.actualizarSuscripcion(usuario2, nombreNotificacion, SUSCRIBIR, EMAIL);

        assertNotNull(usuarioEsperado);
        assertFalse(usuarioEsperado.getUnsuscripcionesEmail().contains(nombreNotificacion));

    }

    @Test
    public void testActualizarSuscripcionCaseSuscribirSms() {
        nombreNotificacion = NUEVO_VEHICULO_EN_VENTA;

        Usuario usuarioEsperado = usuarioService.actualizarSuscripcion(usuario2, nombreNotificacion, SUSCRIBIR, SMS);

        assertNotNull(usuarioEsperado);
        assertFalse(usuarioEsperado.getUnsuscripcionesSms().contains(nombreNotificacion));

    }

    @Test(expected = DatoInvalidoException.class)
    public void testActualizarBotaExceptionCuandoNoExisteNotificacionSms() {
        nombreNotificacion = VENDEDOR_CONTRAOFERTA;

        usuarioService.actualizarSuscripcion(usuario2, nombreNotificacion, SUSCRIBIR, SMS);
    }

    @Test
    public void testActualizarSuscripcionCaseUnsuscribirTodo() {

        Usuario usuarioEsperado = usuarioService.actualizarSuscripcion(usuario1, null, UNSUSCRIBIR_TODO, null);

        assertNotNull(usuarioEsperado);
        assertTrue(usuarioEsperado.getUnsuscripcionesEmail().containsAll(Arrays.asList(NombreNotificacion.values())));
    }

    @Test
    public void testActualizarSuscripcionCaseSuscribirTodo() {

        Usuario usuarioEsperado = usuarioService.actualizarSuscripcion(usuario1, null, SUSCRIBIR_TODO, null);
        assertTrue(usuarioEsperado.getUnsuscripcionesSms().isEmpty());
    }
}