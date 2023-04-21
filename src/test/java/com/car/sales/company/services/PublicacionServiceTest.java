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
import java.time.Month;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class PublicacionServiceTest {

    private Integer nuevoPrecio;
    private Publicacion publicacion1;
    private Publicacion publicacion2;
    private Usuario vendedor;
    private Vehiculo vehiculo;
    @InjectMocks
    private PublicacionService publicacionService;

    @Before
    public void setUp() {


        vendedor = new Usuario("Jorge", "Lopez", "ci", "5203717",
                "vendedor", "jorgito-122@gmail.com");

        vehiculo = new Vehiculo("1HGBH41JXMN109716", "Toyota", "Scion", "2020", "16000");

        publicacion1 = new Publicacion();
        publicacion1.setVendedor(vendedor);
        publicacion1.setVehiculo(vehiculo);

        publicacion1.setEstaDisponibleEnWeb(true);

        publicacion2 = new Publicacion();
        publicacion2.setVendedor(vendedor);
        publicacion2.setVehiculo(new Vehiculo("1HGBH41JXMN109716", "Toyota", "CH-R", "2021", "22600"));

        publicacion2.setEstaDisponibleEnWeb(true);


    }

    @Test
    public void testPublicarVehiculo() {
        Publicacion publicacionActual = publicacionService.publicarVehiculo(vendedor, vehiculo);

        assertNotNull(publicacionActual);
        assertTrue(publicacionActual.isEstaDisponibleEnWeb());
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

        publicacion1.setFecha(LocalDate.of(2023, Month.APRIL, 10));
        publicacion2.setFecha(LocalDate.now());

        publicacionService.vehiculosPublicados.add(publicacion1);
        publicacionService.vehiculosPublicados.add(publicacion2);

        List<Publicacion> vehiculosPublicadosActual = publicacionService.darDeBajaPublicaciones();
        assertNotNull(vehiculosPublicadosActual);
        assertEquals(1, vehiculosPublicadosActual.size());
    }

    @Test
    public void testDarDeBajaPublicacionesNoBorraNadaCuandoPublicacionTieneOfertas() {
        publicacion1.setOfertasCompradores(Collections.singletonList(new Oferta(15800)));
        publicacion2.setOfertasCompradores(Arrays.asList(new Oferta(22000), new Oferta(22400)));

        publicacionService.vehiculosPublicados.add(publicacion1);
        publicacionService.vehiculosPublicados.add(publicacion2);

        List<Publicacion> vehiculosPublicadosActual = publicacionService.darDeBajaPublicaciones();
        assertEquals(2, vehiculosPublicadosActual.size());

    }

    @Test
    public void testRePublicarVehiculo() {
        vehiculo.setVin("1HGBH41JXMN109716");
        vehiculo.setPrecio("16000");
        nuevoPrecio = 14000;

        Publicacion publicacionActual = publicacionService.rePublicarVehiculo(vendedor, vehiculo, nuevoPrecio);
        assertNotNull(publicacionActual);
        assertTrue(publicacionService.vehiculosPublicados.contains(publicacionActual));
    }

    @Test(expected = DatoInvalidoException.class)
    public void testRepublicarVehiculoBotaExceptionCuandoNuevoPrecioEsMayorAlPrecioActual() {
        vehiculo.setPrecio("17000");
        nuevoPrecio = 18000;

        publicacionService.rePublicarVehiculo(vendedor, vehiculo, nuevoPrecio);
    }
}