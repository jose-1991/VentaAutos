package com.car.sales.company;

import com.car.sales.company.dao.OfertaDAO;
import com.car.sales.company.dao.PublicacionDAO;
import com.car.sales.company.dao.UsuarioDAO;
import com.car.sales.company.models.*;
import com.car.sales.company.services.NotificacionService;
import com.car.sales.company.services.PublicacionService;
import com.car.sales.company.services.UsuarioService;
import com.car.sales.company.services.VentaService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static com.car.sales.company.models.TipoUsuario.COMPRADOR;
import static com.car.sales.company.models.TipoUsuario.VENDEDOR;


public class UserStore {
    public static void main(String[] args) {
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        PublicacionDAO publicacionDAO = new PublicacionDAO();
        OfertaDAO ofertaDAO = new OfertaDAO();
        UsuarioService usuarioService = new UsuarioService(usuarioDAO);
        NotificacionService notificacionService = new NotificacionService(usuarioDAO);
        VentaService ventaService = new VentaService(notificacionService, ofertaDAO, publicacionDAO);
        PublicacionService publicacionService = new PublicacionService(notificacionService,
                publicacionDAO, usuarioDAO);
        Usuario usuario = new Usuario("Javier", "Rodriguez", "licencia", "12345678",
                "javi.31_82@hotmail.com", VENDEDOR, "77426426");
        usuario.setListaUnsuscribciones(new ArrayList<>());
        usuario.setAceptaNotificacionSms(true);
//        usuario.setAceptaNotificacionSms(true);
        Usuario usuario1 = new Usuario("jose", "sanz", "licencia", "48123984",
                "javi.31_82@hotmail.com", COMPRADOR, "77426426");
        usuario1.setAceptaNotificacionSms(true);
        usuario1.setListaUnsuscribciones(new ArrayList<>());
//        usuario.setUnsuscribcionesSms(new ArrayList<>());
//        usuario.getUnsuscripcionesSms().add(COMPRADOR_PRIMERA_OFERTA);
//        usuario.getUnsuscripcionesSms().add(COMPRADOR_ACEPTA_OFERTA);

        Vehiculo vehiculo = obtenerVehiculoRandom();
        Publicacion publicacion = new Publicacion();
        publicacion.setVendedor(usuario);
        publicacion.setProducto(vehiculo);
        publicacion.setOfertasCompradores(new ArrayList<>());
        publicacion.getOfertasCompradores().add(new Oferta(usuario1,90,80,LocalDateTime.now()));
        publicacion.setEstaDisponibleEnWeb(true);
        publicacion.setPrecio(800);
        publicacion.setFecha(LocalDate.now());

//        System.out.println(publicacionDAO.obtenerPublicacion(UUID.fromString("062c8e8f-80cd-4f18-a31c-1e88452efbac")));
//        System.out.println(usuarioDAO.obtenerCompradores());
//        System.out.println(usuarioDAO.obtenerCompradores().size());
//        publicacion.setId(UUID.fromString("865272c5-b716-44ca-8963-4f9030e813d8"));

        System.out.println(PublicacionDAO.ejecutarQueryParaSeleccion( "select id, tipo_usuario from comercio" +
                ".notificacion WHERE tipo_notificacion =" +
                " 'SMS' and" +
                " tipo_usuario = 'COMPRADOR'", Notificacion.class));
//        Usuario usuario2 = usuarioDAO.obtenerUsuario("111111222");
//        System.out.println(usuario2.toString());
//        publicacionService.publicarProducto(usuario, vehiculo, 80);
//        ventaService.interactuar(publicacion,usuario1, ACEPTAR_OFERTA, 0);
//            ofertaDAO.agregarOferta(new Oferta(usuario1,10,0,LocalDateTime.now()), UUID.fromString("d406d889-f690-4611-a7dd-cfde8d4e1509"));
//        usuarioService.actualizarSuscripciones(usuario, "77774752");
//        Usuario usuario2 = usuarioService.actualizarSuscripciones("23527494", COMPRADOR_PRIMERA_OFERTA, SMS,
//                UNSUSCRIBIR);
//        System.out.println(usuario2.getListaUnsuscribciones().size());
//        System.out.println(LocalDate.now().minusDays(6));
//        usuarioService.registrarUsuario(usuario);
//        publicacionDAO.registrarPublicacionProducto(publicacion);
//        System.out.println(publicacionDAO.obtenerPublicacionesDeBaja());
//                System.out.println(UUID.randomUUID());
//        System.out.println(new Date());
//        publicacionDAO.rePublicarProducto(UUID.fromString("865272c5-b716-44ca-8963-4f9030e813d8"), 50);
//        System.out.println(usuarioDAO.modificarUsuario("12345678", "788899765"));
//        List<Usuario> usuarios = usuarioDAO.obtenerCompradores();
//        System.out.println(usuarios);
//        usuarioDAO.eliminarUsuarioEnDb("490123984");
//        System.out.println(usuarioDAO.usuarioEnDbEstaVacia());
//        System.out.println(usuarioService.modificarUsuario("12345678", "  "));
//       usuarioDAO.suscribirTodo("12345678", true);
//        System.out.println(usuarioService.modificarUsuario("490123984", null));
//        publicacionDAO.registarPublicacionEnDb(publicacion);
//        notificacionDAO.registrarNotificacionEnDb(notificacion);
//        usuarioService.interaccionSuscripciones(usuario, COMPRADOR_PRIMERA_OFERTA, Accion.UNSUSCRIBIR_TODO, null);
//        publicacionService.darDeBajaPublicaciones();
//        ofertaDAO.agregarOferta(new Oferta(usuario1, 90,
//                0, LocalDateTime.now()), UUID.fromString("c408b45e-2d67-44fd-85da-93a73ed644b3"));
//        ofertaDAO.interaccionContraOferta("40123984", UUID.fromString("564847e8-187d-4783-90d0-d38708f949bb"), 80);
//        Publicacion publicacion1 = ventaService.interactuar(publicacion,usuario1,Accion.CONTRA_OFERTAR,80);
//        System.out.println(publicacion1.getOfertasCompradores().get(0).getMontoContraOferta());
//        ofertaDAO.actualizarOferta(UUID.fromString("564847e8-187d-4783-90d0-d38708f949bb"), "40123984", Accion.RETIRAR_OFERTA);
//        System.out.println((int) ((Math.random() * 13) + 2010));
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
