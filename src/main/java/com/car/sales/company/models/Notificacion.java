package com.car.sales.company.models;

public class Notificacion {
    NombreNotificacion nombreNotificacion;
    TipoNotificacion tipoNotificacion;
    TipoUsuario tipoUsuario;

    public Notificacion() {
    }

    public Notificacion(NombreNotificacion nombreNotificacion, TipoNotificacion tipoNotificacion, TipoUsuario tipoUsuario) {
        this.nombreNotificacion = nombreNotificacion;
        this.tipoNotificacion = tipoNotificacion;
        this.tipoUsuario = tipoUsuario;
    }

    public NombreNotificacion getNombreNotificacion() {
        return nombreNotificacion;
    }

    public void setNombreNotificacion(NombreNotificacion nombreNotificacion) {
        this.nombreNotificacion = nombreNotificacion;
    }

    public TipoNotificacion getTipoNotificacion() {
        return tipoNotificacion;
    }

    public void setTipoNotificacion(TipoNotificacion tipoNotificacion) {
        this.tipoNotificacion = tipoNotificacion;
    }

    public TipoUsuario getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(TipoUsuario tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }
}
