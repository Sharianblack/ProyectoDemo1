package util;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBDD {
    private static String url = "jdbc:mysql://localhost:3306/mydb?serverTimezone=UTC";
    private static String username = "root";
    private static String password = "";

    public static Connection getConnection()throws SQLException {
        return DriverManager.getConnection(url,username,password);
    }
    //metodo de prueba
    public static void main (String [] args) {
        try(Connection conn = ConexionBDD.getConnection()){
            if(conn != null) {
                System.out.println("Conexión exitosa a la base de datos");
            }
        }catch(SQLException e){
            System.out.println("Error al conectarse a la bdd: " + e.getMessage());
        }
    }
}