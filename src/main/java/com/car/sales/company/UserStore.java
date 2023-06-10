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
import java.util.Collections;

import static com.car.sales.company.models.Accion.*;
import static com.car.sales.company.models.NombreNotificacion.*;
import static com.car.sales.company.models.TipoNotificacion.SMS;
import static com.car.sales.company.models.TipoUsuario.COMPRADOR;
import static com.car.sales.company.models.TipoUsuario.VENDEDOR;

public class UserStore {
    public static void main(String[] args) {
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        PublicacionDAO publicacionDAO = new PublicacionDAO();
        OfertaDAO ofertaDAO = new OfertaDAO();
        UsuarioService usuarioService = new UsuarioService(usuarioDAO);
        NotificacionService notificacionService = new NotificacionService();
        VentaService ventaService = new VentaService(notificacionService, ofertaDAO, publicacionDAO);
        PublicacionService publicacionService = new PublicacionService(notificacionService,
                publicacionDAO, usuarioDAO);
        Usuario usuario = new Usuario("Javier", "Rodriguez", "licencia", "111111222",
                "javi.31_82@hotmail.com", VENDEDOR, "77426426");
//        usuario.setAceptaNotificacionSms(true);
        Usuario usuario1 = new Usuario("jose", "sanz", "licencia", "40123984",
                "javi.31_82@hotmail.com", COMPRADOR, "77426426");
//        usuario.setUnsuscribcionesSms(new ArrayList<>());
//        usuario.getUnsuscripcionesSms().add(COMPRADOR_PRIMERA_OFERTA);
//        usuario.getUnsuscripcionesSms().add(COMPRADOR_ACEPTA_OFERTA);

        Vehiculo vehiculo = new Vehiculo("54GHH73JXMN109736", "Toyota", "Scion", 2020);
        Publicacion publicacion = new Publicacion();
        publicacion.setVendedor(usuario);
        publicacion.setProducto(vehiculo);
        publicacion.setOfertasCompradores(Collections.singletonList(new Oferta(usuario1, 100, 0,
                LocalDateTime.now())));
        publicacion.setEstaDisponibleEnLaWeb(true);
        publicacion.setPrecio(80);
        publicacion.setFecha(LocalDate.now().minusDays(8));

        InputNotificacion inputNotificacion = new InputNotificacion(COMPRADOR_PRIMERA_OFERTA, vehiculo, 120, 0, "javi.31_82@hotmail" +
                ".com", "8771824");

//        usuarioService.modificarUsuario("12345678", null);
        Usuario usuario2 = usuarioService.actualizarSuscripciones(usuario,COMPRADOR_PRIMERA_OFERTA, SMS,
                UNSUSCRIBIR_TODO);
        System.out.println(usuario2.getListaUnsuscribciones().size());
//        System.out.println(LocalDate.now().minusDays(6));
//        usuarioService.registrarUsuario(usuario);
//        publicacionDAO.registrarPublicacionProducto(publicacion);
//        System.out.println(publicacionDAO.obtenerPublicacionesDeBaja());
//                System.out.println(UUID.randomUUID());
//        System.out.println(new Date());
//        publicacionDAO.rePublicarProducto(UUID.fromString("cf522537-35b1-484c-94c7-04f4788b7a7e"), 70);
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
}
