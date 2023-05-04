package com.car.sales.company.services;

import com.car.sales.company.exceptions.DatoInvalidoException;
import com.car.sales.company.exceptions.UsuarioNoEncontradoException;
import com.models.Oferta;
import com.models.Publicacion;
import com.models.Usuario;
import com.models.Vehiculo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import static com.models.Accion.*;

@RunWith(MockitoJUnitRunner.class)
public class VentaServiceTest {

    String tipoUsuario;
    String montoOferta;
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
        tipoUsuario = "Comprador";
        montoOferta = "17000";
        comprador = new Usuario("Ruben", "Sanchez", "ci", "5203746",
                "comprador", "rube.123-122@gmail.com");

        vendedor = new Usuario("Jorge", "Lopez", "ci", "5203717",
                "vendedor", "jorgito-122@gmail.com");

        vehiculo = new Vehiculo("1HGBH41JXMN109716", "Toyota", "Scion", "2020", "16000");

        oferta = new Oferta("17000", comprador);
        oferta2 = new Oferta("16000", comprador);
        oferta3 = new Oferta("16600", comprador);

        publicacion = new Publicacion();
        publicacion.setVendedor(vendedor);
        publicacion.setVehiculo(vehiculo);

        publicacion.getOfertasCompradores().add(oferta);
        publicacion.getOfertasCompradores().add(oferta2);
        publicacion.getOfertasCompradores().add(oferta3);
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
        montoOferta = "-24124fa";

        ventaService.realizarPrimeraOferta(publicacion, comprador, montoOferta);
    }

    @Test
    public void interactuarCaseContraOfertar() {

        tipoUsuario = "Vendedor";
        String nuevoMontoEsperado = "17300";

        Publicacion publicacionActual = ventaService.interactuar(publicacion, oferta, tipoUsuario, CONTRA_OFERTAR, nuevoMontoEsperado);

        Assert.assertEquals(nuevoMontoEsperado, oferta.getMonto());
    }

    @Test(expected = UsuarioNoEncontradoException.class)
    public void interactuarCaseContraOfertarBotaExceptionCuandoEsComprador() {
        tipoUsuario = "Comprador";
        String nuevoMontoEsperado = "17300";
        ventaService.interactuar(publicacion, oferta, tipoUsuario, CONTRA_OFERTAR, nuevoMontoEsperado);
    }

    @Test
    public void interactuarCaseAceptar() {
        tipoUsuario = "Vendedor";

        Publicacion publicacionActual = ventaService.interactuar(publicacion, oferta, tipoUsuario, ACEPTAR, "");

        Assert.assertEquals(1, publicacionActual.getOfertasCompradores().size());
        Assert.assertFalse(publicacionActual.isEstaDisponibleEnLaWeb());
    }

    @Test
    public void interactuarCaseRetirar() {
        tipoUsuario = "comprador";
        Publicacion publicacionActual = ventaService.interactuar(publicacion, oferta, tipoUsuario, RETIRAR, "");
        Assert.assertFalse(publicacionActual.getOfertasCompradores().contains(oferta));

    }

    @Test(expected = DatoInvalidoException.class)
    public void interactuarCaseRetirarBotaExceptionCuandoNoExisteLaOferta() {
        Oferta ofertaEsperada = new Oferta("123908", comprador);
        ventaService.interactuar(publicacion, ofertaEsperada, tipoUsuario, RETIRAR, "");
    }

    @Test
    public void interactuarCaseRechazar() {

        Publicacion publicacionActual = ventaService.interactuar(publicacion, oferta, tipoUsuario, RECHAZAR, "");
        Assert.assertFalse(publicacionActual.getOfertasCompradores().contains(oferta));
    }


}