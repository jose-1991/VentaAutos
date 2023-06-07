package com.car.sales.company.services;

import com.car.sales.company.exceptions.DatoInvalidoException;
import com.car.sales.company.models.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.car.sales.company.models.NombreNotificacion.*;
import static com.car.sales.company.models.TipoNotificacion.*;
import static com.car.sales.company.models.TipoUsuario.COMPRADOR;
import static com.car.sales.company.models.TipoUsuario.VENDEDOR;
import static com.car.sales.company.services.NotificacionService.*;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;

@RunWith(MockitoJUnitRunner.class)
public class NotificacionServiceTest {

    double montoOferta;
    double montoContraOferta;
    Notificacion notificacion;
    Vehiculo vehiculo;
    Usuario vendedor;
    Usuario comprador;

    @InjectMocks
    NotificacionService notificacionService;

    @Before
    public void setUp() {
        montoOferta = 15000;
        montoContraOferta = 16000;

        comprador = new Usuario("Ruben", "Sanchez", "ci", "5203746",
                "rube.123-122@gmail.com", COMPRADOR, "75647362");
        comprador.setAceptaNotificacionSms(true);
        comprador.setListaUnsuscribciones(new ArrayList<>());

        vendedor = new Usuario("Jorge", "Lopez", "ci", "5203717",
                "jorgito-122@gmail.com", VENDEDOR, "78943726");
        vendedor.setAceptaNotificacionSms(true);
        vendedor.setListaUnsuscribciones(new ArrayList<>());

        vehiculo = new Vehiculo("1HGBH41JXMN109716", "Toyota", "Scion", 2020);


        notificacion = new Notificacion(COMPRADOR_PRIMERA_OFERTA, AMBOS, VENDEDOR);
    }

    @Test
    public void testEnviarNotificacionCompradorPrimeraOfertaSoloEmail() {
       notificacion.setTipoNotificacion(SMS);
       vendedor.setListaUnsuscribciones(Collections.singletonList(notificacion));

        InputNotificacion inputNotificacionActual = notificacionService.enviarNotificacion(vendedor, vehiculo,
                montoOferta, 0, C_PRIMERA_OFERTA);

        assertNotNull(inputNotificacionActual.getEmail());
        assertNull(inputNotificacionActual.getCelular());
        assertEquals(COMPRADOR_PRIMERA_OFERTA, inputNotificacionActual.getNombreNotificacion());
    }

    @Test
    public void testEnviarNotificacionCompradorPrimeraOfertaSoloSms() {
        notificacion.setTipoNotificacion(EMAIL);
        vendedor.setListaUnsuscribciones(Collections.singletonList(notificacion));

        InputNotificacion inputNotificacionActual = notificacionService.enviarNotificacion(vendedor, vehiculo,
                montoOferta, 0, C_PRIMERA_OFERTA);

        assertNotNull(inputNotificacionActual.getCelular());
        assertNull(inputNotificacionActual.getEmail());
        assertEquals(COMPRADOR_PRIMERA_OFERTA, inputNotificacionActual.getNombreNotificacion());
    }

    @Test
    public void testEnviarNotificacionCompradorPrimeraOfertaAmbos() {
        vendedor.setListaUnsuscribciones(Collections.EMPTY_LIST);

        InputNotificacion inputNotificacionActual = notificacionService.enviarNotificacion(vendedor, vehiculo,
                montoOferta, 0, C_PRIMERA_OFERTA);

        assertNotNull(inputNotificacionActual.getEmail());
        assertNotNull(inputNotificacionActual.getCelular());
        assertEquals(COMPRADOR_PRIMERA_OFERTA, inputNotificacionActual.getNombreNotificacion());
    }

    @Test
    public void testEnviarNotificacionCompradorNuevaOfertaSoloEmail() {
        notificacion.setNombreNotificacion(COMPRADOR_NUEVA_OFERTA);
        notificacion.setTipoNotificacion(SMS);
        vendedor.setListaUnsuscribciones(Collections.singletonList(notificacion));

        InputNotificacion inputNotificacionActual = notificacionService.enviarNotificacion(vendedor, vehiculo,
                montoOferta, 0, C_NUEVA_OFERTA);

        assertNotNull(inputNotificacionActual.getEmail());
        assertNull(inputNotificacionActual.getCelular());
        assertEquals(COMPRADOR_NUEVA_OFERTA, inputNotificacionActual.getNombreNotificacion());
    }

    @Test
    public void testEnviarNotificacionCompradorNuevaOfertaSoloSms() {
        notificacion.setNombreNotificacion(COMPRADOR_NUEVA_OFERTA);
        notificacion.setTipoNotificacion(EMAIL);
        vendedor.setListaUnsuscribciones(Collections.singletonList(notificacion));

        InputNotificacion inputNotificacionActual = notificacionService.enviarNotificacion(vendedor, vehiculo,
                montoOferta, 0, C_NUEVA_OFERTA);

        assertNotNull(inputNotificacionActual.getCelular());
        assertNull(inputNotificacionActual.getEmail());
        assertEquals(COMPRADOR_NUEVA_OFERTA, inputNotificacionActual.getNombreNotificacion());
    }

    @Test
    public void testEnviarNotificacionCompradorNuevaOfertaAmbos() {
        vendedor.setListaUnsuscribciones(Collections.EMPTY_LIST);

        InputNotificacion inputNotificacionActual = notificacionService.enviarNotificacion(vendedor, vehiculo,
                montoOferta, 0, C_NUEVA_OFERTA);

        assertNotNull(inputNotificacionActual.getEmail());
        assertNotNull(inputNotificacionActual.getCelular());
        assertEquals(COMPRADOR_NUEVA_OFERTA, inputNotificacionActual.getNombreNotificacion());
    }

    @Test
    public void testEnviarNotificacionCompradorAceptaOfertaSoloEmail() {
        notificacion.setNombreNotificacion(COMPRADOR_ACEPTA_OFERTA);
        notificacion.setTipoNotificacion(SMS);
        vendedor.setListaUnsuscribciones(Collections.singletonList(notificacion));

        InputNotificacion inputNotificacionActual = notificacionService.enviarNotificacion(vendedor, vehiculo,
                montoOferta, montoContraOferta, C_ACEPTA_OFERTA);

        assertNotNull(inputNotificacionActual.getEmail());
        assertNull(inputNotificacionActual.getCelular());
        assertEquals(COMPRADOR_ACEPTA_OFERTA, inputNotificacionActual.getNombreNotificacion());
    }

    @Test
    public void testEnviarNotificacionCompradorAceptaOfertaSoloSms() {
        notificacion.setNombreNotificacion(COMPRADOR_ACEPTA_OFERTA);
        notificacion.setTipoNotificacion(EMAIL);
        vendedor.setListaUnsuscribciones(Collections.singletonList(notificacion));

        InputNotificacion inputNotificacionActual = notificacionService.enviarNotificacion(vendedor, vehiculo,
                montoOferta, montoContraOferta, C_ACEPTA_OFERTA);

        assertNotNull(inputNotificacionActual.getCelular());
        assertNull(inputNotificacionActual.getEmail());
        assertEquals(COMPRADOR_ACEPTA_OFERTA, inputNotificacionActual.getNombreNotificacion());
    }

    @Test
    public void testEnviarNotificacionCompradorAceptaOfertaAmbos() {
        vendedor.setListaUnsuscribciones(Collections.EMPTY_LIST);

        InputNotificacion inputNotificacionActual = notificacionService.enviarNotificacion(vendedor, vehiculo,
                montoOferta, montoContraOferta, C_ACEPTA_OFERTA);

        assertNotNull(inputNotificacionActual.getEmail());
        assertNotNull(inputNotificacionActual.getCelular());
        assertEquals(COMPRADOR_ACEPTA_OFERTA, inputNotificacionActual.getNombreNotificacion());
    }

    @Test
    public void testEnviarNotificacionCompradorRetiraOfertaSoloEmail() {
        vendedor.setListaUnsuscribciones(Collections.EMPTY_LIST);

        InputNotificacion inputNotificacionActual = notificacionService.enviarNotificacion(vendedor, vehiculo, montoOferta,
                0, C_RETIRA_OFERTA);

        assertNotNull(inputNotificacionActual.getEmail());
        assertEquals(COMPRADOR_RETIRA_OFERTA, inputNotificacionActual.getNombreNotificacion());

    }

    @Test
    public void testEnviarNotificacionVehiculoExpiradoSoloEmail() {
        vendedor.setListaUnsuscribciones(Collections.EMPTY_LIST);

        InputNotificacion inputNotificacionActual = notificacionService.enviarNotificacion(vendedor, vehiculo, 0,
                0, V_EXPIRADO);

        assertNotNull(inputNotificacionActual.getEmail());
        assertEquals(VEHICULO_EXPIRADO, inputNotificacionActual.getNombreNotificacion());
    }

    @Test
    public void testEnviarNotificacionNuevoVehiculoEnVentaSoloEmail() {
        notificacion.setNombreNotificacion(NUEVO_VEHICULO_EN_VENTA);
        notificacion.setTipoNotificacion(SMS);
        comprador.setListaUnsuscribciones(Collections.singletonList(notificacion));

        InputNotificacion inputNotificacionActual = notificacionService.enviarNotificacion(comprador, vehiculo, 0,
                0, N_VEHICULO_VENTA);

        assertNotNull(inputNotificacionActual.getEmail());
        assertNull(inputNotificacionActual.getCelular());
        assertEquals(NUEVO_VEHICULO_EN_VENTA, inputNotificacionActual.getNombreNotificacion());
    }

    @Test
    public void testEnviarNotificacionNuevoVehiculoEnVentaSoloSms() {
        notificacion.setNombreNotificacion(NUEVO_VEHICULO_EN_VENTA);
        notificacion.setTipoNotificacion(EMAIL);
        comprador.setListaUnsuscribciones(Collections.singletonList(notificacion));

        InputNotificacion inputNotificacionActual = notificacionService.enviarNotificacion(comprador, vehiculo, 0,
                0, N_VEHICULO_VENTA);

        assertNotNull(inputNotificacionActual.getCelular());
        assertNull(inputNotificacionActual.getEmail());
        assertEquals(NUEVO_VEHICULO_EN_VENTA, inputNotificacionActual.getNombreNotificacion());
    }

    @Test
    public void testEnviarNotificacionNuevoVehiculoEnVentaAmbos() {
        comprador.setListaUnsuscribciones(Collections.EMPTY_LIST);

        InputNotificacion inputNotificacionActual = notificacionService.enviarNotificacion(comprador, vehiculo, 0,
                0, N_VEHICULO_VENTA);

        assertNotNull(inputNotificacionActual.getEmail());
        assertNotNull(inputNotificacionActual.getCelular());
        assertEquals(NUEVO_VEHICULO_EN_VENTA, inputNotificacionActual.getNombreNotificacion());
    }

    @Test
    public void testEnviarNotificacionVendedorContraOfertaSoloEmail() {
        comprador.setListaUnsuscribciones(Collections.EMPTY_LIST);

        InputNotificacion inputNotificacionActual = notificacionService.enviarNotificacion(comprador, vehiculo, montoOferta,
                montoContraOferta, V_CONTRAOFERTA);

        assertNotNull(inputNotificacionActual.getEmail());
        assertEquals(VENDEDOR_CONTRAOFERTA, inputNotificacionActual.getNombreNotificacion());
    }

    @Test
    public void testEnviarNotificacionVendedorAceptaOfertaSoloEmail() {
        notificacion.setNombreNotificacion(VENDEDOR_ACEPTA_OFERTA);
        notificacion.setTipoNotificacion(SMS);
        comprador.setListaUnsuscribciones(Collections.singletonList(notificacion));

        InputNotificacion inputNotificacionActual = notificacionService.enviarNotificacion(comprador, vehiculo, montoOferta,
                0, V_ACEPTA_OFERTA);

        assertNotNull(inputNotificacionActual.getEmail());
        assertNull(inputNotificacionActual.getCelular());
        assertEquals(VENDEDOR_ACEPTA_OFERTA, inputNotificacionActual.getNombreNotificacion());
    }

    @Test
    public void testEnviarNotificacionVendedorAceptaOfertaSoloSms() {
        notificacion.setNombreNotificacion(VENDEDOR_ACEPTA_OFERTA);
        notificacion.setTipoNotificacion(EMAIL);
        comprador.setListaUnsuscribciones(Collections.singletonList(notificacion));

        InputNotificacion inputNotificacionActual = notificacionService.enviarNotificacion(comprador, vehiculo, montoOferta,
                0, V_ACEPTA_OFERTA);

        assertNotNull(inputNotificacionActual.getCelular());
        assertNull(inputNotificacionActual.getEmail());
        assertEquals(VENDEDOR_ACEPTA_OFERTA, inputNotificacionActual.getNombreNotificacion());
    }

    @Test
    public void testEnviarNotificacionVendedorAceptaOfertaAmbos() {
        comprador.setListaUnsuscribciones(Collections.EMPTY_LIST);

        InputNotificacion inputNotificacionActual = notificacionService.enviarNotificacion(comprador, vehiculo, montoOferta,
                0, V_ACEPTA_OFERTA);

        assertNotNull(inputNotificacionActual.getEmail());
        assertNotNull(inputNotificacionActual.getCelular());
        assertEquals(VENDEDOR_ACEPTA_OFERTA, inputNotificacionActual.getNombreNotificacion());
    }

    @Test
    public void testEnviarNotificacionVehiculoNoDisponibleSoloEmail() {
        comprador.setListaUnsuscribciones(Collections.EMPTY_LIST);

        InputNotificacion inputNotificacionActual = notificacionService.enviarNotificacion(comprador, vehiculo, 0,
                0, V_NO_DISPONIBLE);

        assertNotNull(inputNotificacionActual.getEmail());
        assertEquals(VEHICULO_NO_DISPONIBLE, inputNotificacionActual.getNombreNotificacion());
    }

    @Test(expected = DatoInvalidoException.class)
    public void testEnviarNotificacionBotaExceptionCuandoElUsuarioNoEstaSuscritoEnAmbos() {
       notificacion.setNombreNotificacion(COMPRADOR_PRIMERA_OFERTA);
       notificacion.setTipoNotificacion(AMBOS);
        vendedor.setListaUnsuscribciones(Collections.singletonList(notificacion));

        notificacionService.enviarNotificacion(vendedor, vehiculo, montoOferta, 0, C_PRIMERA_OFERTA);
    }

    @Test(expected = DatoInvalidoException.class)
    public void testEnviarNotificacionBotaExceptionCuandoNombreNotificacionEsNull() {

        notificacionService.enviarNotificacion(vendedor, vehiculo, 0, 0, null);
    }
}