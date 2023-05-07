package com.car.sales.company.services;

import com.car.sales.company.exceptions.DatoInvalidoException;
import com.car.sales.company.exceptions.UsuarioNoEncontradoException;
import com.car.sales.company.models.Usuario;

import java.util.ArrayList;
import java.util.List;

import static com.car.sales.company.helper.ValidacionHelper.validarString;
import static com.car.sales.company.helper.ValidacionHelper.validarUsuario;

public class UsuarioService {

    private final String VALIDAR_EMAIL = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
    private final String VALIDAR_CELULAR = "^(\\+591)?(6|7)[0-9]{7}$";
    private final String VALIDAR_IDENTIFICACION = "^[a-zA-Z0-9]{7,11}$";      //"^\\d{7,11}([\\s-]\\d[A-Z])?$";
    private final String VALIDAR_SOLO_LETRAS = "^[a-zA-Z ]+$";
    List<Usuario> usuarios = new ArrayList<>();


    public Usuario registrarUsuario(Usuario usuario) {
        if (usuario != null) {
            validarUsuario(usuario);
            if (usuario.getTipoUsuario().equalsIgnoreCase("Vendedor") && usuario.isAceptaNotificacionSms()){
                usuario.getUnsuscripcionesSms().add("CompradorPrimeraOFerta");
                usuario.getUnsuscripcionesSms().add("CompradorAceptaOferta");
            }else if (usuario.isAceptaNotificacionSms()){
                usuario.getUnsuscripcionesSms().add("NuevoVehiculoEnVenta");
                usuario.getUnsuscripcionesSms().add("VendedorAceptaOferta");
            }
            usuarios.add(usuario);
            return usuario;
        }
        throw new DatoInvalidoException("El usuario no debe ser nulo");

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

    public Usuario modificarUsuario(String identificacion, String nuevoTipoUsuario, String nuevoCelular) {
        validarString(identificacion);
        validarString(nuevoTipoUsuario);

        for (Usuario usuario : usuarios) {
            if (usuario.getIdentificacion().equals(identificacion)) {
                usuario.setTipoUsuario(nuevoTipoUsuario);
                usuario.setCelular(nuevoCelular);
                return usuario;
            }
        }
        throw new UsuarioNoEncontradoException("No existe usuario registrado con la identificacion ingresada");
    }

    private boolean validarValorDeCampo(String nombreCampo, String valor) {
        String regex = "";
        String mensajeError = "   -->  ";
        switch (nombreCampo) {
            case "nombre":
            case "apellido":
            case "tipoUsuario":
            case "tipoIdentificacion":
                regex = VALIDAR_SOLO_LETRAS;
                mensajeError += "solo debe contener letras";
                break;
            case "identificacion":
                regex = VALIDAR_IDENTIFICACION;
                mensajeError += "error en el formato de identificacion";
                break;
            case "email":
                regex = VALIDAR_EMAIL;
                mensajeError += "no es un email valido";
                break;
            case "celular":
                regex = VALIDAR_CELULAR;
                mensajeError = "no es un celular valido";
                break;
        }
        if (valor.matches(regex)) {
            return true;
        }
        throw new RuntimeException(valor + mensajeError);
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
