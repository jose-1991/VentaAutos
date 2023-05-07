package com.car.sales.company.models;

import java.util.Objects;

public class Oferta {
    private String monto;
    private Usuario comprador;

    public Oferta(String monto, Usuario comprador) {
        this.monto = monto;
        this.comprador = comprador;
    }

    public Oferta(Usuario comprador) {
        this.comprador = comprador;
    }

    public String getMonto() {
        return monto;
    }

    public void setMonto(String monto) {
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
        return Objects.equals(monto, oferta.monto) && Objects.equals(comprador.getIdentificacion(), oferta.comprador.getIdentificacion());
    }

    @Override
    public int hashCode() {
        return Objects.hash(monto, comprador);
    }
}
