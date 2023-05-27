package com.car.sales.company.dao;

import com.car.sales.company.models.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.car.sales.company.models.TipoNotificacion.EMAIL;
import static com.car.sales.company.models.TipoNotificacion.SMS;

public class UsuarioDAO {
    String query;

    private static Connection obtenerConexion() throws SQLException {
        return ConexionDB.obtenerInstancia();
    }

    public List<Usuario> obtenerCompradores() {
        query = "SELECT * FROM comercio.usuario where tipo_usuario = 'COMPRADOR'";
        List<Usuario> listaCompradores = new ArrayList<>();

        try (Statement statement = obtenerConexion().createStatement(); ResultSet resultSet =
                statement.executeQuery(query)) {
            while (resultSet.next()) {
                Usuario usuario = obtenerUsuario(resultSet);
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

            query = "INSERT INTO comercio.unsuscripcion VALUES(?,?,?)";
            statement = obtenerConexion().prepareStatement(query);
            obtenerConexion().setAutoCommit(false);
            for (NombreNotificacion nombreNotificacion : usuario.getUnsuscripcionesSms()) {
                statement.setString(1, usuario.getIdentificacion());
                statement.setString(2, nombreNotificacion.toString());
                statement.setString(3, SMS.toString());
                statement.addBatch();
            }
            statement.executeBatch();
            obtenerConexion().commit();
            statement.close();
            System.out.println("Usuario registrado con exito!");
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

            System.out.println("Usuario eliminado con exito!");

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

            query = "SELECT * FROM comercio.usuario WHERE identificacion = '" + identificacion + "'";
            try (Statement statement = obtenerConexion().createStatement();
                 ResultSet resultSet = statement.executeQuery(query)) {
                if (resultSet.next()) {
                    usuario = obtenerUsuario(resultSet);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al modificar Usuario");
            e.printStackTrace();
        }
        return usuario;
    }

    public void actualizarSuscripcion(String identificacion, NombreNotificacion nombreNotificacion, Accion accion,
                                      TipoNotificacion tipoNotificacion) {
        switch (accion) {
            case SUSCRIBIR:
                query = "DELETE FROM comercio.unsuscripcion WHERE usuario_id = ? AND nombre_notificacion = ? AND " +
                        "tipo =?";
                break;
            case UNSUSCRIBIR:
                query = "INSERT INTO comercio.unsuscripcion VALUES(?,?,?)";
                break;
        }
        try (PreparedStatement statement = obtenerConexion().prepareStatement(query)) {
            statement.setString(1, identificacion);
            statement.setString(2, nombreNotificacion.toString());
            statement.setString(3, tipoNotificacion.toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Usuario obtenerUsuario(ResultSet resultSet) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setUnsuscribcionesSms(new ArrayList<>());
        usuario.setUnsuscribcionesEmail(new ArrayList<>());
        usuario.setIdentificacion(resultSet.getString("identificacion"));
        usuario.setNombre(resultSet.getString("nombre"));
        usuario.setApellido(resultSet.getString("apellido"));
        usuario.setTipoIdentificacion(resultSet.getString("tipo_identificacion"));
        usuario.setTipoUsuario(TipoUsuario.valueOf(resultSet.getString("tipo_usuario")));
        usuario.setEmail(resultSet.getString("email"));
        usuario.setCelular(resultSet.getString("celular"));
        usuario.setAceptaNotificacionSms(resultSet.getBoolean("acepta_notificacion_sms"));

        String query = "SELECT * FROM unsuscripcion WHERE usuario_id = " + usuario.getIdentificacion();
        Statement statement = obtenerConexion().createStatement();
        ResultSet resultSetUnsuscripcion = statement.executeQuery(query);
        while (resultSetUnsuscripcion.next()) {
            NombreNotificacion nombreNotificacion = NombreNotificacion.valueOf(resultSetUnsuscripcion.getString(
                    "nombre_notificacion"));
            if (TipoNotificacion.valueOf(resultSetUnsuscripcion.getString("tipo")).equals(SMS)) {
                usuario.getUnsuscripcionesSms().add(nombreNotificacion);
            } else {
                usuario.getUnsuscripcionesEmail().add(nombreNotificacion);
            }
        }
        return usuario;
    }

    public void suscribirTodo(Usuario usuario) {
        query = "DELETE FROM comercio.unsuscripcion WHERE usuario_id = ?";
        if (!usuario.isAceptaNotificacionSms()) {
            query += " AND tipo = 'EMAIL'";
        }
        try (PreparedStatement statement = obtenerConexion().prepareStatement(query)) {
            statement.setString(1, usuario.getIdentificacion());
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void unsuscribirTodo(String identificacion, List<NombreNotificacion> listaNotificacionesEmail,
                                List<NombreNotificacion> listaNotificacionesSms) {
        query = "INSERT INTO comercio.unsuscripcion VALUES(?,?,?)";

        try {
            PreparedStatement statement = obtenerConexion().prepareStatement(query);
            obtenerConexion().setAutoCommit(false);

            for (NombreNotificacion nombreNotificacion : listaNotificacionesEmail) {
                statement.setString(1, identificacion);
                statement.setString(2, nombreNotificacion.toString());
                statement.setString(3, EMAIL.toString());
                statement.addBatch();
            }
            if (listaNotificacionesSms != null) {
                for (NombreNotificacion nombreNotificacion : listaNotificacionesSms) {
                    statement.setString(1, identificacion);
                    statement.setString(2, nombreNotificacion.toString());
                    statement.setString(3, SMS.toString());
                    statement.addBatch();
                }
            }
            statement.executeBatch();
            obtenerConexion().commit();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
