package com.car.sales.company;

import com.car.sales.company.dao.NotificacionDAO;
import com.car.sales.company.dao.PublicacionDAO;
import com.car.sales.company.dao.UsuarioDAO;
import com.car.sales.company.models.*;
import com.car.sales.company.services.UsuarioService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;

import static com.car.sales.company.models.NombreNotificacion.COMPRADOR_PRIMERA_OFERTA;
import static com.car.sales.company.models.TipoNotificacion.EMAIL;
import static com.car.sales.company.models.TipoUsuario.COMPRADOR;
import static com.car.sales.company.models.TipoUsuario.VENDEDOR;

public class UserStore {
    public static void main(String[] args) {
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        PublicacionDAO publicacionDAO = new PublicacionDAO();
        NotificacionDAO notificacionDAO = new NotificacionDAO();
        UsuarioService usuarioService = new UsuarioService(usuarioDAO);
        Usuario usuario = new Usuario("Javier", "Rodriguez", "licencia", "45123984",
                "javi.31_82@hotmail.com", VENDEDOR, "77426426");
        usuario.setAceptaNotificacionSms(true);
        Usuario usuario1 = new Usuario("jose", "sanz", "licencia", "65445678",
                "javi.31_82@hotmail.com", COMPRADOR, "77426426");
//        usuario.setUnsuscribcionesSms(new ArrayList<>());
//        usuario.getUnsuscripcionesSms().add(COMPRADOR_PRIMERA_OFERTA);
//        usuario.getUnsuscripcionesSms().add(COMPRADOR_ACEPTA_OFERTA);

        Vehiculo vehiculo = new Vehiculo("1YGBH73JXMN109736", "Toyota", "Scion", 2020);
        Publicacion publicacion = new Publicacion();
        publicacion.setVendedor(usuario);
        publicacion.setProducto(vehiculo);
        publicacion.setOfertasCompradores(Collections.singletonList(new Oferta(100, 0, usuario1, LocalDateTime.now())));
        publicacion.setEstaDisponibleEnLaWeb(true);
        publicacion.setPrecio(80);
        publicacion.setFecha(LocalDate.now().minusDays(8));

        Notificacion notificacion = new Notificacion(COMPRADOR_PRIMERA_OFERTA, vehiculo, 120, 0, "javi.31_82@hotmail" +
                ".com", "8771824");

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
        usuarioService.interaccionSuscripciones(usuario, COMPRADOR_PRIMERA_OFERTA, Accion.UNSUSCRIBIR, EMAIL);


    }
}
