package com.car.sales.company.services;

import com.car.sales.company.dao.PublicacionDAO;
import com.car.sales.company.exceptions.DatoInvalidoException;
import com.car.sales.company.exceptions.UsuarioNoEncontradoException;
import com.car.sales.company.models.Producto;
import com.car.sales.company.models.Publicacion;
import com.car.sales.company.models.Usuario;
import com.car.sales.company.models.Vehiculo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.car.sales.company.helper.ValidacionHelper.tieneMaximoDiasSinOfertas;
import static com.car.sales.company.helper.ValidacionHelper.validarVehiculo;
import static com.car.sales.company.models.NombreNotificacion.NUEVO_VEHICULO_EN_VENTA;
import static com.car.sales.company.models.NombreNotificacion.VEHICULO_EXPIRADO;
import static com.car.sales.company.models.TipoUsuario.VENDEDOR;

public class PublicacionService {

    private NotificacionService notificacionService;
    private UsuarioService usuarioService;
    private PublicacionDAO publicacionDAO;
    private List<Publicacion> ProductosPublicados = new ArrayList<>();

    public List<Publicacion> getProductosPublicados() {
        return ProductosPublicados;
    }

    public PublicacionService(NotificacionService notificacionService, UsuarioService usuarioService,
                              PublicacionDAO publicacionDAO) {
        this.notificacionService = notificacionService;
        this.usuarioService = usuarioService;
        this.publicacionDAO = publicacionDAO;
    }

    public Publicacion publicarProducto(Usuario vendedor, Producto producto) {
        Publicacion publicacion = new Publicacion();
        if (vendedor != null && vendedor.getTipoUsuario().equals(VENDEDOR) && producto != null) {
            if (producto instanceof Vehiculo) {
                Vehiculo vehiculo = (Vehiculo) producto;
                validarVehiculo(vehiculo);
            }
            publicacion.setVendedor(vendedor);
            publicacion.setProducto(producto);
            publicacion.setFecha(LocalDate.now());
            publicacion.setEstaDisponibleEnLaWeb(true);
            publicacionDAO.registarPublicacionEnDb(publicacion);
            if (!usuarioService.getListaUsuariosRegistrados().isEmpty()) {
                notificacionService.notificarTodosLosCompradores(usuarioService.getListaUsuariosRegistrados(), producto, NUEVO_VEHICULO_EN_VENTA);
            }
            return publicacion;
        }
        throw new DatoInvalidoException("El usuario debe ser de tipo vendedor");
    }

    public Publicacion publicarProducto1(Usuario vendedor, Producto producto) {
        Publicacion publicacion = new Publicacion();
        try {
            usuarioService.modificarUsuario(vendedor.getIdentificacion(), "76256432");

        } catch (UsuarioNoEncontradoException exception) {
            if (vendedor.getTipoUsuario().equals(VENDEDOR) && producto != null) {
                if (producto instanceof Vehiculo) {
                    Vehiculo vehiculo = (Vehiculo) producto;
                    validarVehiculo(vehiculo);
                }
                publicacion.setVendedor(vendedor);
                publicacion.setProducto(producto);
                publicacion.setFecha(LocalDate.now());
                publicacion.setEstaDisponibleEnLaWeb(true);
                ProductosPublicados.add(publicacion);
                if (!usuarioService.getListaUsuariosRegistrados().isEmpty()) {
                    notificacionService.notificarTodosLosCompradores(usuarioService.getListaUsuariosRegistrados(), producto, NUEVO_VEHICULO_EN_VENTA);
                }
                return publicacion;
            }
        }

        throw new DatoInvalidoException("El usuario debe ser de tipo vendedor");
    }

    public int darDeBajaPublicaciones() {
        int publicacionesDeBaja = 0;
        for (Publicacion publicacion : ProductosPublicados) {
            if (publicacion.getOfertasCompradores().size() < 1 && tieneMaximoDiasSinOfertas(publicacion.getFecha())) {
                publicacion.setEstaDisponibleEnLaWeb(false);
                notificacionService.enviarNotificacion(publicacion.getVendedor(), publicacion.getProducto(), 0, 0,
                        VEHICULO_EXPIRADO);
                publicacionesDeBaja++;
            }
        }
        return publicacionesDeBaja;
    }

    public Publicacion rePublicarProducto(Publicacion publicacion, double nuevoPrecioProducto) {
        if (nuevoPrecioProducto < publicacion.getPrecio()) {
            publicacion.setPrecio(nuevoPrecioProducto);
            publicacion.setEstaDisponibleEnLaWeb(true);
            notificacionService.notificarTodosLosCompradores(usuarioService.getListaUsuariosRegistrados(), publicacion.getProducto(),
                    NUEVO_VEHICULO_EN_VENTA);
        } else {
            throw new DatoInvalidoException("el nuevo precio debe ser menor al precio actual");
        }
        return publicacion;
    }


}
