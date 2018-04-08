package org.elixir.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DBCon {
	private static String url = "jdbc:mysql://localhost:3306/10_case_analysis?useSSL=false";
	private static String driverName = "com.mysql.cj.jdbc.Driver";
	private static String username = "root";
	private static String password = "123456";

	public static Connection getConnection() {
		Connection con = null;
		try {
			Class.forName(driverName);
			try {
				con = DriverManager.getConnection(url, username, password);
			} catch (SQLException ex) {
				// log an exception. fro example:
				System.out.println("Failed to create the database connection.");
				System.out.println("Exiting ...");
				System.exit(1);
			}
		} catch (ClassNotFoundException ex) {
			// log an exception. for example:
			System.out.println("Driver not found.");
			System.out.println("Exiting ...");
			System.exit(1);
		}
		return con;
	}
}
