package com.car.sales.company.dao;

import com.car.sales.company.models.Producto;
import com.car.sales.company.models.Publicacion;
import com.car.sales.company.models.Vehiculo;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PublicacionDAO {
    String query;

    private Connection obtenerConexion() throws SQLException {
        return ConexionDB.obtenerInstancia();
    }

    public void registrarPublicacion(Publicacion publicacion) {
        query = "INSERT INTO comercio.publicacion(publicacion_ID, usuario_ID, producto_ID, fecha, oferta_ID," +
                "esta_disponible_web) VALUES(?,?,?,?,?,?)";

        try (PreparedStatement statement = obtenerConexion().prepareStatement(query)) {
            statement.setString(1, UUID.randomUUID().toString());
            statement.setString(2, publicacion.getVendedor().getIdentificacion());
            statement.setString(3, (obtenerIdProducto(publicacion.getProducto())));
            statement.setDate(4, Date.valueOf(LocalDate.now()));
            statement.setString(5, null);
            statement.setBoolean(6, publicacion.isEstaDisponibleEnLaWeb());
            statement.executeUpdate();

            System.out.println("Publicacion registrada con exito!");
        } catch (SQLException exception) {
            System.out.println("Error al registrar publicacion");
            exception.printStackTrace();
        }
    }

    private String obtenerIdProducto(Producto producto) {
        return ((Vehiculo) producto).getVin();
    }

    public void actualizarEstadoPublicacionEnWeb(UUID id, boolean activo) {
        query = "UPDATE comercio.publicacion SET esta_disponible_web = ? WHERE publicacion_ID = " + id;

        try (PreparedStatement statement = obtenerConexion().prepareStatement(query)) {
            statement.setBoolean(1, activo);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

//    public List<Publicacion> obtenerPublicacionDeBaja() {
//        List<Publicacion> publicacionesDeBaja = new ArrayList<>();
//        query = "SELECT * FROM comercio.publicacion";
//        try (Statement statement = obtenerConexion().createStatement();
//                ResultSet resultSet = statement.executeQuery(query)) {
//            Publicacion publicacion = new Publicacion();
//            publicacion.setId();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//    }
}
