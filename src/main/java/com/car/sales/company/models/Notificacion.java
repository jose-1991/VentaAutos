package com.car.sales.company.models;

public class Notificacion {
    private NombreNotificacion nombreNotificacion;
    private Producto producto;
    private double montoOferta;
    private double montoContraOferta;
    private String email;
    private String celular;

    public Notificacion(NombreNotificacion nombreNotificacion, Producto producto, double montoOferta,
                        double montoContraOferta, String email, String celular) {
        this.nombreNotificacion = nombreNotificacion;
        this.producto = producto;
        this.montoOferta = montoOferta;
        this.montoContraOferta = montoContraOferta;
        this.email = email;
        this.celular = celular;
    }

    public NombreNotificacion getNombreNotificacion() {
        return nombreNotificacion;
    }

    public void setNombreNotificacion(NombreNotificacion nombreNotificacion) {
        this.nombreNotificacion = nombreNotificacion;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public double getMontoOferta() {
        return montoOferta;
    }

    public void setMontoOferta(double montoOferta) {
        this.montoOferta = montoOferta;
    }

    public double getMontoContraOferta() {
        return montoContraOferta;
    }

    public void setMontoContraOferta(double montoContraOferta) {
        this.montoContraOferta = montoContraOferta;
    }
}
