package com.car.sales.company.helper;

import com.car.sales.company.exceptions.DatoInvalidoException;
import com.models.Usuario;
import com.models.Vehiculo;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class ValidacionHelper {

    private static final String VALIDAR_VIN = "^[A-Z\\d]{8}[\\dX][A-Z\\d]{2}\\d{6}$";
    private static final long MAX_DIAS_SIN_OFERTA = 5;

    public static void validarUsuario(Usuario usuario) {

        validarString(usuario.getNombre());
        validarString(usuario.getApellido());
        validarEnteroPositivo(usuario.getIdentificacion());
        validarString(usuario.getEmail());
        validarString(usuario.getTipoUsuario());
        if (usuario.getCelular() != null && !usuario.getCelular().trim().isEmpty()) {
            usuario.setAceptaNotificacionSms(true);
        } else {
            usuario.setCelular(null);
        }
    }

    public static void validarVehiculo(Vehiculo vehiculo) {

        validarVin(vehiculo.getVin());
        validarString(vehiculo.getMarca());
        validarString(vehiculo.getModelo());
        validarEnteroPositivo(vehiculo.getAnio());
        validarPositivoDecimal(vehiculo.getPrecio());
    }

    private static String validarVin(String vin) {
        if (vin.matches(VALIDAR_VIN)) {
            return vin;
        }
        throw new DatoInvalidoException("El Vin ingresado no es valido");
    }

    public static String validarString(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new DatoInvalidoException("El dato ingresado no es valido");
        }
        return valor;
    }

    public static boolean tieneMaximoDiasSinOfertas(LocalDate fechaPublicacion) {
        return ChronoUnit.DAYS.between(fechaPublicacion, LocalDate.now()) >= MAX_DIAS_SIN_OFERTA;
    }

    public static Integer validarEnteroPositivo(String valor) {

        try {
            valor = validarString(valor);
            Integer number = Integer.parseInt(valor);
            if (number >= 1) {
                return number;
            } else {
                throw new DatoInvalidoException("numeros negativos no son validos");
            }
        } catch (NumberFormatException e) {
            throw new DatoInvalidoException("el valor ingresado debe ser un numero");
        }
    }

    public static double validarPositivoDecimal(String valor) {

        try {
            valor = validarString(valor);
            double number = Double.parseDouble(valor);
            if (number >= 1) {
                return Math.round(number * 100.0) / 100.0;
            } else {
                throw new DatoInvalidoException("numeros negativos son invalidos");
            }
        } catch (NumberFormatException e) {
            throw new DatoInvalidoException("el valor ingresado debe ser un numero");
        }
    }
}
