package com.car.sales.company.services;

import com.car.sales.company.exceptions.DatoInvalidoException;
import com.car.sales.company.models.*;

import java.util.Arrays;
import java.util.List;

import static com.car.sales.company.models.NombreNotificacion.*;
import static com.car.sales.company.models.TipoUsuario.COMPRADOR;

public class NotificacionService {
    public int contador;

    static final List<NombreNotificacion> NOTIFICACIONES_SMS_LIST = Arrays.asList(COMPRADOR_PRIMERA_OFERTA, COMPRADOR_ACEPTA_OFERTA,
            NUEVO_VEHICULO_EN_VENTA, VENDEDOR_ACEPTA_OFERTA);
    static final List<NombreNotificacion> NOTIFICACIONES_EMAIL_LIST = Arrays.asList(NombreNotificacion.values());

    public Notificacion enviarNotificacion(Usuario usuario, Producto producto, double montoOferta, double montoContraOferta,
                                           NombreNotificacion nombreNotificacion) {
        Notificacion notificacion = new Notificacion(nombreNotificacion, producto, montoOferta, montoContraOferta,null, null);
        if (nombreNotificacion == null){
            throw new DatoInvalidoException("Nombre de notifiacion invalido");
        }
        if (NOTIFICACIONES_SMS_LIST.contains(nombreNotificacion)) {
            if (usuario.isAceptaNotificacionSms() && !usuario.getUnsuscripcionesSms().contains(nombreNotificacion)) {
                notificacion.setCelular(usuario.getCelular());
            }
        }
        if (!usuario.getUnsuscripcionesEmail().contains(nombreNotificacion)) {
            notificacion.setEmail(usuario.getEmail());
        } if (notificacion.getEmail() == null && notificacion.getCelular() == null){
            throw new DatoInvalidoException("El usuario no esta suscrito a la notificacion ingresada");
        }
        contador++;
        enviar(notificacion);
        return notificacion;
    }

    public void enviar(Notificacion notificacion) {
    }

    public void notificarTodosLosCompradores(List<Usuario> usuarios, Producto producto,
                                             NombreNotificacion nombreNotificacion){
        for (Usuario usuario : usuarios) {
            if (usuario.getTipoUsuario().equals(COMPRADOR)) {
                enviarNotificacion(usuario, producto, 0,0, nombreNotificacion);
            }
        }
    }
}
