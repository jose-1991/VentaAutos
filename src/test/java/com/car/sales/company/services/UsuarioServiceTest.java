package com.car.sales.company.services;

import com.car.sales.company.dao.UsuarioDAO;
import com.car.sales.company.exceptions.DatoInvalidoException;
import com.car.sales.company.models.NombreNotificacion;
import com.car.sales.company.models.Notificacion;
import com.car.sales.company.models.Usuario;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.car.sales.company.models.Accion.*;
import static com.car.sales.company.models.NombreNotificacion.*;
import static com.car.sales.company.models.TipoNotificacion.EMAIL;
import static com.car.sales.company.models.TipoNotificacion.SMS;
import static com.car.sales.company.models.TipoUsuario.COMPRADOR;
import static com.car.sales.company.models.TipoUsuario.VENDEDOR;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UsuarioServiceTest {

    NombreNotificacion nombreNotificacion;
    String identificacionEsperado;
    Usuario usuarioEsperado;
    Notificacion notificacion;

    @Mock
    private UsuarioDAO usuarioDaoMock;
    @InjectMocks
    private UsuarioService usuarioService;

    @Before
    public void setUp() {

        identificacionEsperado = "A3278129";
        usuarioEsperado = new Usuario("Jorge", "Foronda", "ci", "5203717",
                "jorgito-122@gmail.com", VENDEDOR, null);
        notificacion = new Notificacion("1",COMPRADOR_PRIMERA_OFERTA,SMS,VENDEDOR);
    }

    @Test
    public void testRegistrarUsuario() {
        usuarioEsperado.setCelular(null);
        Usuario usuarioActual = usuarioService.registrarUsuario(usuarioEsperado);

        Assert.assertNotNull(usuarioActual);
        Assert.assertEquals(usuarioEsperado.getNombre(), usuarioActual.getNombre());
        Assert.assertNull(usuarioActual.getCelular());
        Assert.assertFalse(usuarioActual.isAceptaNotificacionSms());
        verify(usuarioDaoMock).registrarUsuario(any());
    }

    @Test
    public void testRegistrarUsuarioCuandoCelularEsVacioNoActualizaElConsentimiento() {

        usuarioEsperado.setCelular("  ");
        Usuario usuarioActual = usuarioService.registrarUsuario(usuarioEsperado);

        Assert.assertNotNull(usuarioActual);
        Assert.assertFalse(usuarioActual.isAceptaNotificacionSms());
        verify(usuarioDaoMock).registrarUsuario(any());
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
        verify(usuarioDaoMock).registrarUsuario(any());
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
        verify(usuarioDaoMock).registrarUsuario(any());
    }

    @Test
    public void testEliminarUsuario() {
        identificacionEsperado = "12323984";
        usuarioService.eliminarUsuario(identificacionEsperado);

        Assert.assertNotNull(identificacionEsperado);
        verify(usuarioDaoMock).eliminarUsuario(anyString());
    }

    @Test(expected = DatoInvalidoException.class)
    public void testEliminarUsuarioBotaExceptionCuandoLaIdentificacionEsNull() {

        usuarioService.eliminarUsuario(null);

    }

    @Test
    public void testModificarUsuario() {
        String celularEsperado = "76898123";
        usuarioEsperado.setCelular(celularEsperado);
        usuarioEsperado.setAceptaNotificacionSms(true);

        when(usuarioDaoMock.modificarUsuario(anyString(),anyString())).thenReturn(usuarioEsperado);
        Usuario usuarioActual = usuarioService.modificarUsuario("490123984", celularEsperado);

        assertEquals(celularEsperado,usuarioActual.getCelular());
        Assert.assertTrue(usuarioActual.isAceptaNotificacionSms());

    }

    @Test(expected = DatoInvalidoException.class)
    public void testModificarUsuarioBotaExceptionCuandoLaIdentificacionEsNulo() {

        usuarioService.modificarUsuario(null, "75463462");
    }

    @Test
    public void testActualizarSuscripcionCaseUnsuscribirEmail() {
        nombreNotificacion = COMPRADOR_PRIMERA_OFERTA;
        notificacion.setTipoNotificacion(EMAIL);
        usuarioEsperado.setListaUnsuscribciones(Collections.singletonList(notificacion));

        when(usuarioDaoMock.unsucribirNotificacion(anyString(),any(),any())).thenReturn(usuarioEsperado);
        Usuario usuarioActual = usuarioService.actualizarSuscripciones(usuarioEsperado, nombreNotificacion, EMAIL,
                UNSUSCRIBIR);

        assertNotNull(usuarioActual);
        assertTrue(usuarioActual.getListaUnsuscribciones().contains(notificacion));
        assertEquals(nombreNotificacion, usuarioActual.getListaUnsuscribciones().get(0).getNombreNotificacion());
        assertEquals(EMAIL, usuarioActual.getListaUnsuscribciones().get(0).getTipoNotificacion());
    }

    @Test
    public void testActualizarSuscripcionCaseUnsuscribirSms() {
        nombreNotificacion = COMPRADOR_ACEPTA_OFERTA;
        notificacion.setNombreNotificacion(nombreNotificacion);
        notificacion.setTipoNotificacion(SMS);
        usuarioEsperado.setListaUnsuscribciones(Collections.singletonList(notificacion));

        when(usuarioDaoMock.unsucribirNotificacion(anyString(),any(),any())).thenReturn(usuarioEsperado);
        Usuario usuarioActual = usuarioService.actualizarSuscripciones(usuarioEsperado, nombreNotificacion,SMS ,
                UNSUSCRIBIR);

        assertNotNull(usuarioActual);
        assertTrue(usuarioActual.getListaUnsuscribciones().contains(notificacion));
        assertEquals(nombreNotificacion, usuarioActual.getListaUnsuscribciones().get(0).getNombreNotificacion());
        assertEquals(SMS, usuarioActual.getListaUnsuscribciones().get(0).getTipoNotificacion());
    }

    @Test
    public void testActualizarSuscripcionCaseSuscribirEmail() {
        nombreNotificacion = VEHICULO_EXPIRADO;

        usuarioEsperado.setListaUnsuscribciones(Collections.EMPTY_LIST);

        when(usuarioDaoMock.suscribirNotificacion(anyString(),any(),any())).thenReturn(usuarioEsperado);
        Usuario usuarioActual = usuarioService.actualizarSuscripciones(usuarioEsperado, nombreNotificacion,EMAIL,
                SUSCRIBIR);

        assertNotNull(usuarioActual);
        assertTrue(usuarioActual.getListaUnsuscribciones().isEmpty());

    }

    @Test
    public void testActualizarSuscripcionCaseSuscribirSms() {
        nombreNotificacion = VEHICULO_EXPIRADO;

        usuarioEsperado.setListaUnsuscribciones(Collections.EMPTY_LIST);

        when(usuarioDaoMock.suscribirNotificacion(anyString(),any(),any())).thenReturn(usuarioEsperado);
        Usuario usuarioActual = usuarioService.actualizarSuscripciones(usuarioEsperado, nombreNotificacion,SMS,
                SUSCRIBIR);

        assertNotNull(usuarioActual);
        assertTrue(usuarioActual.getListaUnsuscribciones().isEmpty());

    }

    @Test
    public void testActualizarSuscripcionCaseUnsuscribirTodo() {
        List<Notificacion> listaUnsuscrpciones = new ArrayList<>();
        for (int x=0; x<8;x++){
            listaUnsuscrpciones.add(notificacion);
        }
        usuarioEsperado.setListaUnsuscribciones(listaUnsuscrpciones);

        when(usuarioDaoMock.unsuscribirTodo(any())).thenReturn(usuarioEsperado);
        Usuario usuarioActual = usuarioService.actualizarSuscripciones(usuarioEsperado, null,null , UNSUSCRIBIR_TODO);

        assertNotNull(usuarioActual);
        assertEquals(8, usuarioActual.getListaUnsuscribciones().size());
    }

    @Test
    public void testActualizarSuscripcionCaseSuscribirTodo() {

        usuarioEsperado.setListaUnsuscribciones(Collections.EMPTY_LIST);

        when(usuarioDaoMock.suscribirTodo(anyString())).thenReturn(usuarioEsperado);
        Usuario usuarioActual = usuarioService.actualizarSuscripciones(usuarioEsperado, null,null , SUSCRIBIR_TODO);

        assertTrue(usuarioActual.getListaUnsuscribciones().isEmpty());
    }
}