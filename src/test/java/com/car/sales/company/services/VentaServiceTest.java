package com.car.sales.company.services;

import com.car.sales.company.exceptions.DatoInvalidoException;
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
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class VentaServiceTest {

    private double montoOferta;
    private double montoContraOferta;
    private Oferta oferta;
    private Oferta oferta2;
    private Oferta oferta3;
    private Usuario comprador;
    private Usuario comprador2;
    private Usuario comprador3;
    private Usuario vendedor;
    private Vehiculo vehiculo;
    private Publicacion publicacion;
    @InjectMocks
    private VentaService ventaService;

    @Before
    public void setUp() {

        montoOferta = 1730;
        montoContraOferta = 1203;
        comprador = new Usuario("Ruben", "Sanchez", "ci", "5203746",
                "rube.123-122@gmail.com", COMPRADOR, null);
        comprador2 = new Usuario("Joel", "Sanchez", "ci", "1245362",
                "rube.123-122@gmail.com", COMPRADOR, null);
        comprador3 = new Usuario("Jenny", "Soria", "ci", "9763642",
                "rube.123-122@gmail.com", COMPRADOR, null);

        vendedor = new Usuario("Jorge", "Lopez", "ci", "5203717",
                "jorgito-122@gmail.com", VENDEDOR, null);

        vehiculo = new Vehiculo("1HGBH41JXMN109716", "Toyota", "Scion", 2020, 16000);

        oferta = new Oferta(17000, 0, comprador, LocalDateTime.now());
        oferta2 = new Oferta(14000, 0, comprador2, LocalDateTime.now());
        oferta3 = new Oferta(18600, 0, comprador3, LocalDateTime.now());

        publicacion = new Publicacion();
        publicacion.setVendedor(vendedor);
        publicacion.setVehiculo(vehiculo);

        publicacion.setOfertasCompradores(Arrays.asList(oferta, oferta2, oferta3));
    }

    @Test
    public void testRealizarPrimeraOferta() {
        comprador.setIdentificacion("5203717");
        montoOferta = 17000;

        Oferta ofertaActual = ventaService.realizarPrimeraOferta(publicacion, comprador, montoOferta);

        Assert.assertNotNull(ofertaActual);
        assertEquals(1, publicacion.getOfertasCompradores().size());
    }

    @Test(expected = DatoInvalidoException.class)
    public void testRealizarPrimeraOfertaBotaExceptionCuandoNoSeIngresaUnMontoValido() {
        montoOferta = -4650;

        ventaService.realizarPrimeraOferta(publicacion, comprador, montoOferta);
    }

    // TODO: 15/5/2023  que se testea en la publicacion en contra oferta, si solo setea el montocontraoferta
    @Test
    public void testInteractuarCaseContraOfertarCaseVendedor() {
        montoContraOferta = 19000;

        Publicacion publicacionActual = ventaService.interactuar(publicacion, comprador, CONTRA_OFERTAR, montoContraOferta);

        assertEquals(montoContraOferta, publicacionActual.getOfertasCompradores().get(0).getMontoContraOferta(), 0.0);
    }

    @Test(expected = DatoInvalidoException.class)
    public void testInteractuarCaseContraOfertarBotaExceptionCuandoElUsuarioEsVendedor() {
        montoContraOferta = 19000;

        ventaService.interactuar(publicacion, vendedor, CONTRA_OFERTAR, montoContraOferta);
    }

    @Test
    public void testInteractuarCaseAceptarCaseVendedorCuandoHayUnaMejorOferta() {
        oferta3.setMontoOferta(19000);
        Publicacion publicacionActual = ventaService.interactuar(publicacion, vendedor, ACEPTAR_OFERTA, 0);

        assertFalse(publicacionActual.isEstaDisponibleEnLaWeb());
        assertEquals(19000, publicacionActual.getOfertasCompradores().get(2).getMontoOferta(), 0.0);
        assertTrue(publicacionActual.getOfertasCompradores().get(0).isInactivo());
        assertTrue(publicacionActual.getOfertasCompradores().get(1).isInactivo());
    }

    @Test
    public void testInteractuarCaseAceptarCaseVendedorCuandoHayDosMejoresOfertas() {
        oferta3.setMontoOferta(19000);
        oferta3.setFechaOferta(LocalDateTime.now());
        oferta2.setMontoOferta(19000);
        oferta2.setFechaOferta(LocalDateTime.now().minusHours(3));

        Publicacion publicacionActual = ventaService.interactuar(publicacion, vendedor, ACEPTAR_OFERTA, 0);

        assertFalse(publicacionActual.isEstaDisponibleEnLaWeb());
        assertEquals(19000, publicacionActual.getOfertasCompradores().get(1).getMontoOferta(), 0.0);
        assertTrue(publicacionActual.getOfertasCompradores().get(0).isInactivo());
        assertTrue(publicacionActual.getOfertasCompradores().get(2).isInactivo());
    }

    @Test
    public void testInteractuarCaseAceptarCaseComprador() {
        oferta3.setMontoOferta(19000);
        oferta3.setMontoContraOferta(20000);
        Publicacion publicacionActual = ventaService.interactuar(publicacion, comprador3, ACEPTAR_OFERTA, 0);

        assertFalse(publicacionActual.isEstaDisponibleEnLaWeb());
        assertEquals(20000, publicacionActual.getOfertasCompradores().get(2).getMontoContraOferta(), 0.0);
        assertTrue(publicacionActual.getOfertasCompradores().get(0).isInactivo());
        assertTrue(publicacionActual.getOfertasCompradores().get(1).isInactivo());

    }

    @Test(expected = DatoInvalidoException.class)
    public void testInteractuarCaseRetirarBotaExceptionCuandoElUsuarioEsVendedor() {

        ventaService.interactuar(publicacion, vendedor, RETIRAR_OFERTA, 0);
    }

    @Test
    public void testInteractuarCaseRetirarCaseComprador() {

        Publicacion publicacionActual = ventaService.interactuar(publicacion, comprador, RETIRAR_OFERTA, 0);

        assertTrue(publicacionActual.getOfertasCompradores().get(0).isInactivo());
    }
}