package com.car.sales.company.dao;

import com.car.sales.company.models.NombreNotificacion;
import com.car.sales.company.models.TipoUsuario;
import com.car.sales.company.models.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.car.sales.company.models.TipoNotificacion.SMS;

public class UsuarioDAO {
    String query;

    private Connection obtenerConexion() throws SQLException {
        return ConexionDB.obtenerInstancia();
    }

    public List<Usuario> obtenerCompradores() {
        query = "SELECT * FROM comercio.usuario where tipo_usuario = 'COMPRADOR'";
        List<Usuario> listaCompradores = new ArrayList<>();

        try (Statement statement = obtenerConexion().createStatement(); ResultSet resultSet =
                statement.executeQuery(query)) {
            while (resultSet.next()) {
                Usuario usuario = new Usuario();
                usuario.setNombre(resultSet.getString("nombre"));
                usuario.setApellido(resultSet.getString("apellido"));
                usuario.setTipoUsuario(TipoUsuario.valueOf(resultSet.getString("tipo_usuario")));
                usuario.setTipoIdentificacion(resultSet.getString("tipo_identificacion"));
                usuario.setIdentificacion(resultSet.getString("id"));
                usuario.setEmail(resultSet.getString("email"));
                usuario.setCelular(resultSet.getString("celular"));
                usuario.setAceptaNotificacionSms(resultSet.getBoolean("acepta_notificacion_sms"));
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

            System.out.println("Usuario registrado con exito!");
        } catch (SQLException exception) {
            System.out.println("Error al registrar usuario");
            exception.printStackTrace();
        }
    }

    public void eliminarUsuario(String identificacion) {

        query = "DELETE FROM comercio.usuario WHERE usuario_ID = ?";
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

        Usuario usuario = new Usuario();
        query = "SELECT * FROM comercio.usuario WHERE id = '" + identificacion + "'";
        try  {
            Statement statement = obtenerConexion().createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                usuario.setIdentificacion(resultSet.getString("id"));
                usuario.setNombre(resultSet.getString("nombre"));
                usuario.setApellido(resultSet.getString("apellido"));
                usuario.setTipoIdentificacion(resultSet.getString("tipo_identificacion"));
                usuario.setTipoUsuario(TipoUsuario.valueOf(resultSet.getString("tipo_usuario")));
                usuario.setEmail(resultSet.getString("email"));
                usuario.setCelular(resultSet.getString("celular"));
                usuario.setAceptaNotificacionSms(resultSet.getBoolean("acepta_notificacion_sms"));

                String query = "UPDATE comercio.usuario SET celular = ? WHERE id = ?";
                PreparedStatement updateStatement = obtenerConexion().prepareStatement(query);
                updateStatement.setString(1, celular);
                updateStatement.setString(2, identificacion);
                updateStatement.executeUpdate();
            }
            resultSet.close();
            statement.close();
            obtenerConexion().close();
        } catch (SQLException e) {
            System.out.println("Error al modificar Usuario");
            e.printStackTrace();
        }
        return usuario;
    }


    public void registrarUnsuscripciones(List<NombreNotificacion> unsuscripcionesSms, String usuarioId) {
        query = "INSERT INTO comercio.unsuscripcion VALUES(?,?,?)";

        try {
            PreparedStatement statement = obtenerConexion().prepareStatement(query);
            obtenerConexion().setAutoCommit(false);
            for (NombreNotificacion nombreNotificacion : unsuscripcionesSms) {
                statement.setString(1, usuarioId);
                statement.setString(2, nombreNotificacion.toString());
                statement.setString(3, SMS.toString());
                statement.addBatch();
            }
            statement.executeBatch();
            obtenerConexion().commit();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }
}
