package com.car.sales.company.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Publicacion {
    private Usuario vendedor;
    private Vehiculo vehiculo;
    private LocalDate fecha;
    private List<Oferta> ofertasCompradores;
    private List<Oferta> ofertasVendedores;
    private Notificacion notificacion;

    public Publicacion() {
        ofertasCompradores = new ArrayList<>();
        ofertasVendedores = new ArrayList<>();
    }

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

    public List<Oferta> getOfertasVendedores() {
        return ofertasVendedores;
    }

    public void setOfertasVendedores(List<Oferta> ofertasVendedores) {
        this.ofertasVendedores = ofertasVendedores;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }
}
