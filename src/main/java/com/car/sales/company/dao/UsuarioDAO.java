package com.car.sales.company.dao;

import com.car.sales.company.models.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.car.sales.company.models.TipoNotificacion.AMBOS;
import static com.car.sales.company.models.TipoNotificacion.EMAIL;


public class UsuarioDAO {
    private final String REGISTRAR_UNSUSCRIPCION = "INSERT INTO comercio.unsuscripcion VALUES(?,?,?,?";
    private final String ELIMINAR_UNSUSCRIPCION = "DELETE FROM comercio.unsuscripcion WHERE usuario_id = ? ";
    private final String SELECCIONAR_USUARIOS = "SELECT * FROM comercio.usuario WHERE ";
    String query;

    private static Connection obtenerConexion() throws SQLException {
        return ConexionDB.obtenerInstancia();
    }

    public List<Usuario> obtenerCompradores() {
        query = SELECCIONAR_USUARIOS + "tipo_usuario = 'COMPRADOR'";
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

            query = REGISTRAR_UNSUSCRIPCION;
            statement = obtenerConexion().prepareStatement(query);
            obtenerConexion().setAutoCommit(false);
            for (Notificacion notificacion : usuario.getListaUnsuscribciones()) {
                statement.setString(1, usuario.getIdentificacion());
                statement.setString(2, notificacion.getNombreNotificacion().toString());
                statement.setString(3, notificacion.getTipoNotificacion().toString());
                statement.setString(4, notificacion.getTipoUsuario().toString());
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

            query = SELECCIONAR_USUARIOS + "identificacion = '" + identificacion + "'";
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

    // TODO: 30/5/2023 cambiar nombre metodo
    public static Usuario obtenerUsuario(ResultSet resultSet) throws SQLException {
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

        String query = "SELECT * FROM unsuscripcion WHERE usuario_id = " + usuario.getIdentificacion();
        Statement statement = obtenerConexion().createStatement();
        ResultSet resultSetUnsuscripcion = statement.executeQuery(query);
        Notificacion notificacion = new Notificacion();
        while (resultSetUnsuscripcion.next()) {
            notificacion.setNombreNotificacion(NombreNotificacion.valueOf(resultSetUnsuscripcion.getString(
                    "nombre_notificacion")));
            notificacion.setTipoNotificacion(TipoNotificacion.valueOf(resultSetUnsuscripcion.getString(
                    "tipo_notificacion")));
            notificacion.setTipoUsuario(TipoUsuario.valueOf(resultSetUnsuscripcion.getString(
                    "tipo_usuario")));
            usuario.getListaUnsuscribciones().add(notificacion);
        }
        return usuario;
    }

    public void suscribirTodo(Usuario usuario) {
        query = ELIMINAR_UNSUSCRIPCION;

        try (PreparedStatement statement = obtenerConexion().prepareStatement(query)) {
            statement.setString(1, usuario.getIdentificacion());
            statement.executeUpdate();


        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void unsuscribirTodo(Usuario usuario, List<Notificacion> notificacionList) {
        query = REGISTRAR_UNSUSCRIPCION;
        TipoNotificacion tipoNotificacion = EMAIL;
        if (usuario.isAceptaNotificacionSms()){
            tipoNotificacion  = AMBOS;
        }
        try {
            PreparedStatement statement = obtenerConexion().prepareStatement(query);
            obtenerConexion().setAutoCommit(false);

            for (Notificacion notificacion : notificacionList) {
                if (usuario.getListaUnsuscribciones().contains(notificacion)){
                    statement.setString(1, usuario.getIdentificacion());
                    statement.setString(2, notificacion.toString());
                    statement.setString(3, tipoNotificacion.toString());
                    statement.setString(4, notificacion.getTipoUsuario().toString());
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

    public void suscribirNotificacion(Usuario usuario, Notificacion notificacion) {
        query = ELIMINAR_UNSUSCRIPCION + "AND nombre_notificacion = ? AND tipo = ?";

        try (PreparedStatement statement = obtenerConexion().prepareStatement(query)) {
            statement.setString(1, usuario.getIdentificacion());
            statement.setString(2, notificacion.getNombreNotificacion().toString());
            if (usuario.isAceptaNotificacionSms()) {
                statement.setString(3, notificacion.getTipoNotificacion().toString());
            }else if (notificacion.getTipoNotificacion().equals(EMAIL)){
                statement.setString(3, EMAIL.toString());
            }
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void unsucribirNotificacion(String identificacion, Notificacion notificacion) {

        query = REGISTRAR_UNSUSCRIPCION;

        try (PreparedStatement statement = obtenerConexion().prepareStatement(query)) {
            statement.setString(1, identificacion);
            statement.setString(2, notificacion.getNombreNotificacion().toString());
            statement.setString(3, notificacion.getTipoNotificacion().toString());
            statement.setString(4, notificacion.getTipoUsuario().toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
