package com.car.sales.company.services;

import com.car.sales.company.exceptions.DatoInvalidoException;
import com.car.sales.company.exceptions.UsuarioNoEncontradoException;
import com.car.sales.company.models.Accion;
import com.car.sales.company.models.Oferta;
import com.car.sales.company.models.Publicacion;
import com.car.sales.company.models.Usuario;

import static com.car.sales.company.helper.ValidacionHelper.validarEnteroPositivo;

public class VentaService {

    public Oferta realizarPrimeraOferta(Publicacion publicacion, Usuario comprador, String montoOferta) {
        Oferta oferta = new Oferta(validarEnteroPositivo(montoOferta).toString(), comprador);
        publicacion.getOfertasCompradores().add(oferta);
        return oferta;
    }

    public Publicacion interactuar(Publicacion publicacion, Oferta oferta, String tipoUsuario, Accion accion, String nuevoMonto) {

        switch (accion) {
            case CONTRA_OFERTAR:
                if (tipoUsuario.equalsIgnoreCase("vendedor")) {
                    oferta.setMonto(validarEnteroPositivo(nuevoMonto).toString());

                } else {
                    throw new UsuarioNoEncontradoException("Solo el vendedor puede realizar una contra oferta");
                }
                break;
            case ACEPTAR:
                publicacion.getOfertasCompradores().removeIf(o -> !o.equals(oferta));
                publicacion.setEstaDisponibleEnLaWeb(false);
                break;
            case RETIRAR:
                if (publicacion.getOfertasCompradores().contains(oferta) && tipoUsuario.equalsIgnoreCase("comprador")) {
                    publicacion.getOfertasCompradores().removeIf(o -> o.equals(oferta));
                } else {
                    throw new DatoInvalidoException(" - la oferta ingresada no existe \n - El usuario debe ser de tipo comprador");
                }
                break;
            case RECHAZAR:
                publicacion.getOfertasCompradores().removeIf(o -> o.equals(oferta));
                break;
        }
        return publicacion;
    }
}
