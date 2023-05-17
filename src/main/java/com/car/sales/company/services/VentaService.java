package com.car.sales.company.services;

import com.car.sales.company.exceptions.DatoInvalidoException;
import com.car.sales.company.exceptions.UsuarioNoEncontradoException;
import com.car.sales.company.models.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static com.car.sales.company.helper.ValidacionHelper.validarPositivoDecimal;
import static com.car.sales.company.models.NombreNotificacion.*;
import static com.car.sales.company.models.TipoUsuario.COMPRADOR;
import static com.car.sales.company.models.TipoUsuario.VENDEDOR;

public class VentaService {
    NotificacionService notificacionService = new NotificacionService();
    private static double montoNotificacion;

    public Oferta realizarPrimeraOferta(Publicacion publicacion, Usuario usuario, double montoOferta) {
        Oferta oferta = new Oferta(validarPositivoDecimal(montoOferta), 0, usuario, LocalDateTime.now());
        publicacion.setOfertasCompradores(Collections.singletonList(oferta));
        notificacionService.enviarNotificacion(publicacion.getVendedor(), publicacion.getVehiculo(), montoOferta, 0,
                COMPRADOR_PRIMERA_OFERTA);
        return oferta;
    }

    public Publicacion interactuar(Publicacion publicacion, Usuario usuario, Accion accion, double nuevoMonto) {
        switch (accion) {
            case CONTRA_OFERTAR:
                return interactuarConContraOferta(publicacion, usuario, nuevoMonto);
            case ACEPTAR:
                return interactuarConAceptar(publicacion, usuario);
            case RETIRAR:
                return interactuarConRetirar(publicacion, usuario);
        }
        return publicacion;
    }

    private Publicacion interactuarConRetirar(Publicacion publicacion, Usuario usuario) {
        if (usuario.getTipoUsuario().equals(COMPRADOR)) {
            for (Oferta ofertaActual : publicacion.getOfertasCompradores()) {
                if (ofertaActual.getComprador().getIdentificacion().equals(usuario.getIdentificacion())) {
                    ofertaActual.setInactivo(true);
                    notificacionService.enviarNotificacion(publicacion.getVendedor(), publicacion.getVehiculo(),
                            ofertaActual.getMontoOferta(), ofertaActual.getMontoContraOferta(), COMPRADOR_RETIRA_OFERTA);
                }
            }
        } else {
            throw new DatoInvalidoException("El usuario debe ser de tipo comprador");
        }
        return publicacion;
    }

    private Publicacion interactuarConAceptar(Publicacion publicacion, Usuario usuario) {
        Oferta mejorOferta = null;
        NombreNotificacion nombreNotificacion = null;
        if (usuario.getTipoUsuario().equals(VENDEDOR)) {
            mejorOferta = obtenerMayorOferta(publicacion.getOfertasCompradores());
            nombreNotificacion = VENDEDOR_ACEPTA_OFERTA;
            usuario = mejorOferta.getComprador();
        } else {
            for (Oferta ofertaActual : publicacion.getOfertasCompradores()) {
                if (ofertaActual.getComprador().getIdentificacion().equals(usuario.getIdentificacion()) &&
                        ofertaActual.getMontoContraOferta() > 0) {
                    mejorOferta = ofertaActual;
                    nombreNotificacion = COMPRADOR_ACEPTA_OFERTA;
                    usuario = publicacion.getVendedor();
                }
            }
        }
        if (mejorOferta != null) {
            notificacionService.enviarNotificacion(usuario, publicacion.getVehiculo(),
                    mejorOferta.getMontoOferta(), mejorOferta.getMontoContraOferta(), nombreNotificacion);
            notificarCompradoresVehiculoVendido(publicacion, mejorOferta);
            publicacion.setEstaDisponibleEnLaWeb(false);
        }
        return publicacion;

    }

    private Publicacion interactuarConContraOferta(Publicacion publicacion, Usuario usuario, double nuevoMonto) {
        if (usuario.getTipoUsuario().equals(VENDEDOR)) {
            Oferta mejorOferta = obtenerMayorOferta(publicacion.getOfertasCompradores());
            for (Oferta oferta : publicacion.getOfertasCompradores()) {
                if (oferta.getComprador().getIdentificacion().equals(mejorOferta.getComprador().getIdentificacion())) {
                    oferta.setMontoContraOferta(nuevoMonto);
                    notificacionService.enviarNotificacion(mejorOferta.getComprador(), publicacion.getVehiculo(), oferta.getMontoOferta(),
                            oferta.getMontoContraOferta(), VENDEDOR_CONTRAOFERTA);
                }
            }
        } else {
            throw new UsuarioNoEncontradoException("Solo el vendedor puede realizar una contra oferta");
        }

        return publicacion;
    }
//     TODO: 11/5/2023 como saber cuando se llama a notificacionService

//    devolver publicacion (metodo original)
//    arreglar test ventaService
//    optimizar metodo interactuar

    public Publicacion interactuar1(Publicacion publicacion, Usuario usuario, Accion accion, double nuevoMonto) {
        NombreNotificacion nombreNotificacion = null;
        Oferta mejorOferta = null;
        if (usuario.getTipoUsuario().equals(VENDEDOR)) {
            switch (accion) {
                case CONTRA_OFERTAR:
                    mejorOferta = obtenerMayorOferta(publicacion.getOfertasCompradores());
                    for (Oferta oferta : publicacion.getOfertasCompradores()) {
                        if (oferta.getComprador().getIdentificacion().equals(mejorOferta.getComprador().getIdentificacion())) {
                            oferta.setMontoContraOferta(nuevoMonto);
                        }
                    }
                    nombreNotificacion = VENDEDOR_CONTRAOFERTA;
                    break;
                case ACEPTAR:
                    mejorOferta = obtenerMayorOferta(publicacion.getOfertasCompradores());
                    nombreNotificacion = VENDEDOR_ACEPTA_OFERTA;
                    notificarCompradoresVehiculoVendido(publicacion, mejorOferta);
                    publicacion.setEstaDisponibleEnLaWeb(false);
                    break;
                case RETIRAR:
                    throw new DatoInvalidoException("El usuario debe ser de tipo comprador");
            }
            if (mejorOferta != null) {
                notificacionService.enviarNotificacion(mejorOferta.getComprador(), publicacion.getVehiculo(), mejorOferta.getMontoOferta(),
                        mejorOferta.getMontoContraOferta(), nombreNotificacion);
            }
        }

        if (usuario.getTipoUsuario().equals(COMPRADOR)) {
            switch (accion) {
                case CONTRA_OFERTAR:
                    throw new DatoInvalidoException("Solo el vendedor puede realizar una contra oferta");
                case ACEPTAR:
                    for (Oferta oferta : publicacion.getOfertasCompradores()) {
                        if (oferta.getComprador().getIdentificacion().equals(usuario.getIdentificacion()) &&
                                oferta.getMontoContraOferta() > 0) {
                            mejorOferta = oferta;
                            notificarCompradoresVehiculoVendido(publicacion, oferta);
                            publicacion.setEstaDisponibleEnLaWeb(false);
                        }
                    }
                    nombreNotificacion = COMPRADOR_ACEPTA_OFERTA;
                    break;
                case RETIRAR:
                    for (Oferta ofertaActual : publicacion.getOfertasCompradores()) {
                        if (ofertaActual.getComprador().getIdentificacion().equals(usuario.getIdentificacion())) {
                            mejorOferta = ofertaActual;
                            ofertaActual.setInactivo(true);
                        }
                    }
                    nombreNotificacion = COMPRADOR_RETIRA_OFERTA;
                    break;

            }
            if (mejorOferta != null) {
                notificacionService.enviarNotificacion(publicacion.getVendedor(), publicacion.getVehiculo(), mejorOferta.getMontoOferta(),
                        mejorOferta.getMontoContraOferta(), nombreNotificacion);
            }

        }
        return publicacion;
    }

    private void notificarCompradoresVehiculoVendido(Publicacion publicacion, Oferta oferta) {
        for (Oferta ofertaActual : publicacion.getOfertasCompradores()) {
            if (!ofertaActual.getComprador().getIdentificacion().equals(oferta.getComprador().getIdentificacion())) {
                ofertaActual.setInactivo(true);
                notificacionService.enviarNotificacion(ofertaActual.getComprador(), publicacion.getVehiculo(),
                        0, 0, VEHICULO_NO_DISPONIBLE);
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

