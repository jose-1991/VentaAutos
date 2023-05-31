package com.car.sales.company.dao;

import com.car.sales.company.models.Publicacion;
import com.car.sales.company.models.Usuario;
import com.car.sales.company.models.Vehiculo;

import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.car.sales.company.dao.UsuarioDAO.obtenerUsuario;
import static com.car.sales.company.helper.ValidacionHelper.MAX_DIAS_SIN_OFERTA;

public class PublicacionDAO {
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

            query = "INSERT INTO comercio.publicacion(id, usuario_id, producto_id, fecha, precio," +
                    "esta_disponible_web) VALUES(?,?,?,?,?,?)";

            statement = obtenerConexion().prepareStatement(query);
            statement.setString(1, String.valueOf(UUID.randomUUID()));
            statement.setString(2, publicacion.getVendedor().getIdentificacion());
            statement.setString(3, vehiculo.getVin());
            statement.setDate(4, Date.valueOf(publicacion.getFecha()));
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

    public void rePublicarProducto(UUID id, double precio) {
        // TODO: 30/5/2023 update fecha  /hecho
        query = "UPDATE comercio.publicacion SET esta_disponible_web = ?, precio = ?,fecha = ? WHERE id = '" + id + "'";

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
        // TODO: 30/5/2023 update query para que retorne lo necesario
        query = "SELECT * FROM publicacion AS p INNER JOIN usuario AS u ON p.usuario_id = u.identificacion INNER JOIN" +
                " producto ON" +
                " p.producto_id = producto.vin";
        try (Statement statement = obtenerConexion().createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            UUID publicacionId;
            LocalDate fechaPublicacion;
            while (resultSet.next()) {
                publicacionId = UUID.fromString(resultSet.getString("id"));
                fechaPublicacion = (resultSet.getDate("fecha")).toLocalDate();
                int numeroOfertas = obtenerNumeroOfertas(publicacionId);

                if (numeroOfertas < 1 && tieneMaximoDiasSinOfertas(fechaPublicacion)) {
                    Publicacion publicacion = obtenerPublicacion(resultSet);
                    Usuario vendedor = obtenerUsuario(resultSet);
                    Vehiculo vehiculo = obtenerVehiculo(resultSet);
                    publicacion.setVendedor(vendedor);
                    publicacion.setProducto(vehiculo);
                    publicacionesDeBaja.add(publicacion);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return publicacionesDeBaja;
    }

    public void darDeBajaPublicacion(List<Publicacion> listaPublicaciones) {
        // TODO: 30/5/2023 actualizar el query
        query = "UPDATE comercio.publicacion SET esta_disponible_web = ? WHERE id = ?";

        try {
            PreparedStatement statement = obtenerConexion().prepareStatement(query);
            obtenerConexion().setAutoCommit(false);

            for (Publicacion publicacion: listaPublicaciones){
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

    // TODO: 30/5/2023 cambiar nombre de metodos
    private Publicacion obtenerPublicacion(ResultSet resultSet) throws SQLException {
        Publicacion publicacion = new Publicacion();
        publicacion.setId(UUID.fromString(resultSet.getString("id")));
        publicacion.setFecha((resultSet.getDate("fecha")).toLocalDate());
        publicacion.setPrecio(resultSet.getDouble("precio"));
        publicacion.setEstaDisponibleEnLaWeb(resultSet.getBoolean("esta_disponible_web"));
        return publicacion;
    }

    // TODO: 30/5/2023 cambiar nombre de metodos
    private Vehiculo obtenerVehiculo(ResultSet resultSet) throws SQLException {
        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setVin(resultSet.getString("vin"));
        vehiculo.setStockNumber(UUID.fromString(resultSet.getString("stock_number")));
        vehiculo.setMarca(resultSet.getString("marca"));
        vehiculo.setModelo(resultSet.getString("modelo"));
        vehiculo.setAnio(resultSet.getInt("anio"));
        return vehiculo;
    }

    private int obtenerNumeroOfertas(UUID publicacionId) throws SQLException {
        String queryOferta = "SELECT count(*) FROM comercio.oferta WHERE publicacion_id = '" + publicacionId + "'";
        int numeroOfertas = 0;
        Statement statementOferta = obtenerConexion().createStatement();
        ResultSet resultSetOferta = statementOferta.executeQuery(queryOferta);
        while (resultSetOferta.next()) {
            numeroOfertas = resultSetOferta.getInt(1);
        }
        return numeroOfertas;
    }

    private boolean tieneMaximoDiasSinOfertas(LocalDate fechaPublicacion) {
        return ChronoUnit.DAYS.between(fechaPublicacion, LocalDate.now()) >= MAX_DIAS_SIN_OFERTA;
    }
}
