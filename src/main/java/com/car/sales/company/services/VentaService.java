package com.car.sales.company.services;

import com.car.sales.company.exceptions.DatoInvalidoException;
import com.car.sales.company.exceptions.UsuarioNoEncontradoException;
import com.car.sales.company.models.*;

import java.time.LocalDateTime;
import java.util.Collections;

import static com.car.sales.company.helper.ValidacionHelper.validarPositivoDecimal;
import static com.car.sales.company.models.NombreNotificacion.*;
import static com.car.sales.company.models.TipoUsuario.COMPRADOR;
import static com.car.sales.company.models.TipoUsuario.VENDEDOR;

public class VentaService {
    NotificacionService notificacionService = new NotificacionService();

    public Oferta realizarPrimeraOferta(Publicacion publicacion, Usuario usuario, double montoOferta) {
        Oferta oferta = new Oferta(validarPositivoDecimal(montoOferta), 0, usuario, LocalDateTime.now());
        publicacion.setOfertasCompradores(Collections.singletonList(oferta));
        notificacionService.enviarNotificacion(publicacion.getVendedor(), publicacion.getVehiculo(), montoOferta,
                COMPRADOR_PRIMERA_OFERTA);
        return oferta;
    }

    public Publicacion interactuar(Publicacion publicacion, Oferta oferta, TipoUsuario tipoUsuario, Accion accion, double nuevoMonto) {
        switch (accion) {
            case CONTRA_OFERTAR:
                if (tipoUsuario.equals(VENDEDOR)) {
                    oferta.setMontoContraOferta(nuevoMonto);
                    notificacionService.enviarNotificacion(oferta.getComprador(), publicacion.getVehiculo(), nuevoMonto,
                            VENDEDOR_CONTRAOFERTA);

                } else {
                    throw new UsuarioNoEncontradoException("Solo el vendedor puede realizar una contra oferta");
                }
                break;
            case ACEPTAR:
                Oferta mayorOferta = new Oferta(0, 0, null, LocalDateTime.now());
                for (Oferta ofertaActual : publicacion.getOfertasCompradores()) {
                    if (ofertaActual.getMontoOferta() > mayorOferta.getMontoOferta()) {
                        mayorOferta = ofertaActual;
                    } else if (ofertaActual.getMontoOferta() == mayorOferta.getMontoOferta() &&
                            ofertaActual.getFechaOferta().isBefore(mayorOferta.getFechaOferta())) {
                        mayorOferta = ofertaActual;
                    }
                }
                for (Oferta ofertaActual : publicacion.getOfertasCompradores()) {
                    if (!ofertaActual.equals(mayorOferta)) {
                        ofertaActual.setInactivo(true);
                        notificacionService.enviarNotificacion(ofertaActual.getComprador(), publicacion.getVehiculo(),
                                0, VEHICULO_EXPIRADO);
                    }
                }
                publicacion.setEstaDisponibleEnLaWeb(false);
                if (tipoUsuario.equals(COMPRADOR)) {
                    notificacionService.enviarNotificacion(publicacion.getVendedor(), publicacion.getVehiculo(),
                            mayorOferta.getMontoOferta(), COMPRADOR_ACEPTA_OFERTA);
                } else {
                    notificacionService.enviarNotificacion(oferta.getComprador(), publicacion.getVehiculo(),
                            mayorOferta.getMontoOferta(), VENDEDOR_ACEPTA_OFERTA);
                }
                break;
            case RETIRAR:
                if (publicacion.getOfertasCompradores().contains(oferta) && tipoUsuario.equals(COMPRADOR)) {
                    for (Oferta ofertaActual : publicacion.getOfertasCompradores()) {
                        if (ofertaActual.equals(oferta)) {
                            ofertaActual.setInactivo(true);
                        }
                    }
                    notificacionService.enviarNotificacion(publicacion.getVendedor(), publicacion.getVehiculo(),
                            oferta.getMontoOferta(), COMPRADOR_RETIRA_OFERTA);
                } else {
                    throw new DatoInvalidoException(" - la oferta ingresada no existe \n - El usuario debe ser de tipo comprador");
                }
                break;
            case RECHAZAR:
                if (tipoUsuario.equals(VENDEDOR)) {
                    for (Oferta ofertaActual : publicacion.getOfertasCompradores()) {
                        if (ofertaActual.equals(oferta)) {
                            ofertaActual.setInactivo(true);
                        }
                    }
                    notificacionService.enviarNotificacion(oferta.getComprador(), publicacion.getVehiculo(),
                            oferta.getMontoOferta(), VENDEDOR_DECLINA_OFERTA);
                }
                break;
        }
        return publicacion;
    }
}
