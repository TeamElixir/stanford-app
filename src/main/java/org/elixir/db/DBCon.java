package org.elixir.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBCon {

    private static String driverName = "com.mysql.cj.jdbc.Driver";

    private static String username = "root";

    private static String password = "toor";

    public static Connection getConnection(String dbName) {
        System.out.println("DBName: " + dbName);
        String url = "jdbc:mysql://localhost:3306/" + dbName + "?useSSL=false";
        Connection con = null;
        try {
            Class.forName(driverName);
            try {
                con = DriverManager.getConnection(url, username, password);
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Failed to create the database connection.");
                System.out.println("Exiting ...");
                System.exit(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Driver not found.");
            System.out.println("Exiting ...");
            System.exit(1);
        }
        return con;
    }
}
