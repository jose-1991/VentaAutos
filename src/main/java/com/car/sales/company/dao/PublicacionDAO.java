package com.car.sales.company.dao;

import com.car.sales.company.exceptions.DatoInvalidoException;
import com.car.sales.company.models.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.car.sales.company.dao.ConexionDB.obtenerInstancia;
import static com.car.sales.company.dao.OfertaDAO.ejecutarQueryParaModificaciones;

public class PublicacionDAO {
    String UPDATE_PUBLICACION = "UPDATE comercio.publicacion ";
    String query;

    public void registrarPublicacionProducto(Publicacion publicacion) {
        Vehiculo vehiculo = (Vehiculo) publicacion.getProducto();
        query = "INSERT INTO comercio.producto VALUES('" + vehiculo.getVin() + "','" + vehiculo.getStockNumber() + "','" +
                vehiculo.getMarca() + "','" + vehiculo.getModelo() + "','" + vehiculo.getAnio() + "')";
        ejecutarQueryParaModificaciones(query);

        query = "INSERT INTO comercio.publicacion VALUES('" + publicacion.getId() + "','" + publicacion.getVendedor().getIdentificacion() +
                "','" + vehiculo.getVin() + "','" + publicacion.getFecha() + "','" + publicacion.getPrecio() + "','1')";
        ejecutarQueryParaModificaciones(query);
    }

    public void rePublicarProducto(UUID id, double precio) {
        query = UPDATE_PUBLICACION + "SET esta_disponible_web = 1, precio = '" + precio + "',fecha = '" + LocalDate.now() + "' WHERE id = '" + id + "'";

        ejecutarQueryParaModificaciones(query);
    }

    public List<Publicacion> obtenerPublicacionesParaDarDeBaja() {
        query = "SELECT * FROM comercio.publicacion AS p INNER JOIN comercio.usuario AS u ON p.usuario_id = u" +
                ".identificacion INNER JOIN " +
                "comercio.producto ON p.producto_id = producto.vin WHERE id NOT IN (SELECT DISTINCT " +
                "publicacion_id " +
                "FROM comercio.oferta) AND " +
                "fecha  NOT BETWEEN DATE_SUB(curdate(), INTERVAL 5 DAY) AND curdate()";
        return ejecutarQueryParaSeleccion(query, Publicacion.class);
    }

    public void darDeBajaPublicaciones(List<Publicacion> listaPublicaciones) {
        query = UPDATE_PUBLICACION + "SET esta_disponible_en_web = '0' WHERE id = ?";
        ejecutarQueriesConBatch(listaPublicaciones, query);
    }

    private Statement agregarAlBatch(String query, int numeroQueries) {
        try (Statement statement = obtenerInstancia().createStatement()){
            int contador = 0;
            obtenerInstancia().setAutoCommit(false);
            statement.addBatch(query);
            if (contador == numeroQueries){

            }
            return statement;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static<T> void ejecutarQueriesConBatch(List<T> elementos, String query) {
       try (PreparedStatement statement = obtenerInstancia().prepareStatement(query)){
           obtenerInstancia().setAutoCommit(false);
           Method method;
           for (T obj: elementos){
               method = obj.getClass().getDeclaredMethod("getId");
               statement.setString(1,method.invoke(obj).toString());
               statement.addBatch();
           }
           statement.executeBatch();
           obtenerInstancia().commit();
           obtenerInstancia().setAutoCommit(true);
           obtenerInstancia().close();
       } catch (SQLException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
           throw new RuntimeException(e);
       }
    }

    public Publicacion obtenerPublicacion(UUID id) {

        query = "SELECT *, o.fecha AS fecha_oferta  FROM comercio.publicacion p LEFT JOIN comercio.oferta o ON  p.id = o.publicacion_id " +
                "WHERE p.id = '" + id + "'";

        List<Publicacion> listaPublicaciones = ejecutarQueryParaSeleccion(query, Publicacion.class);

        return listaPublicaciones.get(0);
    }

    public static <T> List<T> ejecutarQueryParaSeleccion(String query, Class<T> clazz) {
        try {
            ResultSet resultSet = ejecutarQuery(query);
            return convertirAListaDeObjetos(resultSet, clazz);
        } catch (SQLException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> List<T> convertirAListaDeObjetos(ResultSet resultSet, Class<T> clazz) throws InstantiationException
            , IllegalAccessException, SQLException, InvocationTargetException{
        List<T> listaObjetos = new ArrayList<>();
        do {
            T obj = clazz.newInstance();
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.getName().startsWith("set")) {
                    String columna = convertirASnakeCase(method.getName().substring(3));
                    switch (method.getParameterTypes()[0].getName()) {
                        case "java.lang.String":
                            method.invoke(obj, resultSet.getString(columna));
                            break;
                        case "double":
                            method.invoke(obj, resultSet.getDouble(columna));
                            break;
                        case "java.time.LocalDateTime":
                            method.invoke(obj, convertirALocalDateTime(resultSet.getString("fecha_oferta")));
                            break;
                        case "java.time.LocalDate":
                            method.invoke(obj, resultSet.getDate(columna).toLocalDate());
                            break;
                        case "com.car.sales.company.models.TipoUsuario":
                            method.invoke(obj, TipoUsuario.valueOf(resultSet.getString(columna)));
                            break;
                        case "boolean":
                            method.invoke(obj, resultSet.getBoolean(columna));
                            break;
                        case "java.util.UUID":
                            method.invoke(obj, UUID.fromString(resultSet.getString(columna)));
                            break;
                        case "com.car.sales.company.models.Usuario":
                            method.invoke(obj, convertirAUsuario(resultSet.getString("usuario_id")));
                            break;
                        case "com.car.sales.company.models.Producto":
                            method.invoke(obj, convertirAProducto(resultSet.getString("producto_id")));
                            break;
                        case "int":
                            method.invoke(obj, resultSet.getInt(columna));
                            break;
                    }
                }
            }
            listaObjetos.add(obj);
        } while (resultSet.next());
        return listaObjetos;
    }

    private static Object convertirAProducto(String vin) {
        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setVin(vin);
        return vehiculo;
    }

    private static Object convertirAUsuario(String usuarioId) {
        Usuario usuario = new Usuario();
        usuario.setIdentificacion(usuarioId);
        return usuario;
    }

    private static LocalDateTime convertirALocalDateTime(String dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(dateTime, formatter);

    }

    private static ResultSet ejecutarQuery(String query) throws SQLException {
        Statement statement = obtenerInstancia().createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        if (!resultSet.next()) {
            throw new DatoInvalidoException("No se encontraron resultados");
        }
        return resultSet;

    }

    public static String convertirASnakeCase(String camelCase) {
        String regex = "([a-z])([A-Z]+)";
        String replacement = "$1_$2";
        return camelCase.replaceAll(regex, replacement).toLowerCase();


    }

}
