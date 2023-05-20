package com.car.sales.company.dao;

import com.car.sales.company.models.Notificacion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class NotificacionDAO {

    private Connection obtenerConexion() throws SQLException {
        return ConexionDB.obtenerInstancia();
    }

    public void registrarNotificacionEnDb(Notificacion notificacion) {
        String query = "INSERT INTO comercio.notificacion(nombre_notificacion, producto_ID, monto_oferta, " +
                "monto_contra_oferta, email, celular) VALUES(?,?,?,?,?,?)";

        try (PreparedStatement statement = obtenerConexion().prepareStatement(query)) {
            statement.setString(1, notificacion.getNombreNotificacion().toString());
            statement.setString(2, UUID.randomUUID().toString());
            statement.setDouble(3, notificacion.getMontoOferta());
            statement.setDouble(4, notificacion.getMontoContraOferta());
            statement.setString(5, notificacion.getEmail());
            statement.setString(6, notificacion.getCelular());
            statement.executeUpdate();

            System.out.println("Notificacion registrada con exito!");
        } catch (SQLException exception) {
            System.out.println("Error al registrar notificacion");
            exception.printStackTrace();
        }
    }
}
