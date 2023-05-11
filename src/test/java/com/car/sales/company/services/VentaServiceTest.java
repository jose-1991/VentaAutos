package com.car.sales.company.services;

import com.car.sales.company.exceptions.DatoInvalidoException;
import com.car.sales.company.exceptions.UsuarioNoEncontradoException;
import com.car.sales.company.models.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.Arrays;

import static com.car.sales.company.models.Accion.*;
import static com.car.sales.company.models.TipoUsuario.*;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class VentaServiceTest {

    TipoUsuario tipoUsuario;
    double montoOferta;
    double montoContraOferta;
    Oferta oferta;
    Oferta oferta2;
    Oferta oferta3;
    Usuario comprador;
    Usuario vendedor;
    Vehiculo vehiculo;
    Publicacion publicacion;
    @InjectMocks
    VentaService ventaService;

    @Before
    public void setUp() {
        tipoUsuario = COMPRADOR;
        montoOferta = 1730;
        montoContraOferta = 1203;
        comprador = new Usuario("Ruben", "Sanchez", "ci", "5203746",
                "rube.123-122@gmail.com", COMPRADOR, null);

        vendedor = new Usuario("Jorge", "Lopez", "ci", "5203717",
                "jorgito-122@gmail.com", VENDEDOR, null);

        vehiculo = new Vehiculo("1HGBH41JXMN109716", "Toyota", "Scion", 2020, 16000);

        oferta = new Oferta(17000, 0, comprador, LocalDateTime.now());
        oferta2 = new Oferta(14000, 0, comprador, LocalDateTime.now());
        oferta3 = new Oferta(13600, 0, comprador, LocalDateTime.now());

        publicacion = new Publicacion();
        publicacion.setVendedor(vendedor);
        publicacion.setVehiculo(vehiculo);

        publicacion.setOfertasCompradores(Arrays.asList(oferta, oferta2, oferta3));
    }

    @Test
    public void realizarPrimeraOferta() {
        comprador.setIdentificacion("5203717");
        montoOferta = 17000;

        Oferta ofertaActual = ventaService.realizarPrimeraOferta(publicacion, comprador, montoOferta);

        Assert.assertNotNull(ofertaActual);
        assertEquals("5203717", ofertaActual.getComprador().getIdentificacion());
    }

    @Test(expected = DatoInvalidoException.class)
    public void realizarPrimeraOfertaBotaExceptionCuandoNoSeIngresaUnMontoValido() {
        montoOferta = -4650;

        ventaService.realizarPrimeraOferta(publicacion, comprador, montoOferta);
    }

    @Test
    public void interactuarCaseContraOfertar() {
        tipoUsuario = VENDEDOR;
        montoContraOferta = 17300;

        Oferta ofertaActual = ventaService.interactuar(publicacion, oferta, tipoUsuario, CONTRA_OFERTAR, montoContraOferta);

        assertNotNull(ofertaActual);
        assertEquals(montoContraOferta, ofertaActual.getMontoContraOferta(), 0.0);
    }

    @Test(expected = UsuarioNoEncontradoException.class)
    public void interactuarCaseContraOfertarBotaExceptionCuandoNoEsVendedor() {
        tipoUsuario = COMPRADOR;
        montoContraOferta = 17300;

        ventaService.interactuar(publicacion, oferta, tipoUsuario, CONTRA_OFERTAR, montoContraOferta);
    }

    @Test
    public void interactuarCaseAceptarCaseVendedor() {
        tipoUsuario = VENDEDOR;
        oferta.setMontoOferta(18000);
        oferta.setFechaOferta(LocalDateTime.now());
        oferta2.setMontoOferta(18000);
        oferta2.setFechaOferta(LocalDateTime.now().minusHours(8));

        Oferta ofertaActual = ventaService.interactuar(publicacion, null, tipoUsuario, ACEPTAR, 0);

        assertNotNull(ofertaActual);
        assertEquals(oferta2.getMontoOferta(), ofertaActual.getMontoOferta(), 0.0);
        assertEquals(oferta2.getFechaOferta(), ofertaActual.getFechaOferta());
    }

    @Test
    public void interactuarCaseAceptarCaseComprador() {
        tipoUsuario = COMPRADOR;
        oferta.setMontoContraOferta(200);

        Oferta ofertaActual = ventaService.interactuar(publicacion, oferta, tipoUsuario, ACEPTAR, 0);

        assertNotNull(ofertaActual);
        assertEquals(oferta.getMontoContraOferta(), ofertaActual.getMontoContraOferta(), 0.0);
    }

    @Test(expected = UsuarioNoEncontradoException.class)
    public void interactuarCaseAceptarCaseCompradorBotaExceptionCuandoNoExisteContraOferta() {
        oferta.setMontoContraOferta(0);

        ventaService.interactuar(publicacion, oferta, tipoUsuario, ACEPTAR, 0);
    }

    @Test
    public void interactuarCaseRetirar() {
        tipoUsuario = COMPRADOR;

        Oferta ofertaActual = ventaService.interactuar(publicacion, oferta, tipoUsuario, RETIRAR, 0);
        Assert.assertTrue(ofertaActual.isInactivo());
    }

    @Test(expected = DatoInvalidoException.class)
    public void interactuarCaseRetirarBotaExceptionCuandoNoEsComprador() {
        tipoUsuario = VENDEDOR;

        ventaService.interactuar(publicacion, oferta, tipoUsuario, RETIRAR, 0);
    }
}