package com.car.sales.company.services;

import com.car.sales.company.exceptions.DatoInvalidoException;
import com.car.sales.company.models.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.Collections;

import static com.car.sales.company.models.NombreNotificacion.*;
import static com.car.sales.company.models.TipoUsuario.COMPRADOR;
import static com.car.sales.company.models.TipoUsuario.VENDEDOR;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class NotificacionServiceTest {

    double montoOfertaEsperado;
    Vehiculo vehiculo;
    Usuario vendedor;
    Usuario comprador;
    Publicacion publicacion;
    Oferta oferta;
    NombreNotificacion nombreNotificacion;
    @InjectMocks
    NotificacionService notificacionService;

    @Before
    public void setUp() {
        nombreNotificacion = VEHICULO_EXPIRADO;

        comprador = new Usuario("Ruben", "Sanchez", "ci", "5203746",
                "rube.123-122@gmail.com", COMPRADOR, "75647362");

        vendedor = new Usuario("Jorge", "Lopez", "ci", "5203717",
                "jorgito-122@gmail.com", VENDEDOR, "78943726");

        vehiculo = new Vehiculo("1HGBH41JXMN109716", "Toyota", "Scion", 2020, 16000);

        oferta = new Oferta(14000, 14500, comprador, LocalDateTime.now());
    }

    @Test
    public void testEnviarNotificacionCompradorPrimeraOfertaSoloEmail() {
        nombreNotificacion = COMPRADOR_PRIMERA_OFERTA;
        montoOfertaEsperado = 14000;
        vendedor.setAceptaNotificacionSms(false);

        Notificacion notificacionActual = notificacionService.enviarNotificacion(vendedor, vehiculo, montoOfertaEsperado, nombreNotificacion);

        assertNotNull(notificacionActual.getEmail());
        assertNull(notificacionActual.getCelular());
        assertEquals(nombreNotificacion, notificacionActual.getNombreNotificacion());
        assertEquals(montoOfertaEsperado, notificacionActual.getMontoOferta(), 0.0);
    }

    @Test
    public void testEnviarNotificacionCompradorPrimeraOfertaSoloSms() {
        nombreNotificacion = COMPRADOR_PRIMERA_OFERTA;
        montoOfertaEsperado = 14000;
        vendedor.setAceptaNotificacionSms(true);
        vendedor.setUnsuscribcionesEmail(Collections.singletonList(nombreNotificacion));

        Notificacion notificacionActual = notificacionService.enviarNotificacion(vendedor, vehiculo, montoOfertaEsperado, nombreNotificacion);

        assertNotNull(notificacionActual.getCelular());
        assertNull(notificacionActual.getEmail());
        assertEquals(nombreNotificacion, notificacionActual.getNombreNotificacion());
        assertEquals(montoOfertaEsperado, notificacionActual.getMontoOferta(), 0.0);
    }

    @Test
    public void testEnviarNotificacionCompradorPrimeraOfertaAmbos() {
        nombreNotificacion = COMPRADOR_PRIMERA_OFERTA;
        vendedor.setAceptaNotificacionSms(true);
        montoOfertaEsperado = 14000;

        Notificacion notificacionActual = notificacionService.enviarNotificacion(vendedor, vehiculo, montoOfertaEsperado, nombreNotificacion);

        assertNotNull(notificacionActual.getEmail());
        assertNotNull(notificacionActual.getCelular());
        assertEquals(nombreNotificacion, notificacionActual.getNombreNotificacion());
        assertEquals(montoOfertaEsperado, notificacionActual.getMontoOferta(), 0.0);
    }

    @Test
    public void testEnviarNotificacionCompradorAceptaOfertaSoloEmail() {
        nombreNotificacion = COMPRADOR_ACEPTA_OFERTA;
        vendedor.setAceptaNotificacionSms(false);


        Notificacion notificacionActual = notificacionService.enviarNotificacion(vendedor, vehiculo, montoOfertaEsperado, nombreNotificacion);

        assertNotNull(notificacionActual.getEmail());
        assertNull(notificacionActual.getCelular());
        assertEquals(nombreNotificacion, notificacionActual.getNombreNotificacion());
        assertEquals(montoOfertaEsperado, notificacionActual.getMontoOferta(), 0.0);
    }

    @Test
    public void testEnviarNotificacionCompradorAceptaOfertaSoloSms() {
        nombreNotificacion = COMPRADOR_ACEPTA_OFERTA;
        vendedor.setAceptaNotificacionSms(true);
        vendedor.setUnsuscribcionesEmail(Collections.singletonList(nombreNotificacion));


        Notificacion notificacionActual = notificacionService.enviarNotificacion(vendedor, vehiculo, montoOfertaEsperado, nombreNotificacion);

        assertNotNull(notificacionActual.getCelular());
        assertNull(notificacionActual.getEmail());
        assertEquals(nombreNotificacion, notificacionActual.getNombreNotificacion());
        assertEquals(montoOfertaEsperado, notificacionActual.getMontoOferta(), 0.0);
    }

    @Test
    public void testEnviarNotificacionCompradorAceptaOfertaAmbos() {
        nombreNotificacion = COMPRADOR_ACEPTA_OFERTA;
        montoOfertaEsperado = 14000;
        vendedor.setAceptaNotificacionSms(true);

        Notificacion notificacionActual = notificacionService.enviarNotificacion(vendedor, vehiculo, montoOfertaEsperado, nombreNotificacion);

        assertNotNull(notificacionActual.getEmail());
        assertNotNull(notificacionActual.getCelular());
        assertEquals(nombreNotificacion, notificacionActual.getNombreNotificacion());
        assertEquals(montoOfertaEsperado, notificacionActual.getMontoOferta(), 0.0);
    }

    @Test
    public void testEnviarNotificacionCompradorRetiraOfertaSoloEmail() {
        nombreNotificacion = COMPRADOR_RETIRA_OFERTA;


        Notificacion notificacionActual = notificacionService.enviarNotificacion(vendedor, vehiculo, oferta.getMontoOferta(), nombreNotificacion);

        assertNotNull(notificacionActual.getEmail());
        assertEquals(nombreNotificacion, notificacionActual.getNombreNotificacion());

    }

    @Test
    public void testEnviarNotificacionVehiculoExpiradoSoloEmail() {
        nombreNotificacion = VEHICULO_EXPIRADO;


        Notificacion notificacionActual = notificacionService.enviarNotificacion(vendedor, vehiculo, 0, nombreNotificacion);

        assertNotNull(notificacionActual.getEmail());
        assertEquals(nombreNotificacion, notificacionActual.getNombreNotificacion());

    }

    @Test
    public void testEnviarNotificacionNuevoVehiculoEnVentaSoloEmail() {
        nombreNotificacion = NUEVO_VEHICULO_EN_VENTA;
        comprador.setAceptaNotificacionSms(false);

        Notificacion notificacionActual = notificacionService.enviarNotificacion(comprador, vehiculo, 0, nombreNotificacion);

        assertNotNull(notificacionActual.getEmail());
        assertNull(notificacionActual.getCelular());
        assertEquals(nombreNotificacion, notificacionActual.getNombreNotificacion());
    }

    @Test
    public void testEnviarNotificacionNuevoVehiculoEnVentaSoloSms() {
        nombreNotificacion = NUEVO_VEHICULO_EN_VENTA;
        comprador.setAceptaNotificacionSms(true);
        comprador.setUnsuscribcionesEmail(Collections.singletonList(nombreNotificacion));

        Notificacion notificacionActual = notificacionService.enviarNotificacion(comprador, vehiculo, 0, nombreNotificacion);

        assertNotNull(notificacionActual.getCelular());
        assertNull(notificacionActual.getEmail());
        assertEquals(nombreNotificacion, notificacionActual.getNombreNotificacion());
    }

    @Test
    public void testEnviarNotificacionNuevoVehiculoEnVentaAmbos() {
        nombreNotificacion = NUEVO_VEHICULO_EN_VENTA;
        comprador.setCelular("69587463");
        comprador.setAceptaNotificacionSms(true);

        Notificacion notificacionActual = notificacionService.enviarNotificacion(comprador, vehiculo, 0, nombreNotificacion);

        assertNotNull(notificacionActual.getEmail());
        assertNotNull(notificacionActual.getCelular());
        assertEquals(nombreNotificacion, notificacionActual.getNombreNotificacion());
    }

    @Test
    public void testEnviarNotificacionVendedorContraOfertaSoloEmail() {
        nombreNotificacion = VENDEDOR_CONTRAOFERTA;

        Notificacion notificacionActual = notificacionService.enviarNotificacion(comprador, vehiculo, oferta.getMontoContraOferta(), nombreNotificacion);

        assertNotNull(notificacionActual.getEmail());
        assertEquals(nombreNotificacion, notificacionActual.getNombreNotificacion());
    }

    @Test
    public void testEnviarNotificacionVendedorAceptaOfertaSoloEmail() {
        nombreNotificacion = VENDEDOR_ACEPTA_OFERTA;
        comprador.setAceptaNotificacionSms(false);

        Notificacion notificacionActual = notificacionService.enviarNotificacion(comprador, vehiculo, oferta.getMontoOferta(), nombreNotificacion);

        assertNotNull(notificacionActual.getEmail());
        assertNull(notificacionActual.getCelular());
        assertEquals(nombreNotificacion, notificacionActual.getNombreNotificacion());
    }

    @Test
    public void testEnviarNotificacionVendedorAceptaOfertaSoloSms() {
        nombreNotificacion = VENDEDOR_ACEPTA_OFERTA;
        comprador.setAceptaNotificacionSms(true);
        comprador.setUnsuscribcionesEmail(Collections.singletonList(nombreNotificacion));

        Notificacion notificacionActual = notificacionService.enviarNotificacion(comprador, vehiculo, oferta.getMontoOferta(), nombreNotificacion);

        assertNotNull(notificacionActual.getCelular());
        assertNull(notificacionActual.getEmail());
        assertEquals(nombreNotificacion, notificacionActual.getNombreNotificacion());
    }

    @Test
    public void testEnviarNotificacionVendedorAceptaOfertaAmbos() {
        nombreNotificacion = VENDEDOR_ACEPTA_OFERTA;
        comprador.setCelular("69587463");
        comprador.setAceptaNotificacionSms(true);

        Notificacion notificacionActual = notificacionService.enviarNotificacion(comprador, vehiculo, oferta.getMontoOferta(), nombreNotificacion);

        assertNotNull(notificacionActual.getEmail());
        assertNotNull(notificacionActual.getCelular());
        assertEquals(nombreNotificacion, notificacionActual.getNombreNotificacion());
    }

    @Test
    public void testEnviarNotificacionVehiculoNoDisponibleSoloEmail() {
        nombreNotificacion = VEHICULO_NO_DISPONIBLE;

        Notificacion notificacionActual = notificacionService.enviarNotificacion(comprador, vehiculo, 0, nombreNotificacion);

        assertNotNull(notificacionActual.getEmail());
        assertEquals(nombreNotificacion, notificacionActual.getNombreNotificacion());
    }

    @Test(expected = DatoInvalidoException.class)
    public void testEnviarNotificacionBotaExceptionCuandoElUsuarioNoEstaSuscritoEnAmbos() {
        nombreNotificacion = COMPRADOR_PRIMERA_OFERTA;
        vendedor.setUnsuscribcionesEmail(Collections.singletonList(nombreNotificacion));
        vendedor.setUnsuscribcionesSms(Collections.singletonList(nombreNotificacion));

        notificacionService.enviarNotificacion(vendedor, vehiculo, montoOfertaEsperado, nombreNotificacion);
    }

    @Test (expected = DatoInvalidoException.class)
    public void testEnviarNotificacionBotaExceptionCuandoNombreNotificacionEsNull(){
        nombreNotificacion = null;

        notificacionService.enviarNotificacion(vendedor,vehiculo,0,nombreNotificacion);
    }
}