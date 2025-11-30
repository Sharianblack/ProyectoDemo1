package dao;

import util.ConexionBDD;
import java.sql.*;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * DAO para gestionar tokens de recuperación de contraseña
 */
public class RecoveryTokenDao {

    // ========================================================================
    // 1. GENERAR TOKEN ÚNICO Y SEGURO
    // ========================================================================
    /**
     * Genera un token aleatorio de 64 caracteres
     */
    private String generarToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[48]; // 48 bytes = 64 caracteres en Base64
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    // ========================================================================
    // 2. CREAR NUEVO TOKEN DE RECUPERACIÓN
    // ========================================================================
    /**
     * Crea un nuevo token de recuperación para un usuario
     * @param userId ID del usuario
     * @param ipSolicitud IP desde donde se solicitó
     * @return El token generado, o null si hay error
     */
    public String crearToken(int userId, String ipSolicitud) {
        Connection conn = null;
        PreparedStatement ps = null;
        String token = null;

        try {
            conn = ConexionBDD.getConnection();

            // Primero, invalidar tokens anteriores del mismo usuario
            String sqlInvalidar = "UPDATE password_recovery_tokens SET usado = 1 " +
                    "WHERE id_usuario = ? AND usado = 0";
            ps = conn.prepareStatement(sqlInvalidar);
            ps.setInt(1, userId);
            ps.executeUpdate();
            ps.close();

            // Generar nuevo token
            token = generarToken();

            // Crear nuevo registro de token (expira en 1 hora)
            String sqlCrear = "INSERT INTO password_recovery_tokens " +
                    "(id_usuario, token, fecha_expiracion, ip_solicitud) " +
                    "VALUES (?, ?, DATE_ADD(NOW(), INTERVAL 1 HOUR), ?)";
            ps = conn.prepareStatement(sqlCrear);
            ps.setInt(1, userId);
            ps.setString(2, token);
            ps.setString(3, ipSolicitud);

            int result = ps.executeUpdate();

            if (result > 0) {
                System.out.println("✓ Token creado para usuario ID: " + userId);
                return token;
            }

        } catch (SQLException e) {
            System.err.println("✗ Error al crear token");
            e.printStackTrace();
        } finally {
            cerrarRecursos(ps, conn);
        }

        return null;
    }

    // ========================================================================
    // 3. VALIDAR TOKEN
    // ========================================================================
    /**
     * Valida que un token sea válido (no usado, no expirado)
     * @param token Token a validar
     * @return ID del usuario si es válido, -1 si no es válido
     */
    public int validarToken(String token) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int userId = -1;

        try {
            conn = ConexionBDD.getConnection();

            String sql = "SELECT id_usuario FROM password_recovery_tokens " +
                    "WHERE token = ? AND usado = 0 AND fecha_expiracion > NOW()";
            ps = conn.prepareStatement(sql);
            ps.setString(1, token);
            rs = ps.executeQuery();

            if (rs.next()) {
                userId = rs.getInt("id_usuario");
                System.out.println("✓ Token válido para usuario ID: " + userId);
            } else {
                System.out.println("✗ Token inválido o expirado");
            }

        } catch (SQLException e) {
            System.err.println("✗ Error al validar token");
            e.printStackTrace();
        } finally {
            cerrarRecursos(rs, ps, conn);
        }

        return userId;
    }

    // ========================================================================
    // 4. MARCAR TOKEN COMO USADO
    // ========================================================================
    /**
     * Marca un token como usado después de restablecer la contraseña
     */
    public boolean marcarTokenUsado(String token) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean success = false;

        try {
            conn = ConexionBDD.getConnection();

            String sql = "UPDATE password_recovery_tokens SET usado = 1 WHERE token = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, token);

            int result = ps.executeUpdate();
            success = (result > 0);

            if (success) {
                System.out.println("✓ Token marcado como usado");
            }

        } catch (SQLException e) {
            System.err.println("✗ Error al marcar token como usado");
            e.printStackTrace();
        } finally {
            cerrarRecursos(null, ps, conn);
        }

        return success;
    }

    // ========================================================================
    // 5. LIMPIAR TOKENS EXPIRADOS
    // ========================================================================
    /**
     * Elimina tokens expirados o ya usados (mantenimiento)
     * Se puede ejecutar periódicamente
     */
    public int limpiarTokensExpirados() {
        Connection conn = null;
        PreparedStatement ps = null;
        int eliminados = 0;

        try {
            conn = ConexionBDD.getConnection();

            String sql = "DELETE FROM password_recovery_tokens " +
                    "WHERE fecha_expiracion < NOW() OR usado = 1";
            ps = conn.prepareStatement(sql);

            eliminados = ps.executeUpdate();

            if (eliminados > 0) {
                System.out.println("✓ Se eliminaron " + eliminados + " tokens expirados/usados");
            }

        } catch (SQLException e) {
            System.err.println("✗ Error al limpiar tokens");
            e.printStackTrace();
        } finally {
            cerrarRecursos(null, ps, conn);
        }

        return eliminados;
    }

    // ========================================================================
    // 6. OBTENER INFO DEL TOKEN (para debugging)
    // ========================================================================
    /**
     * Obtiene información detallada de un token (para debugging)
     */
    public void infoToken(String token) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConexionBDD.getConnection();

            String sql = "SELECT t.*, u.Nombre, u.Correo " +
                    "FROM password_recovery_tokens t " +
                    "JOIN usuarios u ON t.id_usuario = u.id_usuario " +
                    "WHERE t.token = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, token);
            rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println("=== INFO DEL TOKEN ===");
                System.out.println("Usuario: " + rs.getString("Nombre"));
                System.out.println("Correo: " + rs.getString("Correo"));
                System.out.println("Creado: " + rs.getTimestamp("fecha_creacion"));
                System.out.println("Expira: " + rs.getTimestamp("fecha_expiracion"));
                System.out.println("Usado: " + (rs.getBoolean("usado") ? "Sí" : "No"));
                System.out.println("IP: " + rs.getString("ip_solicitud"));
            } else {
                System.out.println("Token no encontrado");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cerrarRecursos(rs, ps, conn);
        }
    }

    // ========================================================================
    // MÉTODO AUXILIAR PARA CERRAR RECURSOS
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