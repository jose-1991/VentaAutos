package com.car.sales.company.models;


public class Notificacion {
    private String id;
    private NombreNotificacion nombreNotificacion;
    private TipoNotificacion tipoNotificacion;
    private TipoUsuario tipoUsuario;

    public Notificacion() {
    }

    public Notificacion(String id, NombreNotificacion nombreNotificacion, TipoNotificacion tipoNotificacion,
                        TipoUsuario tipoUsuario) {
        this.id = id;
        this.nombreNotificacion = nombreNotificacion;
        this.tipoNotificacion = tipoNotificacion;
        this.tipoUsuario = tipoUsuario;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
