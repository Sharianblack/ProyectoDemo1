package dao;

import model.Usuario;
import util.ConexionBDD;
import util.PasswordUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDao {

    // ========================================================================
    // 1. MÉTODO PARA VALIDAR LOGIN (CON BCRYPT)
    // ========================================================================
    public Usuario validateUser(String username, String password) {
        Usuario user = null;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConexionBDD.getConnection();

            // 🔥 PRIMERO VERIFICAR SI EL USUARIO EXISTE (ACTIVO O INACTIVO)
            String sql = "SELECT id_usuario, Nombre, Correo, PasswordHash, Rol, Telefono, Direccion, activo " +
                    "FROM Usuarios WHERE Correo = ?";

            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            rs = ps.executeQuery();

            if (rs.next()) {
                int activoValue = rs.getInt("activo");
                boolean usuarioActivo = (activoValue == 1);
                String storedHash = rs.getString("PasswordHash");

                System.out.println("========== DEBUG LOGIN ==========");
                System.out.println("Usuario: " + username);
                System.out.println("Password ingresada: " + password);
                System.out.println("Hash en BD: " + storedHash);
                System.out.println("Longitud hash: " + (storedHash != null ? storedHash.length() : "NULL"));
                System.out.println("Es BCrypt? " + PasswordUtil.isBCryptHash(storedHash));
                System.out.println("Activo: " + activoValue + " (boolean: " + usuarioActivo + ")");

                // 🔥 VERIFICAR CONTRASEÑA CON BCRYPT
                boolean passwordMatch;

                if (PasswordUtil.isBCryptHash(storedHash)) {
                    // Hash BCrypt - verificar con BCrypt
                    passwordMatch = PasswordUtil.checkPassword(password, storedHash);
                    System.out.println("Resultado BCrypt.checkPassword: " + passwordMatch);
                } else {
                    // Hash antiguo (texto plano) - comparación directa
                    passwordMatch = password.equals(storedHash);
                    System.out.println("Comparación texto plano: " + passwordMatch);

                    // 🔥 MIGRACIÓN AUTOMÁTICA: Si login exitoso, actualizar a BCrypt
                    if (passwordMatch) {
                        int userId = rs.getInt("id_usuario");
                        actualizarPasswordABCrypt(userId, password);
                        System.out.println("🔄 Contraseña migrada a BCrypt para usuario: " + username);
                    }
                }
                
                System.out.println("Resultado final passwordMatch: " + passwordMatch);
                System.out.println("=================================");

                if (passwordMatch) {
                    // Verificar si el usuario está activo
                    if (!usuarioActivo) {
                        System.out.println("🚫 Usuario bloqueado: " + username + " (activo=" + activoValue + ")");
                        // Retornar un usuario especial con id = -1 para indicar "bloqueado"
                        user = new Usuario();
                        user.setId(-1); // Indicador de usuario bloqueado
                        user.setCorreo(username);
                        return user;
                    }
                    
                    user = new Usuario();
                    user.setId(rs.getInt("id_usuario"));
                    user.setNombre(rs.getString("Nombre"));
                    user.setCorreo(rs.getString("Correo"));
                    user.setRol(rs.getString("Rol"));
                    user.setTelefono(rs.getString("Telefono"));
                    user.setDireccion(rs.getString("Direccion"));
                    user.setActivo(rs.getBoolean("activo"));

                    System.out.println("✅ Usuario encontrado: " + username);
                } else {
                    System.out.println("❌ Contraseña incorrecta para: " + username);
                }
            } else {
                System.out.println("❌ Usuario no encontrado");
            }
        } catch (SQLException e) {
            System.out.println("❌ Error en validateUser");
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
            System.out.println("✅ Se listaron " + usuarios.size() + " usuarios");
        } catch (SQLException e) {
            System.out.println("❌ Error al listar usuarios");
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
                System.out.println("✅ Usuario encontrado por correo: " + correo);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al obtener usuario por correo");
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
            String sql = "SELECT id_usuario, Nombre, Correo, PasswordHash, Rol, Telefono, Direccion, activo, fecha_registro " +
                    "FROM Usuarios WHERE id_usuario = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                user = new Usuario();
                user.setId(rs.getInt("id_usuario"));
                user.setNombre(rs.getString("Nombre"));
                user.setCorreo(rs.getString("Correo"));
                user.setPasswordHash(rs.getString("PasswordHash"));
                user.setRol(rs.getString("Rol"));
                user.setTelefono(rs.getString("Telefono"));
                user.setDireccion(rs.getString("Direccion"));
                user.setActivo(rs.getBoolean("activo"));
                user.setFechaRegistro(rs.getTimestamp("fecha_registro"));
                System.out.println("✅ Usuario encontrado: ID " + id);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al obtener usuario por ID");
            e.printStackTrace();
        } finally {
            cerrarRecursos(rs, ps, conn);
        }
        return user;
    }

    // ========================================================================
    // 5. CREAR NUEVO USUARIO (CON BCRYPT)
    // ========================================================================
    public boolean crearUsuario(Usuario user) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean success = false;

        try {
            conn = ConexionBDD.getConnection();

            // 🔥 ENCRIPTAR CONTRASEÑA ANTES DE GUARDAR
            String passwordHash = PasswordUtil.hashPassword(user.getPasswordHash());

            String sql = "INSERT INTO Usuarios (Nombre, Correo, PasswordHash, Rol, Telefono, Direccion, activo) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";

            ps = conn.prepareStatement(sql);
            ps.setString(1, user.getNombre());
            ps.setString(2, user.getCorreo());
            ps.setString(3, passwordHash); // 🔥 Guardar hash BCrypt
            ps.setString(4, user.getRol());
            ps.setString(5, user.getTelefono());
            ps.setString(6, user.getDireccion());
            ps.setBoolean(7, user.isActivo());

            int result = ps.executeUpdate();
            success = (result > 0);

            if (success) {
                System.out.println("✅ Usuario creado con contraseña encriptada: " + user.getCorreo());
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al crear usuario");
            e.printStackTrace();
        } finally {
            cerrarRecursos(null, ps, conn);
        }
        return success;
    }
// ========================================================================
// AGREGA ESTE MÉTODO A TU UsuarioDao.java (después del método crearUsuario)
// ========================================================================

    /**
     * Crea un usuario con un hash ya existente (NO vuelve a hashear)
     * Se usa cuando el password ya viene hasheado (ej: verificación de email)
     * @param user Usuario con passwordHash ya encriptado
     * @return true si se creó correctamente
     */
    public boolean crearUsuarioConHashExistente(Usuario user) {
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
            ps.setString(3, user.getPasswordHash()); // 🔥 Usar el hash tal cual (NO hashear de nuevo)
            ps.setString(4, user.getRol());
            ps.setString(5, user.getTelefono());
            ps.setString(6, user.getDireccion());
            ps.setBoolean(7, user.isActivo());

            int result = ps.executeUpdate();
            success = (result > 0);

            if (success) {
                System.out.println("✅ Usuario creado con hash existente: " + user.getCorreo());
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al crear usuario con hash existente");
            e.printStackTrace();
        } finally {
            cerrarRecursos(null, ps, conn);
        }
        return success;
    }
    // ========================================================================
    // 6. ACTUALIZAR USUARIO EXISTENTE
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
                System.out.println("✅ Usuario actualizado: ID " + user.getId());
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al actualizar usuario");
            e.printStackTrace();
        } finally {
            cerrarRecursos(null, ps, conn);
        }
        return success;
    }

    // ========================================================================
    // 7. ACTUALIZAR CONTRASEÑA DE USUARIO (CON BCRYPT)
    // ========================================================================
    public boolean actualizarPassword(int userId, String newPassword) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean success = false;

        try {
            conn = ConexionBDD.getConnection();

            // 🔥 ENCRIPTAR NUEVA CONTRASEÑA
            String passwordHash = PasswordUtil.hashPassword(newPassword);

            String sql = "UPDATE Usuarios SET PasswordHash = ? WHERE id_usuario = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, passwordHash); // 🔥 Guardar hash BCrypt
            ps.setInt(2, userId);

            int result = ps.executeUpdate();
            success = (result > 0);

            if (success) {
                System.out.println("✅ Contraseña actualizada con BCrypt para usuario ID: " + userId);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al actualizar contraseña");
            e.printStackTrace();
        } finally {
            cerrarRecursos(null, ps, conn);
        }
        return success;
    }

    // ========================================================================
    // 8. CAMBIAR ESTADO DEL USUARIO (Activar/Desactivar)
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
                System.out.println("✅ Usuario " + estado + ": ID " + userId);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al cambiar estado del usuario");
            e.printStackTrace();
        } finally {
            cerrarRecursos(null, ps, conn);
        }
        return success;
    }

    // ========================================================================
    // 9. BUSCAR USUARIOS (por nombre, correo o rol)
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
            System.out.println("✅ Búsqueda: " + usuarios.size() + " usuarios encontrados");
        } catch (SQLException e) {
            System.out.println("❌ Error al buscar usuarios");
            e.printStackTrace();
        } finally {
            cerrarRecursos(rs, ps, conn);
        }
        return usuarios;
    }

    // ========================================================================
    // 10. VERIFICAR SI UN CORREO YA EXISTE
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
    // 11. VERIFICAR SI UN CORREO YA EXISTE (EXCEPTO EL USUARIO ACTUAL)
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


    // PRIVADO: MIGRACIÓN AUTOMÁTICA A BCRYPT

    /**
     * Actualiza una contraseña antigua a BCrypt (migración automática)
     */
    private void actualizarPasswordABCrypt(int userId, String plainPassword) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ConexionBDD.getConnection();
            String passwordHash = PasswordUtil.hashPassword(plainPassword);

            String sql = "UPDATE Usuarios SET PasswordHash = ? WHERE id_usuario = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, passwordHash);
            ps.setInt(2, userId);
            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("❌ Error al migrar contraseña a BCrypt");
            e.printStackTrace();
        } finally {
            cerrarRecursos(null, ps, null);
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
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

    // MÉTODOS LEGACY - Mantener por compatibilidad
    @Deprecated
    public boolean registerUser(Usuario user) {
        return crearUsuario(user);
    }

    @Deprecated
    public boolean userExists(String username) {
        return correoExiste(username);
    }
}