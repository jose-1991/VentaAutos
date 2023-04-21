package com.car.sales.company.exceptions;

public class DatoInvalidoException extends RuntimeException{
    public DatoInvalidoException(String mensaje){
        super(mensaje);
    }
}
