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
import static com.car.sales.company.models.NombreNotificacion.*;
import static com.car.sales.company.models.TipoNotificacion.EMAIL;
import static com.car.sales.company.models.TipoUsuario.VENDEDOR;
import static com.car.sales.company.services.NotificacionService.NOTIFICACIONES_SMS_LIST;

public class UsuarioService {

    private final String VALIDAR_EMAIL = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
    private final String VALIDAR_CELULAR = "^(\\+591)?(6|7)[0-9]{7}$";
    private final String VALIDAR_IDENTIFICACION = "^[a-zA-Z0-9]{7,11}$";      //"^\\d{7,11}([\\s-]\\d[A-Z])?$";
    private final String VALIDAR_SOLO_LETRAS = "^[a-zA-Z ]+$";
    List<Usuario> usuarios = new ArrayList<>();


    public Usuario registrarUsuario(Usuario usuario) {
        if (usuario != null) {
            validarUsuario(usuario);
            usuarios.add(usuario);
            return usuario;
        }
        throw new DatoInvalidoException("El usuario no debe ser nulo");
    }

    public static void validarUsuario(Usuario usuario) {

        validarString(usuario.getNombre());
        validarString(usuario.getApellido());
        validarString(usuario.getTipoIdentificacion());
        validarString(usuario.getIdentificacion());
        validarString(usuario.getEmail());
        validarTipoUsuario(usuario.getTipoUsuario());
        if (usuario.getCelular() != null && !usuario.getCelular().trim().isEmpty()) {
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
        for (Usuario usuario : usuarios) {
            if (usuario.getIdentificacion().equals(identificacion)) {
                usuarios.remove(usuario);
                return usuario;
            }
        }
        throw new UsuarioNoEncontradoException("No existe usuario registrado con la identificacion ingresada");
    }

    public Usuario modificarUsuario(String identificacion, String nuevoCelular) {
        validarString(identificacion);

        for (Usuario usuario : usuarios) {
            if (usuario.getIdentificacion().equals(identificacion)) {
                usuario.setCelular(nuevoCelular);
                return usuario;
            }
        }
        throw new UsuarioNoEncontradoException("No existe usuario registrado con la identificacion ingresada");
    }

    // evaluar el caso de unsuscribir todas las notificaciones
    public Usuario actualizarSuscripcion(Usuario usuario, NombreNotificacion nombreNotificacion, Accion accion, TipoNotificacion tipoNotificacion) {
        switch (accion) {
            case SUSCRIBIR:
                if (tipoNotificacion.equals(EMAIL)) {
                    usuario.getUnsuscripcionesEmail().remove(nombreNotificacion);
                } else if (usuario.isAceptaNotificacionSms() && NOTIFICACIONES_SMS_LIST.contains(nombreNotificacion)) {
                    usuario.getUnsuscripcionesSms().remove(nombreNotificacion);
                } else {
                    throw new DatoInvalidoException("No es posible Suscribirse a la notificacion ingresada");
                }
                break;
            case UNSUSCRIBIR:
                if (tipoNotificacion.equals(EMAIL)) {
                    usuario.getUnsuscripcionesEmail().add(nombreNotificacion);
                } else if (usuario.isAceptaNotificacionSms() && NOTIFICACIONES_SMS_LIST.contains(nombreNotificacion)) {
                    usuario.getUnsuscripcionesSms().add(nombreNotificacion);
                } else {
                    throw new DatoInvalidoException("No es posible unsuscribirse a la notificacion ingresada");
                }
                break;
            case SUSCRIBIR_TODO:
                usuario.getUnsuscripcionesEmail().clear();
                if (usuario.isAceptaNotificacionSms()) {
                    usuario.getUnsuscripcionesSms().clear();
                }
                break;
            case UNSUSCRIBIR_TODO:
                for (NombreNotificacion notificacion : NombreNotificacion.values()) {
                    if (!usuario.getUnsuscripcionesEmail().contains(notificacion)) {
                        usuario.getUnsuscripcionesEmail().add(notificacion);
                    }
                }
                if (usuario.isAceptaNotificacionSms()) {
                    for (NombreNotificacion notificacion : NOTIFICACIONES_SMS_LIST) {
                        if (!usuario.getUnsuscripcionesSms().contains(notificacion)) {
                            usuario.getUnsuscripcionesSms().add(notificacion);
                        }
                    }
                }
                break;
        }
        return usuario;

    }

//    private String validarEmail(String email) {
//        validarString("email", email);
//        String regex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
//        if (email.matches(regex)) {
//            return email;
//        }
//        throw new RuntimeException(email + " -> no es un email valido");
//    }
//
//    private String validarCelular(String celular) {
//        validarString("celular", celular);
//        String regex = "^(\\+591)?(6|7)[0-9]{7}$";
//        if (celular.matches(regex)) {
//            return celular;
//        }
//        throw new RuntimeException(celular + " -> no tiene el formato adecuado");
//    }
//
//    private String validarIdentificacion(String identificacion, String tipoIdentificacion){
//        validarString("identificacion", identificacion);
//        String regex = "^\\d{7,11}([\\s-]\\d[A-Z])?$";
//        if (tipoIdentificacion.equalsIgnoreCase("Pasaporte")){
//            regex = "^[a-zA-Z0-9]{11}$";
//        }
//        if (identificacion.matches(regex)){
//            return identificacion;
//        }
//        throw new RuntimeException(identificacion + " -> identificacion invalida");
//    }
}
