package com.car.sales.company.dao;

import com.car.sales.company.models.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.car.sales.company.models.TipoNotificacion.SMS;


public class UsuarioDAO {
    private final String SELECT_ID_NOTIFICACION = "SELECT id FROM comercio.notificacion WHERE";
    private static final String SELECT_NOTIFICACIONES_ID_DE_USUARIO = "SELECT notificacion_id FROM comercio" +
            ".usuario_notificacion WHERE usuario_id = ?";
    private final String REGISTRAR_UNSUSCRIPCION = "INSERT INTO comercio.usuario_notificacion VALUES(?,?)";
    private final String ELIMINAR_UNSUSCRIPCIONES = "DELETE FROM comercio.usuario_notificacion WHERE usuario_id = ? ";
    private final String SELECCIONAR_USUARIOS = "SELECT * FROM comercio.usuario WHERE ";
    String query;

    private static Connection obtenerConexion() throws SQLException {
        return ConexionDB.obtenerInstancia();
    }

    public List<Usuario> obtenerCompradores() {
        query = SELECCIONAR_USUARIOS + "tipo_usuario = 'COMPRADOR'";
        List<Usuario> listaCompradores = new ArrayList<>();

        try (Statement statement = obtenerConexion().createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                Usuario usuario = convertirUsuario(resultSet);
                listaCompradores.add(usuario);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listaCompradores;
    }

    public void registrarUsuario(Usuario usuario) {
        query = "INSERT INTO comercio.usuario VALUES(?,?,?,?,?,?,?,?)";
        try {
            PreparedStatement statement = obtenerConexion().prepareStatement(query);
            statement.setString(1, usuario.getIdentificacion());
            statement.setString(2, usuario.getNombre());
            statement.setString(3, usuario.getApellido());
            statement.setString(4, usuario.getTipoIdentificacion());
            statement.setString(5, usuario.getTipoUsuario().toString());
            statement.setString(6, usuario.getEmail());
            statement.setString(7, usuario.getCelular());
            statement.setBoolean(8, usuario.isAceptaNotificacionSms());
            statement.executeUpdate();

            query = SELECT_ID_NOTIFICACION + " tipo_notificacion = ? and tipo_usuario = ?";
            statement = obtenerConexion().prepareStatement(query);
            statement.setString(1, SMS.toString());
            statement.setString(2, usuario.getTipoUsuario().toString());
            ResultSet resultSet = statement.executeQuery();
            query = REGISTRAR_UNSUSCRIPCION;
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

        } catch (SQLException exception) {
            System.out.println("Error al registrar usuario");
            exception.printStackTrace();
        }
    }

    public void eliminarUsuario(String identificacion) {

        query = "DELETE FROM comercio.usuario WHERE identificacion = ?";
        try (PreparedStatement statement = obtenerConexion().prepareStatement(query)) {
            statement.setString(1, identificacion);
            statement.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error al eliminar un usuario");
            e.printStackTrace();
        }
    }

    public Usuario modificarUsuario(String identificacion, String celular) {
        query =
                "UPDATE comercio.usuario SET celular = ?, acepta_notificacion_sms = ?  WHERE identificacion = " + identificacion;
        Usuario usuario = null;
        boolean notificacionSms = celular != null;
        try (PreparedStatement updateStatement = obtenerConexion().prepareStatement(query)) {
            updateStatement.setString(1, celular);
            updateStatement.setBoolean(2, notificacionSms);
            updateStatement.executeUpdate();

            query = SELECCIONAR_USUARIOS + "identificacion = '" + identificacion + "'";
            try (Statement statement = obtenerConexion().createStatement();
                 ResultSet resultSet = statement.executeQuery(query)) {
                if (resultSet.next()) {
                    usuario = convertirUsuario(resultSet);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al modificar Usuario");
            e.printStackTrace();
        }
        return usuario;
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
        String query = "SELECT * FROM comercio.notificacion WHERE id IN ("+SELECT_NOTIFICACIONES_ID_DE_USUARIO +")";
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

    public void suscribirTodo(Usuario usuario) {
        query = ELIMINAR_UNSUSCRIPCIONES;

        try (PreparedStatement statement = obtenerConexion().prepareStatement(query)) {
            statement.setString(1, usuario.getIdentificacion());
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void unsuscribirTodo(Usuario usuario) {
        query = SELECT_ID_NOTIFICACION +" id NOT IN" +
                "("+SELECT_NOTIFICACIONES_ID_DE_USUARIO+") AND tipo_usuario = ?";
        try {
            PreparedStatement statement = obtenerConexion().prepareStatement(query);
            statement.setString(1, usuario.getIdentificacion());
            statement.setString(2, usuario.getTipoUsuario().toString());
            ResultSet resultSet = statement.executeQuery();
            query = REGISTRAR_UNSUSCRIPCION;
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
            e.printStackTrace();
        }
    }

    public void suscribirNotificacion(Usuario usuario, NombreNotificacion nombreNotificacion, TipoNotificacion tipoNotificacion) {
        query = ELIMINAR_UNSUSCRIPCIONES + " AND notificacion_id =" +
                "("+SELECT_ID_NOTIFICACION +" nombre = ? AND tipo_notificacion = ?)";

        try (PreparedStatement statement = obtenerConexion().prepareStatement(query)) {
            statement.setString(1, usuario.getIdentificacion());
            statement.setString(2, nombreNotificacion.toString());
            statement.setString(3, tipoNotificacion.toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void unsucribirNotificacion(Usuario usuario, NombreNotificacion nombreNotificacion, TipoNotificacion tipoNotificacion) {
        query = "SELECT * FROM comercio.usuario_notificacion u " +
                "    RIGHT JOIN comercio.notificacion n " +
                "    ON  u.notificacion_id = n.id AND u.usuario_id = ? " +
                "    WHERE n.id =("+ SELECT_ID_NOTIFICACION+" nombre = ? AND tipo_notificacion = ?);";
        try {
            PreparedStatement statement = obtenerConexion().prepareStatement(query);
            statement.setString(1, usuario.getIdentificacion());
            statement.setString(2, nombreNotificacion.toString());
            statement.setString(3, tipoNotificacion.toString());
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                if (resultSet.getString("usuario_id") == null) {
                    query = REGISTRAR_UNSUSCRIPCION;
                    statement = obtenerConexion().prepareStatement(query);
                    statement.setString(1, usuario.getIdentificacion());
                    statement.setString(2, resultSet.getString("id"));
                    statement.executeUpdate();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
