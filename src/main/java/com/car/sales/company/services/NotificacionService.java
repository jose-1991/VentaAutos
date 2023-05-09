package com.car.sales.company.services;

import com.car.sales.company.exceptions.DatoInvalidoException;
import com.car.sales.company.models.NombreNotificacion;
import com.car.sales.company.models.Notificacion;
import com.car.sales.company.models.Usuario;
import com.car.sales.company.models.Vehiculo;

import java.util.Arrays;
import java.util.List;

import static com.car.sales.company.models.NombreNotificacion.*;

public class NotificacionService {

    static final List<NombreNotificacion> NOTIFICACIONES_SMS_LIST = Arrays.asList(COMPRADOR_PRIMERA_OFERTA, COMPRADOR_ACEPTA_OFERTA,
            NUEVO_VEHICULO_EN_VENTA, VENDEDOR_ACEPTA_OFERTA);

    public Notificacion enviarNotificacion(Usuario usuario, Vehiculo vehiculo, double montoOferta, NombreNotificacion nombreNotificacion) {
        Notificacion notificacion = new Notificacion(nombreNotificacion, vehiculo, montoOferta, null, null);

        if (NOTIFICACIONES_SMS_LIST.contains(nombreNotificacion)) {
            if (usuario.isAceptaNotificacionSms() && !usuario.getUnsuscripcionesSms().contains(nombreNotificacion)) {
                notificacion.setCelular(usuario.getCelular());
            }
        }
        if (!usuario.getUnsuscripcionesEmail().contains(nombreNotificacion)) {
            notificacion.setEmail(usuario.getEmail());
        } else {
            throw new DatoInvalidoException("El usuario NO esta suscrito a la notificacion Ingresada");
        }
        enviar(notificacion);
        return notificacion;
    }

    public void enviar(Notificacion notificacion) {
    }
}
