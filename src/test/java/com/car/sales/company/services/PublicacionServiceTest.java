package com.car.sales.company.services;

import com.car.sales.company.dao.PublicacionDAO;
import com.car.sales.company.dao.UsuarioDAO;
import com.car.sales.company.exceptions.DatoInvalidoException;
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
import java.util.*;

import static com.car.sales.company.models.TipoUsuario.COMPRADOR;
import static com.car.sales.company.models.TipoUsuario.VENDEDOR;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PublicacionServiceTest {

    private LocalDateTime fechaActual;
    private double nuevoPrecio;
    private double precio;
    private Publicacion publicacion1;
    private Publicacion publicacion2;
    private Usuario vendedor;
    private Usuario comprador;
    private Usuario comprador2;
    private Vehiculo vehiculo;
    private List<Usuario> listaUsuariosEsperada;
    private List<Publicacion> listaPublicacionesEsperada;

    @Mock
    private UsuarioDAO usuarioDaoMock;
    @Mock
    private PublicacionDAO publicacionDaoMock;
    @Mock
    private NotificacionService notificacionServiceMock;
    @InjectMocks
    private PublicacionService publicacionService;

    @Before
    public void setUp() {
        fechaActual = LocalDateTime.now();
        precio = 10;

        comprador = new Usuario("Ruben", "Sanchez", "ci", "5203746",
                "rube.123-122@gmail.com", COMPRADOR, null);
        comprador2 = new Usuario("Jaime", "Mendoza", "ci", "5398547",
                "jmp.123-122@gmail.com", COMPRADOR, null);

        vendedor = new Usuario("Jorge", "Lopez", "ci", "5203717",
                "jorgito-122@gmail.com", VENDEDOR, null);

        vehiculo = obtenerVehiculoRandom();

        publicacion1 = new Publicacion();
        publicacion1.setVendedor(vendedor);
        publicacion1.setProducto(vehiculo);
        publicacion1.setOfertasCompradores(new ArrayList<>());
        publicacion1.setEstaDisponibleEnWeb(true);

        publicacion2 = new Publicacion();
        publicacion2.setVendedor(vendedor);
        publicacion2.setProducto(obtenerVehiculoRandom());
        publicacion2.setOfertasCompradores(new ArrayList<>());
        publicacion2.setEstaDisponibleEnWeb(true);

        listaUsuariosEsperada = Arrays.asList(comprador, comprador2);
    }

    @Test
    public void testPublicarProducto() {

        when(usuarioDaoMock.obtenerCompradores()).thenReturn(listaUsuariosEsperada);
        Publicacion publicacionActual = publicacionService.publicarProducto(vendedor, vehiculo, precio);

        assertNotNull(publicacionActual);
        assertTrue(publicacionActual.isEstaDisponibleEnWeb());
        verify(publicacionDaoMock).registrarPublicacionProducto(any());
    }

    @Test(expected = DatoInvalidoException.class)
    public void testPublicarProductoBotaExceptionCuandoElTipoUsuarioNoEsVendedor() {

        publicacionService.publicarProducto(comprador, vehiculo, precio);
    }

    @Test(expected = DatoInvalidoException.class)
    public void testPublicarProductoBotaExceptionCuandoIngresaVinConFormatoInvalido() {
        vendedor.setTipoUsuario(VENDEDOR);
        vehiculo.setVin("12323556GF");

        publicacionService.publicarProducto(vendedor, vehiculo, precio);
    }

    @Test
    public void testDarDeBajaPublicaciones() {
        listaPublicacionesEsperada = Collections.singletonList(publicacion1);
        publicacion1.setFecha(LocalDate.now().minusDays(9));
        publicacion2.setFecha(LocalDate.now());

        when(publicacionDaoMock.obtenerPublicacionesParaDarDeBaja()).thenReturn(listaPublicacionesEsperada);
        int numeroDeBajasActual = publicacionService.darDeBajaPublicaciones();

        assertEquals(listaPublicacionesEsperada.size(), numeroDeBajasActual);
        verify(notificacionServiceMock).enviarNotificacion(any(), any(), anyDouble(), anyDouble(), any());
        verify(publicacionDaoMock).darDeBajaPublicaciones(anyList());

    }

    @Test
    public void testDarDeBajaPublicacionesNoInhabilitaNadaCuandoPublicacionTieneOfertas() {
        listaPublicacionesEsperada = Collections.EMPTY_LIST;
        publicacion1.setOfertasCompradores(Collections.singletonList(new Oferta(comprador, 15800, 0, fechaActual)));
        publicacion2.setOfertasCompradores(Arrays.asList(new Oferta(comprador, 22000, 0, fechaActual),
                new Oferta(comprador, 22400, 0, fechaActual)));

        when(publicacionDaoMock.obtenerPublicacionesParaDarDeBaja()).thenReturn(listaPublicacionesEsperada);
        int numeroDeBajasActual = publicacionService.darDeBajaPublicaciones();
        assertEquals(listaPublicacionesEsperada.size(), numeroDeBajasActual);
        verify(notificacionServiceMock, times(0)).enviarNotificacion(any(), any(), anyDouble(), anyDouble(), any());
    }

    @Test
    public void testRePublicarProducto() {
        publicacion1.setOfertasCompradores(Collections.EMPTY_LIST);
        publicacion1.setEstaDisponibleEnWeb(false);
        publicacion1.setPrecio(15000);
        nuevoPrecio = 14000;

        Publicacion publicacionActual = publicacionService.rePublicarProducto(publicacion1, nuevoPrecio);

        assertNotNull(publicacionActual);
        assertTrue(publicacionActual.isEstaDisponibleEnWeb());
        assertEquals(LocalDate.now(), publicacionActual.getFecha());
        verify(publicacionDaoMock).rePublicarProducto(any(), anyDouble());
    }

    @Test(expected = DatoInvalidoException.class)
    public void testRepublicarProductoBotaExceptionCuandoNuevoPrecioEsMayorAlPrecioActual() {
        publicacion1.setPrecio(17000);
        nuevoPrecio = 18000;

        publicacionService.rePublicarProducto(publicacion1, nuevoPrecio);
    }

    @Test
    public void testObtenerVehiculoRandom(){

       Vehiculo vehiculoActual = obtenerVehiculoRandom();

       assertNotNull(vehiculoActual);
    }

    public static Vehiculo obtenerVehiculoRandom() {
        Vehiculo vehiculo = new Vehiculo();
        List<String> listaMarcas = Arrays.asList("Toyota", "Nissan", "Mitsubishi", "Ford", "Hyundai", "Chevrolet",
                "Kia", "Mazda", "Suzuki", "BMW");
        List<String> listaModelos = Arrays.asList("Alto", "Scion", "Versa", "Focus", "Veloster", "Celica", "Montero",
                "Demio", "Baleno", "CHR");
        int indexMarca = (int) (Math.random() * listaMarcas.size());
        int indexModelo = (int) (Math.random() * listaModelos.size());
        int anioRandom = (int) ((Math.random() * 13) + 2010);
        vehiculo.setVin(generarRandomVin());
        vehiculo.setMarca(listaMarcas.get(indexMarca));
        vehiculo.setModelo(listaModelos.get(indexModelo));
        vehiculo.setAnio(anioRandom);
        return vehiculo;
    }

    public static String generarRandomVin() {
        String characters = "0123456789ABCDEFGHJKLMNPRSTUVWXYZ";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(17);

        for (int i = 0; i < 17; i++) {
            int index = random.nextInt(characters.length());
            char randomChar = characters.charAt(index);
            sb.append(randomChar);
        }
        return sb.toString();
    }
}