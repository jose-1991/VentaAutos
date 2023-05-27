package com.car.sales.company.services;

import com.car.sales.company.dao.NotificacionDAO;
import com.car.sales.company.exceptions.DatoInvalidoException;
import com.car.sales.company.models.NombreNotificacion;
import com.car.sales.company.models.Notificacion;
import com.car.sales.company.models.Producto;
import com.car.sales.company.models.Usuario;

import java.util.Arrays;
import java.util.List;

import static com.car.sales.company.models.NombreNotificacion.*;
import static com.car.sales.company.models.TipoUsuario.COMPRADOR;

public class NotificacionService {
    private UsuarioService usuarioService;
    private NotificacionDAO notificacionDAO;

    public NotificacionService(UsuarioService usuarioService, NotificacionDAO notificacionDAO) {
        this.usuarioService = usuarioService;
        this.notificacionDAO = notificacionDAO;
    }

    public static final List<NombreNotificacion> NOTIFICACIONES_SMS_VENDEDOR = Arrays.asList(COMPRADOR_PRIMERA_OFERTA,
            COMPRADOR_ACEPTA_OFERTA);
    public static final List<NombreNotificacion> NOTIFICACIONES_SMS_COMPRADOR = Arrays.asList(NUEVO_VEHICULO_EN_VENTA,
            VENDEDOR_ACEPTA_OFERTA);
    public static final List<NombreNotificacion> NOTIFICACIONES_EMAIL_VENDEDOR = Arrays.asList(COMPRADOR_PRIMERA_OFERTA,
            COMPRADOR_ACEPTA_OFERTA, COMPRADOR_RETIRA_OFERTA, VEHICULO_EXPIRADO, COMPRADOR_NUEVA_OFERTA);
    public static final List<NombreNotificacion> NOTIFICACIONES_EMAIL_COMPRADOR = Arrays.asList(NUEVO_VEHICULO_EN_VENTA,
            VENDEDOR_CONTRAOFERTA, VENDEDOR_ACEPTA_OFERTA, VENDEDOR_DECLINA_OFERTA, VEHICULO_NO_DISPONIBLE);

    public Notificacion enviarNotificacion(Usuario usuario, Producto producto, double montoOferta, double montoContraOferta,
                                           NombreNotificacion nombreNotificacion) {
        Notificacion notificacion = new Notificacion(nombreNotificacion, producto, montoOferta, montoContraOferta, null, null);
        if (nombreNotificacion == null) {
            throw new DatoInvalidoException("Nombre de notificacion invalido");
        }
        if (NOTIFICACIONES_SMS_VENDEDOR.contains(nombreNotificacion) || NOTIFICACIONES_SMS_COMPRADOR.contains(nombreNotificacion)) {
            if (usuario.isAceptaNotificacionSms() && !usuario.getUnsuscripcionesSms().contains(nombreNotificacion)) {
                notificacion.setCelular(usuario.getCelular());
            }
        }
        if (!usuario.getUnsuscripcionesEmail().contains(nombreNotificacion)) {
            notificacion.setEmail(usuario.getEmail());
        }
        if (notificacion.getEmail() == null && notificacion.getCelular() == null) {
            throw new DatoInvalidoException("El usuario no esta suscrito a la notificacion ingresada");
        }
        notificacionDAO.registrarNotificacionEnDb(notificacion);
        enviar(notificacion);
        return notificacion;
    }

    public void enviar(Notificacion notificacion) {

    }

    public void notificarTodosLosCompradores(List<Usuario> compradores, Producto producto,
                                             NombreNotificacion nombreNotificacion) {
        for (Usuario usuario : compradores) {
            enviarNotificacion(usuario, producto, 0, 0, nombreNotificacion);
        }
    }

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
