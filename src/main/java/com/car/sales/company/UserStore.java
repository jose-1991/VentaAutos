package com.car.sales.company;

import com.car.sales.company.dao.NotificacionDAO;
import com.car.sales.company.dao.PublicacionDAO;
import com.car.sales.company.dao.UsuarioDAO;
import com.car.sales.company.models.Notificacion;
import com.car.sales.company.models.Publicacion;
import com.car.sales.company.models.Usuario;
import com.car.sales.company.models.Vehiculo;
import com.car.sales.company.services.UsuarioService;

import java.util.ArrayList;

import static com.car.sales.company.models.NombreNotificacion.COMPRADOR_ACEPTA_OFERTA;
import static com.car.sales.company.models.NombreNotificacion.COMPRADOR_PRIMERA_OFERTA;
import static com.car.sales.company.models.TipoUsuario.VENDEDOR;

public class UserStore {
    public static void main(String[] args) {
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        PublicacionDAO publicacionDAO = new PublicacionDAO();
        NotificacionDAO notificacionDAO = new NotificacionDAO();
        UsuarioService usuarioService = new UsuarioService(usuarioDAO);
        Usuario usuario = new Usuario("Javier", "Rodriguez", "licencia", "45123984",
                "javi.31_82@hotmail.com", VENDEDOR, "77426426");
//        usuario.setUnsuscribcionesSms(new ArrayList<>());
//        usuario.getUnsuscripcionesSms().add(COMPRADOR_PRIMERA_OFERTA);
//        usuario.getUnsuscripcionesSms().add(COMPRADOR_ACEPTA_OFERTA);

        Vehiculo vehiculo = new Vehiculo("1HGBH41JXMN109716", "Toyota", "Scion", 2020);
        Publicacion publicacion = new Publicacion();
        publicacion.setVendedor(usuario);
        publicacion.setProducto(vehiculo);
        publicacion.setOfertasCompradores(new ArrayList<>());
        publicacion.setEstaDisponibleEnLaWeb(true);

        Notificacion notificacion = new Notificacion(COMPRADOR_PRIMERA_OFERTA, vehiculo, 120, 0, "javi.31_82@hotmail" +
                ".com", "8771824");

//        List<Usuario> usuarios = usuarioDAO.obtenerCompradores();
//        System.out.println(usuarios);
//        usuarioDAO.eliminarUsuarioEnDb("490123984");
//        System.out.println(usuarioDAO.usuarioEnDbEstaVacia());
        System.out.println(usuarioService.modificarUsuario("40123984", "75422212"));
//       usuarioDAO.modificarUsuarioEnDb("490123984", "77436426");
//        System.out.println(usuarioService.modificarUsuario("490123984", null));
//        publicacionDAO.registarPublicacionEnDb(publicacion);
//        notificacionDAO.registrarNotificacionEnDb(notificacion);


    }
}
