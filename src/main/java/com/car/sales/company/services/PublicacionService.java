package com.car.sales.company.services;

import com.car.sales.company.exceptions.DatoInvalidoException;
import com.car.sales.company.models.Publicacion;
import com.car.sales.company.models.Usuario;
import com.car.sales.company.models.Vehiculo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.car.sales.company.helper.ValidacionHelper.*;

public class PublicacionService {

    List<Publicacion> vehiculosPublicados = new ArrayList<>();

    public Publicacion publicarVehiculo(Usuario vendedor, Vehiculo vehiculo) {
        Publicacion publicacion = new Publicacion();
        if (vendedor != null && vendedor.getTipoUsuario().equalsIgnoreCase("vendedor") && vehiculo != null) {
            validarVehiculo(vehiculo);
            publicacion.setVendedor(vendedor);
            publicacion.setVehiculo(vehiculo);
            publicacion.setFecha(LocalDate.now());
            publicacion.setEstaDisponibleEnLaWeb(true);
            vehiculosPublicados.add(publicacion);
            return publicacion;
        }
        throw new DatoInvalidoException("El usuario debe ser de tipo vendedor");
    }

    public int darDeBajaPublicaciones() {
        int publicacionesDeBaja = 0;
        for (Publicacion publicacion : vehiculosPublicados) {
            if (publicacion.getOfertasCompradores().size() < 1 && tieneMaximoDiasSinOfertas(publicacion.getFecha())) {
                publicacion.setEstaDisponibleEnLaWeb(false);
                publicacionesDeBaja++;
            }
        }
        return publicacionesDeBaja;
    }

    public Publicacion rePublicarVehiculo(Publicacion publicacion, Integer nuevoPrecioVehiculo) {
        if (nuevoPrecioVehiculo < validarEnteroPositivo(publicacion.getVehiculo().getPrecio())) {
            publicacion.getVehiculo().setPrecio(nuevoPrecioVehiculo.toString());
            publicacion.setEstaDisponibleEnLaWeb(true);
        } else {
            throw new DatoInvalidoException("el nuevo precio debe ser menor al precio actual");
        }
        return publicacion;
    }

}