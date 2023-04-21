package com.car.sales.company.models;

import java.util.Objects;
public class Usuario {
    private String nombre;
    private String apellido;
    private String tipoIdentificacion;
    private String identificacion;
    private String tipoUsuario;
    private String email;
    private String celular;
    private boolean aceptaNotificacionSms;

    public Usuario(String nombre, String apellido, String tipoIdentificacion, String identificacion, String email,
                   String tipoUsuario, String celular) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.identificacion = identificacion;
        this.tipoIdentificacion = tipoIdentificacion;
        this.email = email;
        this.tipoUsuario = tipoUsuario;
        this.celular = celular;
    }

    public Usuario(String nombre, String apellido, String tipoIdentificacion, String identificacion, String tipoUsuario, String email) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.tipoIdentificacion = tipoIdentificacion;
        this.identificacion = identificacion;
        this.tipoUsuario = tipoUsuario;
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
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

    public boolean isAceptaNotificacionSms() {
        return aceptaNotificacionSms;
    }

    public void setAceptaNotificacionSms(boolean aceptaNotificacionSms) {
        this.aceptaNotificacionSms = aceptaNotificacionSms;
    }

    public String getTipoIdentificacion() {
        return tipoIdentificacion;
    }

    public void setTipoIdentificacion(String tipoIdentificacion) {
        this.tipoIdentificacion = tipoIdentificacion;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

}
