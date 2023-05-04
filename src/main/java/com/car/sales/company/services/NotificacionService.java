package com.car.sales.company.services;

import com.models.*;

import java.util.List;

import static com.models.TipoNotificacion.EMAIL;

public class NotificacionService {

    public Notificacion ValidarNotificacion(Publicacion publicacion, Oferta oferta, String nombreNotificacion, Usuario usuario,
                                            TipoNotificacion tipoNotificacion) {
        Notificacion notificacion = new Notificacion(nombreNotificacion, publicacion);
        if (oferta != null) {
            notificacion.setOferta(oferta);
        }
        switch (tipoNotificacion) {
            case AMBOS:
                if (!usuario.getUnsuscripcionesSms().contains(nombreNotificacion)) {
                    notificacion.setCelular(usuario.getCelular());
                }
            case EMAIL:
                if (!usuario.getUnsuscripcionesEmail().contains(nombreNotificacion)) {
                    notificacion.setEmail(usuario.getEmail());
                    enviarNotificacion(notificacion);
                }
                break;
        }
        return notificacion;
    }

    public void enviarNotificacion(Notificacion notificacion) {
    }

    public List<String> actualizarSuscripcion(Usuario usuario, String nombreNotificacion, Accion accion, TipoNotificacion tipoNotificacion) {
        switch (accion) {
            case SUSCRIBIR:
                if (tipoNotificacion == EMAIL) {
                    usuario.getUnsuscripcionesEmail().remove(nombreNotificacion);
                } else {
                    usuario.getUnsuscripcionesSms().remove(nombreNotificacion);
                }
                break;
            case UNSUSCRIBIR:
                if (tipoNotificacion == EMAIL) {
                    usuario.getUnsuscripcionesEmail().add(nombreNotificacion);
                } else {
                    usuario.getUnsuscripcionesSms().add(nombreNotificacion);
                }
        }
        return usuario.getUnsuscripcionesEmail();
    }
}
