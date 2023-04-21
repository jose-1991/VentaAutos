package com.car.sales.company.services;

import com.car.sales.company.models.Publicacion;
import com.car.sales.company.exceptions.DatoInvalidoException;
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
            vehiculosPublicados.add(publicacion);
            return publicacion;
        }
        throw new DatoInvalidoException("El usuario debe ser de tipo vendedor");
    }

    public List<Publicacion> darDeBajaPublicaciones() {
        vehiculosPublicados.removeIf(p -> p.getOfertasCompradores().size() < 1 &&
                tieneMaximoDiasSinOfertas(p.getFecha()));
        return vehiculosPublicados;
    }

    public Publicacion rePublicarVehiculo(Usuario vendedor, Vehiculo vehiculo, Integer nuevoPrecioVehiculo) {
        if (nuevoPrecioVehiculo < validarEnteroPositivo(vehiculo.getPrecio())) {
            vehiculo.setPrecio(nuevoPrecioVehiculo.toString());
        } else {
            throw new DatoInvalidoException("el nuevo precio debe ser menor al precio actual");
        }
        return publicarVehiculo(vendedor, vehiculo);
    }

}
