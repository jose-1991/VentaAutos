package com.car.sales.company.services;

import com.car.sales.company.dao.UsuarioDAO;
import com.car.sales.company.exceptions.DatoInvalidoException;
import com.car.sales.company.models.Accion;
import com.car.sales.company.models.Notificacion;
import com.car.sales.company.models.Usuario;

import java.util.ArrayList;

import static com.car.sales.company.helper.ValidacionHelper.validarString;
import static com.car.sales.company.helper.ValidacionHelper.validarTipoUsuario;
import static com.car.sales.company.models.NombreNotificacion.*;
import static com.car.sales.company.models.TipoNotificacion.SMS;
import static com.car.sales.company.models.TipoUsuario.COMPRADOR;
import static com.car.sales.company.models.TipoUsuario.VENDEDOR;
import static com.car.sales.company.services.NotificacionService.NOTIFICACIONES_LIST;

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
        usuario.setListaUnsuscribciones(new ArrayList<>());
        validarTipoUsuario(usuario.getTipoUsuario());
        if (validarCelular(usuario.getCelular()) != null) {
            usuario.setAceptaNotificacionSms(true);
            if (usuario.getTipoUsuario().equals(VENDEDOR)) {
                usuario.getListaUnsuscribciones().add(new Notificacion(COMPRADOR_PRIMERA_OFERTA, SMS, VENDEDOR));
                usuario.getListaUnsuscribciones().add(new Notificacion(COMPRADOR_ACEPTA_OFERTA, SMS, VENDEDOR));
            } else {
                usuario.getListaUnsuscribciones().add(new Notificacion(NUEVO_VEHICULO_EN_VENTA, SMS, COMPRADOR));
                usuario.getListaUnsuscribciones().add(new Notificacion(VENDEDOR_ACEPTA_OFERTA, SMS, COMPRADOR));
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

    public Usuario actualizarSuscripciones(Usuario usuario, Notificacion notificacion, Accion accion) {
        // TODO: 30/5/2023  crear objeto notificacion
        if (!usuario.getTipoUsuario().equals(notificacion.getTipoUsuario())) {
            throw new DatoInvalidoException("La notificacion ingresada no es valida");
        }
        switch (accion) {
            case SUSCRIBIR:
                if (usuario.getListaUnsuscribciones().contains(notificacion)) {
                    if (usuario.isAceptaNotificacionSms()) {
                        usuarioDAO.suscribirNotificacion(usuario, notificacion);
                    }
                }
                break;
            case UNSUSCRIBIR:
                if (!usuario.getListaUnsuscribciones().contains(notificacion)) {
                    usuarioDAO.unsucribirNotificacion(usuario.getIdentificacion(), notificacion);
                }
                break;
            case SUSCRIBIR_TODO:
                usuarioDAO.suscribirTodo(usuario);
                break;
            case UNSUSCRIBIR_TODO:
                usuarioDAO.unsuscribirTodo(usuario, NOTIFICACIONES_LIST);
                break;
        }
        return usuario;
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
}

