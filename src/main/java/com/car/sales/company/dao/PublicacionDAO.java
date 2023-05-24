package com.car.sales.company.dao;

import com.car.sales.company.models.Producto;
import com.car.sales.company.models.Publicacion;
import com.car.sales.company.models.Vehiculo;

import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.car.sales.company.helper.ValidacionHelper.MAX_DIAS_SIN_OFERTA;

public class PublicacionDAO {
    String query;

    private Connection obtenerConexion() throws SQLException {
        return ConexionDB.obtenerInstancia();
    }

    public void registrarPublicacion(Publicacion publicacion) {
        Vehiculo vehiculo = (Vehiculo) publicacion.getProducto();
        query = "INSERT INTO comercio.producto VALUES(?,?,?,?,?)";
        try {
            PreparedStatement statement = obtenerConexion().prepareStatement(query);
            statement.setString(1, vehiculo.getVin());
            statement.setString(2, vehiculo.getStockNumber().toString());
            statement.setString(3, vehiculo.getMarca());
            statement.setString(4, vehiculo.getModelo());
            statement.setInt(5, vehiculo.getAnio());
            statement.executeUpdate();

            query = "INSERT INTO comercio.publicacion(id, usuario_id, producto_id, fecha, precio," +
                    "esta_disponible_web) VALUES(?,?,?,?,?,?)";

            statement = obtenerConexion().prepareStatement(query);
            statement.setString(1, String.valueOf(UUID.randomUUID()));
            statement.setString(2, publicacion.getVendedor().getIdentificacion());
            statement.setString(3, vehiculo.getVin());
            statement.setDate(4, Date.valueOf(LocalDate.now()));
            statement.setDouble(5, publicacion.getPrecio());
            statement.setBoolean(6, publicacion.isEstaDisponibleEnLaWeb());
            statement.executeUpdate();

            statement.close();
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

    public List<Publicacion> obtenerPublicacionesDeBaja() {
        List<Publicacion> publicacionesDeBaja = new ArrayList<>();
        query = "SELECT * FROM comercio.publicacion INNER JOIN ";
        try {
            Statement statement = obtenerConexion().createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                Publicacion publicacion = new Publicacion();
                publicacion.setId(UUID.fromString(resultSet.getString("id")));
                publicacion.setFecha(resultSet.getDate("fecha").toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                publicacion.setPrecio(resultSet.getDouble("precio"));
                String usuarioId = resultSet.getString("usuario_id");
                String productoId = resultSet.getString("producto_id");

                boolean tieneOferta;
                query = "SELECT * FROM comercio.oferta WHERE publicacion_id = " + publicacion.getId();
                ResultSet resultSetOferta = statement.executeQuery(query);
                tieneOferta = resultSetOferta.next();

                if (!tieneOferta && tieneMaximoDiasSinOfertas(publicacion.getFecha())) {

                }


            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return publicacionesDeBaja;
    }

    private boolean tieneMaximoDiasSinOfertas(LocalDate fechaPublicacion) {
        return ChronoUnit.DAYS.between(fechaPublicacion, LocalDate.now()) >= MAX_DIAS_SIN_OFERTA;
    }
}
