package com.cajero.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBUtilMySQL {
    private static Connection conx;

    public static Connection openConnection() {
        if (conx != null) return  conx;
        Properties properties = new Properties();
        try (// un recurso para el bloque try -> closeable | autocloseable
            InputStream inputStream = DBUtilMySQL.class.getClassLoader().getResourceAsStream("db-mysql.properties");
        ) {
            properties.load(inputStream);
            String driver = properties.getProperty("driver");
            String url = properties.getProperty("url");
            String user = properties.getProperty("user");
            String password = properties.getProperty("password");

            Class.forName(driver);
            conx = DriverManager.getConnection(url, user, password);
            System.out.println("Conexión exitosa");
        } catch (IOException | ClassNotFoundException | SQLException e) { // uso de multicatch -> son excepciones especificas con el mismo catch
            System.err.println("Error: " + e.getMessage());
        }
        return conx;
    }

    public static void closeConnection(Connection cnx) {
        if (cnx == null) {
            System.out.println("Conexión ya cerrada");
            return;
        }
        try {
            cnx.close();
            System.out.println("Conexión cerrada exitosamente");
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

}
