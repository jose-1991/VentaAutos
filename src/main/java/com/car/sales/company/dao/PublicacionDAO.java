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

import static com.car.sales.company.dao.OfertaDAO.ejecutarQueryParaModificaciones;
import static com.car.sales.company.dao.UsuarioDAO.convertirUsuario;

public class PublicacionDAO {
    String UPDATE_PUBLICACION = "UPDATE comercio.publicacion ";
    String query;

    private static Connection obtenerConexion() throws SQLException {
        return ConexionDB.obtenerInstancia();
    }

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
        List<Publicacion> publicacionesDeBaja = new ArrayList<>();
        query = "SELECT * FROM comercio.publicacion AS p INNER JOIN comercio.usuario AS u ON p.usuario_id = u" +
                ".identificacion INNER JOIN " +
                "comercio.producto ON p.producto_id = comerico.producto.vin WHERE id NOT IN (SELECT DISTINCT " +
                "publicacion_id " +
                "FROM comercio.oferta) AND" +
                "fecha  NOT BETWEEN DATE_SUB(curdate(), INTERVAL 5 DAY) AND curdate()";
        try (Statement statement = obtenerConexion().createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                Publicacion publicacion = convertirPublicacion(resultSet);
                Usuario vendedor = convertirUsuario(resultSet);
                Vehiculo vehiculo = convertirVehiculo(resultSet);
                publicacion.setVendedor(vendedor);
                publicacion.setProducto(vehiculo);
                publicacionesDeBaja.add(publicacion);
            }
        } catch (SQLException e) {
            throw new DatoInvalidoException("Hubo un error al obtener publicacicones para dar de baja! Intente " +
                    "nuevamente");
        }
        return publicacionesDeBaja;
    }

    public void darDeBajaPublicaciones(List<Publicacion> listaPublicaciones) {
        query = UPDATE_PUBLICACION + "SET esta_disponible_web = ? WHERE id = ?";

        try {
            PreparedStatement statement = obtenerConexion().prepareStatement(query);
            obtenerConexion().setAutoCommit(false);

            for (Publicacion publicacion : listaPublicaciones) {
                statement.setBoolean(1, false);
                statement.setString(2, publicacion.getId().toString());
                statement.addBatch();
            }
            statement.executeBatch();
            obtenerConexion().commit();
            statement.close();
        } catch (SQLException e) {
            throw new DatoInvalidoException("Hubo un error al dar de baja publicaciones! Intente nuevamente");
        }
    }

    private Publicacion convertirPublicacion(ResultSet resultSet) throws SQLException {
        Publicacion publicacion = new Publicacion();
        publicacion.setId(UUID.fromString(resultSet.getString("id")));
        publicacion.setFecha((resultSet.getDate("fecha")).toLocalDate());
        publicacion.setPrecio(resultSet.getDouble("precio"));
        publicacion.setEstaDisponibleEnLaWeb(resultSet.getBoolean("esta_disponible_web"));
        return publicacion;
    }

    private Vehiculo convertirVehiculo(ResultSet resultSet) throws SQLException {
        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setVin(resultSet.getString("vin"));
        vehiculo.setStockNumber(UUID.fromString(resultSet.getString("stock_number")));
        vehiculo.setMarca(resultSet.getString("marca"));
        vehiculo.setModelo(resultSet.getString("modelo"));
        vehiculo.setAnio(resultSet.getInt("anio"));
        return vehiculo;
    }

    public Publicacion obtenerPublicacion(UUID id) {
        Publicacion publicacion = new Publicacion();
        Usuario usuario = new Usuario();
        Vehiculo vehiculo = new Vehiculo();

        query = "SELECT *, o.fecha AS fecha_oferta  FROM comercio.publicacion p LEFT JOIN comercio.oferta o ON  p.id = o.publicacion_id " +
                "WHERE p.id = '" + id + "'";

        try (Statement statement = obtenerConexion().createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                publicacion.setId(UUID.fromString(resultSet.getString("id")));
                usuario.setIdentificacion(resultSet.getString("usuario_id"));
                vehiculo.setVin(resultSet.getString("producto_id"));
                publicacion.setFecha(resultSet.getDate("fecha").toLocalDate());
                publicacion.setPrecio(resultSet.getDouble("precio"));
                publicacion.setEstaDisponibleEnLaWeb(resultSet.getBoolean("esta_disponible_web"));
                publicacion.setVendedor(usuario);
                publicacion.setProducto(vehiculo);
                publicacion.setOfertasCompradores(new ArrayList<>());
                //    2023-06-21T15:13:01.759
                if (resultSet.getDate("fecha_oferta") != null) {
                    Oferta oferta = new Oferta();
                    usuario.setIdentificacion(resultSet.getString("usuario_id"));
                    oferta.setComprador(usuario);
                    oferta.setMontoOferta(resultSet.getDouble("monto_oferta"));
                    oferta.setMontoContraOferta(resultSet.getDouble("monto_contra_oferta"));
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    String dateTime = resultSet.getString("fecha_oferta");
                    LocalDateTime localDateTime = LocalDateTime.parse(dateTime, formatter);
                    oferta.setFechaOferta(localDateTime);
                    publicacion.getOfertasCompradores().add(oferta);
                }
            }
        } catch (SQLException e) {
            throw new DatoInvalidoException("Hubo un error al obtener publicacion! Intente nuevamente");
        }
        return publicacion;
    }

    public List<Object> generico(String query, int numeroColumnas) {
        List<Object> objectList = new ArrayList<>();
        try (Statement statement = obtenerConexion().createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                for (int x = 0; x<numeroColumnas; x++){
                    objectList.add(resultSet.getObject(x));
                }
            }
        } catch (SQLException e) {

        }
        return objectList;
    }

    public static <T> T ejecutarQueryParaSeleccion(String query, Class<T> clazz){
        T obj;
        try (Statement statement = obtenerConexion().createStatement();
             ResultSet resultSet = statement.executeQuery(query))   {
            obj = clazz.newInstance();
            while (resultSet.next()){
                for (Method method: clazz.getDeclaredMethods()){
                    if (method.getName().startsWith("set")){
                        String columna = convertirASnakeCase(method.getName().substring(3));
                        switch (method.getParameterTypes()[0].getName()){
                            case "java.lang.Integer":
                                method.invoke(obj, resultSet.getInt(columna));
                                break;
                            case "java.lang.String":
                                method.invoke(obj, resultSet.getString(columna));
                                break;
                            case "java.time.LocalDateTime":
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                                String dateTime = resultSet.getString("fecha_oferta");
                                LocalDateTime localDateTime = LocalDateTime.parse(dateTime, formatter);
                                method.invoke(obj, localDateTime);
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
            }

        } catch (SQLException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        return obj;
    }

    public static String convertirASnakeCase(String camelCase) {
        String regex = "([a-z])([A-Z]+)";
        String replacement = "$1_$2";
        return camelCase
                .replaceAll(regex, replacement)
                .toLowerCase();


    }

}
