package com.car.sales.company.dao;

import com.car.sales.company.exceptions.DatoInvalidoException;
import com.car.sales.company.models.*;

import java.sql.*;
import java.util.ArrayList;
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
        query =
                "INSERT INTO comercio.usuario VALUES('" + usuario.getIdentificacion() + "','" + usuario.getNombre() + "','" + usuario.getApellido() +
                        "','" + usuario.getTipoIdentificacion() + "','" + usuario.getTipoUsuario() + "','" + usuario.getEmail() + "'," +
                        "'" + usuario.getCelular() + "','" + aceptaNotificacionesSms + "')";
        ejecutarQueryParaModificaciones(query);
        query =
                SELECT_ID_FROM_NOTIFICACION + "WHERE tipo_notificacion = 'SMS' and tipo_usuario = '" +
                        usuario.getTipoUsuario().toString() + "'";
        List<Notificacion> listaDeIdNotificaciones = ejecutarQueryParaSeleccion(query, Notificacion.class);

        query = "INSERT INTO comercio.usuario_notificacion VALUES('"+usuario.getIdentificacion()+"', ?)";
        ejecutarQueriesConBatch(listaDeIdNotificaciones, query);

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
        query = ELIMINAR_UNSUSCRIPCIONES + "WHERE usuario_id = '" + identificacion + "'";
        if (!usuarioTieneCelularYConsentimientoSms(identificacion)) {
            query += "AND notificacion_id NOT IN (SELECT id FROM comercio.notificacion " +
                    "WHERE tipo_notificacion = 'SMS')";
        }
        ejecutarQueryParaModificaciones(query);
        return obtenerUsuario(identificacion);
    }

    public Usuario unsuscribirTodo(String usuarioId) {

        query = SELECT_ID_FROM_NOTIFICACION + "WHERE tipo_notificacion = 'EMAIL' AND id NOT IN" +
                "(" + SELECT_NOTIFICACIONES_ID_DE_USUARIO + "WHERE usuario_id ='" + usuarioId + "') AND tipo_usuario = (SELECT tipo_usuario FROM comercio" +
                ".usuario WHERE identificacion ='" + usuarioId + "')";
        if (usuarioTieneCelularYConsentimientoSms(usuarioId)) {
            query = query.replace("tipo_notificacion = 'EMAIL' AND", "");
        }
        Usuario usuarioModificado;
        try {
            Statement statement = obtenerConexion().createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            query = REGISTRAR_UNSUSCRIPCION + "VALUES(?,?)";
            PreparedStatement preparedStatement = obtenerConexion().prepareStatement(query);
            obtenerConexion().setAutoCommit(false);
            while (resultSet.next()) {
                String notificacionId = resultSet.getString("id");
                preparedStatement.setString(1, usuarioId);
                preparedStatement.setString(2, notificacionId);
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
            usuarioModificado = obtenerUsuario(usuarioId);
            resultSet.close();
            obtenerConexion().commit();
            preparedStatement.close();
        } catch (SQLException e) {
            throw new DatoInvalidoException("Hubo un error al unsuscribir todas las notificaciones! Intente " +
                    "nuevamente");
        }
        return usuarioModificado;
    }

    public Usuario suscribirNotificacion(String identificacion, NombreNotificacion nombreNotificacion,
                                         TipoNotificacion tipoNotificacion) {

        verificarCelularYConsentimientoSms(identificacion, tipoNotificacion);

        query = ELIMINAR_UNSUSCRIPCIONES + "WHERE usuario_id = '" + identificacion + "' AND notificacion_id =" +
                "(" + SELECT_ID_FROM_NOTIFICACION + "WHERE nombre = '" + nombreNotificacion + "' AND tipo_notificacion ='" + tipoNotificacion + "')";

        ejecutarQueryParaModificaciones(query);
        return obtenerUsuario(identificacion);
    }

    public Usuario unsucribirNotificacion(String identificacion, NombreNotificacion nombreNotificacion,
                                          TipoNotificacion tipoNotificacion) {

        verificarCelularYConsentimientoSms(identificacion, tipoNotificacion);

        query = "SELECT * FROM comercio.usuario_notificacion u " +
                "    RIGHT JOIN comercio.notificacion n " +
                "    ON  u.notificacion_id = n.id AND u.usuario_id = ? " +
                "    WHERE n.id =(" + SELECT_ID_FROM_NOTIFICACION + "WHERE nombre = ? AND tipo_notificacion = ?)";

        Usuario usuario;
        try {
            PreparedStatement statement = obtenerConexion().prepareStatement(query);
            statement.setString(1, identificacion);
            statement.setString(2, nombreNotificacion.toString());
            statement.setString(3, tipoNotificacion.toString());
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                if (resultSet.getString("usuario_id") == null) {
                    query = REGISTRAR_UNSUSCRIPCION + "VALUES('" + identificacion + "','" + resultSet.getString("id") + "')";
                    ejecutarQueryParaModificaciones(query);
                }
            }
            usuario = obtenerUsuario(identificacion);
            resultSet.close();
            statement.close();
            obtenerConexion().close();
        } catch (SQLException e) {
            if (e instanceof SQLIntegrityConstraintViolationException) {
                throw new DatoInvalidoException("El usuario ya esta suscrito a la notificacion ingresada");
            }
            throw new DatoInvalidoException("Hubo un error al unsuscribir notificacion! Intente nuevamente");
        }
        return usuario;
    }

    public Usuario obtenerUsuario(String identificacion) {
        query = SELECCIONAR_USUARIOS + "WHERE identificacion = '"+identificacion+"'";
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
