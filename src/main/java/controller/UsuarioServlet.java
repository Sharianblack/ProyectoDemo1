package controller;

import model.Usuario;
import dao.UsuarioDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/UsuarioServlet")
public class UsuarioServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UsuarioDao usuarioDao;

    @Override
    public void init() {
        usuarioDao = new UsuarioDao();
    }

    // ========================================================================
    // MÉTODO GET - Para listar y cargar datos
    // ========================================================================
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Verificar que sea Admin
        if (!esAdmin(request, response)) {
            return;
        }

        String action = request.getParameter("action");

        if (action == null) {
            action = "listar";
        }

        switch (action) {
            case "listar":
                listarUsuarios(request, response);
                break;
            case "buscar":
                buscarUsuarios(request, response);
                break;
            default:
                listarUsuarios(request, response);
                break;
        }
    }

    // ========================================================================
    // MÉTODO POST - Para crear, actualizar, eliminar
    // ========================================================================
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Verificar que sea Admin
        if (!esAdmin(request, response)) {
            return;
        }

        String action = request.getParameter("action");

        if (action == null) {
            response.sendRedirect("UsuarioServlet?action=listar");
            return;
        }

        switch (action) {
            case "crear":
                crearUsuario(request, response);
                break;
            case "actualizar":
                actualizarUsuario(request, response);
                break;
            case "cambiarEstado":
                cambiarEstadoUsuario(request, response);
                break;
            case "cambiarPassword":
                cambiarPassword(request, response);
                break;
            default:
                response.sendRedirect("UsuarioServlet?action=listar");
                break;
        }
    }

    // ========================================================================
    // LISTAR TODOS LOS USUARIOS
    // ========================================================================
    private void listarUsuarios(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Usuario> usuarios = usuarioDao.listarTodos();
        request.setAttribute("usuarios", usuarios);
        request.getRequestDispatcher("gestionUsuarios.jsp").forward(request, response);
    }

    // ========================================================================
    // BUSCAR USUARIOS
    // ========================================================================
    private void buscarUsuarios(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String criterio = request.getParameter("criterio");
        List<Usuario> usuarios;

        if (criterio != null && !criterio.trim().isEmpty()) {
            usuarios = usuarioDao.buscarUsuarios(criterio);
            request.setAttribute("criterio", criterio);
        } else {
            usuarios = usuarioDao.listarTodos();
        }

        request.setAttribute("usuarios", usuarios);
        request.getRequestDispatcher("gestionUsuarios.jsp").forward(request, response);
    }

    // ========================================================================
    // CREAR NUEVO USUARIO
    // ========================================================================
    private void crearUsuario(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Obtener datos del formulario
            String nombre = request.getParameter("nombre");
            String correo = request.getParameter("correo");
            String password = request.getParameter("password");
            String rol = request.getParameter("rol");
            String telefono = request.getParameter("telefono");
            String direccion = request.getParameter("direccion");

            // Validaciones básicas
            if (nombre == null || nombre.trim().isEmpty() ||
                    correo == null || correo.trim().isEmpty() ||
                    password == null || password.trim().isEmpty() ||
                    rol == null || rol.trim().isEmpty()) {

                request.setAttribute("error", "Todos los campos obligatorios deben estar completos");
                listarUsuarios(request, response);
                return;
            }

            // Verificar si el correo ya existe
            if (usuarioDao.correoExiste(correo)) {
                request.setAttribute("error", "El correo '" + correo + "' ya está registrado");
                listarUsuarios(request, response);
                return;
            }

            // Crear usuario
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setNombre(nombre.trim());
            nuevoUsuario.setCorreo(correo.trim().toLowerCase());
            nuevoUsuario.setPasswordHash(password); // En producción, usar hash real
            nuevoUsuario.setRol(rol);
            nuevoUsuario.setTelefono(telefono != null ? telefono.trim() : "");
            nuevoUsuario.setDireccion(direccion != null ? direccion.trim() : "");
            nuevoUsuario.setActivo(true);

            boolean creado = usuarioDao.crearUsuario(nuevoUsuario);

            if (creado) {
                // ========================================================================
                // ENVIAR CORREO DE BIENVENIDA AL NUEVO USUARIO
                // ========================================================================
                try {
                    // Construir URL del login
                    String urlLogin = request.getScheme() + "://" +
                            request.getServerName() + ":" +
                            request.getServerPort() +
                            request.getContextPath() + "/login.jsp";

                    // Enviar correo de bienvenida
                    boolean correoEnviado = util.EmailUtil.enviarCorreoBienvenida(
                            correo.trim().toLowerCase(),
                            nombre.trim(),
                            rol,
                            password, // Contraseña temporal
                            urlLogin
                    );

                    if (correoEnviado) {
                        request.setAttribute("success",
                                "Usuario '" + nombre + "' creado exitosamente. " +
                                        "✉️ Se ha enviado un correo de bienvenida a " + correo);
                        System.out.println("✓ Correo de bienvenida enviado a: " + correo);
                    } else {
                        request.setAttribute("success",
                                "Usuario '" + nombre + "' creado exitosamente. " +
                                        "⚠️ No se pudo enviar el correo de bienvenida.");
                        System.out.println("✗ Error al enviar correo de bienvenida a: " + correo);
                    }
                } catch (Exception emailEx) {
                    // Si falla el correo, no afecta la creación del usuario
                    System.err.println("✗ Error al enviar correo de bienvenida: " + emailEx.getMessage());
                    request.setAttribute("success",
                            "Usuario '" + nombre + "' creado exitosamente. " +
                                    "⚠️ No se pudo enviar el correo de bienvenida (verifica la configuración de email).");
                }
                // ========================================================================

                System.out.println("✓ Usuario creado: " + correo);
            } else {
                request.setAttribute("error", "Error al crear el usuario");
                System.out.println("✗ Error al crear usuario: " + correo);
            }

        } catch (Exception e) {
            request.setAttribute("error", "Error inesperado: " + e.getMessage());
            e.printStackTrace();
        }

        listarUsuarios(request, response);
    }

    // ========================================================================
    // ACTUALIZAR USUARIO EXISTENTE
    // ========================================================================
    private void actualizarUsuario(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Obtener ID del usuario a actualizar
            int userId = Integer.parseInt(request.getParameter("userId"));

            // Obtener datos del formulario
            String nombre = request.getParameter("nombre");
            String correo = request.getParameter("correo");
            String rol = request.getParameter("rol");
            String telefono = request.getParameter("telefono");
            String direccion = request.getParameter("direccion");
            boolean activo = request.getParameter("activo") != null;

            // Validaciones básicas
            if (nombre == null || nombre.trim().isEmpty() ||
                    correo == null || correo.trim().isEmpty() ||
                    rol == null || rol.trim().isEmpty()) {

                request.setAttribute("error", "Todos los campos obligatorios deben estar completos");
                listarUsuarios(request, response);
                return;
            }

            // Verificar si el correo ya existe (excepto el usuario actual)
            if (usuarioDao.correoExisteExceptoUsuario(correo, userId)) {
                request.setAttribute("error", "El correo '" + correo + "' ya está registrado por otro usuario");
                listarUsuarios(request, response);
                return;
            }

            // Actualizar usuario
            Usuario usuario = new Usuario();
            usuario.setId(userId);
            usuario.setNombre(nombre.trim());
            usuario.setCorreo(correo.trim().toLowerCase());
            usuario.setRol(rol);
            usuario.setTelefono(telefono != null ? telefono.trim() : "");
            usuario.setDireccion(direccion != null ? direccion.trim() : "");
            usuario.setActivo(activo);

            boolean actualizado = usuarioDao.actualizarUsuario(usuario);

            if (actualizado) {
                request.setAttribute("success", "Usuario '" + nombre + "' actualizado exitosamente");
                System.out.println("✓ Usuario actualizado: ID " + userId);
            } else {
                request.setAttribute("error", "Error al actualizar el usuario");
                System.out.println("✗ Error al actualizar usuario: ID " + userId);
            }

        } catch (NumberFormatException e) {
            request.setAttribute("error", "ID de usuario inválido");
            e.printStackTrace();
        } catch (Exception e) {
            request.setAttribute("error", "Error inesperado: " + e.getMessage());
            e.printStackTrace();
        }

        listarUsuarios(request, response);
    }

    // ========================================================================
    // CAMBIAR ESTADO DEL USUARIO (Activar/Desactivar)
    // ========================================================================
    private void cambiarEstadoUsuario(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int userId = Integer.parseInt(request.getParameter("userId"));
            boolean nuevoEstado = Boolean.parseBoolean(request.getParameter("estado"));

            // Obtener info del usuario antes de cambiar estado
            Usuario usuario = usuarioDao.obtenerPorId(userId);

            if (usuario == null) {
                request.setAttribute("error", "Usuario no encontrado");
                listarUsuarios(request, response);
                return;
            }

            // No permitir desactivar el propio usuario admin
            HttpSession session = request.getSession();
            Integer adminId = (Integer) session.getAttribute("userId");

            if (adminId != null && adminId == userId && !nuevoEstado) {
                request.setAttribute("error", "No puedes desactivar tu propia cuenta");
                listarUsuarios(request, response);
                return;
            }

            boolean cambiado = usuarioDao.cambiarEstado(userId, nuevoEstado);

            if (cambiado) {
                String accion = nuevoEstado ? "activado" : "desactivado";
                request.setAttribute("success", "Usuario '" + usuario.getNombre() + "' " + accion + " exitosamente");
                System.out.println("✓ Usuario " + accion + ": ID " + userId);
            } else {
                request.setAttribute("error", "Error al cambiar el estado del usuario");
                System.out.println("✗ Error al cambiar estado: ID " + userId);
            }

        } catch (NumberFormatException e) {
            request.setAttribute("error", "ID de usuario inválido");
            e.printStackTrace();
        } catch (Exception e) {
            request.setAttribute("error", "Error inesperado: " + e.getMessage());
            e.printStackTrace();
        }

        listarUsuarios(request, response);
    }

    // ========================================================================
    // CAMBIAR CONTRASEÑA DE USUARIO
    // ========================================================================
    private void cambiarPassword(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int userId = Integer.parseInt(request.getParameter("userId"));
            String nuevaPassword = request.getParameter("nuevaPassword");

            if (nuevaPassword == null || nuevaPassword.trim().isEmpty()) {
                request.setAttribute("error", "La contraseña no puede estar vacía");
                listarUsuarios(request, response);
                return;
            }

            if (nuevaPassword.length() < 4) {
                request.setAttribute("error", "La contraseña debe tener al menos 4 caracteres");
                listarUsuarios(request, response);
                return;
            }

            Usuario usuario = usuarioDao.obtenerPorId(userId);

            if (usuario == null) {
                request.setAttribute("error", "Usuario no encontrado");
                listarUsuarios(request, response);
                return;
            }

            boolean cambiado = usuarioDao.actualizarPassword(userId, nuevaPassword);

            if (cambiado) {
                request.setAttribute("success", "Contraseña actualizada para '" + usuario.getNombre() + "'");
                System.out.println("✓ Contraseña cambiada: ID " + userId);
            } else {
                request.setAttribute("error", "Error al cambiar la contraseña");
                System.out.println("✗ Error al cambiar contraseña: ID " + userId);
            }

        } catch (NumberFormatException e) {
            request.setAttribute("error", "ID de usuario inválido");
            e.printStackTrace();
        } catch (Exception e) {
            request.setAttribute("error", "Error inesperado: " + e.getMessage());
            e.printStackTrace();
        }

        listarUsuarios(request, response);
    }

    // ========================================================================
    // VERIFICAR QUE EL USUARIO SEA ADMINISTRADOR
    // ========================================================================
    private boolean esAdmin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login.jsp");
            return false;
        }

        String rol = (String) session.getAttribute("rol");

        if (rol == null || !rol.equalsIgnoreCase("Admin")) {
            response.sendRedirect("login.jsp");
            return false;
        }

        return true;
    }
}