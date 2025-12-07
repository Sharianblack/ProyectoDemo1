package dao;

import model.Mascota;
import util.ConexionBDD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.Period;

public class MascotaDao {

    // ========================================================================
    // 1. LISTAR MASCOTAS DE UN CLIENTE (POR id_usuario)
    // ========================================================================
    public List<Mascota> listarMascotasCliente(int idUsuario) {
        List<Mascota> mascotas = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConexionBDD.getConnection();
            String sql = "SELECT m.*, u.Nombre as nombre_propietario, u.Correo as correo_propietario " +
                    "FROM mascotas m " +
                    "INNER JOIN usuarios u ON m.id_usuario_propietario = u.id_usuario " +
                    "WHERE m.id_usuario_propietario = ? " +
                    "ORDER BY m.nombre";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, idUsuario);
            rs = ps.executeQuery();

            while (rs.next()) {
                Mascota mascota = mapearMascota(rs);
                mascotas.add(mascota);
            }

            System.out.println("✅ Se listaron " + mascotas.size() + " mascotas");

        } catch (SQLException e) {
            System.err.println("❌ Error al listar mascotas");
            e.printStackTrace();
        } finally {
            cerrarRecursos(rs, ps, conn);
        }

        return mascotas;
    }

    // ========================================================================
    // 2. CREAR NUEVA MASCOTA
    // ========================================================================
    public boolean crearMascota(Mascota mascota) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean success = false;

        try {
            conn = ConexionBDD.getConnection();
            String sql = "INSERT INTO mascotas (id_usuario_propietario, nombre, especie, raza, sexo, fecha_nacimiento) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, mascota.getIdUsuarioPropietario());
            ps.setString(2, mascota.getNombre());
            ps.setString(3, mascota.getEspecie());
            ps.setString(4, mascota.getRaza());
            ps.setString(5, mascota.getSexo());
            ps.setDate(6, mascota.getFechaNacimiento());

            int result = ps.executeUpdate();
            success = (result > 0);

            if (success) {
                System.out.println("✅ Mascota creada: " + mascota.getNombre());
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al crear mascota");
            e.printStackTrace();
        } finally {
            cerrarRecursos(null, ps, conn);
        }

        return success;
    }

    // ========================================================================
    // 3. ACTUALIZAR MASCOTA
    // ========================================================================
    public boolean actualizarMascota(Mascota mascota) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean success = false;

        try {
            conn = ConexionBDD.getConnection();
            String sql = "UPDATE mascotas SET nombre = ?, especie = ?, raza = ?, " +
                    "sexo = ?, fecha_nacimiento = ? WHERE id_mascota = ?";

            ps = conn.prepareStatement(sql);
            ps.setString(1, mascota.getNombre());
            ps.setString(2, mascota.getEspecie());
            ps.setString(3, mascota.getRaza());
            ps.setString(4, mascota.getSexo());
            ps.setDate(5, mascota.getFechaNacimiento());
            ps.setInt(6, mascota.getIdMascota());

            int result = ps.executeUpdate();
            success = (result > 0);

            if (success) {
                System.out.println("✅ Mascota actualizada: ID " + mascota.getIdMascota());
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar mascota");
            e.printStackTrace();
        } finally {
            cerrarRecursos(null, ps, conn);
        }

        return success;
    }

    // ========================================================================
    // 4. ELIMINAR MASCOTA
    // ========================================================================
    public boolean eliminarMascota(int idMascota) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean success = false;

        try {
            conn = ConexionBDD.getConnection();
            String sql = "DELETE FROM mascotas WHERE id_mascota = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, idMascota);

            int result = ps.executeUpdate();
            success = (result > 0);

            if (success) {
                System.out.println("✅ Mascota eliminada: ID " + idMascota);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al eliminar mascota");
            e.printStackTrace();
        } finally {
            cerrarRecursos(null, ps, conn);
        }

        return success;
    }

    // ========================================================================
    // 5. OBTENER MASCOTA POR ID
    // ========================================================================
    public Mascota obtenerPorId(int idMascota) {
        Mascota mascota = null;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConexionBDD.getConnection();
            String sql = "SELECT m.*, u.Nombre as nombre_propietario, u.Correo as correo_propietario " +
                    "FROM mascotas m " +
                    "INNER JOIN usuarios u ON m.id_usuario_propietario = u.id_usuario " +
                    "WHERE m.id_mascota = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, idMascota);
            rs = ps.executeQuery();

            if (rs.next()) {
                mascota = mapearMascota(rs);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al obtener mascota");
            e.printStackTrace();
        } finally {
            cerrarRecursos(rs, ps, conn);
        }

        return mascota;
    }

    // ========================================================================
    // 6. VERIFICAR SI MASCOTA PERTENECE A USUARIO
    // ========================================================================
    public boolean mascotaPerteneceAUsuario(int idMascota, int idUsuario) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean pertenece = false;

        try {
            conn = ConexionBDD.getConnection();
            String sql = "SELECT id_mascota FROM mascotas " +
                    "WHERE id_mascota = ? AND id_usuario_propietario = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, idMascota);
            ps.setInt(2, idUsuario);
            rs = ps.executeQuery();

            pertenece = rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cerrarRecursos(rs, ps, conn);
        }

        return pertenece;
    }

    // ========================================================================
    // MÃTODO AUXILIAR: MAPEAR RESULTSET A MASCOTA
    // ========================================================================
    private Mascota mapearMascota(ResultSet rs) throws SQLException {
        Mascota mascota = new Mascota();
        mascota.setIdMascota(rs.getInt("id_mascota"));
        mascota.setIdUsuarioPropietario(rs.getInt("id_usuario_propietario"));
        mascota.setNombre(rs.getString("nombre"));
        mascota.setEspecie(rs.getString("especie"));
        mascota.setRaza(rs.getString("raza"));
        mascota.setSexo(rs.getString("sexo"));
        mascota.setFechaNacimiento(rs.getDate("fecha_nacimiento"));
        mascota.setNombrePropietario(rs.getString("nombre_propietario"));
        mascota.setCorreoPropietario(rs.getString("correo_propietario"));

        // Calcular edad
        if (mascota.getFechaNacimiento() != null) {
            LocalDate fechaNac = mascota.getFechaNacimiento().toLocalDate();
            LocalDate ahora = LocalDate.now();
            Period periodo = Period.between(fechaNac, ahora);
            mascota.setEdad(periodo.getYears());
        }

        return mascota;
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
            e.printStackTrace();
        }
    }
}