package com.car.sales.company.services;

import com.car.sales.company.dao.UsuarioDAO;
import com.car.sales.company.exceptions.DatoInvalidoException;
import com.car.sales.company.models.Accion;
import com.car.sales.company.models.NombreNotificacion;
import com.car.sales.company.models.TipoNotificacion;
import com.car.sales.company.models.Usuario;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.car.sales.company.helper.ValidacionHelper.validarString;
import static com.car.sales.company.helper.ValidacionHelper.validarTipoUsuario;
import static com.car.sales.company.models.Accion.SUSCRIBIR;
import static com.car.sales.company.models.NombreNotificacion.*;
import static com.car.sales.company.models.TipoNotificacion.SMS;
import static com.car.sales.company.models.TipoUsuario.COMPRADOR;
import static com.car.sales.company.models.TipoUsuario.VENDEDOR;
import static com.car.sales.company.services.NotificacionService.*;

public class UsuarioService {

    private final String VALIDAR_EMAIL = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
    private final String VALIDAR_CELULAR = "^(\\+591)?(6|7)[0-9]{7}$";
    private final String VALIDAR_PASAPORTE = "^\\d{7,11}([\\s-]\\d[A-Z])?$";      //"^\\d{7,11}([\\s-]\\d[A-Z])?$";
    private final String VALIDAR_CI_LICENCIA = "^[0-9]{7,11}$";
    private final String PASAPORTE = "Pasaporte";
    private UsuarioDAO usuarioDAO;

    public UsuarioService(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    public Usuario registrarUsuario(Usuario usuario) {
        if (usuario != null) {
            validarUsuario(usuario);
            usuarioDAO.registrarUsuario(usuario);
            return usuario;
        }
        throw new DatoInvalidoException("El usuario no debe ser nulo");
    }

    public void validarUsuario(Usuario usuario) {

        validarString(usuario.getNombre());
        validarString(usuario.getApellido());

        validarIdentificacion(usuario.getIdentificacion(), validarString(usuario.getTipoIdentificacion()));
        validarEmail(usuario.getEmail());
        usuario.setUnsuscribcionesEmail(new ArrayList<>());
        validarTipoUsuario(usuario.getTipoUsuario());
        if (validarCelular(usuario.getCelular()) != null) {
            usuario.setAceptaNotificacionSms(true);
            usuario.setUnsuscribcionesSms(new ArrayList<>());
            if (usuario.getTipoUsuario().equals(VENDEDOR)) {
                usuario.getUnsuscripcionesSms().add(COMPRADOR_PRIMERA_OFERTA);
                usuario.getUnsuscripcionesSms().add(COMPRADOR_ACEPTA_OFERTA);
            } else {
                usuario.getUnsuscripcionesSms().add(NUEVO_VEHICULO_EN_VENTA);
                usuario.getUnsuscripcionesSms().add(VENDEDOR_ACEPTA_OFERTA);
            }
        } else {
            usuario.setCelular(null);
        }
    }

    public void eliminarUsuario(String identificacion) {
        validarString(identificacion);
        usuarioDAO.eliminarUsuario(identificacion);

    }

    public Usuario modificarUsuario(String identificacion, String celular) {
        validarString(identificacion);
        return usuarioDAO.modificarUsuario(identificacion, validarCelular(celular));
    }

    public Usuario interaccionSuscripciones(Usuario usuario, NombreNotificacion nombreNotificacion, Accion accion, TipoNotificacion tipoNotificacion) {
        switch (accion) {
            case SUSCRIBIR:
            case UNSUSCRIBIR:
                suscripcionOrUnsuscripcionNotificacion(usuario, nombreNotificacion, accion, tipoNotificacion);
                break;
            case SUSCRIBIR_TODO:
                usuarioDAO.suscribirTodo(usuario);
                break;
            case UNSUSCRIBIR_TODO:
                List<NombreNotificacion> listaNotificacionesEmail;
                List<NombreNotificacion> listaNotificacionesSms = null;
                if (usuario.getTipoUsuario().equals(COMPRADOR)) {
                    listaNotificacionesEmail = NOTIFICACIONES_EMAIL_COMPRADOR;
                    if (usuario.isAceptaNotificacionSms()) {
                        listaNotificacionesSms = NOTIFICACIONES_SMS_COMPRADOR;
                    }
                } else {
                    listaNotificacionesEmail = NOTIFICACIONES_EMAIL_VENDEDOR;
                    if (usuario.isAceptaNotificacionSms()) {
                        listaNotificacionesSms = NOTIFICACIONES_SMS_VENDEDOR;
                    }
                }
                usuarioDAO.unsuscribirTodo(usuario, listaNotificacionesEmail, listaNotificacionesSms);
                break;
        }
        return usuario;
    }

    private void suscripcionOrUnsuscripcionNotificacion1(Usuario usuario, NombreNotificacion nombreNotificacion,
                                                         Accion accion, TipoNotificacion tipoNotificacion) {

        switch (tipoNotificacion) {
            case EMAIL:
                if (accion.equals(SUSCRIBIR)) {
                    usuario.getUnsuscripcionesEmail().remove(nombreNotificacion);
//                    usuarioDAO.suscribirNotificacion(usuario.getIdentificacion(),nombreNotificacion);
                } else {
                    usuario.getUnsuscripcionesEmail().add(nombreNotificacion);
                }
                break;
            case SMS:
                if (usuario.isAceptaNotificacionSms() && NOTIFICACIONES_SMS_VENDEDOR.contains(nombreNotificacion)) {
                    if (accion.equals(SUSCRIBIR)) {
                        usuario.getUnsuscripcionesSms().remove(nombreNotificacion);
                    } else {
                        usuario.getUnsuscripcionesSms().add(nombreNotificacion);
                    }
                } else {
                    throw new DatoInvalidoException("La notificacion ingresada no es valida");
                }
                break;
        }
    }

    private void suscripcionOrUnsuscripcionNotificacion(Usuario usuario, NombreNotificacion nombreNotificacion,
                                                        Accion accion, TipoNotificacion tipoNotificacion) {
        switch (usuario.getTipoUsuario()) {
            case COMPRADOR:
                if (tipoNotificacion.equals(SMS)) {
                    if (usuario.isAceptaNotificacionSms() && NOTIFICACIONES_SMS_COMPRADOR.contains(nombreNotificacion)) {
                        usuarioDAO.actualizarSuscripcion(usuario.getIdentificacion(), nombreNotificacion, accion, tipoNotificacion);
                    }
                } else {
                    if (NOTIFICACIONES_EMAIL_COMPRADOR.contains(nombreNotificacion)) {
                        usuarioDAO.actualizarSuscripcion(usuario.getIdentificacion(), nombreNotificacion, accion, tipoNotificacion);
                    }
                }
                break;
            case VENDEDOR:
                if (tipoNotificacion.equals(SMS)) {
                    if (usuario.isAceptaNotificacionSms() && NOTIFICACIONES_SMS_VENDEDOR.contains(nombreNotificacion)) {
                        usuarioDAO.actualizarSuscripcion(usuario.getIdentificacion(), nombreNotificacion, accion, tipoNotificacion);
                    }
                } else {
                    if (NOTIFICACIONES_EMAIL_VENDEDOR.contains(nombreNotificacion)) {
                        usuarioDAO.actualizarSuscripcion(usuario.getIdentificacion(), nombreNotificacion, accion, tipoNotificacion);
                    }
                }
                break;
        }
    }

    private void validarEmail(String email) {
        validarString(email);
        if (!email.matches(VALIDAR_EMAIL)) {
            throw new RuntimeException(email + " -> no es un email valido");
        }
    }

    private String validarCelular(String celular) {
        if (celular == null || celular.trim().isEmpty()) {
            return null;
        }
        if (celular.matches(VALIDAR_CELULAR)) {
            return celular;
        }
        throw new RuntimeException(celular + " -> no tiene el formato adecuado");
    }

    private void validarIdentificacion(String identificacion, String tipoIdentificacion) {
        validarString(identificacion);
        String regex = VALIDAR_CI_LICENCIA;
        if (tipoIdentificacion.equals(PASAPORTE)) {
            regex = VALIDAR_PASAPORTE;
        }
        if (identificacion.matches(regex)) {
            return;
        }
        throw new RuntimeException(identificacion + " -> identificacion invalida");
    }

    //    nombreNotificacion = llave  Integer = contador
//    si notificacion existe -> contador++
//    sino nuevo registro y devolver la lista de llaves
    public int guardarNotificaciones(NombreNotificacion nombreNotificacion) {
        Map<NombreNotificacion, Integer> mapa = new HashMap<>();

        if (mapa.containsKey(nombreNotificacion)) {
            mapa.put(nombreNotificacion, mapa.get(nombreNotificacion) + 1);
        } else {
            throw new IllegalArgumentException();
        }
        return mapa.get(nombreNotificacion);

    }
}

