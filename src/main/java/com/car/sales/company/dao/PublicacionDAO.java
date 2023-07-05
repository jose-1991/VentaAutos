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
        List<String> listaQueries = new ArrayList<>();
        for (Publicacion publicacion : listaPublicaciones) {
            query = UPDATE_PUBLICACION + "SET esta_disponible_en_web = '0' WHERE id = '" + publicacion.getId() + "'";
            listaQueries.add(query);
        }
        ejecutarQueriesConBatch(listaQueries);
    }

    public static void ejecutarQueriesConBatch(List<String> listaQueries) {
        try (Statement statement = obtenerInstancia().createStatement();) {
            obtenerInstancia().setAutoCommit(false);
            for (String query : listaQueries) {
                statement.addBatch(query);
            }
            statement.executeBatch();
            obtenerInstancia().commit();
            obtenerInstancia().setAutoCommit(true);
            obtenerInstancia().close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Publicacion obtenerPublicacion(UUID id) {

        query = "SELECT *, o.fecha AS fecha_oferta  FROM comercio.publicacion p LEFT JOIN comercio.oferta o ON  p.id = o.publicacion_id " +
                "WHERE p.id = '" + id + "'";

        List<Publicacion> listaPublicaciones = ejecutarQueryParaSeleccion(query, Publicacion.class);

        return listaPublicaciones.get(0);
    }

    public static <T> T ejecutarQueryParaSeleccion1(String query, Class<T> clazz) {
        ResultSet resultSet1 = null;
        try {
            resultSet1 = ejecutarQuery(query);
            return convertirAObjeto(resultSet1, clazz);
        } catch (SQLException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }

    private static <T> T convertirAObjeto(ResultSet resultSet, Class<T> clazz) throws InstantiationException, IllegalAccessException, SQLException, InvocationTargetException {
        T obj = clazz.newInstance();
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getName().startsWith("set")) {
                String columna = convertirASnakeCase(method.getName().substring(3));
                switch (method.getParameterTypes()[0].getName()) {
                    case "java.lang.Integer":
                        method.invoke(obj, resultSet.getInt(columna));
                        break;
                    case "java.lang.String":
                        method.invoke(obj, resultSet.getString(columna));
                        break;
                    case "java.time.LocalDateTime":
                        method.invoke(obj, convertirALocalDateTime(resultSet.getString("fecha_oferta")));
                        break;
                    case "java.lang.Double":
                        method.invoke(obj, resultSet.getDouble(columna));
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
                }
            }
        }
        return obj;
    }

    private static LocalDateTime convertirALocalDateTime(String dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
       return LocalDateTime.parse(dateTime, formatter);

    }

    private static ResultSet ejecutarQuery(String query) throws SQLException {
        Statement statement = obtenerInstancia().createStatement();
        ResultSet resultSet= statement.executeQuery(query);
        if (!resultSet.next()){
            throw new DatoInvalidoException("No se encontraron resultados");
        }
        return resultSet;

    }

    public static <T> List<T> ejecutarQueryParaSeleccion(String query, Class<T> clazz) {

        List<T> listaObjetos = new ArrayList<>();
        try (Statement statement = obtenerInstancia().createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                T obj = clazz.newInstance();
                for (Method method : clazz.getDeclaredMethods()) {
                    if (method.getName().startsWith("set")) {
                        String columna = convertirASnakeCase(method.getName().substring(3));
                        switch (obj.getClass().getName()) {
                            case "com.car.sales.company.models.Usuario":
                                casoUsuario(obj, method, resultSet, columna);
                                break;
                            case "com.car.sales.company.models.Publicacion":
                                casoPublicacion(obj, method, resultSet, columna);
                                break;
                            case "com.car.sales.company.models.Oferta":
                                casoOferta(obj, method, resultSet, columna);
                                break;
                        }
                    }
                }
                listaObjetos.add(obj);
            }
        } catch (SQLException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        return listaObjetos;
    }

    private static <T> void casoUsuario(T usuario, Method method, ResultSet resultSet, String columna) throws SQLException,
            InvocationTargetException, IllegalAccessException {
        switch (method.getParameterTypes()[0].getName()) {
            case "java.lang.String":
                method.invoke(usuario, resultSet.getString(columna));
                break;
            case "boolean":
                method.invoke(usuario, resultSet.getBoolean(columna));
                break;
            case "com.car.sales.company.models.TipoUsuario":
                method.invoke(usuario, TipoUsuario.valueOf(resultSet.getString(columna)));
                break;
        }
    }

    private static <T> void casoPublicacion(T publicacion, Method method, ResultSet resultSet,
                                            String columna) throws SQLException, InvocationTargetException, IllegalAccessException {
        switch (method.getParameterTypes()[0].getName()) {
            case "java.util.UUID":
                method.invoke(publicacion, UUID.fromString(resultSet.getString(columna)));
                break;
            case "com.car.sales.company.models.Usuario":
                columna = "usuario_id";
                Usuario usuario = new Usuario();
                usuario.setIdentificacion(resultSet.getString(columna));
                method.invoke(publicacion, usuario);
                break;
            case "com.car.sales.company.models.Producto":
                columna = "producto_id";
                Vehiculo vehiculo = new Vehiculo();
                vehiculo.setVin(resultSet.getString(columna));
                method.invoke(publicacion, vehiculo);
                break;
            case "java.time.LocalDate":
                method.invoke(publicacion, resultSet.getDate(columna).toLocalDate());
                break;
            case "double":
                method.invoke(publicacion, resultSet.getDouble(columna));
                break;
            case "boolean":
                method.invoke(publicacion, resultSet.getBoolean(columna));
                break;
        }
    }

    private static <T> void casoOferta(T oferta, Method method, ResultSet resultSet, String columna) throws SQLException, InvocationTargetException, IllegalAccessException {
        switch (method.getParameterTypes()[0].getName()) {
            case "java.lang.String":
                method.invoke(oferta, resultSet.getString(columna));
                break;
            case "java.lang.Double":
                method.invoke(oferta, resultSet.getDouble(columna));
                break;
            case "boolean":
                method.invoke(oferta, resultSet.getBoolean(columna));
                break;
            case "java.time.LocalDateTime":
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String dateTime = resultSet.getString("fecha_oferta");
                LocalDateTime localDateTime = LocalDateTime.parse(dateTime, formatter);
                method.invoke(oferta, localDateTime);
                break;
        }
    }

    public static String convertirASnakeCase(String camelCase) {
        String regex = "([a-z])([A-Z]+)";
        String replacement = "$1_$2";
        return camelCase.replaceAll(regex, replacement).toLowerCase();


    }

}
