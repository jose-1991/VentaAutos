package com.car.sales.company.services;

import com.car.sales.company.exceptions.DatoInvalidoException;
import com.car.sales.company.exceptions.UsuarioNoEncontradoException;
import com.models.*;


import static com.car.sales.company.helper.ValidacionHelper.validarEnteroPositivo;
import static com.models.TipoNotificacion.*;

public class VentaService {
    NotificacionService notificacionService;

    public Oferta realizarPrimeraOferta(Publicacion publicacion, Usuario comprador, String montoOferta) {
        Oferta oferta = new Oferta(validarEnteroPositivo(montoOferta).toString(), comprador);
        publicacion.getOfertasCompradores().add(oferta);
        notificacionService.ValidarNotificacion(publicacion, oferta, "CompradorPrimeraOferta",
                publicacion.getVendedor(), AMBOS);
        return oferta;
    }

    public Publicacion interactuar(Publicacion publicacion, Oferta oferta, String tipoUsuario, Accion accion, String nuevoMonto) {

        switch (accion) {
            case CONTRA_OFERTAR:
                if (tipoUsuario.equalsIgnoreCase("vendedor")) {
                    oferta.setMonto(validarEnteroPositivo(nuevoMonto).toString());
                    notificacionService.ValidarNotificacion(publicacion,oferta, "VendedorContraOferta",
                            oferta.getComprador(), EMAIL);

                } else {
                    throw new UsuarioNoEncontradoException("Solo el vendedor puede realizar una contra oferta");
                }
                break;
            case ACEPTAR:
                for (Oferta ofertaActual : publicacion.getOfertasCompradores()) {
                    if (!ofertaActual.equals(oferta)) {
                        notificacionService.ValidarNotificacion(publicacion,oferta, "VehiculoNoDisponible",
                                oferta.getComprador(),EMAIL);
                        publicacion.getOfertasCompradores().remove(ofertaActual);
                    }
                }
                publicacion.setEstaDisponibleEnLaWeb(false);
                if (tipoUsuario.equalsIgnoreCase("comprador")) {
                    notificacionService.ValidarNotificacion(publicacion,oferta, "CompradorAceptaOferta",publicacion.getVendedor(),AMBOS);
                } else {
                    notificacionService.ValidarNotificacion(publicacion,oferta, "VendedorAceptaOferta",oferta.getComprador(),AMBOS);
                }
                break;
            case RETIRAR:
                if (publicacion.getOfertasCompradores().contains(oferta) && tipoUsuario.equalsIgnoreCase("comprador")) {
                    publicacion.getOfertasCompradores().removeIf(o -> o.equals(oferta));
                    notificacionService.ValidarNotificacion(publicacion,oferta, "CompradorRetiraOferta",publicacion.getVendedor(),EMAIL);
                } else {
                    throw new DatoInvalidoException(" - la oferta ingresada no existe \n - El usuario debe ser de tipo comprador");
                }
                break;
            case RECHAZAR:
                if (tipoUsuario.equalsIgnoreCase("vendedor")) {
                    publicacion.getOfertasCompradores().removeIf(o -> o.equals(oferta));
                    notificacionService.ValidarNotificacion(publicacion,oferta, "VendedorDeclinaOferta",oferta.getComprador(),EMAIL);
                }
                break;
        }
        return publicacion;
    }
}
