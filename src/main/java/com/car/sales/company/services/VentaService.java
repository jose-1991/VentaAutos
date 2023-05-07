package com.car.sales.company.services;

import com.car.sales.company.exceptions.DatoInvalidoException;
import com.car.sales.company.exceptions.UsuarioNoEncontradoException;
import com.car.sales.company.models.Accion;
import com.car.sales.company.models.Oferta;
import com.car.sales.company.models.Publicacion;
import com.car.sales.company.models.Usuario;

import static com.car.sales.company.helper.ValidacionHelper.validarEnteroPositivo;
import static com.car.sales.company.models.TipoNotificacion.AMBOS;
import static com.car.sales.company.models.TipoNotificacion.EMAIL;

public class VentaService {
    NotificacionService notificacionService;

    public Oferta realizarPrimeraOferta(Publicacion publicacion, Usuario comprador, String montoOferta) {
        Oferta oferta = new Oferta(validarEnteroPositivo(montoOferta).toString(), comprador);
        publicacion.getOfertasCompradores().add(oferta);
        notificacionService.ValidarNotificacion(publicacion, oferta, "CompradorPrimeraOferta", AMBOS);
        return oferta;
    }

    public Publicacion interactuar(Publicacion publicacion, Oferta oferta, String tipoUsuario, Accion accion, String nuevoMonto) {

        switch (accion) {
            case CONTRA_OFERTAR:
                if (tipoUsuario.equalsIgnoreCase("vendedor")) {
                    oferta.setMonto(validarEnteroPositivo(nuevoMonto).toString());
                    notificacionService.ValidarNotificacion(publicacion, oferta, "VendedorContraOferta", EMAIL);

                } else {
                    throw new UsuarioNoEncontradoException("Solo el vendedor puede realizar una contra oferta");
                }
                break;
            case ACEPTAR:
                for (Oferta ofertaActual : publicacion.getOfertasCompradores()) {
                    if (!ofertaActual.equals(oferta)) {
                        notificacionService.ValidarNotificacion(publicacion, oferta, "VehiculoNoDisponible", EMAIL);
                        publicacion.getOfertasCompradores().remove(ofertaActual);
                    }
                }
                publicacion.setEstaDisponibleEnLaWeb(false);
                if (tipoUsuario.equalsIgnoreCase("comprador")) {
                    notificacionService.ValidarNotificacion(publicacion, oferta, "CompradorAceptaOferta", AMBOS);
                } else {
                    notificacionService.ValidarNotificacion(publicacion, oferta, "VendedorAceptaOferta", AMBOS);
                }
                break;
            case RETIRAR:
                if (publicacion.getOfertasCompradores().contains(oferta) && tipoUsuario.equalsIgnoreCase("comprador")) {
                    publicacion.getOfertasCompradores().removeIf(o -> o.equals(oferta));
                    notificacionService.ValidarNotificacion(publicacion, oferta, "CompradorRetiraOferta", EMAIL);
                } else {
                    throw new DatoInvalidoException(" - la oferta ingresada no existe \n - El usuario debe ser de tipo comprador");
                }
                break;
            case RECHAZAR:
                if (tipoUsuario.equalsIgnoreCase("vendedor")) {
                    publicacion.getOfertasCompradores().removeIf(o -> o.equals(oferta));
                    notificacionService.ValidarNotificacion(publicacion, oferta, "VendedorDeclinaOferta", EMAIL);
                }
                break;
        }
        return publicacion;
    }
}
