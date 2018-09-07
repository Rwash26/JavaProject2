/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package softwareii;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author reggie.washington
 */
public class Database {

    private static Connection conn = null;
    private Statement stmt = null;
    private String dbUser = null;
    private String dbPass = null;

    public static Connection getConn() {
        return conn;
    }

    public static ResultSet accessDB() {
        final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
        final String DB_URL = "jdbc:mysql://52.206.157.109/U04cUB";

        //  Database credentials
        final String DBUSER = "U04cUB";
        final String DBPASS = "53688204428";

        boolean res = false;
        ResultSet rs = null;

        try {

            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, DBUSER, DBPASS);

        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
        }

        return rs;
    }

}
