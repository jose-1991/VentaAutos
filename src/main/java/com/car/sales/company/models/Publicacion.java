package com.car.sales.company.models;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class Publicacion {
    private UUID id;
    private Usuario vendedor;
    private Producto producto;
    private LocalDate fecha;
    private double precio;
    private List<Oferta> ofertasCompradores;
    private boolean estaDisponibleEnWeb;

    public Publicacion() {
        this.id = UUID.randomUUID();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Usuario getVendedor() {
        return vendedor;
    }

    public void setVendedor(Usuario vendedor) {
        this.vendedor = vendedor;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
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

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public boolean isEstaDisponibleEnWeb() {
        return estaDisponibleEnWeb;
    }

    public void setEstaDisponibleEnWeb(boolean estaDisponibleEnWeb) {
        this.estaDisponibleEnWeb = estaDisponibleEnWeb;
    }

    @Override
    public String toString() {
        return "Publicacion{" +
                "id=" + id +
                ", vendedor=" + vendedor +
                ", producto=" + producto +
                ", fecha=" + fecha +
                ", precio=" + precio +
                ", estaDisponibleEnLaWeb=" + estaDisponibleEnWeb +
                '}';
    }
}
