package dao;

import model.HistorialClinico;
import util.ConexionBDD;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

public class HistorialClinicoDao {

    // ========================================================================
    // CREAR REGISTRO EN HISTORIAL CLÍNICO
    // ========================================================================
    public boolean crearHistorial(HistorialClinico historial) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean success = false;

        String sql = "INSERT INTO historial_clinico (id_cita, id_mascota, id_veterinario, fecha_consulta, " +
                "motivo_consulta, diagnostico, tratamiento, medicamentos, peso_kg, temperatura_c, " +
                "observaciones, proxima_cita) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            conn = ConexionBDD.getConnection();
            ps = conn.prepareStatement(sql);
            
            ps.setInt(1, historial.getIdCita());
            ps.setInt(2, historial.getIdMascota());
            ps.setInt(3, historial.getIdVeterinario());
            ps.setTimestamp(4, historial.getFechaConsulta());
            ps.setString(5, historial.getMotivoConsulta());
            ps.setString(6, historial.getDiagnostico());
            ps.setString(7, historial.getTratamiento());
            ps.setString(8, historial.getMedicamentos());
            
            if (historial.getPesoKg() != null) {
                ps.setBigDecimal(9, historial.getPesoKg());
            } else {
                ps.setNull(9, Types.DECIMAL);
            }
            
            if (historial.getTemperaturaC() != null) {
                ps.setBigDecimal(10, historial.getTemperaturaC());
            } else {
                ps.setNull(10, Types.DECIMAL);
            }
            
            ps.setString(11, historial.getObservaciones());
            
            if (historial.getProximaCita() != null) {
                ps.setDate(12, historial.getProximaCita());
            } else {
                ps.setNull(12, Types.DATE);
            }

            int result = ps.executeUpdate();
            success = (result > 0);

            if (success) {
                System.out.println("✅ Historial clínico creado exitosamente");
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al crear historial clínico: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cerrarRecursos(null, ps, conn);
        }

        return success;
    }

    // ========================================================================
    // OBTENER HISTORIAL COMPLETO DEL VETERINARIO
    // ========================================================================
    public List<HistorialClinico> obtenerHistorialVeterinario(int idVeterinario) {
        List<HistorialClinico> historiales = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String sql = "SELECT h.*, " +
                "m.nombre AS nombre_mascota, m.especie AS especie_mascota, m.raza AS raza_mascota, " +
                "u.Nombre AS nombre_propietario, " +
                "v.Nombre AS nombre_veterinario " +
                "FROM historial_clinico h " +
                "INNER JOIN mascotas m ON h.id_mascota = m.id_mascota " +
                "INNER JOIN usuarios u ON m.id_usuario_propietario = u.id_usuario " +
                "INNER JOIN usuarios v ON h.id_veterinario = v.id_usuario " +
                "WHERE h.id_veterinario = ? " +
                "ORDER BY h.fecha_consulta DESC";

        try {
            conn = ConexionBDD.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, idVeterinario);
            rs = ps.executeQuery();

            while (rs.next()) {
                HistorialClinico historial = mapearHistorial(rs);
                historiales.add(historial);
            }

            System.out.println("✅ Se obtuvieron " + historiales.size() + " registros del historial");

        } catch (SQLException e) {
            System.err.println("❌ Error al obtener historial del veterinario");
            e.printStackTrace();
        } finally {
            cerrarRecursos(rs, ps, conn);
        }

        return historiales;
    }

    // ========================================================================
    // OBTENER HISTORIAL DE UNA MASCOTA ESPECÍFICA
    // ========================================================================
    public List<HistorialClinico> obtenerHistorialMascota(int idMascota, int idVeterinario) {
        List<HistorialClinico> historiales = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String sql = "SELECT h.*, " +
                "m.nombre AS nombre_mascota, m.especie AS especie_mascota, m.raza AS raza_mascota, " +
                "u.Nombre AS nombre_propietario, " +
                "v.Nombre AS nombre_veterinario " +
                "FROM historial_clinico h " +
                "INNER JOIN mascotas m ON h.id_mascota = m.id_mascota " +
                "INNER JOIN usuarios u ON m.id_usuario_propietario = u.id_usuario " +
                "INNER JOIN usuarios v ON h.id_veterinario = v.id_usuario " +
                "WHERE h.id_mascota = ? AND h.id_veterinario = ? " +
                "ORDER BY h.fecha_consulta DESC";

        try {
            conn = ConexionBDD.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, idMascota);
            ps.setInt(2, idVeterinario);
            rs = ps.executeQuery();

            while (rs.next()) {
                HistorialClinico historial = mapearHistorial(rs);
                historiales.add(historial);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al obtener historial de la mascota");
            e.printStackTrace();
        } finally {
            cerrarRecursos(rs, ps, conn);
        }

        return historiales;
    }

    // ========================================================================
    // OBTENER UN REGISTRO POR ID
    // ========================================================================
    public HistorialClinico obtenerHistorialPorId(int idHistorial) {
        HistorialClinico historial = null;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String sql = "SELECT h.*, " +
                "m.nombre AS nombre_mascota, m.especie AS especie_mascota, m.raza AS raza_mascota, " +
                "u.Nombre AS nombre_propietario, " +
                "v.Nombre AS nombre_veterinario " +
                "FROM historial_clinico h " +
                "INNER JOIN mascotas m ON h.id_mascota = m.id_mascota " +
                "INNER JOIN usuarios u ON m.id_usuario_propietario = u.id_usuario " +
                "INNER JOIN usuarios v ON h.id_veterinario = v.id_usuario " +
                "WHERE h.id_historial = ?";

        try {
            conn = ConexionBDD.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, idHistorial);
            rs = ps.executeQuery();

            if (rs.next()) {
                historial = mapearHistorial(rs);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al obtener historial por ID");
            e.printStackTrace();
        } finally {
            cerrarRecursos(rs, ps, conn);
        }

        return historial;
    }

    // ========================================================================
    // ACTUALIZAR HISTORIAL CLÍNICO
    // ========================================================================
    public boolean actualizarHistorial(HistorialClinico historial) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean success = false;

        String sql = "UPDATE historial_clinico SET motivo_consulta = ?, diagnostico = ?, " +
                "tratamiento = ?, medicamentos = ?, peso_kg = ?, temperatura_c = ?, " +
                "observaciones = ?, proxima_cita = ? WHERE id_historial = ?";

        try {
            conn = ConexionBDD.getConnection();
            ps = conn.prepareStatement(sql);
            
            ps.setString(1, historial.getMotivoConsulta());
            ps.setString(2, historial.getDiagnostico());
            ps.setString(3, historial.getTratamiento());
            ps.setString(4, historial.getMedicamentos());
            
            if (historial.getPesoKg() != null) {
                ps.setBigDecimal(5, historial.getPesoKg());
            } else {
                ps.setNull(5, Types.DECIMAL);
            }
            
            if (historial.getTemperaturaC() != null) {
                ps.setBigDecimal(6, historial.getTemperaturaC());
            } else {
                ps.setNull(6, Types.DECIMAL);
            }
            
            ps.setString(7, historial.getObservaciones());
            
            if (historial.getProximaCita() != null) {
                ps.setDate(8, historial.getProximaCita());
            } else {
                ps.setNull(8, Types.DATE);
            }
            
            ps.setInt(9, historial.getIdHistorial());

            int result = ps.executeUpdate();
            success = (result > 0);

            if (success) {
                System.out.println("✅ Historial actualizado: ID " + historial.getIdHistorial());
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar historial");
            e.printStackTrace();
        } finally {
            cerrarRecursos(null, ps, conn);
        }

        return success;
    }

    // ========================================================================
    // BUSCAR EN HISTORIAL
    // ========================================================================
    public List<HistorialClinico> buscarHistorial(int idVeterinario, String criterio) {
        List<HistorialClinico> historiales = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String sql = "SELECT h.*, " +
                "m.nombre AS nombre_mascota, m.especie AS especie_mascota, m.raza AS raza_mascota, " +
                "u.Nombre AS nombre_propietario, " +
                "v.Nombre AS nombre_veterinario " +
                "FROM historial_clinico h " +
                "INNER JOIN mascotas m ON h.id_mascota = m.id_mascota " +
                "INNER JOIN usuarios u ON m.id_usuario_propietario = u.id_usuario " +
                "INNER JOIN usuarios v ON h.id_veterinario = v.id_usuario " +
                "WHERE h.id_veterinario = ? AND (" +
                "m.nombre LIKE ? OR u.Nombre LIKE ? OR h.diagnostico LIKE ? OR h.motivo_consulta LIKE ?) " +
                "ORDER BY h.fecha_consulta DESC";

        try {
            conn = ConexionBDD.getConnection();
            ps = conn.prepareStatement(sql);
            String searchPattern = "%" + criterio + "%";
            ps.setInt(1, idVeterinario);
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);
            ps.setString(4, searchPattern);
            ps.setString(5, searchPattern);
            rs = ps.executeQuery();

            while (rs.next()) {
                HistorialClinico historial = mapearHistorial(rs);
                historiales.add(historial);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al buscar en historial");
            e.printStackTrace();
        } finally {
            cerrarRecursos(rs, ps, conn);
        }

        return historiales;
    }

    // ========================================================================
    // VERIFICAR SI EXISTE HISTORIAL PARA UNA CITA
    // ========================================================================
    public boolean existeHistorialParaCita(int idCita) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean existe = false;

        String sql = "SELECT COUNT(*) FROM historial_clinico WHERE id_cita = ?";

        try {
            conn = ConexionBDD.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, idCita);
            rs = ps.executeQuery();

            if (rs.next()) {
                existe = rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al verificar existencia de historial");
            e.printStackTrace();
        } finally {
            cerrarRecursos(rs, ps, conn);
        }

        return existe;
    }

    // ========================================================================
    // MÉTODOS AUXILIARES
    // ========================================================================
    private HistorialClinico mapearHistorial(ResultSet rs) throws SQLException {
        HistorialClinico historial = new HistorialClinico();
        
        historial.setIdHistorial(rs.getInt("id_historial"));
        historial.setIdCita(rs.getInt("id_cita"));
        historial.setIdMascota(rs.getInt("id_mascota"));
        historial.setIdVeterinario(rs.getInt("id_veterinario"));
        historial.setFechaConsulta(rs.getTimestamp("fecha_consulta"));
        historial.setMotivoConsulta(rs.getString("motivo_consulta"));
        historial.setDiagnostico(rs.getString("diagnostico"));
        historial.setTratamiento(rs.getString("tratamiento"));
        historial.setMedicamentos(rs.getString("medicamentos"));
        historial.setPesoKg(rs.getBigDecimal("peso_kg"));
        historial.setTemperaturaC(rs.getBigDecimal("temperatura_c"));
        historial.setObservaciones(rs.getString("observaciones"));
        historial.setProximaCita(rs.getDate("proxima_cita"));
        
        // Campos adicionales
        historial.setNombreMascota(rs.getString("nombre_mascota"));
        historial.setEspecieMascota(rs.getString("especie_mascota"));
        historial.setRazaMascota(rs.getString("raza_mascota"));
        historial.setNombrePropietario(rs.getString("nombre_propietario"));
        historial.setNombreVeterinario(rs.getString("nombre_veterinario"));
        
        return historial;
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
}
