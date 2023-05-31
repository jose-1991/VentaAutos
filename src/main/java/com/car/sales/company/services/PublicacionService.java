package com.car.sales.company.services;

import com.car.sales.company.dao.PublicacionDAO;
import com.car.sales.company.dao.UsuarioDAO;
import com.car.sales.company.exceptions.DatoInvalidoException;
import com.car.sales.company.models.Producto;
import com.car.sales.company.models.Publicacion;
import com.car.sales.company.models.Usuario;
import com.car.sales.company.models.Vehiculo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.car.sales.company.helper.ValidacionHelper.validarVehiculo;
import static com.car.sales.company.models.TipoUsuario.VENDEDOR;
import static com.car.sales.company.services.NotificacionService.N_VEHICULO_VENTA;
import static com.car.sales.company.services.NotificacionService.V_EXPIRADO;

public class PublicacionService {

    private NotificacionService notificacionService;
    private UsuarioService usuarioService;
    private PublicacionDAO publicacionDAO;
    private UsuarioDAO usuarioDAO;

    public PublicacionService(NotificacionService notificacionService, UsuarioService usuarioService,
                              PublicacionDAO publicacionDAO, UsuarioDAO usuarioDAO) {
        this.notificacionService = notificacionService;
        this.usuarioService = usuarioService;
        this.publicacionDAO = publicacionDAO;
        this.usuarioDAO = usuarioDAO;
    }

    // TODO: 30/5/2023 metodo para crear vehiculo random, o modificar metodo
    public Publicacion publicarProducto(Usuario vendedor, Producto producto, double precio) {
        Publicacion publicacion = new Publicacion();
        if (vendedor != null && vendedor.getTipoUsuario().equals(VENDEDOR) && producto != null) {
            if (producto instanceof Vehiculo) {
                Vehiculo vehiculo = (Vehiculo) producto;
                validarVehiculo(vehiculo);
            }
            publicacion.setProducto(producto);
            publicacion.setVendedor(vendedor);
            publicacion.setFecha(LocalDate.now());
            publicacion.setPrecio(precio);
            publicacion.setEstaDisponibleEnLaWeb(true);
            publicacion.setOfertasCompradores(new ArrayList<>());
            publicacionDAO.registrarPublicacionProducto(publicacion);
            List<Usuario> listaCompradores = usuarioDAO.obtenerCompradores();
            if (!listaCompradores.isEmpty()) {
                notificacionService.notificarTodosLosCompradores(listaCompradores, producto, N_VEHICULO_VENTA);
            }
            return publicacion;
        }
        throw new DatoInvalidoException("El usuario debe ser de tipo vendedor");
    }

    public int darDeBajaPublicaciones() {
        List<Publicacion> listaPublicacionesDeBaja = publicacionDAO.obtenerPublicacionesParaDarDeBaja();
        // TODO: 30/5/2023 optimizar para que solo llame a la DB una sola vez
        for (Publicacion publicacion : listaPublicacionesDeBaja) {
            notificacionService.enviarNotificacion(publicacion.getVendedor(), publicacion.getProducto(), 0, 0,
                    V_EXPIRADO);
        }
        publicacionDAO.darDeBajaPublicacion(listaPublicacionesDeBaja);
        return listaPublicacionesDeBaja.size();
    }

    public Publicacion rePublicarProducto(Publicacion publicacion, double nuevoPrecioProducto) {
        if (nuevoPrecioProducto < publicacion.getPrecio()) {
            publicacionDAO.rePublicarProducto(publicacion.getId(), nuevoPrecioProducto);
            notificacionService.notificarTodosLosCompradores(usuarioDAO.obtenerCompradores(), publicacion.getProducto(),
                    N_VEHICULO_VENTA);
        } else {
            throw new DatoInvalidoException("el nuevo precio debe ser menor al precio actual");
        }
        return publicacion;
    }


}
