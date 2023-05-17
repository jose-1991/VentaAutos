package com.car.sales.company.models;

public class Notificacion {
    private NombreNotificacion nombreNotificacion;
    private Vehiculo vehiculo;
    private double montoOferta;
    private double montoContraOferta;
    private String email;
    private String celular;

    public Notificacion(NombreNotificacion nombreNotificacion, Vehiculo vehiculo, double montoOferta,double montoContraOferta, String email, String celular) {
        this.nombreNotificacion = nombreNotificacion;
        this.vehiculo = vehiculo;
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

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(Vehiculo vehiculo) {
        this.vehiculo = vehiculo;
    }

    public double getMontoOferta() {
        return montoOferta;
    }

    public void setMontoOferta(double montoOferta) {
        this.montoOferta = montoOferta;
    }
}
