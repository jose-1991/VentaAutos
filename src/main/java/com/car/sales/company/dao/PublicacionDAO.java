package com.car.sales.company.dao;

import com.car.sales.company.models.Publicacion;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.UUID;

public class PublicacionDAO {

    private Connection obtenerConexion() throws SQLException {
        return ConexionDB.obtenerInstancia();
    }

    public void registarPublicacionEnDb(Publicacion publicacion){
        String query = "INSERT INTO comercio.publicacion(usuario_ID, producto_ID, fecha, oferta_ID," +
                "esta_disponible_web) VALUES(?,?,?,?,?)";

        try(PreparedStatement statement = obtenerConexion().prepareStatement(query)){
            statement.setString(1, publicacion.getVendedor().getIdentificacion());
            statement.setString(2, UUID.randomUUID().toString());
            statement.setDate(3, Date.valueOf(LocalDate.now()));
            statement.setString(4, null);
            statement.setBoolean(5, publicacion.isEstaDisponibleEnLaWeb());
            statement.executeUpdate();

            System.out.println("Publicacion registrada con exito!");
        } catch (SQLException exception) {
            System.out.println("Error al registrar publicacion");
            exception.printStackTrace();
        }
    }
}
