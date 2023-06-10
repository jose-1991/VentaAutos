package com.car.sales.company.services;

import com.car.sales.company.exceptions.DatoInvalidoException;
import com.car.sales.company.models.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;

import static com.car.sales.company.models.NombreNotificacion.*;
import static com.car.sales.company.models.TipoNotificacion.*;
import static com.car.sales.company.models.TipoUsuario.COMPRADOR;
import static com.car.sales.company.models.TipoUsuario.VENDEDOR;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;

@RunWith(MockitoJUnitRunner.class)
public class NotificacionServiceTest {

    double montoOferta;
    double montoContraOferta;
    Notificacion notificacionSms;
    Notificacion notificacionEmail;
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

        vendedor = new Usuario("Jorge", "Lopez", "ci", "5203717",
                "jorgito-122@gmail.com", VENDEDOR, "78943726");
        vendedor.setAceptaNotificacionSms(true);

        vehiculo = new Vehiculo("1HGBH41JXMN109716", "Toyota", "Scion", 2020);


        notificacionSms = new Notificacion("1",COMPRADOR_PRIMERA_OFERTA, SMS, VENDEDOR);
        notificacionEmail = new Notificacion("2",COMPRADOR_PRIMERA_OFERTA, EMAIL, VENDEDOR);
    }

    @Test
    public void testEnviarNotificacionCompradorPrimeraOfertaSoloEmail() {
        vendedor.setListaUnsuscribciones(Collections.singletonList(notificacionSms));

        InputNotificacion inputNotificacionActual = notificacionService.enviarNotificacion(vendedor, vehiculo,
                montoOferta, 0, COMPRADOR_PRIMERA_OFERTA);

        assertNotNull(inputNotificacionActual.getEmail());
        assertNull(inputNotificacionActual.getCelular());
        assertEquals(COMPRADOR_PRIMERA_OFERTA, inputNotificacionActual.getNombreNotificacion());
    }

    @Test
    public void testEnviarNotificacionCompradorPrimeraOfertaSoloSms() {
        vendedor.setListaUnsuscribciones(Collections.singletonList(notificacionEmail));

        InputNotificacion inputNotificacionActual = notificacionService.enviarNotificacion(vendedor, vehiculo,
                montoOferta, 0, COMPRADOR_PRIMERA_OFERTA);

        assertNotNull(inputNotificacionActual.getCelular());
        assertNull(inputNotificacionActual.getEmail());
        assertEquals(COMPRADOR_PRIMERA_OFERTA, inputNotificacionActual.getNombreNotificacion());
    }

    @Test
    public void testEnviarNotificacionCompradorPrimeraOfertaAmbos() {
        vendedor.setListaUnsuscribciones(Collections.EMPTY_LIST);

        InputNotificacion inputNotificacionActual = notificacionService.enviarNotificacion(vendedor, vehiculo,
                montoOferta, 0, COMPRADOR_PRIMERA_OFERTA);

        assertNotNull(inputNotificacionActual.getEmail());
        assertNotNull(inputNotificacionActual.getCelular());
        assertEquals(COMPRADOR_PRIMERA_OFERTA, inputNotificacionActual.getNombreNotificacion());
    }

    @Test
    public void testEnviarNotificacionCompradorNuevaOfertaSoloEmail() {
        notificacionSms.setNombreNotificacion(COMPRADOR_NUEVA_OFERTA);
        vendedor.setListaUnsuscribciones(Collections.singletonList(notificacionSms));

        InputNotificacion inputNotificacionActual = notificacionService.enviarNotificacion(vendedor, vehiculo,
                montoOferta, 0, COMPRADOR_NUEVA_OFERTA);

        assertNotNull(inputNotificacionActual.getEmail());
        assertNull(inputNotificacionActual.getCelular());
        assertEquals(COMPRADOR_NUEVA_OFERTA, inputNotificacionActual.getNombreNotificacion());
    }

    @Test
    public void testEnviarNotificacionCompradorNuevaOfertaSoloSms() {
        notificacionEmail.setNombreNotificacion(COMPRADOR_NUEVA_OFERTA);
        vendedor.setListaUnsuscribciones(Collections.singletonList(notificacionEmail));

        InputNotificacion inputNotificacionActual = notificacionService.enviarNotificacion(vendedor, vehiculo,
                montoOferta, 0, COMPRADOR_NUEVA_OFERTA);

        assertNotNull(inputNotificacionActual.getCelular());
        assertNull(inputNotificacionActual.getEmail());
        assertEquals(COMPRADOR_NUEVA_OFERTA, inputNotificacionActual.getNombreNotificacion());
    }

    @Test
    public void testEnviarNotificacionCompradorNuevaOfertaAmbos() {
        vendedor.setListaUnsuscribciones(Collections.EMPTY_LIST);

        InputNotificacion inputNotificacionActual = notificacionService.enviarNotificacion(vendedor, vehiculo,
                montoOferta, 0, COMPRADOR_NUEVA_OFERTA);

        assertNotNull(inputNotificacionActual.getEmail());
        assertNotNull(inputNotificacionActual.getCelular());
        assertEquals(COMPRADOR_NUEVA_OFERTA, inputNotificacionActual.getNombreNotificacion());
    }

    @Test
    public void testEnviarNotificacionCompradorAceptaOfertaSoloEmail() {
        notificacionSms.setNombreNotificacion(COMPRADOR_ACEPTA_OFERTA);
        vendedor.setListaUnsuscribciones(Collections.singletonList(notificacionSms));

        InputNotificacion inputNotificacionActual = notificacionService.enviarNotificacion(vendedor, vehiculo,
                montoOferta, montoContraOferta, COMPRADOR_ACEPTA_OFERTA);

        assertNotNull(inputNotificacionActual.getEmail());
        assertNull(inputNotificacionActual.getCelular());
        assertEquals(COMPRADOR_ACEPTA_OFERTA, inputNotificacionActual.getNombreNotificacion());
    }

    @Test
    public void testEnviarNotificacionCompradorAceptaOfertaSoloSms() {
        notificacionEmail.setNombreNotificacion(COMPRADOR_ACEPTA_OFERTA);
        vendedor.setListaUnsuscribciones(Collections.singletonList(notificacionEmail));

        InputNotificacion inputNotificacionActual = notificacionService.enviarNotificacion(vendedor, vehiculo,
                montoOferta, montoContraOferta, COMPRADOR_ACEPTA_OFERTA);

        assertNotNull(inputNotificacionActual.getCelular());
        assertNull(inputNotificacionActual.getEmail());
        assertEquals(COMPRADOR_ACEPTA_OFERTA, inputNotificacionActual.getNombreNotificacion());
    }

    @Test
    public void testEnviarNotificacionCompradorAceptaOfertaAmbos() {
        vendedor.setListaUnsuscribciones(Collections.EMPTY_LIST);

        InputNotificacion inputNotificacionActual = notificacionService.enviarNotificacion(vendedor, vehiculo,
                montoOferta, montoContraOferta, COMPRADOR_ACEPTA_OFERTA);

        assertNotNull(inputNotificacionActual.getEmail());
        assertNotNull(inputNotificacionActual.getCelular());
        assertEquals(COMPRADOR_ACEPTA_OFERTA, inputNotificacionActual.getNombreNotificacion());
    }

    @Test
    public void testEnviarNotificacionCompradorRetiraOfertaSoloEmail() {
        vendedor.setListaUnsuscribciones(Collections.EMPTY_LIST);

        InputNotificacion inputNotificacionActual = notificacionService.enviarNotificacion(vendedor, vehiculo, montoOferta,
                0, COMPRADOR_RETIRA_OFERTA);

        assertNotNull(inputNotificacionActual.getEmail());
        assertEquals(COMPRADOR_RETIRA_OFERTA, inputNotificacionActual.getNombreNotificacion());

    }

    @Test
    public void testEnviarNotificacionVehiculoExpiradoSoloEmail() {
        vendedor.setListaUnsuscribciones(Collections.EMPTY_LIST);

        InputNotificacion inputNotificacionActual = notificacionService.enviarNotificacion(vendedor, vehiculo, 0,
                0, VEHICULO_EXPIRADO);

        assertNotNull(inputNotificacionActual.getEmail());
        assertEquals(VEHICULO_EXPIRADO, inputNotificacionActual.getNombreNotificacion());
    }

    @Test
    public void testEnviarNotificacionNuevoVehiculoEnVentaSoloEmail() {
        notificacionSms.setNombreNotificacion(NUEVO_VEHICULO_EN_VENTA);
        comprador.setListaUnsuscribciones(Collections.singletonList(notificacionSms));

        InputNotificacion inputNotificacionActual = notificacionService.enviarNotificacion(comprador, vehiculo, 0,
                0, NUEVO_VEHICULO_EN_VENTA);

        assertNotNull(inputNotificacionActual.getEmail());
        assertNull(inputNotificacionActual.getCelular());
        assertEquals(NUEVO_VEHICULO_EN_VENTA, inputNotificacionActual.getNombreNotificacion());
    }

    @Test
    public void testEnviarNotificacionNuevoVehiculoEnVentaSoloSms() {
        notificacionEmail.setNombreNotificacion(NUEVO_VEHICULO_EN_VENTA);
        comprador.setListaUnsuscribciones(Collections.singletonList(notificacionEmail));

        InputNotificacion inputNotificacionActual = notificacionService.enviarNotificacion(comprador, vehiculo, 0,
                0, NUEVO_VEHICULO_EN_VENTA);

        assertNotNull(inputNotificacionActual.getCelular());
        assertNull(inputNotificacionActual.getEmail());
        assertEquals(NUEVO_VEHICULO_EN_VENTA, inputNotificacionActual.getNombreNotificacion());
    }

    @Test
    public void testEnviarNotificacionNuevoVehiculoEnVentaAmbos() {
        comprador.setListaUnsuscribciones(Collections.EMPTY_LIST);

        InputNotificacion inputNotificacionActual = notificacionService.enviarNotificacion(comprador, vehiculo, 0,
                0, NUEVO_VEHICULO_EN_VENTA);

        assertNotNull(inputNotificacionActual.getEmail());
        assertNotNull(inputNotificacionActual.getCelular());
        assertEquals(NUEVO_VEHICULO_EN_VENTA, inputNotificacionActual.getNombreNotificacion());
    }

    @Test
    public void testEnviarNotificacionVendedorContraOfertaSoloEmail() {
        comprador.setListaUnsuscribciones(Collections.EMPTY_LIST);

        InputNotificacion inputNotificacionActual = notificacionService.enviarNotificacion(comprador, vehiculo, montoOferta,
                montoContraOferta, VENDEDOR_CONTRAOFERTA);

        assertNotNull(inputNotificacionActual.getEmail());
        assertEquals(VENDEDOR_CONTRAOFERTA, inputNotificacionActual.getNombreNotificacion());
    }

    @Test
    public void testEnviarNotificacionVendedorAceptaOfertaSoloEmail() {
        notificacionSms.setNombreNotificacion(VENDEDOR_ACEPTA_OFERTA);
        comprador.setListaUnsuscribciones(Collections.singletonList(notificacionSms));

        InputNotificacion inputNotificacionActual = notificacionService.enviarNotificacion(comprador, vehiculo, montoOferta,
                0, VENDEDOR_ACEPTA_OFERTA);

        assertNotNull(inputNotificacionActual.getEmail());
        assertNull(inputNotificacionActual.getCelular());
        assertEquals(VENDEDOR_ACEPTA_OFERTA, inputNotificacionActual.getNombreNotificacion());
    }

    @Test
    public void testEnviarNotificacionVendedorAceptaOfertaSoloSms() {
        notificacionEmail.setNombreNotificacion(VENDEDOR_ACEPTA_OFERTA);
        comprador.setListaUnsuscribciones(Collections.singletonList(notificacionEmail));

        InputNotificacion inputNotificacionActual = notificacionService.enviarNotificacion(comprador, vehiculo, montoOferta,
                0, VENDEDOR_ACEPTA_OFERTA);

        assertNotNull(inputNotificacionActual.getCelular());
        assertNull(inputNotificacionActual.getEmail());
        assertEquals(VENDEDOR_ACEPTA_OFERTA, inputNotificacionActual.getNombreNotificacion());
    }

    @Test
    public void testEnviarNotificacionVendedorAceptaOfertaAmbos() {
        comprador.setListaUnsuscribciones(Collections.EMPTY_LIST);

        InputNotificacion inputNotificacionActual = notificacionService.enviarNotificacion(comprador, vehiculo, montoOferta,
                0, VENDEDOR_ACEPTA_OFERTA);

        assertNotNull(inputNotificacionActual.getEmail());
        assertNotNull(inputNotificacionActual.getCelular());
        assertEquals(VENDEDOR_ACEPTA_OFERTA, inputNotificacionActual.getNombreNotificacion());
    }

    @Test
    public void testEnviarNotificacionVehiculoNoDisponibleSoloEmail() {
        comprador.setListaUnsuscribciones(Collections.EMPTY_LIST);

        InputNotificacion inputNotificacionActual = notificacionService.enviarNotificacion(comprador, vehiculo, 0,
                0, VEHICULO_NO_DISPONIBLE);

        assertNotNull(inputNotificacionActual.getEmail());
        assertEquals(VEHICULO_NO_DISPONIBLE, inputNotificacionActual.getNombreNotificacion());
    }

    @Test(expected = DatoInvalidoException.class)
    public void testEnviarNotificacionBotaExceptionCuandoElUsuarioNoEstaSuscrito() {
        vendedor.setListaUnsuscribciones(Arrays.asList(notificacionSms,notificacionEmail));

        notificacionService.enviarNotificacion(vendedor, vehiculo, montoOferta, 0, COMPRADOR_PRIMERA_OFERTA);
    }

    @Test(expected = DatoInvalidoException.class)
    public void testEnviarNotificacionBotaExceptionCuandoNombreNotificacionEsNull() {

        notificacionService.enviarNotificacion(vendedor, vehiculo, 0, 0, null);
    }
}