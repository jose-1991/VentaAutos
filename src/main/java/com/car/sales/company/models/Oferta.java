package com.car.sales.company.models;

import java.util.Objects;

public class Oferta {
    private double monto;
    private Usuario comprador;

    public Oferta(double monto, Usuario comprador) {
        this.monto = monto;
        this.comprador = comprador;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public Usuario getComprador() {
        return comprador;
    }

    public void setComprador(Usuario comprador) {
        this.comprador = comprador;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Oferta)) return false;
        Oferta oferta = (Oferta) o;
        return Double.compare(oferta.monto, monto) == 0 && comprador.getIdentificacion().equals(oferta.comprador.getIdentificacion());
    }

}
