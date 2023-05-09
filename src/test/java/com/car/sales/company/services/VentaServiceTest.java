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
import static com.car.sales.company.models.TipoUsuario.COMPRADOR;
import static com.car.sales.company.models.TipoUsuario.VENDEDOR;

@RunWith(MockitoJUnitRunner.class)
public class VentaServiceTest {

    TipoUsuario tipoUsuario;
    double montoOferta;
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
        montoOferta = 17000;
        comprador = new Usuario("Ruben", "Sanchez", "ci", "5203746",
                "rube.123-122@gmail.com", COMPRADOR, null);

        vendedor = new Usuario("Jorge", "Lopez", "ci", "5203717",
                "jorgito-122@gmail.com", VENDEDOR, null);

        vehiculo = new Vehiculo("1HGBH41JXMN109716", "Toyota", "Scion", 2020, 16000);

        oferta = new Oferta(17000, 0, comprador, LocalDateTime.now());
        oferta2 = new Oferta(16000, 0, comprador, LocalDateTime.now());
        oferta3 = new Oferta(16600, 0, comprador, LocalDateTime.now());

        publicacion = new Publicacion();
        publicacion.setVendedor(vendedor);
        publicacion.setVehiculo(vehiculo);

        publicacion.setOfertasCompradores(Arrays.asList(oferta, oferta2, oferta3));
    }

    @Test
    public void realizarPrimeraOferta() {
        comprador.setIdentificacion("5203717");

        Oferta ofertaActual = ventaService.realizarPrimeraOferta(publicacion, comprador, montoOferta);
        Assert.assertNotNull(ofertaActual);
        Assert.assertEquals("5203717", ofertaActual.getComprador().getIdentificacion());
    }

    @Test(expected = DatoInvalidoException.class)
    public void realizarPrimeraOfertaBotaExceptionCuandoNoSeIngresaUnMontoValido() {
        montoOferta = 0;

        ventaService.realizarPrimeraOferta(publicacion, comprador, montoOferta);
    }

    @Test
    public void interactuarCaseContraOfertar() {

        tipoUsuario = VENDEDOR;
        double montoContraOferta = 17300;

        Publicacion publicacionActual = ventaService.interactuar(publicacion, oferta, tipoUsuario, CONTRA_OFERTAR, montoContraOferta);

        Assert.assertEquals(montoContraOferta, oferta.getMontoContraOferta(), 0.0);
    }

    @Test(expected = UsuarioNoEncontradoException.class)
    public void interactuarCaseContraOfertarBotaExceptionCuandoEsComprador() {
        tipoUsuario = COMPRADOR;
        montoOferta = 17300;
        ventaService.interactuar(publicacion, oferta, tipoUsuario, CONTRA_OFERTAR, montoOferta);
    }

    @Test
    public void interactuarCaseAceptar() {
        tipoUsuario = VENDEDOR;

        Publicacion publicacionActual = ventaService.interactuar(publicacion, oferta, tipoUsuario, ACEPTAR, 0);

        Assert.assertEquals(3, publicacionActual.getOfertasCompradores().size());
        Assert.assertFalse(publicacionActual.isEstaDisponibleEnLaWeb());
    }

    @Test
    public void interactuarCaseRetirar() {
        tipoUsuario = COMPRADOR;
        Publicacion publicacionActual = ventaService.interactuar(publicacion, oferta, tipoUsuario, RETIRAR, 0);
        Assert.assertTrue(oferta.isInactivo());

    }

    @Test(expected = DatoInvalidoException.class)
    public void interactuarCaseRetirarBotaExceptionCuandoNoExisteLaOferta() {
        Oferta ofertaEsperada = new Oferta(123908, 0, comprador, LocalDateTime.now());
        ventaService.interactuar(publicacion, ofertaEsperada, tipoUsuario, RETIRAR, 0);
    }

    @Test
    public void interactuarCaseRechazar() {
        tipoUsuario = VENDEDOR;
        Publicacion publicacionActual = ventaService.interactuar(publicacion, oferta, tipoUsuario, RECHAZAR, 0);
        Assert.assertTrue(oferta.isInactivo());
    }


}