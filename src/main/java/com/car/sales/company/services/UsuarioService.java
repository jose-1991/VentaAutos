package com.car.sales.company.services;

import com.car.sales.company.dao.UsuarioDAO;
import com.car.sales.company.exceptions.DatoInvalidoException;
import com.car.sales.company.models.*;

import java.util.ArrayList;

import static com.car.sales.company.helper.ValidacionHelper.validarString;
import static com.car.sales.company.helper.ValidacionHelper.validarTipoUsuario;


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
        validarCelular(celular);
        return usuarioDAO.modificarUsuario(identificacion, validarCelular(celular));
    }

    public Usuario actualizarSuscripciones(Usuario usuario, NombreNotificacion nombreNotificacion, TipoNotificacion tipoNotificacion, Accion accion) {
        Usuario usuarioModificado = null;
        switch (accion) {
            case SUSCRIBIR:
                usuarioModificado = usuarioDAO.suscribirNotificacion(usuario.getIdentificacion(), nombreNotificacion,
                        tipoNotificacion);
                break;
            case UNSUSCRIBIR:
                usuarioModificado = usuarioDAO.unsucribirNotificacion(usuario.getIdentificacion(), nombreNotificacion,
                        tipoNotificacion);
                break;
            case SUSCRIBIR_TODO:
                usuarioModificado = usuarioDAO.suscribirTodo(usuario.getIdentificacion());
                break;
            case UNSUSCRIBIR_TODO:
                usuarioModificado = usuarioDAO.unsuscribirTodo(usuario);
                break;
        }
        return usuarioModificado;
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

