package com.car.sales.company.services;

import com.car.sales.company.exceptions.DatoInvalidoException;
import com.models.Oferta;
import com.models.Publicacion;
import com.models.Usuario;
import com.models.Vehiculo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class PublicacionServiceTest {

    private Integer nuevoPrecio;
    private Publicacion publicacion1;
    private Publicacion publicacion2;
    private Usuario vendedor;
    private Usuario comprador;
    private Vehiculo vehiculo;
    @InjectMocks
    private PublicacionService publicacionService;

    @Before
    public void setUp() {


        comprador = new Usuario("Ruben", "Sanchez", "ci", "5203746",
                "comprador", "rube.123-122@gmail.com");

        vendedor = new Usuario("Jorge", "Lopez", "ci", "5203717",
                "vendedor", "jorgito-122@gmail.com");

        vehiculo = new Vehiculo("1HGBH41JXMN109716", "Toyota", "Scion", "2020", "16000");

        publicacion1 = new Publicacion();
        publicacion1.setVendedor(vendedor);
        publicacion1.setVehiculo(vehiculo);

        publicacion1.setEstaDisponibleEnLaWeb(true);

        publicacion2 = new Publicacion();
        publicacion2.setVendedor(vendedor);
        publicacion2.setVehiculo(new Vehiculo("1HGBH41JXMN109716", "Toyota", "CH-R", "2021", "22600"));

        publicacion2.setEstaDisponibleEnLaWeb(true);


    }

    @Test
    public void testPublicarVehiculo() {
        Publicacion publicacionActual = publicacionService.publicarVehiculo(vendedor, vehiculo);

        assertNotNull(publicacionActual);
        assertTrue(publicacionActual.isEstaDisponibleEnLaWeb());
        assertTrue(publicacionService.vehiculosPublicados.contains(publicacionActual));
    }

    @Test(expected = DatoInvalidoException.class)
    public void testPublicarVehiculoBotaExceptionCuandoElTipoUsuarioNoEsVendedor() {
        vendedor.setTipoUsuario("comprador");

        publicacionService.publicarVehiculo(vendedor, vehiculo);
    }

    @Test(expected = DatoInvalidoException.class)
    public void testPublicarVehiculoBotaExceptionCuandoIngresaVinConFormatoInvalido() {
        vendedor.setTipoUsuario("vendedor");
        vehiculo.setVin("123ASD123556GF");

        publicacionService.publicarVehiculo(vendedor, vehiculo);
    }

    @Test
    public void testDarDeBajaPublicaciones() {

        publicacion1.setFecha(LocalDate.of(2023, Month.APRIL, 20));
        publicacion2.setFecha(LocalDate.now());

        publicacionService.vehiculosPublicados.add(publicacion1);
        publicacionService.vehiculosPublicados.add(publicacion2);

        int numeroDeBajasActual = publicacionService.darDeBajaPublicaciones();
        assertEquals(1, numeroDeBajasActual);
    }

    @Test
    public void testDarDeBajaPublicacionesNoInhabilitaNadaCuandoPublicacionTieneOfertas() {
        publicacion1.setOfertasCompradores(Collections.singletonList(new Oferta("15800", comprador)));
        publicacion2.setOfertasCompradores(Arrays.asList(new Oferta("22000", comprador), new Oferta("22400", comprador)));

        publicacionService.vehiculosPublicados.add(publicacion1);
        publicacionService.vehiculosPublicados.add(publicacion2);

        int numeroDeBajasActual = publicacionService.darDeBajaPublicaciones();
        assertEquals(0, numeroDeBajasActual);

    }

    @Test
    public void testRePublicarVehiculo() {
        publicacion1.setOfertasCompradores(Collections.EMPTY_LIST);
        publicacion1.setEstaDisponibleEnLaWeb(false);
        publicacion1.getVehiculo().setPrecio("15000");
        nuevoPrecio = 14000;

        Publicacion publicacionActual = publicacionService.rePublicarVehiculo(publicacion1, nuevoPrecio);
        assertNotNull(publicacionActual);
        assertTrue(publicacionActual.isEstaDisponibleEnLaWeb());
    }

    @Test(expected = DatoInvalidoException.class)
    public void testRepublicarVehiculoBotaExceptionCuandoNuevoPrecioEsMayorAlPrecioActual() {
        publicacion1.getVehiculo().setPrecio("17000");
        nuevoPrecio = 18000;

        publicacionService.rePublicarVehiculo(publicacion1, nuevoPrecio);
    }
}