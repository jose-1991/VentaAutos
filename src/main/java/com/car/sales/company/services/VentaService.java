package com.car.sales.company.services;

import com.car.sales.company.exceptions.DatoInvalidoException;
import com.car.sales.company.exceptions.UsuarioNoEncontradoException;
import com.car.sales.company.models.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static com.car.sales.company.helper.ValidacionHelper.validarPositivoDecimal;
import static com.car.sales.company.models.Accion.*;
import static com.car.sales.company.models.NombreNotificacion.*;
import static com.car.sales.company.models.TipoUsuario.COMPRADOR;
import static com.car.sales.company.models.TipoUsuario.VENDEDOR;

public class VentaService {
    NotificacionService notificacionService = new NotificacionService();
    private static double montoNotificacion;

    public Oferta realizarPrimeraOferta(Publicacion publicacion, Usuario usuario, double montoOferta) {
        Oferta oferta = new Oferta(validarPositivoDecimal(montoOferta), 0, usuario, LocalDateTime.now());
        publicacion.setOfertasCompradores(Collections.singletonList(oferta));
        notificacionService.enviarNotificacion(publicacion.getVendedor(), publicacion.getVehiculo(), montoOferta,
                COMPRADOR_PRIMERA_OFERTA);
        return oferta;
    }

    public Publicacion interactuar2(Publicacion publicacion, Oferta oferta, TipoUsuario tipoUsuario, Accion accion, double nuevoMonto) {
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
                Oferta mejorOferta;
                if (tipoUsuario.equals(VENDEDOR)) {
                    mejorOferta = obtenerMayorOferta(publicacion.getOfertasCompradores());
                    notificacionService.enviarNotificacion(mejorOferta.getComprador(), publicacion.getVehiculo(),
                            mejorOferta.getMontoOferta(), VENDEDOR_ACEPTA_OFERTA);

                } else if (tipoUsuario.equals(COMPRADOR) && oferta.getMontoContraOferta() > 0) {
                    mejorOferta = oferta;
                    notificacionService.enviarNotificacion(publicacion.getVendedor(), publicacion.getVehiculo(),
                            mejorOferta.getMontoContraOferta(), COMPRADOR_ACEPTA_OFERTA);

                } else {
                    throw new UsuarioNoEncontradoException("No es posible Aceptar la oferta");
                }
                for (Oferta ofertaActual : publicacion.getOfertasCompradores()) {
                    if (!ofertaActual.getComprador().getIdentificacion().equals(mejorOferta.getComprador().getIdentificacion())) {
                        ofertaActual.setInactivo(true);
                        notificacionService.enviarNotificacion(ofertaActual.getComprador(), publicacion.getVehiculo(),
                                0, VEHICULO_NO_DISPONIBLE);
                    }
                }
                publicacion.setEstaDisponibleEnLaWeb(false);
            case RETIRAR:
                if (tipoUsuario.equals(COMPRADOR)) {
                    for (Oferta ofertaActual : publicacion.getOfertasCompradores()) {
                        if (ofertaActual.getComprador().getIdentificacion().equals(oferta.getComprador().getIdentificacion())) {
                            ofertaActual.setInactivo(true);
                        }
                    }
                    notificacionService.enviarNotificacion(publicacion.getVendedor(), publicacion.getVehiculo(),
                            oferta.getMontoOferta(), COMPRADOR_RETIRA_OFERTA);
                } else {
                    throw new DatoInvalidoException("El usuario debe ser de tipo comprador");
                }
                break;
        }
        return publicacion;
    }
//     TODO: 11/5/2023 como saber cuando se llama a notificacionService

//    devolver publicacion (metodo original)
//    arreglar test ventaService
//    optimizar metodo interactuar

    public Oferta interactuar1(Publicacion publicacion, Oferta oferta, TipoUsuario tipoUsuario, Accion accion, double nuevoMonto) {
        NombreNotificacion nombreNotificacion = null;
        switch (tipoUsuario) {
            case VENDEDOR:
                if (accion.equals(CONTRA_OFERTAR)) {
                    oferta.setMontoContraOferta(nuevoMonto);
                    nombreNotificacion = VENDEDOR_CONTRAOFERTA;
                }
                if (accion.equals(Accion.ACEPTAR)) {
                    Oferta mejorOferta = obtenerMayorOferta(publicacion.getOfertasCompradores());
                    nuevoMonto = mejorOferta.getMontoOferta();
                    nombreNotificacion = VENDEDOR_ACEPTA_OFERTA;
                    notificarCompradoresVehiculoVendido(publicacion, mejorOferta);
                    publicacion.setEstaDisponibleEnLaWeb(false);
                    oferta = mejorOferta;
                }
                if (accion.equals(Accion.RETIRAR)) {
                    throw new DatoInvalidoException("El usuario debe ser de tipo comprador");
                }
                notificacionService.enviarNotificacion(oferta.getComprador(), publicacion.getVehiculo(), nuevoMonto,
                        nombreNotificacion);
                break;
            case COMPRADOR:

                if (accion.equals(CONTRA_OFERTAR)) {
                    throw new UsuarioNoEncontradoException("Solo el vendedor puede realizar una contra oferta");
                }
                if (accion.equals(Accion.RETIRAR)) {
                    for (Oferta ofertaActual : publicacion.getOfertasCompradores()) {
                        if (ofertaActual.getComprador().getIdentificacion().equals(oferta.getComprador().getIdentificacion())) {
                            ofertaActual.setInactivo(true);
                        }
                    }
                    nombreNotificacion = COMPRADOR_RETIRA_OFERTA;
                    nuevoMonto = oferta.getMontoOferta();
                }
                if (accion.equals(Accion.ACEPTAR)) {
                    if (oferta.getMontoContraOferta() > 0) {
                        publicacion.setEstaDisponibleEnLaWeb(false);
                        nombreNotificacion = COMPRADOR_ACEPTA_OFERTA;
                        nuevoMonto = oferta.getMontoContraOferta();
                        notificarCompradoresVehiculoVendido(publicacion, oferta);
                    } else {
                        throw new DatoInvalidoException("No existe una contraoferta");
                    }
                }
                notificacionService.enviarNotificacion(publicacion.getVendedor(), publicacion.getVehiculo(), nuevoMonto,
                        nombreNotificacion);
                break;
        }
//        notificacionService.enviarNotificacion()
        return oferta;
    }

    public Publicacion interactuar(Publicacion publicacion, Oferta oferta, TipoUsuario tipoUsuario, Accion accion, double nuevoMonto) {
        NombreNotificacion nombreNotificacion;
        switch (tipoUsuario) {
            case VENDEDOR:
                nombreNotificacion = interactuarVendedor(publicacion, oferta, accion, nuevoMonto);
                notificacionService.enviarNotificacion(oferta.getComprador(), publicacion.getVehiculo(), montoNotificacion,
                        nombreNotificacion);
                break;
            case COMPRADOR:
                nombreNotificacion = interactuarComprador(publicacion, oferta, accion);
                notificacionService.enviarNotificacion(publicacion.getVendedor(), publicacion.getVehiculo(), montoNotificacion,
                        nombreNotificacion);
                break;
        }
        return publicacion;
    }

    private NombreNotificacion interactuarComprador(Publicacion publicacion, Oferta oferta, Accion accion) {
        if (accion.equals(CONTRA_OFERTAR)) {
            throw new DatoInvalidoException("Solo el vendedor puede realizar una contra oferta");
        }
        if (accion.equals(RETIRAR)) {
            for (Oferta ofertaActual : publicacion.getOfertasCompradores()) {
                if (ofertaActual.getComprador().getIdentificacion().equals(oferta.getComprador().getIdentificacion())) {
                    ofertaActual.setInactivo(true);
                }
            }
            montoNotificacion = oferta.getMontoOferta();
            return COMPRADOR_RETIRA_OFERTA;
        }
        if (accion.equals(ACEPTAR)) {
            if (oferta.getMontoContraOferta() > 0) {
                publicacion.setEstaDisponibleEnLaWeb(false);
                montoNotificacion = oferta.getMontoContraOferta();
                notificarCompradoresVehiculoVendido(publicacion, oferta);
                return COMPRADOR_ACEPTA_OFERTA;
            } else {
                throw new DatoInvalidoException("No existe una contraoferta");
            }
        }
        return null;
    }

    private NombreNotificacion interactuarVendedor(Publicacion publicacion, Oferta oferta, Accion accion, double nuevoMonto) {

        if (accion.equals(CONTRA_OFERTAR)) {
            oferta.setMontoContraOferta(nuevoMonto);
            montoNotificacion = oferta.getMontoContraOferta();
            return VENDEDOR_CONTRAOFERTA;
        }
        if (accion.equals(ACEPTAR)) {
            Oferta mejorOferta = obtenerMayorOferta(publicacion.getOfertasCompradores());
            montoNotificacion = mejorOferta.getMontoOferta();
            notificarCompradoresVehiculoVendido(publicacion, mejorOferta);
            publicacion.setEstaDisponibleEnLaWeb(false);
            return VENDEDOR_ACEPTA_OFERTA;
        }
        if (accion.equals(Accion.RETIRAR)) {
            throw new DatoInvalidoException("El usuario debe ser de tipo comprador");
        }
        return null;
    }

    private void notificarCompradoresVehiculoVendido(Publicacion publicacion, Oferta oferta) {
        for (Oferta ofertaActual : publicacion.getOfertasCompradores()) {
            if (!ofertaActual.getComprador().getIdentificacion().equals(oferta.getComprador().getIdentificacion())) {
                ofertaActual.setInactivo(true);
                notificacionService.enviarNotificacion(ofertaActual.getComprador(), publicacion.getVehiculo(),
                        0, VEHICULO_NO_DISPONIBLE);
            }
        }
    }

    private Oferta obtenerMayorOferta(List<Oferta> ofertasCompradores) {
        double montoMayor = 0;
        Oferta ofertaMontoMayor = null;
        for (Oferta ofertaActual : ofertasCompradores) {
            if (ofertaActual.getMontoOferta() > montoMayor) {
                montoMayor = ofertaActual.getMontoOferta();
                ofertaMontoMayor = ofertaActual;
            }
            if (ofertaActual.getMontoOferta() == montoMayor && ofertaMontoMayor != null &&
                    ofertaActual.getFechaOferta().isBefore(ofertaMontoMayor.getFechaOferta())) {
                ofertaMontoMayor = ofertaActual;
            }
        }
        return ofertaMontoMayor;
    }
}

