package com.car.sales.company.dao;

import com.car.sales.company.models.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


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

            query = "select id From comercio.notificacion where tipo_notificacion = 'SMS' and tipo_usuario = '" +
                    usuario.getTipoUsuario().toString() + "'";
            Statement statement1 = obtenerConexion().createStatement();
            ResultSet resultSet = statement1.executeQuery(query);
            query = "INSERT INTO comercio.usuario_notificacion VALUES(?,?)";
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

    public void unsuscribirTodo(Usuario usuario) {
        query = "select id from comercio.notificacion where id not in" +
                "(select notificacion_id From comercio.usuario_notificacion Where usuario_id = '" + usuario.getIdentificacion() + "') and " +
                "tipo_usuario = '" + usuario.getTipoUsuario() + "'";

        try {
            Statement statementId = obtenerConexion().createStatement();
            ResultSet resultSet = statementId.executeQuery(query);
            query = "INSERT INTO comercio.usuario_notificacion VALUES(?,?)";
            PreparedStatement statement = obtenerConexion().prepareStatement(query);
            obtenerConexion().setAutoCommit(false);
            while (resultSet.next()){
                String notificacionId = resultSet.getString("id");
                statement.setString(1,usuario.getIdentificacion());
                statement.setString(2,notificacionId);
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
        query = "Delete From comercio.usuario_notificacion where usuario_id = ? and notificacion_id =" +
                "(select id from comercio.notificacion where nombre = ? and " +
                "tipo_notificacion = ?)";

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
        query = "SELECT * " +
                "    FROM comercio.usuario_notificacion u " +
                "    Right JOIN comercio.notificacion n " +
                "    ON  u.notificacion_id = n.id and u.usuario_id = ? " +
                "    Where n.id =(select id from comercio.notificacion where nombre = ? and tipo_notificacion = ?);";
        try {
            PreparedStatement statement = obtenerConexion().prepareStatement(query);
            statement.setString(1, usuario.getIdentificacion());
            statement.setString(2, nombreNotificacion.toString());
            statement.setString(3, tipoNotificacion.toString());
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                if (resultSet.getString("usuario_id") == null) {
                    query = "Insert Into comercio.usuario_notificacion Values(?,?)";
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
