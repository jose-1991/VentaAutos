package com.car.sales.company.services;

import com.car.sales.company.exceptions.DatoInvalidoException;
import com.car.sales.company.exceptions.UsuarioNoEncontradoException;
import com.car.sales.company.models.Usuario;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UsuarioServiceTest {
    String identificacionEsperado;
    Usuario usuarioEsperado;

    @InjectMocks
    private UsuarioService usuarioService;

    @Before
    public void setUp() {

        identificacionEsperado = "A3278129";
        usuarioEsperado = new Usuario("Jorge", "Foronda", "ci", "5203717",
                "vendedor", "jorgito-122@gmail.com");
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

    @Test (expected = DatoInvalidoException.class)
    public void testRegistrarUsuarioBotaExceptionCuandoNoSeIngresaTipoUsuario() {
        usuarioEsperado.setTipoUsuario("");
        Usuario usuarioActual = usuarioService.registrarUsuario(usuarioEsperado);
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
        String tipoUsuarioEsperado = "comprador";
        String celularEsperado = "76898123";

        Usuario usuarioActual = usuarioService.modificarUsuario(identificacionEsperado, tipoUsuarioEsperado, celularEsperado);
        Assert.assertNotNull(tipoUsuarioEsperado);
        Assert.assertEquals(tipoUsuarioEsperado, usuarioActual.getTipoUsuario());
    }
}