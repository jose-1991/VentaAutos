package com.car.sales.company.services;

import com.models.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static com.models.Accion.UNSUSCRIBIR;
import static com.models.TipoNotificacion.AMBOS;
import static com.models.TipoNotificacion.EMAIL;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class NotificacionServiceTest {

    Vehiculo vehiculo;
    Usuario vendedor;
    Usuario comprador;
    Publicacion publicacion;
    Oferta oferta;
    String nombreNotificacionEsperado;
    @InjectMocks
    NotificacionService notificacionService;

    @Before
    public void setUp() {

        comprador = new Usuario("Ruben", "Sanchez", "ci", "5203746",
                "comprador", "rube.123-122@gmail.com");

        vendedor = new Usuario("Jorge", "Lopez", "ci", "5203717",
                "vendedor", "jorgito-122@gmail.com");

        vehiculo = new Vehiculo("1HGBH41JXMN109716", "Toyota", "Scion", "2020", "16000");

        publicacion = new Publicacion();
        publicacion.setVendedor(vendedor);
        publicacion.setVehiculo(vehiculo);
        publicacion.setEstaDisponibleEnLaWeb(true);

        oferta = new Oferta("14000", comprador);
    }

    @Test
    public void testEnviarNotificacion() {
        nombreNotificacionEsperado = "CompradorPrimeraOferta";

        Notificacion notificacionActual = notificacionService.ValidarNotificacion(publicacion, oferta, nombreNotificacionEsperado,
                publicacion.getVendedor(), AMBOS);
        assertNotNull(notificacionActual);
        assertEquals(nombreNotificacionEsperado, notificacionActual.getNombre());
    }

    @Test
    public void testActualizarSuscripcion() {
        nombreNotificacionEsperado = "VendedorAceptaOferta";

        List<String> listaEsperada = notificacionService.actualizarSuscripcion(oferta.getComprador(),
                nombreNotificacionEsperado, UNSUSCRIBIR, EMAIL);

        assertNotNull(listaEsperada);
        assertTrue(listaEsperada.contains(nombreNotificacionEsperado));


    }
}