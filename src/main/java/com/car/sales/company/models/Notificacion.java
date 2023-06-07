package com.car.sales.company.models;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Notificacion)) return false;
        Notificacion that = (Notificacion) o;
        return nombreNotificacion == that.nombreNotificacion && tipoNotificacion == that.tipoNotificacion;
    }

}
