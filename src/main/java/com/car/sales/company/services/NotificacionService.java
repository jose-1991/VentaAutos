package com.car.sales.company.services;

import com.car.sales.company.exceptions.DatoInvalidoException;
import com.car.sales.company.models.*;

import java.util.List;

import static com.car.sales.company.models.TipoNotificacion.*;

public class NotificacionService {

    public InputNotificacion enviarNotificacion(Usuario usuario, Producto producto, double montoOferta, double montoContraOferta,
                                                NombreNotificacion nombreNotificacion) {
        if (nombreNotificacion == null) {
            throw new DatoInvalidoException("Notificacion invalida");
        }
        InputNotificacion inputNotificacion = new InputNotificacion(nombreNotificacion, producto,
                montoOferta, montoContraOferta, null, null);
        if (usuario.isAceptaNotificacionSms() && usuarioEstaSuscrito(usuario.getListaUnsuscribciones(), nombreNotificacion, SMS)) {
            inputNotificacion.setCelular(usuario.getCelular());
        }
        if (usuarioEstaSuscrito(usuario.getListaUnsuscribciones(), nombreNotificacion, EMAIL)) {
            inputNotificacion.setEmail(usuario.getEmail());
        }
        if (inputNotificacion.getEmail() == null && inputNotificacion.getCelular() == null) {
            throw new DatoInvalidoException("El usuario no esta suscrito a la notificacion ingresada");
        }
        enviar(inputNotificacion);
        return inputNotificacion;
    }

    public void enviar(InputNotificacion inputNotificacion) {

    }

    private boolean usuarioEstaSuscrito(List<Notificacion> listaUnsuscripciones, NombreNotificacion nombreNotificacion,
                                        TipoNotificacion tipoNotificacion) {
        return listaUnsuscripciones.stream().noneMatch(n ->
                n.getNombreNotificacion().equals(nombreNotificacion) && n.getTipoNotificacion().equals(tipoNotificacion));
    }

    public void notificarTodosLosCompradores(List<Usuario> compradores, Producto producto,
                                             NombreNotificacion nombreNotificacion) {
        for (Usuario usuario : compradores) {
            enviarNotificacion(usuario, producto, 0, 0, nombreNotificacion);
        }
    }

}
