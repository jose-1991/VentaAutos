package com.car.sales.company.services;

import com.car.sales.company.exceptions.DatoInvalidoException;
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
import static com.car.sales.company.models.TipoUsuario.COMPRADOR;
import static com.car.sales.company.models.TipoUsuario.VENDEDOR;

public class PublicacionService {

    NotificacionService notificacionService = new NotificacionService();
    UsuarioService usuarioService = new UsuarioService();

    List<Publicacion> vehiculosPublicados = new ArrayList<>();

    public Publicacion publicarVehiculo(Usuario vendedor, Vehiculo vehiculo) {
        Publicacion publicacion = new Publicacion();
        if (vendedor != null && vendedor.getTipoUsuario().equals(VENDEDOR) && vehiculo != null) {
            validarVehiculo(vehiculo);
            publicacion.setVendedor(vendedor);
            publicacion.setVehiculo(vehiculo);
            publicacion.setFecha(LocalDate.now());
            publicacion.setOfertasCompradores(new ArrayList<>());
            publicacion.setEstaDisponibleEnLaWeb(true);
            vehiculosPublicados.add(publicacion);
            notificacionService.notificarTodosLosCompradores(usuarioService.usuarios,vehiculo,NUEVO_VEHICULO_EN_VENTA);
            return publicacion;
        }
        throw new DatoInvalidoException("El usuario debe ser de tipo vendedor");
    }

    public int darDeBajaPublicaciones() {
        int publicacionesDeBaja = 0;
        for (Publicacion publicacion : vehiculosPublicados) {
            if (publicacion.getOfertasCompradores().size() < 1 && tieneMaximoDiasSinOfertas(publicacion.getFecha())) {
                publicacion.setEstaDisponibleEnLaWeb(false);
                notificacionService.enviarNotificacion(publicacion.getVendedor(), publicacion.getVehiculo(), 0,0,
                        VEHICULO_EXPIRADO);
                publicacionesDeBaja++;
            }
        }
        return publicacionesDeBaja;
    }

    public Publicacion rePublicarVehiculo(Publicacion publicacion, double nuevoPrecioVehiculo) {
        if (nuevoPrecioVehiculo < publicacion.getVehiculo().getPrecio()) {
            publicacion.getVehiculo().setPrecio(nuevoPrecioVehiculo);
            publicacion.setEstaDisponibleEnLaWeb(true);
            notificacionService.notificarTodosLosCompradores(usuarioService.usuarios,publicacion.getVehiculo(),NUEVO_VEHICULO_EN_VENTA);
        } else {
            throw new DatoInvalidoException("el nuevo precio debe ser menor al precio actual");
        }
        return publicacion;
    }



}
