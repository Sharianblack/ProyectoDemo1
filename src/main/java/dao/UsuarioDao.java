package dao;

import model.Usuario;
import util.ConexionBDD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDao {

    // ========================================================================
    // 1. MÉTODO PARA VALIDAR LOGIN
    // ========================================================================
    public Usuario validateUser(String username, String password) {
        Usuario user = null;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConexionBDD.getConnection();
            // Solo permite login si el usuario está activo
            String sql = "SELECT id_usuario, Nombre, Correo, Rol, Telefono, Direccion, activo " +
                    "FROM Usuarios WHERE Correo = ? AND PasswordHash = ? AND activo = 1";
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);

            rs = ps.executeQuery();

            if (rs.next()) {
                user = new Usuario();
                user.setId(rs.getInt("id_usuario"));
                user.setNombre(rs.getString("Nombre"));
                user.setCorreo(rs.getString("Correo"));
                user.setRol(rs.getString("Rol"));
                user.setTelefono(rs.getString("Telefono"));
                user.setDireccion(rs.getString("Direccion"));
                user.setActivo(rs.getBoolean("activo"));

                System.out.println("✓ Usuario encontrado: " + username);
            } else {
                System.out.println("✗ Usuario no encontrado o inactivo");
            }
        } catch (SQLException e) {
            System.out.println("✗ Error en validateUser");
            e.printStackTrace();
        } finally {
            cerrarRecursos(rs, ps, conn);
        }
        return user;
    }

    // ========================================================================
    // 2. LISTAR TODOS LOS USUARIOS
    // ========================================================================
    public List<Usuario> listarTodos() {
        List<Usuario> usuarios = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConexionBDD.getConnection();
            String sql = "SELECT id_usuario, Nombre, Correo, Rol, Telefono, Direccion, activo, fecha_registro " +
                    "FROM Usuarios ORDER BY fecha_registro DESC";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Usuario user = new Usuario();
                user.setId(rs.getInt("id_usuario"));
                user.setNombre(rs.getString("Nombre"));
                user.setCorreo(rs.getString("Correo"));
                user.setRol(rs.getString("Rol"));
                user.setTelefono(rs.getString("Telefono"));
                user.setDireccion(rs.getString("Direccion"));
                user.setActivo(rs.getBoolean("activo"));
                user.setFechaRegistro(rs.getTimestamp("fecha_registro"));
                usuarios.add(user);
            }
            System.out.println("✓ Se listaron " + usuarios.size() + " usuarios");
        } catch (SQLException e) {
            System.out.println("✗ Error al listar usuarios");
            e.printStackTrace();
        } finally {
            cerrarRecursos(rs, ps, conn);
        }
        return usuarios;
    }

    // ========================================================================
    // 3. OBTENER USUARIO POR CORREO
    // ========================================================================
    public Usuario obtenerPorCorreo(String correo) {
        Usuario user = null;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConexionBDD.getConnection();
            String sql = "SELECT id_usuario, Nombre, Correo, Rol, Telefono, Direccion, activo, fecha_registro " +
                    "FROM Usuarios WHERE Correo = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, correo);
            rs = ps.executeQuery();

            if (rs.next()) {
                user = new Usuario();
                user.setId(rs.getInt("id_usuario"));
                user.setNombre(rs.getString("Nombre"));
                user.setCorreo(rs.getString("Correo"));
                user.setRol(rs.getString("Rol"));
                user.setTelefono(rs.getString("Telefono"));
                user.setDireccion(rs.getString("Direccion"));
                user.setActivo(rs.getBoolean("activo"));
                user.setFechaRegistro(rs.getTimestamp("fecha_registro"));
                System.out.println("✓ Usuario encontrado por correo: " + correo);
            }
        } catch (SQLException e) {
            System.out.println("✗ Error al obtener usuario por correo");
            e.printStackTrace();
        } finally {
            cerrarRecursos(rs, ps, conn);
        }
        return user;
    }

    // ========================================================================
    // 4. OBTENER USUARIO POR ID
    // ========================================================================
    public Usuario obtenerPorId(int id) {
        Usuario user = null;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConexionBDD.getConnection();
            String sql = "SELECT id_usuario, Nombre, Correo, Rol, Telefono, Direccion, activo, fecha_registro " +
                    "FROM Usuarios WHERE id_usuario = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                user = new Usuario();
                user.setId(rs.getInt("id_usuario"));
                user.setNombre(rs.getString("Nombre"));
                user.setCorreo(rs.getString("Correo"));
                user.setRol(rs.getString("Rol"));
                user.setTelefono(rs.getString("Telefono"));
                user.setDireccion(rs.getString("Direccion"));
                user.setActivo(rs.getBoolean("activo"));
                user.setFechaRegistro(rs.getTimestamp("fecha_registro"));
                System.out.println("✓ Usuario encontrado: ID " + id);
            }
        } catch (SQLException e) {
            System.out.println("✗ Error al obtener usuario por ID");
            e.printStackTrace();
        } finally {
            cerrarRecursos(rs, ps, conn);
        }
        return user;
    }

    // ========================================================================
    // 4. CREAR NUEVO USUARIO
    // ========================================================================
    public boolean crearUsuario(Usuario user) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean success = false;

        try {
            conn = ConexionBDD.getConnection();
            String sql = "INSERT INTO Usuarios (Nombre, Correo, PasswordHash, Rol, Telefono, Direccion, activo) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
            ps = conn.prepareStatement(sql);
            ps.setString(1, user.getNombre());
            ps.setString(2, user.getCorreo());
            ps.setString(3, user.getPasswordHash());
            ps.setString(4, user.getRol());
            ps.setString(5, user.getTelefono());
            ps.setString(6, user.getDireccion());
            ps.setBoolean(7, user.isActivo());

            int result = ps.executeUpdate();
            success = (result > 0);

            if (success) {
                System.out.println("✓ Usuario creado: " + user.getCorreo());
            }
        } catch (SQLException e) {
            System.out.println("✗ Error al crear usuario");
            e.printStackTrace();
        } finally {
            cerrarRecursos(null, ps, conn);
        }
        return success;
    }

    // ========================================================================
    // 5. ACTUALIZAR USUARIO EXISTENTE
    // ========================================================================
    public boolean actualizarUsuario(Usuario user) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean success = false;

        try {
            conn = ConexionBDD.getConnection();
            String sql = "UPDATE Usuarios SET Nombre = ?, Correo = ?, Rol = ?, " +
                    "Telefono = ?, Direccion = ?, activo = ? WHERE id_usuario = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, user.getNombre());
            ps.setString(2, user.getCorreo());
            ps.setString(3, user.getRol());
            ps.setString(4, user.getTelefono());
            ps.setString(5, user.getDireccion());
            ps.setBoolean(6, user.isActivo());
            ps.setInt(7, user.getId());

            int result = ps.executeUpdate();
            success = (result > 0);

            if (success) {
                System.out.println("✓ Usuario actualizado: ID " + user.getId());
            }
        } catch (SQLException e) {
            System.out.println("✗ Error al actualizar usuario");
            e.printStackTrace();
        } finally {
            cerrarRecursos(null, ps, conn);
        }
        return success;
    }

    // ========================================================================
    // 6. ACTUALIZAR CONTRASEÑA DE USUARIO
    // ========================================================================
    public boolean actualizarPassword(int userId, String newPassword) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean success = false;

        try {
            conn = ConexionBDD.getConnection();
            String sql = "UPDATE Usuarios SET PasswordHash = ? WHERE id_usuario = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, newPassword);
            ps.setInt(2, userId);

            int result = ps.executeUpdate();
            success = (result > 0);

            if (success) {
                System.out.println("✓ Contraseña actualizada para usuario ID: " + userId);
            }
        } catch (SQLException e) {
            System.out.println("✗ Error al actualizar contraseña");
            e.printStackTrace();
        } finally {
            cerrarRecursos(null, ps, conn);
        }
        return success;
    }

    // ========================================================================
    // 7. CAMBIAR ESTADO DEL USUARIO (Activar/Desactivar)
    // ========================================================================
    public boolean cambiarEstado(int userId, boolean activo) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean success = false;

        try {
            conn = ConexionBDD.getConnection();
            String sql = "UPDATE Usuarios SET activo = ? WHERE id_usuario = ?";
            ps = conn.prepareStatement(sql);
            ps.setBoolean(1, activo);
            ps.setInt(2, userId);

            int result = ps.executeUpdate();
            success = (result > 0);

            if (success) {
                String estado = activo ? "activado" : "desactivado";
                System.out.println("✓ Usuario " + estado + ": ID " + userId);
            }
        } catch (SQLException e) {
            System.out.println("✗ Error al cambiar estado del usuario");
            e.printStackTrace();
        } finally {
            cerrarRecursos(null, ps, conn);
        }
        return success;
    }

    // ========================================================================
    // 8. BUSCAR USUARIOS (por nombre, correo o rol)
    // ========================================================================
    public List<Usuario> buscarUsuarios(String criterio) {
        List<Usuario> usuarios = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConexionBDD.getConnection();
            String sql = "SELECT id_usuario, Nombre, Correo, Rol, Telefono, Direccion, activo, fecha_registro " +
                    "FROM Usuarios WHERE Nombre LIKE ? OR Correo LIKE ? OR Rol LIKE ? " +
                    "ORDER BY fecha_registro DESC";
            ps = conn.prepareStatement(sql);
            String searchPattern = "%" + criterio + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);
            rs = ps.executeQuery();

            while (rs.next()) {
                Usuario user = new Usuario();
                user.setId(rs.getInt("id_usuario"));
                user.setNombre(rs.getString("Nombre"));
                user.setCorreo(rs.getString("Correo"));
                user.setRol(rs.getString("Rol"));
                user.setTelefono(rs.getString("Telefono"));
                user.setDireccion(rs.getString("Direccion"));
                user.setActivo(rs.getBoolean("activo"));
                user.setFechaRegistro(rs.getTimestamp("fecha_registro"));
                usuarios.add(user);
            }
            System.out.println("✓ Búsqueda: " + usuarios.size() + " usuarios encontrados");
        } catch (SQLException e) {
            System.out.println("✗ Error al buscar usuarios");
            e.printStackTrace();
        } finally {
            cerrarRecursos(rs, ps, conn);
        }
        return usuarios;
    }

    // ========================================================================
    // 9. VERIFICAR SI UN CORREO YA EXISTE
    // ========================================================================
    public boolean correoExiste(String correo) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean existe = false;

        try {
            conn = ConexionBDD.getConnection();
            String sql = "SELECT id_usuario FROM Usuarios WHERE Correo = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, correo);
            rs = ps.executeQuery();
            existe = rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cerrarRecursos(rs, ps, conn);
        }
        return existe;
    }

    // ========================================================================
    // 10. VERIFICAR SI UN CORREO YA EXISTE (EXCEPTO EL USUARIO ACTUAL)
    // ========================================================================
    public boolean correoExisteExceptoUsuario(String correo, int userId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean existe = false;

        try {
            conn = ConexionBDD.getConnection();
            String sql = "SELECT id_usuario FROM Usuarios WHERE Correo = ? AND id_usuario != ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, correo);
            ps.setInt(2, userId);
            rs = ps.executeQuery();
            existe = rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cerrarRecursos(rs, ps, conn);
        }
        return existe;
    }

    // ========================================================================
    // 12. LISTAR CLIENTES (CON FALBACK SI LA TABLA 'clientes' NO EXISTE)
    // ========================================================================
    /**
     * Devuelve una lista de usuarios con rol Cliente. Intenta usar la tabla 'clientes'
     * para obtener sólo los clientes registrados en esa tabla; si falla, hace fallback
     * a consultar directamente la tabla 'usuarios' filtrando por Rol='Cliente'.
     */
    public List<Usuario> listarClientes() {
        List<Usuario> clientes = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConexionBDD.getConnection();
            String sqlJoin = "SELECT u.id_usuario, u.Nombre, u.Correo, u.Rol, u.Telefono, u.Direccion, u.activo, u.fecha_registro " +
                    "FROM clientes c JOIN usuarios u ON c.id_usuario = u.id_usuario WHERE u.activo = 1 ORDER BY u.Nombre";
            try {
                ps = conn.prepareStatement(sqlJoin);
                rs = ps.executeQuery();
                while (rs.next()) {
                    Usuario u = new Usuario();
                    u.setId(rs.getInt("id_usuario"));
                    u.setNombre(rs.getString("Nombre"));
                    u.setCorreo(rs.getString("Correo"));
                    u.setRol(rs.getString("Rol"));
                    u.setTelefono(rs.getString("Telefono"));
                    u.setDireccion(rs.getString("Direccion"));
                    u.setActivo(rs.getBoolean("activo"));
                    u.setFechaRegistro(rs.getTimestamp("fecha_registro"));
                    clientes.add(u);
                }
                return clientes;
            } catch (SQLException ex) {
                // Fallback: tabla 'clientes' no existe o query falló
                cerrarRecursos(rs, ps, null);
            }

            String sqlUsuarios = "SELECT id_usuario, Nombre, Correo, Rol, Telefono, Direccion, activo, fecha_registro FROM usuarios WHERE activo = 1 AND (Rol = 'Cliente' OR Rol = 'cliente') ORDER BY Nombre";
            ps = conn.prepareStatement(sqlUsuarios);
            rs = ps.executeQuery();
            while (rs.next()) {
                Usuario u = new Usuario();
                u.setId(rs.getInt("id_usuario"));
                u.setNombre(rs.getString("Nombre"));
                u.setCorreo(rs.getString("Correo"));
                u.setRol(rs.getString("Rol"));
                u.setTelefono(rs.getString("Telefono"));
                u.setDireccion(rs.getString("Direccion"));
                u.setActivo(rs.getBoolean("activo"));
                u.setFechaRegistro(rs.getTimestamp("fecha_registro"));
                clientes.add(u);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cerrarRecursos(rs, ps, conn);
        }

        return clientes;
    }

    // ========================================================================
    // 11. MÉTODO AUXILIAR PARA CERRAR RECURSOS
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

    // ========================================================================
    // MÉTODO LEGACY - Mantener por compatibilidad
    // ========================================================================
    @Deprecated
    public boolean registerUser(Usuario user) {
        return crearUsuario(user);
    }

    @Deprecated
    public boolean userExists(String username) {
        return correoExiste(username);
    }
}