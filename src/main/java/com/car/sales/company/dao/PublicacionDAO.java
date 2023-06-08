package com.car.sales.company.dao;

import com.car.sales.company.models.Publicacion;
import com.car.sales.company.models.Usuario;
import com.car.sales.company.models.Vehiculo;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.car.sales.company.dao.UsuarioDAO.convertirUsuario;

public class PublicacionDAO {
    String UPDATE_PUBLICACION = "UPDATE comercio.publicacion SET";
    String query;

    private Connection obtenerConexion() throws SQLException {
        return ConexionDB.obtenerInstancia();
    }

    public void registrarPublicacionProducto(Publicacion publicacion) {
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

            query = "INSERT INTO comercio.publicacion VALUES(?,?,?,?,?,?)";

            statement = obtenerConexion().prepareStatement(query);
            statement.setString(1, String.valueOf(UUID.randomUUID()));
            statement.setString(2, publicacion.getVendedor().getIdentificacion());
            statement.setString(3, vehiculo.getVin());
            statement.setDate(4, Date.valueOf(publicacion.getFecha()));
            statement.setDouble(5, publicacion.getPrecio());
            statement.setBoolean(6, publicacion.isEstaDisponibleEnLaWeb());
            statement.executeUpdate();
            statement.close();

        } catch (SQLException exception) {
            System.out.println("Error al registrar publicacion");
            exception.printStackTrace();
        }
    }

    public void rePublicarProducto(UUID id, double precio) {
        query = UPDATE_PUBLICACION +" esta_disponible_web = ?, precio = ?,fecha = ? WHERE id = '" + id + "'";

        try (PreparedStatement statement = obtenerConexion().prepareStatement(query)) {
            statement.setBoolean(1, true);
            statement.setDouble(2, precio);
            statement.setDate(3, Date.valueOf(LocalDate.now()));
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public List<Publicacion> obtenerPublicacionesParaDarDeBaja() {
        List<Publicacion> publicacionesDeBaja = new ArrayList<>();
        query = "SELECT * FROM comercio.publicacion AS p INNER JOIN comercio.usuario AS u ON p.usuario_id = u" +
                ".identificacion INNER JOIN " +
                "comercio.producto ON p.producto_id = comerico.producto.vin WHERE id NOT IN (SELECT DISTINCT " +
                "publicacion_id " +
                "FROM comercio.oferta) AND" +
                "fecha  NOT BETWEEN DATE_SUB(curdate(), INTERVAL 5 DAY) AND curdate()";
        try (Statement statement = obtenerConexion().createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                Publicacion publicacion = convertirPublicacion(resultSet);
                Usuario vendedor = convertirUsuario(resultSet);
                Vehiculo vehiculo = convertirVehiculo(resultSet);
                publicacion.setVendedor(vendedor);
                publicacion.setProducto(vehiculo);
                publicacionesDeBaja.add(publicacion);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return publicacionesDeBaja;
    }

    public void darDeBajaPublicaciones(List<Publicacion> listaPublicaciones) {
        query = UPDATE_PUBLICACION + " esta_disponible_web = ? WHERE id = ?";

        try {
            PreparedStatement statement = obtenerConexion().prepareStatement(query);
            obtenerConexion().setAutoCommit(false);

            for (Publicacion publicacion : listaPublicaciones) {
                statement.setBoolean(1, false);
                statement.setString(2, publicacion.getId().toString());
                statement.addBatch();
            }
            statement.executeBatch();
            obtenerConexion().commit();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Publicacion convertirPublicacion(ResultSet resultSet) throws SQLException {
        Publicacion publicacion = new Publicacion();
        publicacion.setId(UUID.fromString(resultSet.getString("id")));
        publicacion.setFecha((resultSet.getDate("fecha")).toLocalDate());
        publicacion.setPrecio(resultSet.getDouble("precio"));
        publicacion.setEstaDisponibleEnLaWeb(resultSet.getBoolean("esta_disponible_web"));
        return publicacion;
    }

    private Vehiculo convertirVehiculo(ResultSet resultSet) throws SQLException {
        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setVin(resultSet.getString("vin"));
        vehiculo.setStockNumber(UUID.fromString(resultSet.getString("stock_number")));
        vehiculo.setMarca(resultSet.getString("marca"));
        vehiculo.setModelo(resultSet.getString("modelo"));
        vehiculo.setAnio(resultSet.getInt("anio"));
        return vehiculo;
    }
}
