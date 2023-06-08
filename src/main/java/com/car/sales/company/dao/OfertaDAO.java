package com.car.sales.company.dao;

import com.car.sales.company.models.Accion;
import com.car.sales.company.models.Oferta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

import static com.car.sales.company.models.Accion.RETIRAR_OFERTA;

public class OfertaDAO {

    private final String UPDATE_OFERTA = "UPDATE comercio.oferta SET ";
    String query;

    private Connection obtenerConexion() throws SQLException {
        return ConexionDB.obtenerInstancia();
    }

    public void agregarOferta(Oferta oferta, UUID publicacionId) {
        query = "INSERT INTO comercio.oferta VALUES(?,?,?,?,?,?)";

        try (PreparedStatement statement = obtenerConexion().prepareStatement(query)) {
            statement.setString(1, oferta.getComprador().getIdentificacion());
            statement.setString(2, publicacionId.toString());
            statement.setDouble(3, oferta.getMontoOferta());
            statement.setDouble(4, oferta.getMontoContraOferta());
            statement.setBoolean(5, oferta.isInactivo());
            statement.setObject(6, oferta.getFechaOferta());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void interaccionContraOferta(String identificacion, UUID id, double nuevoMonto) {
        query = UPDATE_OFERTA + "monto_contra_oferta = ? WHERE publicacion_id = '" + id + "' AND usuario_id = "
                + identificacion;
        try (PreparedStatement statement = obtenerConexion().prepareStatement(query)) {
            statement.setDouble(1, nuevoMonto);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void actualizarOferta(UUID id, String identificacion, Accion accion) {
        query = UPDATE_OFERTA + "inactivo = ? WHERE (usuario_id <> ? AND publicacion_id = ?)";
        if (accion.equals(RETIRAR_OFERTA)) {
            query = query.replace("<>", "=");
        }
        try (PreparedStatement statement = obtenerConexion().prepareStatement(query)) {
            statement.setBoolean(1, true);
            statement.setString(2, identificacion);
            statement.setString(3, id.toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
