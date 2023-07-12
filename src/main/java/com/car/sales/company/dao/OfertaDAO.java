package com.car.sales.company.dao;

import com.car.sales.company.exceptions.DatoInvalidoException;
import com.car.sales.company.models.Accion;
import com.car.sales.company.models.Oferta;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.UUID;

import static com.car.sales.company.dao.ConexionDB.obtenerInstancia;
import static com.car.sales.company.models.Accion.RETIRAR_OFERTA;

public class OfertaDAO {

    private final String UPDATE_OFERTA = "UPDATE comercio.oferta ";
    String query;

    public void agregarOferta(Oferta oferta, UUID publicacionId) {
        query = "INSERT INTO comercio.oferta VALUES('" + oferta.getComprador().getIdentificacion() + "','" + publicacionId +
                "','" + oferta.getMontoOferta() + "','" + oferta.getMontoContraOferta() + "','0','" +
                oferta.getFechaOferta() + "')";

        ejecutarQueryParaModificaciones(query);
    }

    public void interaccionContraOferta(String identificacion, UUID publicacionId, double nuevoMonto) {
        query =
                UPDATE_OFERTA + " SET monto_contra_oferta = '" + nuevoMonto + "' WHERE publicacion_id = '" + publicacionId + "' AND" +
                " usuario_id = '" + identificacion + "'";
        ejecutarQueryParaModificaciones(query);
    }

    public void actualizarOferta(UUID id, String identificacion, Accion accion) {
        query = UPDATE_OFERTA + "SET inactivo = '1' WHERE (usuario_id <> '" + identificacion + "' AND publicacion_id " +
                "= '" + id + "')";
        if (accion.equals(RETIRAR_OFERTA)) {
            query = query.replace("<>", "=");
        }
        ejecutarQueryParaModificaciones(query);
    }

    public static void ejecutarQueryParaModificaciones(String query) {
        try  (PreparedStatement statement = obtenerInstancia().prepareStatement(query)){
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            if (e instanceof SQLIntegrityConstraintViolationException) {
                throw new DatoInvalidoException("Error! primary key duplicate");
            }
            throw new DatoInvalidoException("Hubo un error! Intente nuevamente");
        }
        System.out.println("query ejecutado con exito! \n" + query);
    }

}
