package com.car.sales.company.services;

import com.car.sales.company.exceptions.DatoInvalidoException;
import com.car.sales.company.models.*;

import java.util.Arrays;
import java.util.List;

import static com.car.sales.company.models.NombreNotificacion.*;
import static com.car.sales.company.models.TipoNotificacion.*;
import static com.car.sales.company.models.TipoUsuario.COMPRADOR;
import static com.car.sales.company.models.TipoUsuario.VENDEDOR;

public class NotificacionService {
    private UsuarioService usuarioService;

    public NotificacionService(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }
    Notificacion notificacion1 = new Notificacion(COMPRADOR_PRIMERA_OFERTA,AMBOS,VENDEDOR);
    Notificacion notificacion2 = new Notificacion(COMPRADOR_ACEPTA_OFERTA,AMBOS,VENDEDOR);
    Notificacion notificacion3 = new Notificacion(COMPRADOR_RETIRA_OFERTA,EMAIL,VENDEDOR);
    Notificacion notificacion4 = new Notificacion(VEHICULO_EXPIRADO,EMAIL,VENDEDOR);
    Notificacion notificacion5 = new Notificacion(COMPRADOR_NUEVA_OFERTA,EMAIL,VENDEDOR);
    Notificacion notificacion6 = new Notificacion(NUEVO_VEHICULO_EN_VENTA,AMBOS,COMPRADOR);
    Notificacion notificacion7 = new Notificacion(VENDEDOR_ACEPTA_OFERTA,AMBOS,COMPRADOR);
    Notificacion notificacion8 = new Notificacion(VENDEDOR_CONTRAOFERTA,EMAIL,COMPRADOR);
    Notificacion notificacion9 = new Notificacion(VENDEDOR_DECLINA_OFERTA,EMAIL,COMPRADOR);
    Notificacion notificacion10 = new Notificacion(VEHICULO_NO_DISPONIBLE,EMAIL,COMPRADOR);

    public  final List<Notificacion> NOTIFICACIONES_LIST = Arrays.asList(notificacion1, notificacion2, notificacion3,
            notificacion4, notificacion5,notificacion6,notificacion7,notificacion8,notificacion9,notificacion10);

    public InputNotificacion enviarNotificacion(Usuario usuario, Producto producto, double montoOferta, double montoContraOferta,
                                                NombreNotificacion nombreNotificacion) {
        InputNotificacion inputNotificacion = new InputNotificacion(nombreNotificacion, producto, montoOferta, montoContraOferta, null, null);
        if (nombreNotificacion == null) {
            throw new DatoInvalidoException("Nombre de notificacion invalido");
        }
        if (NOTIFICACIONES_SMS_VENDEDOR.contains(nombreNotificacion) || NOTIFICACIONES_SMS_COMPRADOR.contains(nombreNotificacion)) {
            if (usuario.isAceptaNotificacionSms() && !usuario.getUnsuscripcionesSms().contains(nombreNotificacion)) {
                inputNotificacion.setCelular(usuario.getCelular());
            }
        }
        if (!usuario.getUnsuscripcionesEmail().contains(nombreNotificacion)) {
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

    public void notificarTodosLosCompradores(List<Usuario> compradores, Producto producto,
                                             NombreNotificacion nombreNotificacion) {
        for (Usuario usuario : compradores) {
            enviarNotificacion(usuario, producto, 0, 0, nombreNotificacion);
        }
    }
// ignorar
    public int notificarSinExcederLimite(List<Usuario> usuarios, Producto producto,
                                         NombreNotificacion nombreNotificacion) {
        int notificacionesEnviadas = 0;
        int cantidadGuardadaNotificaciones;
        for (Usuario usuario : usuarios) {
            if (usuario.getTipoUsuario().equals(COMPRADOR)) {
                try {
                    cantidadGuardadaNotificaciones = usuarioService.guardarNotificaciones(nombreNotificacion);
                } catch (IllegalArgumentException exception) {
                    return 1000;
                }
                if (cantidadGuardadaNotificaciones >= 100) {
                    throw new IllegalArgumentException();
                } else {
                    enviarNotificacion(usuario, producto, 0, 0, nombreNotificacion);
                    notificacionesEnviadas++;
                }
            }
        }
        return notificacionesEnviadas;
    }
}
