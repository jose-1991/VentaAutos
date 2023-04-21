package com.car.sales.company.models;

public class Notificacion {
    private String tipoNotificacion;
    boolean envioEmail;
    boolean envioSms;

    public Notificacion(String tipoNotificacion, boolean envioEmail, boolean envioSms) {
        this.tipoNotificacion = tipoNotificacion;
        this.envioEmail = envioEmail;
        this.envioSms = envioSms;
    }

    public String getTipoNotificacion() {
        return tipoNotificacion;
    }

    public void setTipoNotificacion(String tipoNotificacion) {
        this.tipoNotificacion = tipoNotificacion;
    }

    public boolean isEnvioEmail() {
        return envioEmail;
    }

    public void setEnvioEmail(boolean envioEmail) {
        this.envioEmail = envioEmail;
    }

    public boolean isEnvioSms() {
        return envioSms;
    }

    public void setEnvioSms(boolean envioSms) {
        this.envioSms = envioSms;
    }
}
