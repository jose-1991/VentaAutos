package com.car.sales.company.models;

import java.time.LocalDate;
import java.util.List;

public class Publicacion {
    private Usuario vendedor;
    private Vehiculo vehiculo;
    private LocalDate fecha;
    private List<Oferta> ofertasCompradores;
    private boolean estaDisponibleEnLaWeb;



    public Usuario getVendedor() {
        return vendedor;
    }

    public void setVendedor(Usuario vendedor) {
        this.vendedor = vendedor;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(Vehiculo vehiculo) {
        this.vehiculo = vehiculo;
    }

    public List<Oferta> getOfertasCompradores() {
        return ofertasCompradores;
    }

    public void setOfertasCompradores(List<Oferta> ofertasCompradores) {
        this.ofertasCompradores = ofertasCompradores;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public boolean isEstaDisponibleEnLaWeb() {
        return estaDisponibleEnLaWeb;
    }

    public void setEstaDisponibleEnLaWeb(boolean estaDisponibleEnLaWeb) {
        this.estaDisponibleEnLaWeb = estaDisponibleEnLaWeb;
    }
}
