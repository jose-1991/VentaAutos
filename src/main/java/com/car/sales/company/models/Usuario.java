package com.car.sales.company.models;

import java.util.List;

public class Usuario {
    private String nombre;
    private String apellido;
    private String tipoIdentificacion;
    private String identificacion;
    private TipoUsuario tipoUsuario;
    private String email;
    private String celular;
    private boolean aceptaNotificacionSms;
    private List<NombreNotificacion> unsuscribcionesEmail;
    private List<NombreNotificacion> unsuscribcionesSms;

    public Usuario() {
    }

    public Usuario(String nombre, String apellido, String tipoIdentificacion, String identificacion, String email,
                   TipoUsuario tipoUsuario, String celular) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.identificacion = identificacion;
        this.tipoIdentificacion = tipoIdentificacion;
        this.email = email;
        this.tipoUsuario = tipoUsuario;
        this.celular = celular;
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

    public TipoUsuario getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(TipoUsuario tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public List<NombreNotificacion> getUnsuscripcionesEmail() {
        return unsuscribcionesEmail;
    }

    public void setUnsuscribcionesEmail(List<NombreNotificacion> unsuscribcionesEmail) {
        this.unsuscribcionesEmail = unsuscribcionesEmail;
    }

    public List<NombreNotificacion> getUnsuscripcionesSms() {
        return unsuscribcionesSms;
    }

    public void setUnsuscribcionesSms(List<NombreNotificacion> unsuscribcionesSms) {
        this.unsuscribcionesSms = unsuscribcionesSms;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", tipoIdentificacion='" + tipoIdentificacion + '\'' +
                ", identificacion='" + identificacion + '\'' +
                ", tipoUsuario=" + tipoUsuario +
                ", email='" + email + '\'' +
                ", celular='" + celular + '\'' +
                ", aceptaNotificacionSms=" + aceptaNotificacionSms + '\'' +
                ", unsuscripcionesSms=" + unsuscribcionesSms + '\'' +
                ", unsuscripcionesEmail=" + unsuscribcionesEmail +
                '}';
    }
}
