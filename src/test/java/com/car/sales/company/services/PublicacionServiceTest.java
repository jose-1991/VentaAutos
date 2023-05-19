package com.car.sales.company.services;

import com.car.sales.company.exceptions.DatoInvalidoException;
import com.car.sales.company.exceptions.UsuarioNoEncontradoException;
import com.car.sales.company.models.Oferta;
import com.car.sales.company.models.Publicacion;
import com.car.sales.company.models.Usuario;
import com.car.sales.company.models.Vehiculo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.car.sales.company.models.TipoUsuario.COMPRADOR;
import static com.car.sales.company.models.TipoUsuario.VENDEDOR;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PublicacionServiceTest {

    private LocalDateTime fechaActual;
    private Integer nuevoPrecio;
    private Publicacion publicacion1;
    private Publicacion publicacion2;
    private Usuario vendedor;
    private Usuario comprador;
    private Vehiculo vehiculo;

    @Mock
    private UsuarioService usuarioServiceMock;
    @Mock
    private NotificacionService notificacionServiceMock;
    @InjectMocks
    private PublicacionService publicacionService;

    @Before
    public void setUp() {

        fechaActual = LocalDateTime.now();

        comprador = new Usuario("Ruben", "Sanchez", "ci", "5203746",
                "rube.123-122@gmail.com", COMPRADOR, null);

        vendedor = new Usuario("Jorge", "Lopez", "ci", "5203717",
                "jorgito-122@gmail.com", VENDEDOR, null);

        vehiculo = new Vehiculo("1HGBH41JXMN109716", "Toyota", "Scion", 2020);

        publicacion1 = new Publicacion();
        publicacion1.setVendedor(vendedor);
        publicacion1.setProducto(vehiculo);
        publicacion1.setOfertasCompradores(new ArrayList<>());
        publicacion1.setEstaDisponibleEnLaWeb(true);

        publicacion2 = new Publicacion();
        publicacion2.setVendedor(vendedor);
        publicacion2.setProducto(new Vehiculo("1HGBH41JXMN109716", "Toyota", "CH-R", 2021));
        publicacion2.setOfertasCompradores(new ArrayList<>());
        publicacion2.setEstaDisponibleEnLaWeb(true);
    }

    @Test
    public void testPublicarProducto() {
        List<Usuario> listaUsuarios = Collections.singletonList(comprador);
        when(usuarioServiceMock.getListaUsuariosRegistrados()).thenReturn(listaUsuarios);
        Publicacion publicacionActual = publicacionService.publicarProducto(vendedor, vehiculo);

        assertNotNull(publicacionActual);
        assertTrue(publicacionActual.isEstaDisponibleEnLaWeb());
        assertTrue(publicacionService.getProductosPublicados().contains(publicacionActual));
        verify(notificacionServiceMock).notificarTodosLosCompradores(any(), any(), any());
    }

    @Test
    public void testPublicarProducto1() {

        when(usuarioServiceMock.modificarUsuario(anyString(), anyString())).thenThrow(UsuarioNoEncontradoException.class);
        Publicacion publicacionActual = publicacionService.publicarProducto1(vendedor, vehiculo);

        assertNotNull(publicacionActual);
        assertTrue(publicacionActual.isEstaDisponibleEnLaWeb());
        assertTrue(publicacionService.getProductosPublicados().contains(publicacionActual));
        verify(notificacionServiceMock, times(0)).notificarTodosLosCompradores(any(), any(), any());
    }

    @Test(expected = DatoInvalidoException.class)
    public void testPublicarProductoBotaExceptionCuandoElTipoUsuarioNoEsVendedor() {
        vendedor.setTipoUsuario(COMPRADOR);

        publicacionService.publicarProducto(vendedor, vehiculo);
    }

    @Test(expected = DatoInvalidoException.class)
    public void testPublicarProductoBotaExceptionCuandoIngresaVinConFormatoInvalido() {
        vendedor.setTipoUsuario(VENDEDOR);
        vehiculo.setVin("123ASD123556GF");

        publicacionService.publicarProducto(vendedor, vehiculo);
    }

    @Test
    public void testDarDeBajaPublicaciones() {

        publicacion1.setFecha(LocalDate.of(2023, Month.APRIL, 20));
        publicacion2.setFecha(LocalDate.now());

        publicacionService.getProductosPublicados().add(publicacion1);
        publicacionService.getProductosPublicados().add(publicacion2);

        int numeroDeBajasActual = publicacionService.darDeBajaPublicaciones();
        assertEquals(1, numeroDeBajasActual);
        verify(notificacionServiceMock).enviarNotificacion(any(), any(), anyDouble(), anyDouble(), any());
    }

    @Test
    public void testDarDeBajaPublicacionesNoInhabilitaNadaCuandoPublicacionTieneOfertas() {
        publicacion1.setOfertasCompradores(Collections.singletonList(new Oferta(15800, 0, comprador, fechaActual)));
        publicacion2.setOfertasCompradores(Arrays.asList(new Oferta(22000, 0, comprador, fechaActual),
                new Oferta(22400, 0, comprador, fechaActual)));

        publicacionService.getProductosPublicados().add(publicacion1);
        publicacionService.getProductosPublicados().add(publicacion2);

        int numeroDeBajasActual = publicacionService.darDeBajaPublicaciones();
        assertEquals(0, numeroDeBajasActual);
        verify(notificacionServiceMock, times(0)).enviarNotificacion(any(), any(), anyDouble(), anyDouble(), any());
    }

    @Test
    public void testRePublicarProducto() {
        publicacion1.setOfertasCompradores(Collections.EMPTY_LIST);
        publicacion1.setEstaDisponibleEnLaWeb(false);
        publicacion1.setPrecio(15000);
        nuevoPrecio = 14000;

        Publicacion publicacionActual = publicacionService.rePublicarProducto(publicacion1, nuevoPrecio);
        assertNotNull(publicacionActual);
        assertTrue(publicacionActual.isEstaDisponibleEnLaWeb());
        verify(notificacionServiceMock).notificarTodosLosCompradores(any(), any(), any());
    }

    @Test(expected = DatoInvalidoException.class)
    public void testRepublicarProductoBotaExceptionCuandoNuevoPrecioEsMayorAlPrecioActual() {
        publicacion1.setPrecio(17000);
        nuevoPrecio = 18000;

        publicacionService.rePublicarProducto(publicacion1, nuevoPrecio);
    }
}