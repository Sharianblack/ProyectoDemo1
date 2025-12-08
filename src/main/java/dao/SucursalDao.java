package dao;

import model.Sucursal;
import util.ConexionBDD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SucursalDao {

    // ========================================================================
    // 1. LISTAR TODAS LAS SUCURSALES
    // ========================================================================
    public List<Sucursal> listarTodas() {
        List<Sucursal> sucursales = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConexionBDD.getConnection();
            String sql = "SELECT id_sucursal, nombre, direccion, telefono, correo, ciudad, " +
                    "horario_apertura, horario_cierre, activo, fecha_registro " +
                    "FROM Sucursales ORDER BY nombre ASC";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Sucursal sucursal = new Sucursal();
                sucursal.setId(rs.getInt("id_sucursal"));
                sucursal.setNombre(rs.getString("nombre"));
                sucursal.setDireccion(rs.getString("direccion"));
                sucursal.setTelefono(rs.getString("telefono"));
                sucursal.setCorreo(rs.getString("correo"));
                sucursal.setCiudad(rs.getString("ciudad"));
                sucursal.setHorarioApertura(rs.getTime("horario_apertura"));
                sucursal.setHorarioCierre(rs.getTime("horario_cierre"));
                sucursal.setActivo(rs.getBoolean("activo"));
                sucursal.setFechaRegistro(rs.getTimestamp("fecha_registro"));
                sucursales.add(sucursal);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar sucursales: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cerrarRecursos(rs, ps, conn);
        }
        return sucursales;
    }

    // ========================================================================
    // 2. OBTENER SUCURSAL POR ID
    // ========================================================================
    public Sucursal obtenerPorId(int id) {
        Sucursal sucursal = null;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConexionBDD.getConnection();
            String sql = "SELECT id_sucursal, nombre, direccion, telefono, correo, ciudad, " +
                    "horario_apertura, horario_cierre, activo, fecha_registro " +
                    "FROM Sucursales WHERE id_sucursal = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                sucursal = new Sucursal();
                sucursal.setId(rs.getInt("id_sucursal"));
                sucursal.setNombre(rs.getString("nombre"));
                sucursal.setDireccion(rs.getString("direccion"));
                sucursal.setTelefono(rs.getString("telefono"));
                sucursal.setCorreo(rs.getString("correo"));
                sucursal.setCiudad(rs.getString("ciudad"));
                sucursal.setHorarioApertura(rs.getTime("horario_apertura"));
                sucursal.setHorarioCierre(rs.getTime("horario_cierre"));
                sucursal.setActivo(rs.getBoolean("activo"));
                sucursal.setFechaRegistro(rs.getTimestamp("fecha_registro"));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener sucursal: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cerrarRecursos(rs, ps, conn);
        }
        return sucursal;
    }

    // ========================================================================
    // 3. CREAR NUEVA SUCURSAL
    // ========================================================================
    public boolean crear(Sucursal sucursal) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ConexionBDD.getConnection();
            String sql = "INSERT INTO Sucursales (nombre, direccion, telefono, correo, ciudad, " +
                    "horario_apertura, horario_cierre, activo) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            ps = conn.prepareStatement(sql);
            ps.setString(1, sucursal.getNombre());
            ps.setString(2, sucursal.getDireccion());
            ps.setString(3, sucursal.getTelefono());
            ps.setString(4, sucursal.getCorreo());
            ps.setString(5, sucursal.getCiudad());
            ps.setTime(6, sucursal.getHorarioApertura());
            ps.setTime(7, sucursal.getHorarioCierre());
            ps.setBoolean(8, sucursal.isActivo());

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.err.println("Error al crear sucursal: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            cerrarRecursos(null, ps, conn);
        }
    }

    // ========================================================================
    // 4. ACTUALIZAR SUCURSAL
    // ========================================================================
    public boolean actualizar(Sucursal sucursal) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ConexionBDD.getConnection();
            String sql = "UPDATE Sucursales SET nombre = ?, direccion = ?, telefono = ?, " +
                    "correo = ?, ciudad = ?, horario_apertura = ?, horario_cierre = ?, activo = ? " +
                    "WHERE id_sucursal = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, sucursal.getNombre());
            ps.setString(2, sucursal.getDireccion());
            ps.setString(3, sucursal.getTelefono());
            ps.setString(4, sucursal.getCorreo());
            ps.setString(5, sucursal.getCiudad());
            ps.setTime(6, sucursal.getHorarioApertura());
            ps.setTime(7, sucursal.getHorarioCierre());
            ps.setBoolean(8, sucursal.isActivo());
            ps.setInt(9, sucursal.getId());

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar sucursal: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            cerrarRecursos(null, ps, conn);
        }
    }

    // ========================================================================
    // 5. ELIMINAR SUCURSAL
    // ========================================================================
    public boolean eliminar(int id) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ConexionBDD.getConnection();
            String sql = "DELETE FROM Sucursales WHERE id_sucursal = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar sucursal: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            cerrarRecursos(null, ps, conn);
        }
    }

    // ========================================================================
    // 6. CAMBIAR ESTADO (Activar/Desactivar)
    // ========================================================================
    public boolean cambiarEstado(int id, boolean nuevoEstado) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ConexionBDD.getConnection();
            String sql = "UPDATE Sucursales SET activo = ? WHERE id_sucursal = ?";
            ps = conn.prepareStatement(sql);
            ps.setBoolean(1, nuevoEstado);
            ps.setInt(2, id);

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.err.println("Error al cambiar estado: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            cerrarRecursos(null, ps, conn);
        }
    }

    // ========================================================================
    // 7. LISTAR SOLO SUCURSALES ACTIVAS
    // ========================================================================
    public List<Sucursal> listarActivas() {
        List<Sucursal> sucursales = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConexionBDD.getConnection();
            String sql = "SELECT id_sucursal, nombre, direccion, telefono, correo, ciudad, " +
                    "horario_apertura, horario_cierre, activo, fecha_registro " +
                    "FROM Sucursales WHERE activo = 1 ORDER BY nombre ASC";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Sucursal sucursal = new Sucursal();
                sucursal.setId(rs.getInt("id_sucursal"));
                sucursal.setNombre(rs.getString("nombre"));
                sucursal.setDireccion(rs.getString("direccion"));
                sucursal.setTelefono(rs.getString("telefono"));
                sucursal.setCorreo(rs.getString("correo"));
                sucursal.setCiudad(rs.getString("ciudad"));
                sucursal.setHorarioApertura(rs.getTime("horario_apertura"));
                sucursal.setHorarioCierre(rs.getTime("horario_cierre"));
                sucursal.setActivo(rs.getBoolean("activo"));
                sucursal.setFechaRegistro(rs.getTimestamp("fecha_registro"));
                sucursales.add(sucursal);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar sucursales activas: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cerrarRecursos(rs, ps, conn);
        }
        return sucursales;
    }

    // ========================================================================
    // 8. BUSCAR SUCURSALES POR CRITERIO
    // ========================================================================
    public List<Sucursal> buscarSucursales(String criterio) {
        List<Sucursal> sucursales = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConexionBDD.getConnection();
            String sql = "SELECT id_sucursal, nombre, direccion, telefono, correo, ciudad, " +
                    "horario_apertura, horario_cierre, activo, fecha_registro " +
                    "FROM Sucursales WHERE nombre LIKE ? OR ciudad LIKE ? OR direccion LIKE ? " +
                    "ORDER BY nombre ASC";
            ps = conn.prepareStatement(sql);
            String searchPattern = "%" + criterio + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);
            rs = ps.executeQuery();

            while (rs.next()) {
                Sucursal sucursal = new Sucursal();
                sucursal.setId(rs.getInt("id_sucursal"));
                sucursal.setNombre(rs.getString("nombre"));
                sucursal.setDireccion(rs.getString("direccion"));
                sucursal.setTelefono(rs.getString("telefono"));
                sucursal.setCorreo(rs.getString("correo"));
                sucursal.setCiudad(rs.getString("ciudad"));
                sucursal.setHorarioApertura(rs.getTime("horario_apertura"));
                sucursal.setHorarioCierre(rs.getTime("horario_cierre"));
                sucursal.setActivo(rs.getBoolean("activo"));
                sucursal.setFechaRegistro(rs.getTimestamp("fecha_registro"));
                sucursales.add(sucursal);
            }
            System.out.println("✅ Búsqueda: " + sucursales.size() + " sucursales encontradas");
        } catch (SQLException e) {
            System.err.println("Error al buscar sucursales: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cerrarRecursos(rs, ps, conn);
        }
        return sucursales;
    }

    // ========================================================================
    // CERRAR RECURSOS
    // ========================================================================
    private void cerrarRecursos(ResultSet rs, PreparedStatement ps, Connection conn) {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            System.err.println("Error al cerrar recursos: " + e.getMessage());
        }
    }
}
