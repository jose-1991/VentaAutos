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
import static com.car.sales.company.models.NombreNotificacion.NUEVO_VEHICULO_EN_VENTA;
import static com.car.sales.company.models.NombreNotificacion.VEHICULO_EXPIRADO;
import static com.car.sales.company.models.TipoUsuario.VENDEDOR;

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
                notificacionService.notificarTodosLosCompradores(listaCompradores, producto, NUEVO_VEHICULO_EN_VENTA);
            }
            return publicacion;
        }
        throw new DatoInvalidoException("El usuario debe ser de tipo vendedor");
    }

    public int darDeBajaPublicaciones() {
        List<Publicacion> listaPublicacionesDeBaja = publicacionDAO.obtenerPublicacionesDeBaja();
        for (Publicacion publicacion : listaPublicacionesDeBaja) {
            notificacionService.enviarNotificacion(publicacion.getVendedor(), publicacion.getProducto(), 0, 0,
                    VEHICULO_EXPIRADO);
        }
        return listaPublicacionesDeBaja.size();
    }

    public Publicacion rePublicarProducto(Publicacion publicacion, double nuevoPrecioProducto) {
        if (nuevoPrecioProducto < publicacion.getPrecio()) {
            publicacionDAO.rePublicarProducto(publicacion.getId(), nuevoPrecioProducto);
            notificacionService.notificarTodosLosCompradores(usuarioDAO.obtenerCompradores(), publicacion.getProducto(),
                    NUEVO_VEHICULO_EN_VENTA);
        } else {
            throw new DatoInvalidoException("el nuevo precio debe ser menor al precio actual");
        }
        return publicacion;
    }


}
