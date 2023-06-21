package com.car.sales.company.models;

import java.time.LocalDateTime;

public class Oferta {

    private double montoOferta;
    private double montoContraOferta;
    private Usuario comprador;
    private LocalDateTime fechaOferta;
    private boolean inactivo;

    public Oferta() {
    }

    public Oferta(Usuario comprador, double montoOferta, double montoContraOferta,
                  LocalDateTime fechaOferta) {

        this.comprador = comprador;
        this.montoOferta = montoOferta;
        this.montoContraOferta = montoContraOferta;
        this.fechaOferta = fechaOferta;
    }

    public double getMontoOferta() {
        return montoOferta;
    }

    public void setMontoOferta(double montoOferta) {
        this.montoOferta = montoOferta;
    }

    public Usuario getComprador() {
        return comprador;
    }

    public void setComprador(Usuario comprador) {
        this.comprador = comprador;
    }

    public double getMontoContraOferta() {
        return montoContraOferta;
    }

    public void setMontoContraOferta(double montoContraOferta) {
        this.montoContraOferta = montoContraOferta;
    }

    public LocalDateTime getFechaOferta() {
        return fechaOferta;
    }

    public void setFechaOferta(LocalDateTime fechaOferta) {
        this.fechaOferta = fechaOferta;
    }

    public boolean isInactivo() {
        return inactivo;
    }

    public void setInactivo(boolean inactivo) {
        this.inactivo = inactivo;
    }
}
