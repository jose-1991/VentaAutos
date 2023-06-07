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

    public static final Notificacion C_PRIMERA_OFERTA = new Notificacion(COMPRADOR_PRIMERA_OFERTA, AMBOS, VENDEDOR);
    public static final Notificacion C_ACEPTA_OFERTA = new Notificacion(COMPRADOR_ACEPTA_OFERTA, AMBOS, VENDEDOR);
    public static final Notificacion C_RETIRA_OFERTA = new Notificacion(COMPRADOR_RETIRA_OFERTA, EMAIL, VENDEDOR);
    public static final Notificacion V_EXPIRADO = new Notificacion(VEHICULO_EXPIRADO, EMAIL, VENDEDOR);
    public static final Notificacion C_NUEVA_OFERTA = new Notificacion(COMPRADOR_NUEVA_OFERTA, AMBOS, VENDEDOR);
    public static final Notificacion N_VEHICULO_VENTA = new Notificacion(NUEVO_VEHICULO_EN_VENTA, AMBOS, COMPRADOR);
    public static final Notificacion V_ACEPTA_OFERTA = new Notificacion(VENDEDOR_ACEPTA_OFERTA, AMBOS, COMPRADOR);
    public static final Notificacion V_CONTRAOFERTA = new Notificacion(VENDEDOR_CONTRAOFERTA, EMAIL, COMPRADOR);
    public static final Notificacion V_DECLINA_OFERTA = new Notificacion(VENDEDOR_DECLINA_OFERTA, EMAIL, COMPRADOR);
    public static final Notificacion V_NO_DISPONIBLE = new Notificacion(VEHICULO_NO_DISPONIBLE, EMAIL, COMPRADOR);

    public static final List<Notificacion> NOTIFICACIONES_LIST = Arrays.asList(C_PRIMERA_OFERTA, C_ACEPTA_OFERTA, C_RETIRA_OFERTA,
            V_EXPIRADO, C_NUEVA_OFERTA, N_VEHICULO_VENTA, V_ACEPTA_OFERTA, V_CONTRAOFERTA, V_DECLINA_OFERTA, V_NO_DISPONIBLE);


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
