package com.car.sales.company.helper;

import com.car.sales.company.exceptions.DatoInvalidoException;
import com.car.sales.company.models.TipoUsuario;
import com.car.sales.company.models.Vehiculo;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class ValidacionHelper {

    private static final String VALIDAR_VIN = "^[A-Z\\d]{8}[\\dX][A-Z\\d]{2}\\d{6}$";
    private static final long MAX_DIAS_SIN_OFERTA = 5;


    public static void validarVehiculo(Vehiculo vehiculo) {

        validarVin(vehiculo.getVin());
        validarString(vehiculo.getMarca());
        validarString(vehiculo.getModelo());
        validarEnteroPositivo(vehiculo.getAnio());
        validarPositivoDecimal(vehiculo.getPrecio());
    }

    private static void validarVin(String vin) {
        if (vin.matches(VALIDAR_VIN)) {
            return;
        }
        throw new DatoInvalidoException("El Vin ingresado no es valido");
    }

    public static void validarString(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new DatoInvalidoException("El dato ingresado no es valido");
        }
    }

    public static void validarTipoUsuario(TipoUsuario tipoUsuario) {
        if (tipoUsuario == null) {
            throw new DatoInvalidoException("El Tipo de Usuario no debe ser nulo");
        }
    }

    public static boolean tieneMaximoDiasSinOfertas(LocalDate fechaPublicacion) {
        return ChronoUnit.DAYS.between(fechaPublicacion, LocalDate.now()) >= MAX_DIAS_SIN_OFERTA;
    }

    public static void validarEnteroPositivo(int valor) {
        if (valor >= 1) {
        } else {
            throw new DatoInvalidoException("Numeros negativos no son validos");
        }
    }

    public static double validarPositivoDecimal(double valor) {

        if (valor >= 1) {
            return Math.round(valor * 100.0) / 100.0;
        } else {
            throw new DatoInvalidoException("Numeros negativos no son validos");
        }
    }
}
