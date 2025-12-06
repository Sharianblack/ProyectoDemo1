package dao;

import model.Cita;
import util.ConexionBDD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para gestionar citas veterinarias
 */
public class CitaDao {

    // ========================================================================
    // 1. OBTENER TODAS LAS CITAS DE UN VETERINARIO
    // ========================================================================
    /**
     * Obtiene todas las citas de un veterinario con información completa
     * @param idVeterinario ID del veterinario
     * @return Lista de citas con toda la información
     */
    public List<Cita> obtenerCitasVeterinario(int idVeterinario) {
        List<Cita> citas = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConexionBDD.getConnection();

            String sql = "SELECT " +
                    "c.id_cita, c.id_mascota, c.id_veterinario, c.id_sucursal, " +
                    "c.fecha_cita, c.estado, c.observaciones, " +
                    "m.nombre AS nombre_mascota, m.especie AS especie_mascota, " +
                    "u.Nombre AS nombre_cliente, u.Correo AS correo_cliente, u.Telefono AS telefono_cliente, " +
                    "s.nombre AS nombre_sucursal " +
                    "FROM citas c " +
                    "INNER JOIN mascotas m ON c.id_mascota = m.id_mascota " +
                    "INNER JOIN clientes cl ON m.id_cliente = cl.id_cliente " +
                    "INNER JOIN usuarios u ON cl.id_usuario = u.id_usuario " +
                    "INNER JOIN sucursales s ON c.id_sucursal = s.id_sucursal " +
                    "WHERE c.id_veterinario = ? " +
                    "ORDER BY c.fecha_cita DESC";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, idVeterinario);
            rs = ps.executeQuery();

            while (rs.next()) {
                Cita cita = new Cita();

                // Datos de la cita
                cita.setIdCita(rs.getInt("id_cita"));
                cita.setIdMascota(rs.getInt("id_mascota"));
                cita.setIdVeterinario(rs.getInt("id_veterinario"));
                cita.setIdSucursal(rs.getInt("id_sucursal"));
                cita.setFechaCita(rs.getTimestamp("fecha_cita"));
                cita.setEstado(rs.getString("estado"));
                cita.setObservaciones(rs.getString("observaciones"));

                // Información relacionada
                cita.setNombreMascota(rs.getString("nombre_mascota"));
                cita.setEspecieMascota(rs.getString("especie_mascota"));
                cita.setNombreCliente(rs.getString("nombre_cliente"));
                cita.setCorreoCliente(rs.getString("correo_cliente"));
                cita.setTelefonoCliente(rs.getString("telefono_cliente"));
                cita.setNombreSucursal(rs.getString("nombre_sucursal"));

                citas.add(cita);
            }

            System.out.println("✓ Se obtuvieron " + citas.size() + " citas para veterinario ID: " + idVeterinario);

        } catch (SQLException e) {
            System.err.println("✗ Error al obtener citas del veterinario");
            e.printStackTrace();
        } finally {
            cerrarRecursos(rs, ps, conn);
        }

        return citas;
    }

    // ========================================================================
    // 2. OBTENER CITAS DEL DÍA DE UN VETERINARIO
    // ========================================================================
    /**
     * Obtiene las citas de hoy de un veterinario
     */
    public List<Cita> obtenerCitasHoyVeterinario(int idVeterinario) {
        List<Cita> citas = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConexionBDD.getConnection();

            String sql = "SELECT " +
                    "c.id_cita, c.id_mascota, c.id_veterinario, c.id_sucursal, " +
                    "c.fecha_cita, c.estado, c.observaciones, " +
                    "m.nombre AS nombre_mascota, m.especie AS especie_mascota, " +
                    "u.Nombre AS nombre_cliente, u.Correo AS correo_cliente, u.Telefono AS telefono_cliente, " +
                    "s.nombre AS nombre_sucursal " +
                    "FROM citas c " +
                    "INNER JOIN mascotas m ON c.id_mascota = m.id_mascota " +
                    "INNER JOIN clientes cl ON m.id_cliente = cl.id_cliente " +
                    "INNER JOIN usuarios u ON cl.id_usuario = u.id_usuario " +
                    "INNER JOIN sucursales s ON c.id_sucursal = s.id_sucursal " +
                    "WHERE c.id_veterinario = ? " +
                    "AND DATE(c.fecha_cita) = CURDATE() " +
                    "ORDER BY c.fecha_cita ASC";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, idVeterinario);
            rs = ps.executeQuery();

            while (rs.next()) {
                Cita cita = mapearCita(rs);
                citas.add(cita);
            }

            System.out.println("✓ Se obtuvieron " + citas.size() + " citas de hoy para veterinario ID: " + idVeterinario);

        } catch (SQLException e) {
            System.err.println("✗ Error al obtener citas de hoy");
            e.printStackTrace();
        } finally {
            cerrarRecursos(rs, ps, conn);
        }

        return citas;
    }

    // ========================================================================
    // 3. OBTENER CITAS POR ESTADO
    // ========================================================================
    /**
     * Obtiene citas de un veterinario filtradas por estado
     */
    public List<Cita> obtenerCitasPorEstado(int idVeterinario, String estado) {
        List<Cita> citas = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConexionBDD.getConnection();

            String sql = "SELECT " +
                    "c.id_cita, c.id_mascota, c.id_veterinario, c.id_sucursal, " +
                    "c.fecha_cita, c.estado, c.observaciones, " +
                    "m.nombre AS nombre_mascota, m.especie AS especie_mascota, " +
                    "u.Nombre AS nombre_cliente, u.Correo AS correo_cliente, u.Telefono AS telefono_cliente, " +
                    "s.nombre AS nombre_sucursal " +
                    "FROM citas c " +
                    "INNER JOIN mascotas m ON c.id_mascota = m.id_mascota " +
                    "INNER JOIN clientes cl ON m.id_cliente = cl.id_cliente " +
                    "INNER JOIN usuarios u ON cl.id_usuario = u.id_usuario " +
                    "INNER JOIN sucursales s ON c.id_sucursal = s.id_sucursal " +
                    "WHERE c.id_veterinario = ? AND c.estado = ? " +
                    "ORDER BY c.fecha_cita DESC";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, idVeterinario);
            ps.setString(2, estado);
            rs = ps.executeQuery();

            while (rs.next()) {
                Cita cita = mapearCita(rs);
                citas.add(cita);
            }

        } catch (SQLException e) {
            System.err.println("✗ Error al obtener citas por estado");
            e.printStackTrace();
        } finally {
            cerrarRecursos(rs, ps, conn);
        }

        return citas;
    }

    // ========================================================================
    // 4. ACTUALIZAR ESTADO DE CITA
    // ========================================================================
    /**
     * Actualiza el estado de una cita
     */
    public boolean actualizarEstadoCita(int idCita, String nuevoEstado) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean success = false;

        try {
            conn = ConexionBDD.getConnection();

            String sql = "UPDATE citas SET estado = ? WHERE id_cita = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, nuevoEstado);
            ps.setInt(2, idCita);

            int result = ps.executeUpdate();
            success = (result > 0);

            if (success) {
                System.out.println("✓ Estado de cita actualizado: ID " + idCita + " -> " + nuevoEstado);
            }

        } catch (SQLException e) {
            System.err.println("✗ Error al actualizar estado de cita");
            e.printStackTrace();
        } finally {
            cerrarRecursos(null, ps, conn);
        }

        return success;
    }

    // ========================================================================
    // 5. ACTUALIZAR OBSERVACIONES DE CITA
    // ========================================================================
    /**
     * Actualiza las observaciones de una cita
     */
    public boolean actualizarObservaciones(int idCita, String observaciones) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean success = false;

        try {
            conn = ConexionBDD.getConnection();

            String sql = "UPDATE citas SET observaciones = ? WHERE id_cita = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, observaciones);
            ps.setInt(2, idCita);

            int result = ps.executeUpdate();
            success = (result > 0);

            if (success) {
                System.out.println("✓ Observaciones actualizadas para cita ID: " + idCita);
            }

        } catch (SQLException e) {
            System.err.println("✗ Error al actualizar observaciones");
            e.printStackTrace();
        } finally {
            cerrarRecursos(null, ps, conn);
        }

        return success;
    }

    // ========================================================================
    // 6. OBTENER UNA CITA POR ID
    // ========================================================================
    /**
     * Obtiene los detalles de una cita específica
     */
    public Cita obtenerCitaPorId(int idCita) {
        Cita cita = null;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConexionBDD.getConnection();

            String sql = "SELECT " +
                    "c.id_cita, c.id_mascota, c.id_veterinario, c.id_sucursal, " +
                    "c.fecha_cita, c.estado, c.observaciones, " +
                    "m.nombre AS nombre_mascota, m.especie AS especie_mascota, " +
                    "u.Nombre AS nombre_cliente, u.Correo AS correo_cliente, u.Telefono AS telefono_cliente, " +
                    "s.nombre AS nombre_sucursal " +
                    "FROM citas c " +
                    "INNER JOIN mascotas m ON c.id_mascota = m.id_mascota " +
                    "INNER JOIN clientes cl ON m.id_cliente = cl.id_cliente " +
                    "INNER JOIN usuarios u ON cl.id_usuario = u.id_usuario " +
                    "INNER JOIN sucursales s ON c.id_sucursal = s.id_sucursal " +
                    "WHERE c.id_cita = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, idCita);
            rs = ps.executeQuery();

            if (rs.next()) {
                cita = mapearCita(rs);
            }

        } catch (SQLException e) {
            System.err.println("✗ Error al obtener cita por ID");
            e.printStackTrace();
        } finally {
            cerrarRecursos(rs, ps, conn);
        }

        return cita;
    }

    // ========================================================================
    // 7. CREAR NUEVA CITA
    // ========================================================================
    /**
     * Crea una nueva cita en el sistema
     */
    public boolean crearCita(Cita cita) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean success = false;

        try {
            conn = ConexionBDD.getConnection();

            String sql = "INSERT INTO citas (id_mascota, id_veterinario, id_sucursal, fecha_cita, estado, observaciones) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, cita.getIdMascota());
            ps.setInt(2, cita.getIdVeterinario());
            ps.setInt(3, cita.getIdSucursal());
            ps.setTimestamp(4, cita.getFechaCita());
            ps.setString(5, cita.getEstado());
            ps.setString(6, cita.getObservaciones());

            int result = ps.executeUpdate();
            success = (result > 0);

            if (success) {
                System.out.println("✓ Cita creada exitosamente");
            }

        } catch (SQLException e) {
            System.err.println("✗ Error al crear cita");
            e.printStackTrace();
        } finally {
            cerrarRecursos(null, ps, conn);
        }

        return success;
    }

    // ========================================================================
    // 8. ACTUALIZAR CITA COMPLETA
    // ========================================================================
    /**
     * Actualiza todos los datos de una cita
     */
    public boolean actualizarCita(Cita cita) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean success = false;

        try {
            conn = ConexionBDD.getConnection();

            String sql = "UPDATE citas SET id_mascota = ?, id_veterinario = ?, id_sucursal = ?, " +
                    "fecha_cita = ?, estado = ?, observaciones = ? WHERE id_cita = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, cita.getIdMascota());
            ps.setInt(2, cita.getIdVeterinario());
            ps.setInt(3, cita.getIdSucursal());
            ps.setTimestamp(4, cita.getFechaCita());
            ps.setString(5, cita.getEstado());
            ps.setString(6, cita.getObservaciones());
            ps.setInt(7, cita.getIdCita());

            int result = ps.executeUpdate();
            success = (result > 0);

            if (success) {
                System.out.println("✓ Cita actualizada: ID " + cita.getIdCita());
            }

        } catch (SQLException e) {
            System.err.println("✗ Error al actualizar cita");
            e.printStackTrace();
        } finally {
            cerrarRecursos(null, ps, conn);
        }

        return success;
    }

    // ========================================================================
    // 9. ELIMINAR CITA (Borrado físico)
    // ========================================================================
    /**
     * Elimina permanentemente una cita de la base de datos
     */
    public boolean eliminarCita(int idCita) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean success = false;

        try {
            conn = ConexionBDD.getConnection();

            String sql = "DELETE FROM citas WHERE id_cita = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, idCita);

            int result = ps.executeUpdate();
            success = (result > 0);

            if (success) {
                System.out.println("✓ Cita eliminada: ID " + idCita);
            }

        } catch (SQLException e) {
            System.err.println("✗ Error al eliminar cita");
            e.printStackTrace();
        } finally {
            cerrarRecursos(null, ps, conn);
        }

        return success;
    }

    // ========================================================================
    // 10. OBTENER TODAS LAS MASCOTAS DE UN CLIENTE
    // ========================================================================
    /**
     * Obtiene todas las mascotas de un cliente específico
     */
    public List<String[]> obtenerMascotasCliente(int idCliente) {
        List<String[]> mascotas = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConexionBDD.getConnection();

            String sql = "SELECT id_mascota, nombre, especie, raza FROM mascotas WHERE id_cliente = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, idCliente);
            rs = ps.executeQuery();

            while (rs.next()) {
                String[] mascota = new String[4];
                mascota[0] = String.valueOf(rs.getInt("id_mascota"));
                mascota[1] = rs.getString("nombre");
                mascota[2] = rs.getString("especie");
                mascota[3] = rs.getString("raza");
                mascotas.add(mascota);
            }

        } catch (SQLException e) {
            System.err.println("✗ Error al obtener mascotas del cliente");
            e.printStackTrace();
        } finally {
            cerrarRecursos(rs, ps, conn);
        }

        return mascotas;
    }

    // ========================================================================
    // 11. OBTENER TODOS LOS CLIENTES CON SUS MASCOTAS
    // ========================================================================
    /**
     * Obtiene lista de todos los clientes que tienen mascotas
     */
    public List<String[]> obtenerClientesConMascotas() {
        List<String[]> clientes = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConexionBDD.getConnection();

            String sql = "SELECT DISTINCT c.id_cliente, u.Nombre, u.Correo, u.Telefono, " +
                    "COUNT(m.id_mascota) as num_mascotas " +
                    "FROM clientes c " +
                    "INNER JOIN usuarios u ON c.id_usuario = u.id_usuario " +
                    "LEFT JOIN mascotas m ON c.id_cliente = m.id_cliente " +
                    "WHERE u.activo = 1 " +
                    "GROUP BY c.id_cliente, u.Nombre, u.Correo, u.Telefono " +
                    "ORDER BY u.Nombre";

            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                String[] cliente = new String[5];
                cliente[0] = String.valueOf(rs.getInt("id_cliente"));
                cliente[1] = rs.getString("Nombre");
                cliente[2] = rs.getString("Correo");
                cliente[3] = rs.getString("Telefono");
                cliente[4] = String.valueOf(rs.getInt("num_mascotas"));
                clientes.add(cliente);
            }

        } catch (SQLException e) {
            System.err.println("✗ Error al obtener clientes");
            e.printStackTrace();
        } finally {
            cerrarRecursos(rs, ps, conn);
        }

        return clientes;
    }

    // ========================================================================
    // 12. OBTENER TODAS LAS SUCURSALES
    // ========================================================================
    /**
     * Obtiene lista de todas las sucursales activas
     */
    public List<String[]> obtenerSucursales() {
        List<String[]> sucursales = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConexionBDD.getConnection();

            String sql = "SELECT id_sucursal, nombre, direccion, telefono FROM sucursales ORDER BY nombre";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                String[] sucursal = new String[4];
                sucursal[0] = String.valueOf(rs.getInt("id_sucursal"));
                sucursal[1] = rs.getString("nombre");
                sucursal[2] = rs.getString("direccion");
                sucursal[3] = rs.getString("telefono");
                sucursales.add(sucursal);
            }

        } catch (SQLException e) {
            System.err.println("✗ Error al obtener sucursales");
            e.printStackTrace();
        } finally {
            cerrarRecursos(rs, ps, conn);
        }

        return sucursales;
    }

    // ========================================================================
    // MÉTODOS AUXILIARES
    // ========================================================================

    /**
     * Mapea un ResultSet a un objeto Cita
     */
    private Cita mapearCita(ResultSet rs) throws SQLException {
        Cita cita = new Cita();

        cita.setIdCita(rs.getInt("id_cita"));
        cita.setIdMascota(rs.getInt("id_mascota"));
        cita.setIdVeterinario(rs.getInt("id_veterinario"));
        cita.setIdSucursal(rs.getInt("id_sucursal"));
        cita.setFechaCita(rs.getTimestamp("fecha_cita"));
        cita.setEstado(rs.getString("estado"));
        cita.setObservaciones(rs.getString("observaciones"));
        cita.setNombreMascota(rs.getString("nombre_mascota"));
        cita.setEspecieMascota(rs.getString("especie_mascota"));
        cita.setNombreCliente(rs.getString("nombre_cliente"));
        cita.setCorreoCliente(rs.getString("correo_cliente"));
        cita.setTelefonoCliente(rs.getString("telefono_cliente"));
        cita.setNombreSucursal(rs.getString("nombre_sucursal"));

        return cita;
    }

    /**
     * Cierra recursos de base de datos
     */
    private void cerrarRecursos(ResultSet rs, PreparedStatement ps, Connection conn) {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}