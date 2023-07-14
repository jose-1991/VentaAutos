package com.car.sales.company.dao;

import com.car.sales.company.exceptions.DatoInvalidoException;
import com.car.sales.company.models.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.car.sales.company.dao.OfertaDAO.ejecutarQueryParaModificaciones;
import static com.car.sales.company.dao.PublicacionDAO.*;
import static com.car.sales.company.models.TipoNotificacion.SMS;

public class UsuarioDAO {
    private final String SELECT_ID_FROM_NOTIFICACION = "SELECT id, tipo_usuario FROM comercio.notificacion ";
    private static final String SELECT_NOTIFICACIONES_ID_DE_USUARIO = "SELECT notificacion_id FROM comercio" +
            ".usuario_notificacion ";
    private final String REGISTRAR_UNSUSCRIPCION = "INSERT INTO comercio.usuario_notificacion ";
    private final String ELIMINAR_UNSUSCRIPCIONES = "DELETE FROM comercio.usuario_notificacion ";
    private final String SELECCIONAR_USUARIOS = "SELECT * FROM comercio.usuario ";
    String query;

    private static Connection obtenerConexion() throws SQLException {
        return ConexionDB.obtenerInstancia();
    }

    public List<Usuario> obtenerCompradores() {
        query = SELECCIONAR_USUARIOS + "WHERE tipo_usuario = 'COMPRADOR'";
        return ejecutarQueryParaSeleccion(query, Usuario.class);
    }

    public void registrarUsuario(Usuario usuario) {
        int aceptaNotificacionesSms = usuario.isAceptaNotificacionSms() ? 1 : 0;
        List<String> listaQueries = new ArrayList<>();
        query =
                "INSERT INTO comercio.usuario VALUES('" + usuario.getIdentificacion() + "','" + usuario.getNombre() + "','" + usuario.getApellido() +
                        "','" + usuario.getTipoIdentificacion() + "','" + usuario.getTipoUsuario() + "','" + usuario.getEmail() + "'," +
                        "'" + usuario.getCelular() + "','" + aceptaNotificacionesSms + "')";
        listaQueries.add(query);
//        ejecutarQueryParaModificaciones(query);
        query = "INSERT INTO comercio.usuario_notificacion (usuario_id, notificacion_id)" +
                "SELECT usuario.identificacion, notificacion.id FROM usuario, notificacion" +
                " WHERE usuario.identificacion = '" + usuario.getIdentificacion() + "' AND notificacion.tipo_notificacion" +
                " = 'SMS' and tipo_usuario = " + usuario.getTipoUsuario() + "'";
        listaQueries.add(query);
//        ejecutarQueryParaModificaciones(query);
        ejecutarQueriesConBatch(listaQueries);
    }

    public void eliminarUsuario(String identificacion) {
        query = "DELETE FROM comercio.usuario WHERE identificacion = '" + identificacion + "'";
        ejecutarQueryParaModificaciones(query);
    }

    public Usuario modificarUsuario(String identificacion, String celular) {
        int notificacionSms = celular != null ? 1 : 0;
        query =
                "UPDATE comercio.usuario SET celular = '" + celular + "', acepta_notificacion_sms = '" + notificacionSms + "'  WHERE " +
                        "identificacion = '" + identificacion + "'";
        ejecutarQueryParaModificaciones(query);
        return obtenerUsuario(identificacion);
    }

    public Usuario suscribirTodo(String identificacion) {
        query = "DELETE FROM comercio.usuario_notificacion WHERE usuario_id = '" + identificacion + "' AND " +
                "((SELECT acepta_notificacion_sms FROM comercio.usuario WHERE identificacion = '" + identificacion + "') = 0" +
                "OR notificacion_id NOT IN (SELECT id FROM notificacion WHERE tipo_notificacion = 'SMS'))";

        ejecutarQueryParaModificaciones(query);
        return obtenerUsuario(identificacion);

    }

    public Usuario unsuscribirTodo(String usuarioId) {
        query = "INSERT INTO comercio.usuario_notificacion (usuario_id, notificacion_id)" +
                " SELECT '" + usuarioId + "', n.id FROM notificacion AS n" +
                " WHERE  ((SELECT acepta_notificacion_sms FROM comercio.usuario WHERE identificacion = '"+usuarioId+"') = 1" +
                " OR n.tipo_notificacion = 'EMAIL')" +
                " AND n.id NOT IN (SELECT notificacion_id FROM comercio.usuario_notificacion WHERE usuario_id = '" + usuarioId + "')" +
                " AND n.tipo_usuario = (SELECT tipo_usuario FROM comercio.usuario WHERE identificacion = '" + usuarioId + "')";
        ejecutarQueryParaModificaciones(query);
        return obtenerUsuario(usuarioId);
    }

    public Usuario suscribirNotificacion(String identificacion, NombreNotificacion nombreNotificacion,
                                         TipoNotificacion tipoNotificacion) {
        verificarCelularYConsentimientoSms(identificacion, tipoNotificacion);
        query = ELIMINAR_UNSUSCRIPCIONES + "WHERE usuario_id = '" + identificacion + "' " +
                "AND notificacion_id = (" + SELECT_ID_FROM_NOTIFICACION + "WHERE nombre = '" + nombreNotificacion + "' " +
                "AND tipo_notificacion ='" + tipoNotificacion + "')";
        ejecutarQueryParaModificaciones(query);
        return obtenerUsuario(identificacion);
    }

    public Usuario unsucribirNotificacion(String identificacion, NombreNotificacion nombreNotificacion,
                                          TipoNotificacion tipoNotificacion) {

        verificarCelularYConsentimientoSms(identificacion, tipoNotificacion);

        query = " INSERT IGNORE INTO comercio.usuario_notificacion (usuario_id, notificacion_id)" +
                " SELECT '" + identificacion + "', n.id FROM notificacion AS n WHERE n.id = " +
                "(SELECT id FROM notificacion WHERE nombre = '" + nombreNotificacion + "' AND tipo_notificacion = '" + tipoNotificacion + "')";

        ejecutarQueryParaModificaciones(query);
        return obtenerUsuario(identificacion);
    }

    public Usuario obtenerUsuario(String identificacion) {
        query = SELECCIONAR_USUARIOS + "WHERE identificacion = '" + identificacion + "'";
        List<Usuario> listaDeUsuarios = ejecutarQueryParaSeleccion(query, Usuario.class);
        return listaDeUsuarios.get(0);
    }

    private void verificarCelularYConsentimientoSms(String usuarioId, TipoNotificacion tipoNotificacion) {
        if (tipoNotificacion.equals(SMS)) {
            if (!usuarioTieneCelularYConsentimientoSms(usuarioId)) {
                throw new DatoInvalidoException("Error! Posibles errores:\n " +
                        "-El usuario no tiene celular registrado\n " +
                        "-El usuario no acepta recibir notificaciones por sms");
            }
        }
    }

    private boolean usuarioTieneCelularYConsentimientoSms(String usuarioId) {

        query = "SELECT celular, acepta_notificacion_sms FROM comercio.usuario WHERE identificacion = '" + usuarioId + "'";
        String celular = null;
        boolean aceptaNotificacionSms = false;
        try (Statement statement = obtenerConexion().createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            if (resultSet.next()) {
                celular = resultSet.getString("celular");
                aceptaNotificacionSms = resultSet.getBoolean("acepta_notificacion_sms");
            }
        } catch (SQLException e) {
            throw new DatoInvalidoException("Hubo un error al verifificar celular y consentimiento");
        }
        return celular != null && aceptaNotificacionSms;
    }
}
