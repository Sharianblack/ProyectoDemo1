package dao;

import util.ConexionBDD;
import java.sql.*;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * DAO para gestionar tokens de verificación de email
 */
public class EmailVerificationTokenDao {

    /**
     * Genera un token aleatorio de 64 caracteres
     */
    private String generarToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[48];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    /**
     * Crea un token de verificación para un email pendiente
     * @param email Email a verificar
     * @param nombre Nombre del usuario
     * @param passwordHash Contraseña hasheada
     * @param rol Rol del usuario
     * @param telefono Teléfono
     * @param direccion Dirección
     * @param ipSolicitud IP desde donde se registró
     * @return El token generado, o null si hay error
     */
    public String crearTokenVerificacion(String email, String nombre, String passwordHash,
                                         String rol, String telefono, String direccion,
                                         String ipSolicitud) {
        Connection conn = null;
        PreparedStatement ps = null;
        String token = null;

        try {
            conn = ConexionBDD.getConnection();

            // Primero, eliminar tokens anteriores del mismo email
            String sqlInvalidar = "DELETE FROM email_verification_tokens WHERE email = ?";
            ps = conn.prepareStatement(sqlInvalidar);
            ps.setString(1, email);
            ps.executeUpdate();
            ps.close();

            // Generar nuevo token
            token = generarToken();

            // Crear nuevo registro de token (expira en 24 horas)
            String sqlCrear = "INSERT INTO email_verification_tokens " +
                    "(email, nombre, password_hash, rol, telefono, direccion, token, " +
                    "fecha_expiracion, ip_solicitud) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, DATE_ADD(NOW(), INTERVAL 24 HOUR), ?)";

            ps = conn.prepareStatement(sqlCrear);
            ps.setString(1, email);
            ps.setString(2, nombre);
            ps.setString(3, passwordHash);
            ps.setString(4, rol);
            ps.setString(5, telefono);
            ps.setString(6, direccion);
            ps.setString(7, token);
            ps.setString(8, ipSolicitud);

            int result = ps.executeUpdate();

            if (result > 0) {
                System.out.println("✅ Token de verificación creado para: " + email);
                return token;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al crear token de verificación");
            e.printStackTrace();
        } finally {
            cerrarRecursos(ps, conn);
        }

        return null;
    }

    /**
     * Valida un token y retorna los datos del usuario pendiente
     * @param token Token a validar
     * @return Array con datos [email, nombre, password_hash, rol, telefono, direccion]
     *         o null si es inválido
     */
    public String[] validarYObtenerDatos(String token) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String[] datos = null;

        try {
            conn = ConexionBDD.getConnection();

            String sql = "SELECT email, nombre, password_hash, rol, telefono, direccion " +
                    "FROM email_verification_tokens " +
                    "WHERE token = ? AND usado = 0 AND fecha_expiracion > NOW()";

            ps = conn.prepareStatement(sql);
            ps.setString(1, token);
            rs = ps.executeQuery();

            if (rs.next()) {
                datos = new String[6];
                datos[0] = rs.getString("email");
                datos[1] = rs.getString("nombre");
                datos[2] = rs.getString("password_hash");
                datos[3] = rs.getString("rol");
                datos[4] = rs.getString("telefono");
                datos[5] = rs.getString("direccion");

                System.out.println("✅ Token válido para: " + datos[0]);
            } else {
                System.out.println("❌ Token inválido o expirado");
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al validar token");
            e.printStackTrace();
        } finally {
            cerrarRecursos(rs, ps, conn);
        }

        return datos;
    }

    /**
     * Marca un token como usado después de crear el usuario
     */
    public boolean marcarTokenUsado(String token) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean success = false;

        try {
            conn = ConexionBDD.getConnection();

            String sql = "UPDATE email_verification_tokens SET usado = 1 WHERE token = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, token);

            int result = ps.executeUpdate();
            success = (result > 0);

            if (success) {
                System.out.println("✅ Token marcado como usado");
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al marcar token como usado");
            e.printStackTrace();
        } finally {
            cerrarRecursos(null, ps, conn);
        }

        return success;
    }

    /**
     * Elimina tokens expirados o ya usados (mantenimiento)
     */
    public int limpiarTokensExpirados() {
        Connection conn = null;
        PreparedStatement ps = null;
        int eliminados = 0;

        try {
            conn = ConexionBDD.getConnection();

            String sql = "DELETE FROM email_verification_tokens " +
                    "WHERE fecha_expiracion < NOW() OR usado = 1";
            ps = conn.prepareStatement(sql);

            eliminados = ps.executeUpdate();

            if (eliminados > 0) {
                System.out.println("✅ Se eliminaron " + eliminados + " tokens expirados/usados");
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al limpiar tokens");
            e.printStackTrace();
        } finally {
            cerrarRecursos(null, ps, conn);
        }

        return eliminados;
    }

    /**
     * Verifica si un email ya tiene un token pendiente
     */
    public boolean tienTokenPendiente(String email) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean tiene = false;

        try {
            conn = ConexionBDD.getConnection();

            String sql = "SELECT id_token FROM email_verification_tokens " +
                    "WHERE email = ? AND usado = 0 AND fecha_expiracion > NOW()";

            ps = conn.prepareStatement(sql);
            ps.setString(1, email);
            rs = ps.executeQuery();

            tiene = rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cerrarRecursos(rs, ps, conn);
        }

        return tiene;
    }

    // ========================================================================
    // MÉTODOS AUXILIARES
    // ========================================================================
    private void cerrarRecursos(ResultSet rs, PreparedStatement ps, Connection conn) {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void cerrarRecursos(PreparedStatement ps, Connection conn) {
        cerrarRecursos(null, ps, conn);
    }
}