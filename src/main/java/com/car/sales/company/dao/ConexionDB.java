package com.car.sales.company.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {

    private static String url = "jdbc:mysql://localhost:3306/comercio";
    private static String nombreUsuario = "root";
    private static String contrasenia = "sasa";
    private static Connection conexion;

    public static Connection obtenerInstancia() throws SQLException {
        if (conexion == null) {
            conexion = DriverManager.getConnection(url, nombreUsuario, contrasenia);
        }
        return conexion;
    }
}
