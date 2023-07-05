package com.car.sales.company.dao;

import com.car.sales.company.exceptions.DatoInvalidoException;
import com.car.sales.company.models.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.car.sales.company.dao.OfertaDAO.ejecutarQueryParaModificaciones;
import static com.car.sales.company.dao.PublicacionDAO.ejecutarQueryParaSeleccion;
import static com.car.sales.company.models.TipoNotificacion.SMS;

public class UsuarioDAO {
    private final String SELECT_ID_FROM_NOTIFICACION = "SELECT id FROM comercio.notificacion ";
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
        try {
            query = SELECT_ID_FROM_NOTIFICACION + "WHERE tipo_notificacion = ? and tipo_usuario = ?";
            PreparedStatement statement = obtenerConexion().prepareStatement(query);
            statement.setString(1, SMS.toString());
            statement.setString(2, usuario.getTipoUsuario().toString());
            ResultSet resultSet = statement.executeQuery();
            query = REGISTRAR_UNSUSCRIPCION + "VALUES(?,?)";
            statement = obtenerConexion().prepareStatement(query);
            obtenerConexion().setAutoCommit(false);

            while (resultSet.next()) {
                String notificacionId = resultSet.getString("id");
                statement.setString(1, usuario.getIdentificacion());
                statement.setString(2, notificacionId);
                statement.addBatch();
            }
            statement.executeBatch();
            obtenerConexion().commit();
            statement.close();

        } catch (SQLException e) {
            if (e instanceof SQLIntegrityConstraintViolationException) {
                throw new DatoInvalidoException("El usuario ingresado con identificacion = " + usuario.getIdentificacion() +
                        "  -> Ya esta registrado");
            }
            throw new DatoInvalidoException("Hubo un error al registrar usuario! Intente nuevamente");
        }
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

    public static Usuario convertirUsuario(ResultSet resultSet) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setListaUnsuscribciones(new ArrayList<>());
        usuario.setIdentificacion(resultSet.getString("identificacion"));
        usuario.setNombre(resultSet.getString("nombre"));
        usuario.setApellido(resultSet.getString("apellido"));
        usuario.setTipoIdentificacion(resultSet.getString("tipo_identificacion"));
        usuario.setTipoUsuario(TipoUsuario.valueOf(resultSet.getString("tipo_usuario")));
        usuario.setEmail(resultSet.getString("email"));
        usuario.setCelular(resultSet.getString("celular"));
        usuario.setAceptaNotificacionSms(resultSet.getBoolean("acepta_notificacion_sms"));
        String query = "SELECT * FROM comercio.notificacion WHERE id IN (" + SELECT_NOTIFICACIONES_ID_DE_USUARIO +
                "WHERE usuario_id = ?)";
        PreparedStatement statement = obtenerConexion().prepareStatement(query);
        statement.setString(1, usuario.getIdentificacion());
        ResultSet resultSetUnsuscripcion = statement.executeQuery();
        Notificacion notificacion = new Notificacion();
        while (resultSetUnsuscripcion.next()) {
            notificacion.setId(resultSetUnsuscripcion.getString("id"));
            notificacion.setNombreNotificacion(NombreNotificacion.valueOf(resultSetUnsuscripcion.getString("nombre")));
            notificacion.setTipoNotificacion(TipoNotificacion.valueOf(resultSetUnsuscripcion.getString("tipo_notificacion")));
            notificacion.setTipoUsuario(TipoUsuario.valueOf(resultSetUnsuscripcion.getString("tipo_usuario")));
            usuario.getListaUnsuscribciones().add(notificacion);
        }
        return usuario;
    }

    public Usuario suscribirTodo(String identificacion) {
        query = ELIMINAR_UNSUSCRIPCIONES + "WHERE usuario_id = '" + identificacion + "'";
        if (!usuarioTieneCelularYConsentimientoSms(identificacion)){
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
        if (usuarioTieneCelularYConsentimientoSms(usuarioId)){
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
        query = SELECCIONAR_USUARIOS + "WHERE identificacion = ?";
        Usuario usuario = null;
        try {
            PreparedStatement statement = obtenerConexion().prepareStatement(query);
            statement.setString(1, identificacion);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                usuario = convertirUsuario(resultSet);
            }
            resultSet.close();
            statement.close();
            return usuario;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Usuario obtenerUsuarioNew(String identificacion){
        query = SELECCIONAR_USUARIOS + "WHERE identificacion = '"+ identificacion+"'";

        List<Usuario> listaUsuarios = ejecutarQueryParaSeleccion(query, Usuario.class);

        return listaUsuarios.get(0);
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
