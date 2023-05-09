package com.car.sales.company.services;

import com.car.sales.company.exceptions.DatoInvalidoException;
import com.car.sales.company.models.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;

import static com.car.sales.company.models.NombreNotificacion.*;
import static com.car.sales.company.models.TipoUsuario.COMPRADOR;
import static com.car.sales.company.models.TipoUsuario.VENDEDOR;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
                "rube.123-122@gmail.com", COMPRADOR, null);

        vendedor = new Usuario("Jorge", "Lopez", "ci", "5203717",
                "jorgito-122@gmail.com", VENDEDOR, null);

        vehiculo = new Vehiculo("1HGBH41JXMN109716", "Toyota", "Scion", 2020, 16000);

        publicacion = new Publicacion();
        publicacion.setVendedor(vendedor);
        publicacion.setVehiculo(vehiculo);
        publicacion.setEstaDisponibleEnLaWeb(true);

        oferta = new Oferta(14000, 0, comprador, LocalDateTime.now());
    }

    @Test
    public void testEnviarNotificacion() {
        nombreNotificacion = COMPRADOR_PRIMERA_OFERTA;
        montoOfertaEsperado = 14000;

        Notificacion notificacionActual = notificacionService.enviarNotificacion(vendedor, vehiculo, montoOfertaEsperado, nombreNotificacion);
        assertNotNull(notificacionActual);
        assertEquals(montoOfertaEsperado, notificacionActual.getMontoOferta(), 0.0);
    }

    @Test
    public void testEnviarNotificacionNuevoVehiculoEnVenta() {
        nombreNotificacion = NUEVO_VEHICULO_EN_VENTA;
        comprador.setCelular("77436412");
        comprador.setAceptaNotificacionSms(true);


        Notificacion notificacionActual = notificacionService.enviarNotificacion(comprador, vehiculo, 0, nombreNotificacion);
        assertNotNull(notificacionActual);
        assertEquals(nombreNotificacion, notificacionActual.getNombreNotificacion());
        assertNotNull(notificacionActual.getCelular());
        assertNotNull(notificacionActual.getEmail());

    }

    @Test(expected = DatoInvalidoException.class)
    public void testEnviarNotificacionBotaExceptionCuandoElUsuarioNoEstaSuscrito() {
        nombreNotificacion = VEHICULO_EXPIRADO;
        vendedor.getUnsuscripcionesEmail().add(nombreNotificacion);

        notificacionService.enviarNotificacion(vendedor, vehiculo, 0, nombreNotificacion);
    }
}