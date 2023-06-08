package com.car.sales.company.services;

import com.car.sales.company.exceptions.DatoInvalidoException;
import com.car.sales.company.models.InputNotificacion;
import com.car.sales.company.models.Notificacion;
import com.car.sales.company.models.Producto;
import com.car.sales.company.models.Usuario;

import java.util.Arrays;
import java.util.List;

import static com.car.sales.company.models.NombreNotificacion.*;
import static com.car.sales.company.models.TipoNotificacion.*;
import static com.car.sales.company.models.TipoUsuario.COMPRADOR;
import static com.car.sales.company.models.TipoUsuario.VENDEDOR;

public class NotificacionService {

    public InputNotificacion enviarNotificacion(Usuario usuario, Producto producto, double montoOferta, double montoContraOferta,
                                                Notificacion notificacion) {
        if (notificacion == null || usuario.getListaUnsuscribciones().contains(notificacion)) {
            throw new DatoInvalidoException("Notificacion invalida");
        }

        InputNotificacion inputNotificacion = new InputNotificacion(notificacion.getNombreNotificacion(), producto,
                montoOferta, montoContraOferta, null, null);

        switch (notificacion.getTipoNotificacion()) {
            case AMBOS:
                notificacion.setTipoNotificacion(SMS);
                if (usuario.isAceptaNotificacionSms() && !usuario.getListaUnsuscribciones().contains(notificacion)) {
                    inputNotificacion.setCelular(usuario.getCelular());
                }
            case EMAIL:
                notificacion.setTipoNotificacion(EMAIL);
                if (!usuario.getListaUnsuscribciones().contains(notificacion)) {
                    inputNotificacion.setEmail(usuario.getEmail());
                }
                break;
        }

        if (inputNotificacion.getEmail() == null && inputNotificacion.getCelular() == null) {
            throw new DatoInvalidoException("El usuario no esta suscrito a la notificacion ingresada");
        }
        enviar(inputNotificacion);
        return inputNotificacion;
    }

    public void enviar(InputNotificacion inputNotificacion) {

    }

    public void notificarTodosLosCompradores(List<Usuario> compradores, Producto producto,
                                             Notificacion notificacion) {
        for (Usuario usuario : compradores) {
            enviarNotificacion(usuario, producto, 0, 0, notificacion);
        }
    }

}
