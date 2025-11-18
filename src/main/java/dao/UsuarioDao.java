package dao;

import model.Usuario;
import util.ConexionBDD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDao {

    // 1. Método para validar el login (CORREGIDO)
    public Usuario validateUser(String username, String password) {
        Usuario user = null;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConexionBDD.getConnection();

            // SQL Corregida: Usa 'Usuarios', 'Correo' y 'PasswordHash'
            String sql = "SELECT id_usuario, Nombre, Correo, Rol FROM Usuarios WHERE Correo = ? AND PasswordHash = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);

            rs = ps.executeQuery();

            if (rs.next()) {
                user = new Usuario();

                // Mapeo Corregido: Usa los nombres de columnas de tu BD
                user.setId(rs.getInt("id_usuario"));
                user.setNombre(rs.getString("Nombre"));  // Nuevo campo de tu BD
                user.setCorreo(rs.getString("Correo"));  // Antes era username/email
                user.setRol(rs.getString("Rol"));        // Nuevo campo de tu BD

                // Nota: Por seguridad, NO SE SETEA la contraseña en texto plano en el objeto aquí.

                System.out.println(" Usuario encontrado: " + username);
            } else {
                System.out.println(" Usuario no encontrado");
            }
        } catch (SQLException e) {
            System.out.println(" Error en validateUser");
            e.printStackTrace();
        } finally {
            // Cierre de recursos
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return user;
    }

    // 2. Método para registrar usuario nuevo (CORREGIDO)
    public boolean registerUser(Usuario user) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean success = false;

        try {
            conn = ConexionBDD.getConnection();
            // SQL Corregida: Usa 'Usuarios' e incluye todos los campos necesarios.
            String sql = "INSERT INTO Usuarios (Nombre, Correo, PasswordHash, Rol, Telefono, Direccion) VALUES (?, ?, ?, ?, ?, ?)";
            ps = conn.prepareStatement(sql);

            // Asumiendo que el objeto Usuario tiene getters para todos estos campos.
            ps.setString(1, user.getNombre());
            ps.setString(2, user.getCorreo());
            ps.setString(3, user.getPasswordHash()); // Usar el hash de la contraseña
            ps.setString(4, user.getRol());
            ps.setString(5, user.getTelefono());
            ps.setString(6, user.getDireccion());

            int result = ps.executeUpdate();
            success = (result > 0);

            if (success) {
                System.out.println(" Usuario registrado: " + user.getCorreo());
            }
        } catch (SQLException e) {
            System.out.println(" Error al registrar usuario");
            e.printStackTrace();
        } finally {
            // Cierre de recursos
            try {
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return success;
    }

    // 3. Método para verificar si un usuario existe (CORREGIDO)
    public boolean userExists(String username) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean exists = false;

        try {
            conn = ConexionBDD.getConnection();
            // SQL Corregida: Usa 'Usuarios' y 'Correo'
            String sql = "SELECT id_usuario FROM Usuarios WHERE Correo = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            rs = ps.executeQuery();
            exists = rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Cierre de recursos
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return exists;
    }
}