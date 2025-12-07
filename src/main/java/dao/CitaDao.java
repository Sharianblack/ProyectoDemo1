package dao;

import model.Cita;
import util.ConexionBDD;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para gestionar citas veterinarias
 * VERSIÓN SIMPLIFICADA: Sin tabla veterinarios (FK directo a usuarios)
 */
public class CitaDao {

    // Cache para evitar consultas repetidas
    private Boolean existeTablaClientes = null;

    private boolean tieneTablaClientes(Connection conn) {
        if (existeTablaClientes != null) return existeTablaClientes;
        boolean existe = false;
        ResultSet rs = null;
        try {
            DatabaseMetaData meta = conn.getMetaData();
            rs = meta.getTables(null, null, "clientes", new String[]{"TABLE"});
            existe = rs.next();
        } catch (SQLException e) {
            existe = false;
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException ignored) {}
        }
        existeTablaClientes = existe;
        return existe;
    }

    // ========================================================================
    // 1. OBTENER TODAS LAS CITAS DE UN VETERINARIO
    // ========================================================================
    public List<Cita> obtenerCitasVeterinario(int idVeterinario) {
        List<Cita> citas = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        // Query SIN tabla clientes (fallback directo)
        String sql = "SELECT " +
                "c.id_cita, c.id_mascota, c.id_veterinario, c.id_sucursal, " +
                "c.fecha_cita, c.estado, c.observaciones, " +
                "m.nombre AS nombre_mascota, m.especie AS especie_mascota, " +
                "u.Nombre AS nombre_cliente, u.Correo AS correo_cliente, u.Telefono AS telefono_cliente, " +
                "s.nombre AS nombre_sucursal " +
                "FROM citas c " +
                "INNER JOIN mascotas m ON c.id_mascota = m.id_mascota " +
                "INNER JOIN usuarios u ON m.id_usuario_propietario = u.id_usuario " +
                "INNER JOIN sucursales s ON c.id_sucursal = s.id_sucursal " +
                "WHERE c.id_veterinario = ? " +
                "ORDER BY c.fecha_cita DESC";

        try {
            conn = ConexionBDD.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, idVeterinario);
            rs = ps.executeQuery();

            while (rs.next()) {
                Cita cita = mapearCita(rs);
                citas.add(cita);
            }

            System.out.println("✅ Se obtuvieron " + citas.size() + " citas para veterinario ID: " + idVeterinario);

        } catch (SQLException e) {
            System.err.println("❌ Error al obtener citas del veterinario");
            e.printStackTrace();
        } finally {
            cerrarRecursos(rs, ps, conn);
        }

        return citas;
    }

    // ========================================================================
    // 2. OBTENER CITAS DEL DÍA
    // ========================================================================
    public List<Cita> obtenerCitasHoyVeterinario(int idVeterinario) {
        List<Cita> citas = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String sql = "SELECT " +
                "c.id_cita, c.id_mascota, c.id_veterinario, c.id_sucursal, " +
                "c.fecha_cita, c.estado, c.observaciones, " +
                "m.nombre AS nombre_mascota, m.especie AS especie_mascota, " +
                "u.Nombre AS nombre_cliente, u.Correo AS correo_cliente, u.Telefono AS telefono_cliente, " +
                "s.nombre AS nombre_sucursal " +
                "FROM citas c " +
                "INNER JOIN mascotas m ON c.id_mascota = m.id_mascota " +
                "INNER JOIN usuarios u ON m.id_usuario_propietario = u.id_usuario " +
                "INNER JOIN sucursales s ON c.id_sucursal = s.id_sucursal " +
                "WHERE c.id_veterinario = ? AND DATE(c.fecha_cita) = CURDATE() " +
                "ORDER BY c.fecha_cita ASC";

        try {
            conn = ConexionBDD.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, idVeterinario);
            rs = ps.executeQuery();

            while (rs.next()) {
                Cita cita = mapearCita(rs);
                citas.add(cita);
            }

            System.out.println("✅ Se obtuvieron " + citas.size() + " citas de hoy para veterinario ID: " + idVeterinario);

        } catch (SQLException e) {
            System.err.println("❌ Error al obtener citas de hoy");
            e.printStackTrace();
        } finally {
            cerrarRecursos(rs, ps, conn);
        }

        return citas;
    }

    // ========================================================================
    // 3. OBTENER CITAS POR ESTADO
    // ========================================================================
    public List<Cita> obtenerCitasPorEstado(int idVeterinario, String estado) {
        List<Cita> citas = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String sql = "SELECT " +
                "c.id_cita, c.id_mascota, c.id_veterinario, c.id_sucursal, " +
                "c.fecha_cita, c.estado, c.observaciones, " +
                "m.nombre AS nombre_mascota, m.especie AS especie_mascota, " +
                "u.Nombre AS nombre_cliente, u.Correo AS correo_cliente, u.Telefono AS telefono_cliente, " +
                "s.nombre AS nombre_sucursal " +
                "FROM citas c " +
                "INNER JOIN mascotas m ON c.id_mascota = m.id_mascota " +
                "INNER JOIN usuarios u ON m.id_usuario_propietario = u.id_usuario " +
                "INNER JOIN sucursales s ON c.id_sucursal = s.id_sucursal " +
                "WHERE c.id_veterinario = ? AND c.estado = ? " +
                "ORDER BY c.fecha_cita DESC";

        try {
            conn = ConexionBDD.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, idVeterinario);
            ps.setString(2, estado);
            rs = ps.executeQuery();

            while (rs.next()) {
                Cita cita = mapearCita(rs);
                citas.add(cita);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al obtener citas por estado");
            e.printStackTrace();
        } finally {
            cerrarRecursos(rs, ps, conn);
        }

        return citas;
    }

    // ========================================================================
    // 4. OBTENER UNA CITA POR ID
    // ========================================================================
    public Cita obtenerCitaPorId(int idCita) {
        Cita cita = null;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String sql = "SELECT " +
                "c.id_cita, c.id_mascota, c.id_veterinario, c.id_sucursal, " +
                "c.fecha_cita, c.estado, c.observaciones, " +
                "m.nombre AS nombre_mascota, m.especie AS especie_mascota, " +
                "u.Nombre AS nombre_cliente, u.Correo AS correo_cliente, u.Telefono AS telefono_cliente, " +
                "s.nombre AS nombre_sucursal " +
                "FROM citas c " +
                "INNER JOIN mascotas m ON c.id_mascota = m.id_mascota " +
                "INNER JOIN usuarios u ON m.id_usuario_propietario = u.id_usuario " +
                "INNER JOIN sucursales s ON c.id_sucursal = s.id_sucursal " +
                "WHERE c.id_cita = ?";

        try {
            conn = ConexionBDD.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, idCita);
            rs = ps.executeQuery();

            if (rs.next()) {
                cita = mapearCita(rs);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al obtener cita por ID");
            e.printStackTrace();
        } finally {
            cerrarRecursos(rs, ps, conn);
        }

        return cita;
    }

    // ========================================================================
    // 5. CREAR NUEVA CITA
    // ========================================================================
    public boolean crearCita(Cita cita) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean success = false;

        String sql = "INSERT INTO citas (id_mascota, id_veterinario, id_sucursal, fecha_cita, hora_cita, estado, observaciones) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try {
            conn = ConexionBDD.getConnection();

            String estadoSafe = normalizarEstado(cita.getEstado());
            java.sql.Date fecha = null;
            java.sql.Time hora = null;

            if (cita.getFechaCita() != null) {
                fecha = new java.sql.Date(cita.getFechaCita().getTime());
                hora = new java.sql.Time(cita.getFechaCita().getTime());
            }

            ps = conn.prepareStatement(sql);
            ps.setInt(1, cita.getIdMascota());
            ps.setInt(2, cita.getIdVeterinario()); // 🔥 Ahora es directamente id_usuario
            ps.setInt(3, cita.getIdSucursal());
            if (fecha != null) ps.setDate(4, fecha); else ps.setNull(4, Types.DATE);
            if (hora != null) ps.setTime(5, hora); else ps.setNull(5, Types.TIME);
            ps.setString(6, estadoSafe);
            ps.setString(7, cita.getObservaciones());

            int result = ps.executeUpdate();
            success = (result > 0);

            if (success) {
                System.out.println("✅ Cita creada exitosamente");
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al crear cita: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cerrarRecursos(null, ps, conn);
        }

        return success;
    }

    // ========================================================================
    // 6. ACTUALIZAR ESTADO DE CITA
    // ========================================================================
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
            if (success) System.out.println("✅ Estado de cita actualizado: ID " + idCita + " -> " + nuevoEstado);
        } catch (SQLException e) {
            System.err.println("❌ Error actualizarEstadoCita: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cerrarRecursos(null, ps, conn);
        }
        return success;
    }

    // ========================================================================
    // 7. ACTUALIZAR OBSERVACIONES
    // ========================================================================
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
            if (success) System.out.println("✅ Observaciones actualizadas para cita ID " + idCita);
        } catch (SQLException e) {
            System.err.println("❌ Error actualizarObservaciones: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cerrarRecursos(null, ps, conn);
        }
        return success;
    }

    // ========================================================================
    // 8. ACTUALIZAR CITA COMPLETA
    // ========================================================================
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
                System.out.println("✅ Cita actualizada: ID " + cita.getIdCita());
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar cita");
            e.printStackTrace();
        } finally {
            cerrarRecursos(null, ps, conn);
        }

        return success;
    }

    // ========================================================================
    // 9. ELIMINAR CITA
    // ========================================================================
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
                System.out.println("✅ Cita eliminada: ID " + idCita);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al eliminar cita");
            e.printStackTrace();
        } finally {
            cerrarRecursos(null, ps, conn);
        }

        return success;
    }

    // ========================================================================
    // 10. OBTENER MASCOTAS DE UN CLIENTE
    // ========================================================================
    public List<String[]> obtenerMascotasCliente(int idCliente) {
        List<String[]> mascotas = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConexionBDD.getConnection();
            // Sin tabla clientes, usar directamente id_usuario_propietario
            String sql = "SELECT id_mascota, nombre, especie, raza FROM mascotas WHERE id_usuario_propietario = ?";
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
            System.err.println("❌ Error al obtener mascotas del cliente");
            e.printStackTrace();
        } finally {
            cerrarRecursos(rs, ps, conn);
        }

        return mascotas;
    }

    // ========================================================================
    // 11. OBTENER TODOS LOS CLIENTES CON SUS MASCOTAS
    // ========================================================================
    public List<String[]> obtenerClientesConMascotas() {
        List<String[]> clientes = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConexionBDD.getConnection();

            // Sin tabla clientes, consultar directamente usuarios con rol Cliente
            String sql = "SELECT u.id_usuario, u.Nombre, u.Correo, u.Telefono, " +
                    "COUNT(m.id_mascota) as num_mascotas " +
                    "FROM usuarios u " +
                    "LEFT JOIN mascotas m ON u.id_usuario = m.id_usuario_propietario " +
                    "WHERE u.activo = 1 AND (u.Rol = 'Cliente' OR u.Rol = 'cliente') " +
                    "GROUP BY u.id_usuario, u.Nombre, u.Correo, u.Telefono " +
                    "ORDER BY u.Nombre";

            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                String[] cliente = new String[6];
                cliente[0] = ""; // id_cliente (vacío porque no existe tabla clientes)
                cliente[5] = String.valueOf(rs.getInt("id_usuario"));
                cliente[1] = rs.getString("Nombre");
                cliente[2] = rs.getString("Correo");
                cliente[3] = rs.getString("Telefono");
                cliente[4] = String.valueOf(rs.getInt("num_mascotas"));
                clientes.add(cliente);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al obtener clientes");
            e.printStackTrace();
        } finally {
            cerrarRecursos(rs, ps, conn);
        }

        return clientes;
    }

    // ========================================================================
    // 12. OBTENER TODAS LAS SUCURSALES
    // ========================================================================
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
            System.err.println("❌ Error al obtener sucursales");
            e.printStackTrace();
        } finally {
            cerrarRecursos(rs, ps, conn);
        }

        return sucursales;
    }

    // ========================================================================
    // MÉTODOS AUXILIARES
    // ========================================================================

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

    private void cerrarRecursos(ResultSet rs, PreparedStatement ps, Connection conn) {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String normalizarEstado(String estado) {
        if (estado == null) return "Pendiente";
        String s = estado.trim().toLowerCase();
        if (s.contains("pend")) return "Pendiente";
        if (s.contains("confirm")) return "Confirmada";
        if (s.contains("comp") || s.contains("complet")) return "Completada";
        if (s.contains("cancel")) return "Cancelada";
        if (s.equals("pendiente")) return "Pendiente";
        if (s.equals("confirmada")) return "Confirmada";
        if (s.equals("completada")) return "Completada";
        if (s.equals("cancelada")) return "Cancelada";
        return "Pendiente";
    }
}