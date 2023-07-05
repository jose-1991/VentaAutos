package com.car.sales.company.services;

import com.car.sales.company.dao.OfertaDAO;
import com.car.sales.company.dao.PublicacionDAO;
import com.car.sales.company.exceptions.DatoInvalidoException;
import com.car.sales.company.models.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.car.sales.company.helper.ValidacionHelper.validarPositivoDecimal;
import static com.car.sales.company.models.Accion.ACEPTAR_OFERTA;
import static com.car.sales.company.models.Accion.RETIRAR_OFERTA;
import static com.car.sales.company.models.NombreNotificacion.*;
import static com.car.sales.company.models.TipoUsuario.COMPRADOR;
import static com.car.sales.company.models.TipoUsuario.VENDEDOR;

public class VentaService {
    NotificacionService notificacionService;
    OfertaDAO ofertaDAO;
    PublicacionDAO publicacionDAO;

    public VentaService(NotificacionService notificacionService, OfertaDAO ofertaDAO, PublicacionDAO publicacionDAO) {
        this.notificacionService = notificacionService;
        this.ofertaDAO = ofertaDAO;
        this.publicacionDAO = publicacionDAO;
    }

    public Publicacion interactuar(Publicacion publicacion, Usuario usuario, Accion accion, double nuevoMonto) {
        switch (accion) {
            case OFERTAR:
                return interactuarConOfertar(publicacion.getId(), usuario, nuevoMonto);
            case CONTRA_OFERTAR:
                return interactuarConContraOferta(publicacion, usuario, nuevoMonto);
            case ACEPTAR_OFERTA:
                return interactuarConAceptar(publicacion, usuario);
            case RETIRAR_OFERTA:
                return interactuarConRetirar(publicacion, usuario);
        }
        return publicacion;
    }

    private Publicacion interactuarConOfertar(UUID publicacionId, Usuario usuario, double monto) {
        Publicacion publicacion = publicacionDAO.obtenerPublicacion(publicacionId);
        if (usuario.getTipoUsuario().equals(COMPRADOR)) {
            Oferta oferta = new Oferta(usuario, validarPositivoDecimal(monto), 0,
                    LocalDateTime.now());
            NombreNotificacion nombreNotificacion;
            if (publicacion.getOfertasCompradores().isEmpty()) {
                nombreNotificacion = COMPRADOR_PRIMERA_OFERTA;
            } else {
                nombreNotificacion = COMPRADOR_NUEVA_OFERTA;
            }
            publicacion.getOfertasCompradores().add(oferta);
            ofertaDAO.agregarOferta(oferta, publicacionId);
            notificacionService.enviarNotificacion(publicacion.getVendedor().getIdentificacion(), publicacion.getProducto(), monto,
                    0, nombreNotificacion);
        } else {
            throw new DatoInvalidoException("El usuario debe ser Comprador");
        }
        return publicacion;
    }

    private Publicacion interactuarConContraOferta(Publicacion publicacion, Usuario comprador, double nuevoMonto) {
        if (comprador.getTipoUsuario().equals(COMPRADOR)) {
            Oferta ofertaActual = encontrarOfertaUsuario(publicacion, comprador.getIdentificacion());
            if (ofertaActual.getMontoContraOferta() == 0) {
                ofertaActual.setMontoContraOferta(nuevoMonto);
                ofertaDAO.interaccionContraOferta(comprador.getIdentificacion(), publicacion.getId(), nuevoMonto);
                notificacionService.enviarNotificacion(comprador.getIdentificacion(), publicacion.getProducto(), ofertaActual.getMontoOferta(),
                        ofertaActual.getMontoContraOferta(), VENDEDOR_CONTRAOFERTA);
            }else {
                throw new DatoInvalidoException("El usuario ya realizo una contra oferta");
            }
        } else {
            throw new DatoInvalidoException("El usuario debe ser comprador");
        }
        return publicacion;
    }

    private Publicacion interactuarConAceptar(Publicacion publicacion, Usuario usuario) {
        Oferta mejorOferta;
        NombreNotificacion nombreNotificacion = null;
        if (usuario.getTipoUsuario().equals(VENDEDOR)) {
            mejorOferta = obtenerMayorOferta(publicacion.getOfertasCompradores());
            nombreNotificacion = VENDEDOR_ACEPTA_OFERTA;
            usuario = mejorOferta.getComprador();
        } else {
            mejorOferta = encontrarOfertaUsuario(publicacion, usuario.getIdentificacion());
            if (mejorOferta.getMontoContraOferta() > 0) {
                nombreNotificacion = COMPRADOR_ACEPTA_OFERTA;
                usuario = publicacion.getVendedor();
            }
        }
        notificacionService.enviarNotificacion(usuario.getIdentificacion(), publicacion.getProducto(),
                mejorOferta.getMontoOferta(), mejorOferta.getMontoContraOferta(), nombreNotificacion);
        notificarCompradoresVehiculoVendido(publicacion, mejorOferta);
        publicacion.setEstaDisponibleEnWeb(false);
        publicacionDAO.darDeBajaPublicaciones(Collections.singletonList(publicacion));
        ofertaDAO.actualizarOferta(publicacion.getId(), usuario.getIdentificacion(), ACEPTAR_OFERTA);

        return publicacion;
    }

    private Oferta encontrarOfertaUsuario(Publicacion publicacion, String usuarioId) {
        for (Oferta ofertaActual : publicacion.getOfertasCompradores()) {
            if (usuarioTieneOferta(usuarioId, ofertaActual)) {
                return ofertaActual;
            }
        }
        throw new DatoInvalidoException("Usuario no tiene oferta en esta Publicacion");
    }

    private Publicacion interactuarConRetirar(Publicacion publicacion, Usuario usuario) {
        if (usuario.getTipoUsuario().equals(COMPRADOR)) {
            Oferta ofertaActual = encontrarOfertaUsuario(publicacion, usuario.getIdentificacion());
            ofertaActual.setInactivo(true);
            ofertaDAO.actualizarOferta(publicacion.getId(), usuario.getIdentificacion(), RETIRAR_OFERTA);
            notificacionService.enviarNotificacion(publicacion.getVendedor().getIdentificacion(), publicacion.getProducto(),
                    ofertaActual.getMontoOferta(), ofertaActual.getMontoContraOferta(), COMPRADOR_RETIRA_OFERTA);

        } else {
            throw new DatoInvalidoException("El usuario debe ser de tipo comprador");
        }
        return publicacion;
    }

    private boolean usuarioTieneOferta(String usuarioId, Oferta oferta) {
        return usuarioId.equals(oferta.getComprador().getIdentificacion());
    }

    private void notificarCompradoresVehiculoVendido(Publicacion publicacion, Oferta mejorOferta) {
        for (Oferta ofertaActual : publicacion.getOfertasCompradores()) {
            if (!ofertaActual.getComprador().getIdentificacion().equals(mejorOferta.getComprador().getIdentificacion())) {
                ofertaActual.setInactivo(true);
                notificacionService.enviarNotificacion(ofertaActual.getComprador().getIdentificacion(), publicacion.getProducto(),
                        0, 0, VEHICULO_NO_DISPONIBLE);
            }
        }
    }

    private Oferta obtenerMayorOferta(List<Oferta> ofertasCompradores) {
        double montoMayor = 0;
        Oferta ofertaMontoMayor = null;
        if (ofertasCompradores.isEmpty()){
            throw new DatoInvalidoException("La lista de ofertas de la Publicacion esta vacia");
        }
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

