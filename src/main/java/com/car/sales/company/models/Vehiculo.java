package com.car.sales.company.models;

import java.util.UUID;

public class Vehiculo {
    private UUID stockNumber;
    private String vin;
    private String marca;
    private String modelo;
    private int anio;
    private double precio;

    public Vehiculo(String vin, String marca, String modelo, int anio, double precio) {
        this.stockNumber = UUID.randomUUID();
        this.vin = vin;
        this.marca = marca;
        this.modelo = modelo;
        this.anio = anio;
        this.precio = precio;
    }


    public UUID getStockNumber() {
        return stockNumber;
    }

    public void setStockNumber(UUID stockNumber) {
        this.stockNumber = stockNumber;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }
}


