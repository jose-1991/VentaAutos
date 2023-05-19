package com.car.sales.company.services;

import com.car.sales.company.exceptions.DatoInvalidoException;
import com.car.sales.company.exceptions.UsuarioNoEncontradoException;
import com.car.sales.company.models.Accion;
import com.car.sales.company.models.NombreNotificacion;
import com.car.sales.company.models.TipoNotificacion;
import com.car.sales.company.models.Usuario;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.car.sales.company.helper.ValidacionHelper.validarString;
import static com.car.sales.company.helper.ValidacionHelper.validarTipoUsuario;
import static com.car.sales.company.models.Accion.SUSCRIBIR;
import static com.car.sales.company.models.NombreNotificacion.*;
import static com.car.sales.company.models.TipoUsuario.VENDEDOR;
import static com.car.sales.company.services.NotificacionService.NOTIFICACIONES_EMAIL_LIST;
import static com.car.sales.company.services.NotificacionService.NOTIFICACIONES_SMS_LIST;

public class UsuarioService {

    private final String VALIDAR_EMAIL = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
    private final String VALIDAR_CELULAR = "^(\\+591)?(6|7)[0-9]{7}$";
    private final String VALIDAR_PASAPORTE = "^\\d{7,11}([\\s-]\\d[A-Z])?$";      //"^\\d{7,11}([\\s-]\\d[A-Z])?$";
    private final String VALIDAR_CI_LICENCIA = "^[0-9]{7,11}$";
    private final String PASAPORTE = "Pasaporte";
    List<Usuario> listaUsuariosRegistrados = new ArrayList<>();

    public Usuario registrarUsuario(Usuario usuario) {
        if (usuario != null) {
            validarUsuario(usuario);
            listaUsuariosRegistrados.add(usuario);
            return usuario;
        }
        throw new DatoInvalidoException("El usuario no debe ser nulo");
    }

    public void validarUsuario(Usuario usuario) {

        validarString(usuario.getNombre());
        validarString(usuario.getApellido());

        validarIdentificacion(usuario.getIdentificacion(), validarString(usuario.getTipoIdentificacion()));
        validarEmail(usuario.getEmail());
        validarTipoUsuario(usuario.getTipoUsuario());
        if (usuario.getCelular() != null && !usuario.getCelular().trim().isEmpty()) {
            validarCelular(usuario.getCelular());
            usuario.setAceptaNotificacionSms(true);
            if (usuario.getTipoUsuario().equals(VENDEDOR)) {
                usuario.setUnsuscribcionesSms(Arrays.asList(COMPRADOR_PRIMERA_OFERTA, COMPRADOR_ACEPTA_OFERTA));
            } else {
                usuario.setUnsuscribcionesSms(Arrays.asList(NUEVO_VEHICULO_EN_VENTA, VENDEDOR_ACEPTA_OFERTA));
            }
        } else {
            usuario.setCelular(null);
        }
    }

    public Usuario eliminarUsuario(String identificacion) {
        validarString(identificacion);
        for (Usuario usuario : listaUsuariosRegistrados) {
            if (usuario.getIdentificacion().equals(identificacion)) {
                listaUsuariosRegistrados.remove(usuario);
                return usuario;
            }
        }
        throw new UsuarioNoEncontradoException("No existe usuario registrado con la identificacion ingresada");
    }

    public Usuario modificarUsuario(String identificacion, String nuevoCelular) {
        validarString(identificacion);

        for (Usuario usuario : listaUsuariosRegistrados) {
            if (usuario.getIdentificacion().equals(identificacion)) {
                usuario.setCelular(nuevoCelular);
                usuario.setAceptaNotificacionSms(true);
                return usuario;
            }
        }
        throw new UsuarioNoEncontradoException("No existe usuario registrado con la identificacion ingresada");
    }

    public Usuario actualizarSuscripcion(Usuario usuario, NombreNotificacion nombreNotificacion, Accion accion, TipoNotificacion tipoNotificacion) {

        switch (accion) {
            case SUSCRIBIR:
            case UNSUSCRIBIR:
                suscripcionOrUnsuscripcionNotificacion(usuario, nombreNotificacion, accion, tipoNotificacion);
                break;
            case SUSCRIBIR_TODO:
                usuario.getUnsuscripcionesEmail().clear();
                if (usuario.isAceptaNotificacionSms()) {
                    usuario.getUnsuscripcionesSms().clear();
                }
                break;
            case UNSUSCRIBIR_TODO:
                usuario.setUnsuscribcionesEmail(NOTIFICACIONES_EMAIL_LIST);
                if (usuario.isAceptaNotificacionSms()) {
                    usuario.setUnsuscribcionesSms(NOTIFICACIONES_SMS_LIST);
                }
                break;
        }
        return usuario;
    }

    private void suscripcionOrUnsuscripcionNotificacion(Usuario usuario, NombreNotificacion nombreNotificacion, Accion accion, TipoNotificacion tipoNotificacion) {

        switch (tipoNotificacion) {
            case EMAIL:
                if (accion.equals(SUSCRIBIR)) {
                    usuario.getUnsuscripcionesEmail().remove(nombreNotificacion);
                } else {
                    usuario.getUnsuscripcionesEmail().add(nombreNotificacion);
                }
                break;
            case SMS:
                if (usuario.isAceptaNotificacionSms() && NOTIFICACIONES_SMS_LIST.contains(nombreNotificacion)) {
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

    private void validarEmail(String email) {
        validarString(email);
        if (email.matches(VALIDAR_EMAIL)) {
            return;
        }
        throw new RuntimeException(email + " -> no es un email valido");
    }

    private void validarCelular(String celular) {
        validarString(celular);
        if (celular.matches(VALIDAR_CELULAR)) {
            return;
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
}

