package com.car.sales.company.dao;

import com.car.sales.company.exceptions.DatoInvalidoException;
import com.car.sales.company.models.TipoUsuario;
import com.car.sales.company.models.Usuario;

import java.sql.*;

public class UsuarioDAO {
    String query;

    private Connection obtenerConexion() throws SQLException {
        return ConexionDB.obtenerInstancia();
    }

    public void registrarUsuarioEnDb(Usuario usuario) {
        query = "INSERT INTO comercio.usuario(usuario_ID, nombre, apellido, tipo_identificacion, " +
                "tipo_usuario, email, celular) VALUES(?,?,?,?,?,?,?)";

        try (PreparedStatement statement = obtenerConexion().prepareStatement(query)) {
            statement.setString(1, usuario.getIdentificacion());
            statement.setString(2, usuario.getNombre());
            statement.setString(3, usuario.getApellido());
            statement.setString(4, usuario.getTipoIdentificacion());
            statement.setString(5, usuario.getTipoUsuario().toString());
            statement.setString(6, usuario.getEmail());
            statement.setString(7, usuario.getCelular());
            statement.executeUpdate();

            System.out.println("Usuario registrado con exito!");
        } catch (SQLException exception) {
            System.out.println("Error al registrar usuario");
            exception.printStackTrace();
        }
    }

    public void eliminarUsuarioEnDb(String identificacion) {

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

    public void modificarUsuarioEnDb(String identificacion, String celular) {

        query = "UPDATE comercio.usuario SET celular = ?  WHERE usuario_ID = " + identificacion;
        try (PreparedStatement statement = obtenerConexion().prepareStatement(query)) {
            statement.setString(1, celular);
            statement.executeUpdate();

            System.out.println("Usuario modificado con exito!");
        } catch (SQLException e) {
            System.out.println("Error al modificar usuario");
            e.printStackTrace();
        }
    }

    public Usuario obtenerUsuarioDeDb(String identificacion) {
        if (!UsuarioExisteEnDb(identificacion)) {
            throw new DatoInvalidoException("No existe un usuario registrado con la identificacion ingresada");
        }

        Usuario usuario = new Usuario();
        query = "SELECT * FROM comercio.usuario WHERE usuario_ID = " + identificacion;
        try (Statement statement = obtenerConexion().createStatement(); ResultSet resultSet =
                statement.executeQuery(query)) {
            while (resultSet.next()) {
                usuario.setNombre(resultSet.getString("nombre"));
                usuario.setApellido(resultSet.getString("apellido"));
                usuario.setTipoIdentificacion(resultSet.getString("tipo_identificacion"));
                usuario.setIdentificacion(resultSet.getString("usuario_ID"));
                usuario.setTipoUsuario(TipoUsuario.valueOf(resultSet.getString("tipo_usuario")));
                usuario.setEmail(resultSet.getString("email"));
                usuario.setCelular(resultSet.getString("celular"));
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener Usuario");
            e.printStackTrace();
        }
        return usuario;
    }

    private boolean UsuarioExisteEnDb(String identificacion) {
        query = "SELECT usuario_ID FROM comercio.usuario WHERE usuario_ID = " + identificacion;
        boolean usuarioExiste = false;

        try (Statement statement = obtenerConexion().createStatement(); ResultSet resultSet =
                statement.executeQuery(query)) {
            usuarioExiste = resultSet.next();
        } catch (SQLException e) {
            System.out.println("Error al verificar si el Usuario existe");
            e.printStackTrace();
        }
        return usuarioExiste;
    }

    // TODO: 22/5/2023 se crea un helper, o se mueve el metodo, para reutilizar en otra clase DAO
    public boolean usuariosEnDbEstaVacia() {
        String query = "SELECT count(*) FROM comercio.usuario";

        try (Statement statement = obtenerConexion().createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                int count = resultSet.getInt("count(*)");
                if (count > 0) {
                    return false;
                }
            }
        } catch (SQLException exception) {
            System.out.println("Error al verificar si la tabla de usuarios esta vacia");
        }
        return true;

    }


}
