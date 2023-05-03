package com.car.sales.company.services;

import com.car.sales.company.exceptions.DatoInvalidoException;
import com.car.sales.company.models.Accion;
import com.car.sales.company.models.Notificacion;
import com.car.sales.company.models.Usuario;

public class NotificacionService {

    public Notificacion enviarNotificacion(Usuario usuario, String tipoNotificacion) {
        Notificacion notificacion = new Notificacion(tipoNotificacion);
        if (usuario.getNotificacionesEmail().get(tipoNotificacion)) {
            notificacion.setEmail(usuario.getEmail());
        } else if (usuario.getNotificacionesSms().get(tipoNotificacion))
            notificacion.setCelular(usuario.getCelular());
        else {
            throw new DatoInvalidoException("El tipo de notificacion ingresado no existe");
        }
        return notificacion;
    }

    public void suscripcionNotificacion(Usuario usuario, String tipoNotificacion, Accion accion, String metodoEnvio) {
        boolean estaDisponible = false;
        switch (accion) {
            case SUSCRIBIR:
                estaDisponible = true;
                break;
        }
        if (metodoEnvio.equalsIgnoreCase("Email")) {
            usuario.getNotificacionesEmail().put(tipoNotificacion, estaDisponible);
        } else {
            usuario.getNotificacionesSms().put(tipoNotificacion, estaDisponible);
        }
    }
}
