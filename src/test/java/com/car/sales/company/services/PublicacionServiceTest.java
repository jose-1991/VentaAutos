package com.car.sales.company.services;

import com.car.sales.company.exceptions.DatoInvalidoException;
import com.car.sales.company.models.Oferta;
import com.car.sales.company.models.Publicacion;
import com.car.sales.company.models.Usuario;
import com.car.sales.company.models.Vehiculo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static com.car.sales.company.models.TipoUsuario.COMPRADOR;
import static com.car.sales.company.models.TipoUsuario.VENDEDOR;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class PublicacionServiceTest {

    private LocalDateTime fechaActual;
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

        fechaActual = LocalDateTime.now();

        comprador = new Usuario("Ruben", "Sanchez", "ci", "5203746",
                "rube.123-122@gmail.com", COMPRADOR, null);

        vendedor = new Usuario("Jorge", "Lopez", "ci", "5203717",
                "jorgito-122@gmail.com", VENDEDOR, null);

        vehiculo = new Vehiculo("1HGBH41JXMN109716", "Toyota", "Scion", 2020, 16000);

        publicacion1 = new Publicacion();
        publicacion1.setVendedor(vendedor);
        publicacion1.setVehiculo(vehiculo);
        publicacion1.setOfertasCompradores(new ArrayList<>());

        publicacion1.setEstaDisponibleEnLaWeb(true);

        publicacion2 = new Publicacion();
        publicacion2.setVendedor(vendedor);
        publicacion2.setVehiculo(new Vehiculo("1HGBH41JXMN109716", "Toyota", "CH-R", 2021, 22600));
        publicacion2.setOfertasCompradores(new ArrayList<>());

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
        vendedor.setTipoUsuario(COMPRADOR);

        publicacionService.publicarVehiculo(vendedor, vehiculo);
    }

    @Test(expected = DatoInvalidoException.class)
    public void testPublicarVehiculoBotaExceptionCuandoIngresaVinConFormatoInvalido() {
        vendedor.setTipoUsuario(VENDEDOR);
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
        publicacion1.setOfertasCompradores(Collections.singletonList(new Oferta(15800, 0, comprador, fechaActual)));
        publicacion2.setOfertasCompradores(Arrays.asList(new Oferta(22000, 0, comprador, fechaActual),
                new Oferta(22400, 0, comprador, fechaActual)));

        publicacionService.vehiculosPublicados.add(publicacion1);
        publicacionService.vehiculosPublicados.add(publicacion2);

        int numeroDeBajasActual = publicacionService.darDeBajaPublicaciones();
        assertEquals(0, numeroDeBajasActual);

    }

    @Test
    public void testRePublicarVehiculo() {
        publicacion1.setOfertasCompradores(Collections.EMPTY_LIST);
        publicacion1.setEstaDisponibleEnLaWeb(false);
        publicacion1.getVehiculo().setPrecio(15000);
        nuevoPrecio = 14000;

        Publicacion publicacionActual = publicacionService.rePublicarVehiculo(publicacion1, nuevoPrecio);
        assertNotNull(publicacionActual);
        assertTrue(publicacionActual.isEstaDisponibleEnLaWeb());
    }

    @Test(expected = DatoInvalidoException.class)
    public void testRepublicarVehiculoBotaExceptionCuandoNuevoPrecioEsMayorAlPrecioActual() {
        publicacion1.getVehiculo().setPrecio(17000);
        nuevoPrecio = 18000;

        publicacionService.rePublicarVehiculo(publicacion1, nuevoPrecio);
    }
}