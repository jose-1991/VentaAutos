package com.car.sales.company.services;

import com.car.sales.company.dao.PublicacionDAO;
import com.car.sales.company.dao.UsuarioDAO;
import com.car.sales.company.exceptions.DatoInvalidoException;
import com.car.sales.company.models.Producto;
import com.car.sales.company.models.Publicacion;
import com.car.sales.company.models.Usuario;
import com.car.sales.company.models.Vehiculo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static com.car.sales.company.helper.ValidacionHelper.validarVehiculo;
import static com.car.sales.company.models.TipoUsuario.VENDEDOR;


public class PublicacionService {

    private NotificacionService notificacionService;
    private PublicacionDAO publicacionDAO;
    private UsuarioDAO usuarioDAO;

    public PublicacionService(NotificacionService notificacionService, PublicacionDAO publicacionDAO, UsuarioDAO usuarioDAO) {
        this.notificacionService = notificacionService;
        this.publicacionDAO = publicacionDAO;
        this.usuarioDAO = usuarioDAO;
    }

    // TODO: 30/5/2023 metodo para crear vehiculo random, o modificar metodo
    public Publicacion publicarProducto(Usuario vendedor, Producto producto, double precio) {
        Publicacion publicacion = new Publicacion();
        if (vendedor != null && vendedor.getTipoUsuario().equals(VENDEDOR) && producto != null) {
            if (producto instanceof Vehiculo) {
                Vehiculo vehiculo = (Vehiculo) producto;
                validarVehiculo(vehiculo);
            }
            publicacion.setProducto(producto);
            publicacion.setVendedor(vendedor);
            publicacion.setFecha(LocalDate.now());
            publicacion.setPrecio(precio);
            publicacion.setEstaDisponibleEnLaWeb(true);
            publicacion.setOfertasCompradores(new ArrayList<>());
            publicacionDAO.registrarPublicacionProducto(publicacion);
            List<Usuario> listaCompradores = usuarioDAO.obtenerCompradores();
            if (!listaCompradores.isEmpty()) {
//                notificacionService.notificarTodosLosCompradores(listaCompradores, producto, N_VEHICULO_VENTA);
            }
            return publicacion;
        }
        throw new DatoInvalidoException("El usuario debe ser de tipo vendedor");
    }

    public int darDeBajaPublicaciones() {
        List<Publicacion> listaPublicacionesDeBaja = publicacionDAO.obtenerPublicacionesParaDarDeBaja();
        // TODO: 30/5/2023 optimizar para que solo llame a la DB una sola vez
        for (Publicacion publicacion : listaPublicacionesDeBaja) {
//            notificacionService.enviarNotificacion(publicacion.getVendedor(), publicacion.getProducto(), 0, 0,
//                    V_EXPIRADO);
        }
        publicacionDAO.darDeBajaPublicaciones(listaPublicacionesDeBaja);
        return listaPublicacionesDeBaja.size();
    }

    public Publicacion rePublicarProducto(Publicacion publicacion, double nuevoPrecioProducto) {
        if (nuevoPrecioProducto < publicacion.getPrecio()) {
            publicacion.setPrecio(nuevoPrecioProducto);
            publicacion.setFecha(LocalDate.now());
            publicacion.setEstaDisponibleEnLaWeb(true);
            publicacionDAO.rePublicarProducto(publicacion.getId(), nuevoPrecioProducto);
//            notificacionService.notificarTodosLosCompradores(usuarioDAO.obtenerCompradores(), publicacion.getProducto(),
//                    N_VEHICULO_VENTA);
        } else {
            throw new DatoInvalidoException("el nuevo precio debe ser menor al precio actual");
        }
        return publicacion;
    }

    public Vehiculo obtenerVehiculoRandom() {
        Vehiculo vehiculo = new Vehiculo();
        List<String> listaMarcas = Arrays.asList("Toyota", "Nissan", "Mitsubishi", "Ford", "Hyundai", "Chevrolet",
                "Kia", "Mazda", "Suzuki", "BMW");
        List<String> listaModelos = Arrays.asList("Alto", "Scion", "Versa", "Focus", "Veloster", "Celica", "Montero",
                "Demio", "Baleno", "CHR");
        int indexMarca = (int) (Math.random() * listaMarcas.size());
        int indexModelo = (int) (Math.random() * listaModelos.size());
        int anioRandom = (int) ((Math.random() * 13) + 2010);
        vehiculo.setVin(generarRandomVin());
        vehiculo.setMarca(listaMarcas.get(indexMarca));
        vehiculo.setModelo(listaModelos.get(indexModelo));
        vehiculo.setAnio(anioRandom);
        return vehiculo;
    }

    public String generarRandomVin() {
        String characters = "0123456789ABCDEFGHJKLMNPRSTUVWXYZ";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(17);

        for (int i = 0; i < 17; i++) {
            int index = random.nextInt(characters.length());
            char randomChar = characters.charAt(index);
            sb.append(randomChar);
        }
        return sb.toString();
    }


}
