package controller;

import model.Usuario;
import dao.UsuarioDao;
import dao.EmailVerificationTokenDao;
import util.ValidacionUtil;
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
    private EmailVerificationTokenDao tokenDao;

    @Override
    public void init() {
        usuarioDao = new UsuarioDao();
        tokenDao = new EmailVerificationTokenDao();
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

        String criterio = request.getParameter("criterio");
        List<Usuario> usuarios;

        if (criterio != null && !criterio.trim().isEmpty()) {
            usuarios = usuarioDao.buscarUsuarios(criterio.trim());
            request.setAttribute("criterio", criterio);
        } else {
            usuarios = usuarioDao.listarTodos();
        }

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

            // Guardar datos para mantenerlos en caso de error
            request.setAttribute("formNombre", nombre);
            request.setAttribute("formCorreo", correo);
            request.setAttribute("formRol", rol);
            request.setAttribute("formTelefono", telefono);
            request.setAttribute("formDireccion", direccion);
            request.setAttribute("mostrarModal", "crear");

            // Validar campos obligatorios
            if (!ValidacionUtil.noEstaVacio(nombre)) {
                request.setAttribute("error", "El nombre es requerido");
                listarUsuarios(request, response);
                return;
            }

            if (!ValidacionUtil.esNombreValido(nombre)) {
                request.setAttribute("error", "El nombre solo debe contener letras y espacios");
                listarUsuarios(request, response);
                return;
            }

            if (!ValidacionUtil.esTextoValido(nombre, 3, 100)) {
                request.setAttribute("error", "El nombre debe tener entre 3 y 100 caracteres");
                listarUsuarios(request, response);
                return;
            }

            if (!ValidacionUtil.esEmailValido(correo)) {
                request.setAttribute("error", "El correo electrónico no es válido");
                listarUsuarios(request, response);
                return;
            }

            if (!ValidacionUtil.esPasswordValido(password)) {
                request.setAttribute("error", "La contraseña debe tener al menos 6 caracteres");
                listarUsuarios(request, response);
                return;
            }

            if (!ValidacionUtil.esRolValido(rol)) {
                request.setAttribute("error", "Rol inválido. Debe ser: Admin, Veterinario o Cliente");
                listarUsuarios(request, response);
                return;
            }

            // Validar teléfono si se proporciona
            if (ValidacionUtil.noEstaVacio(telefono) && !ValidacionUtil.esTelefonoValido(telefono)) {
                request.setAttribute("error", "El teléfono debe tener exactamente 10 dígitos");
                listarUsuarios(request, response);
                return;
            }

            // 🔥 VALIDAR FORMATO DE EMAIL
            String mensajeValidacion = util.EmailValidator.validarConMensaje(correo);
            if (mensajeValidacion != null) {
                request.setAttribute("error", "Correo inválido: " + mensajeValidacion);
                listarUsuarios(request, response);
                return;
            }

            // 🔥 RECHAZAR EMAILS TEMPORALES
            if (util.EmailValidator.esEmailTemporal(correo)) {
                request.setAttribute("error", "No se permiten correos temporales o desechables");
                listarUsuarios(request, response);
                return;
            }

            // Normalizar el correo
            correo = util.EmailValidator.normalizar(correo);

            // Verificar si el correo ya existe
            if (usuarioDao.correoExiste(correo)) {
                request.setAttribute("error", "El correo '" + correo + "' ya está registrado");
                listarUsuarios(request, response);
                return;
            }

            // 🔥 NUEVA VALIDACIÓN: Verificar si tiene token pendiente
            if (tokenDao.tienTokenPendiente(correo)) {
                request.setAttribute("error",
                        "Este correo tiene una verificación pendiente del registro público. " +
                                "El usuario debe completar esa verificación primero o esperar 24 horas.");
                listarUsuarios(request, response);
                return;
            }

            // Sanitizar datos
            String nombreSanitizado = ValidacionUtil.sanitizar(nombre.trim());
            String direccionSanitizada = ValidacionUtil.sanitizar(direccion != null ? direccion.trim() : "");

            // Crear usuario
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setNombre(nombreSanitizado);
            nuevoUsuario.setCorreo(correo);
            nuevoUsuario.setPasswordHash(password);
            nuevoUsuario.setRol(rol);
            nuevoUsuario.setTelefono(telefono != null ? telefono.trim() : "");
            nuevoUsuario.setDireccion(direccionSanitizada);
            nuevoUsuario.setActivo(true);

            boolean creado = usuarioDao.crearUsuario(nuevoUsuario);

            if (creado) {
                // Limpiar atributos del formulario
                request.removeAttribute("formNombre");
                request.removeAttribute("formCorreo");
                request.removeAttribute("formRol");
                request.removeAttribute("formTelefono");
                request.removeAttribute("formDireccion");
                request.removeAttribute("mostrarModal");

                // Enviar correo de bienvenida
                try {
                    String urlLogin = request.getScheme() + "://" +
                            request.getServerName() + ":" +
                            request.getServerPort() +
                            request.getContextPath() + "/login.jsp";

                    boolean correoEnviado = util.EmailUtil.enviarCorreoBienvenida(
                            correo,
                            nombre.trim(),
                            rol,
                            password,
                            urlLogin
                    );

                    if (correoEnviado) {
                        request.setAttribute("success",
                                "✅ Usuario '" + nombre + "' (" + rol + ") creado exitosamente. " +
                                        "📧 Se ha enviado un correo de bienvenida a " + correo);
                        System.out.println("✅ Correo de bienvenida enviado a: " + correo);
                    } else {
                        request.setAttribute("success",
                                "✅ Usuario '" + nombre + "' creado exitosamente. " +
                                        "⚠️ No se pudo enviar el correo de bienvenida.");
                        System.out.println("❌ Error al enviar correo de bienvenida a: " + correo);
                    }
                } catch (Exception emailEx) {
                    System.err.println("❌ Error al enviar correo de bienvenida: " + emailEx.getMessage());
                    request.setAttribute("success",
                            "✅ Usuario '" + nombre + "' creado exitosamente. " +
                                    "⚠️ No se pudo enviar el correo de bienvenida (verifica la configuración de email).");
                }

                System.out.println("✅ Usuario creado por Admin: " + correo + " (Rol: " + rol + ")");
            } else {
                request.setAttribute("error", "Error al crear el usuario");
                System.out.println("❌ Error al crear usuario: " + correo);
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

            // Preservar datos del formulario para el caso de error
            request.setAttribute("formUserId", userId);
            request.setAttribute("formNombre", nombre);
            request.setAttribute("formCorreo", correo);
            request.setAttribute("formRol", rol);
            request.setAttribute("formTelefono", telefono);
            request.setAttribute("formDireccion", direccion);
            request.setAttribute("formActivo", activo);
            request.setAttribute("mostrarModal", "editar");

            // Validaciones básicas
            if (nombre == null || nombre.trim().isEmpty() ||
                    correo == null || correo.trim().isEmpty() ||
                    rol == null || rol.trim().isEmpty()) {

                request.setAttribute("error", "Todos los campos obligatorios deben estar completos");
                listarUsuarios(request, response);
                return;
            }

            // Validación de nombre (solo letras y espacios)
            if (!util.ValidacionUtil.esNombreValido(nombre)) {
                request.setAttribute("error", "El nombre solo debe contener letras y espacios");
                listarUsuarios(request, response);
                return;
            }

            // Validación de correo
            if (!util.ValidacionUtil.esEmailValido(correo)) {
                request.setAttribute("error", "El formato del correo electrónico no es válido");
                listarUsuarios(request, response);
                return;
            }

            // Validación de teléfono (si se proporciona)
            if (telefono != null && !telefono.trim().isEmpty()) {
                if (!util.ValidacionUtil.esTelefonoValido(telefono)) {
                    request.setAttribute("error", "El teléfono debe tener exactamente 10 dígitos");
                    listarUsuarios(request, response);
                    return;
                }
            }

            // Validación de rol
            if (!util.ValidacionUtil.esRolValido(rol)) {
                request.setAttribute("error", "El rol seleccionado no es válido");
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
                // Limpiar atributos del formulario en caso de éxito
                request.removeAttribute("formUserId");
                request.removeAttribute("formNombre");
                request.removeAttribute("formCorreo");
                request.removeAttribute("formRol");
                request.removeAttribute("formTelefono");
                request.removeAttribute("formDireccion");
                request.removeAttribute("formActivo");
                request.removeAttribute("mostrarModal");

                request.setAttribute("success", "Usuario '" + nombre + "' actualizado exitosamente");
                System.out.println("✅ Usuario actualizado: ID " + userId);
            } else {
                request.setAttribute("error", "Error al actualizar el usuario");
                System.out.println("❌ Error al actualizar usuario: ID " + userId);
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
                System.out.println("✅ Usuario " + accion + ": ID " + userId);
            } else {
                request.setAttribute("error", "Error al cambiar el estado del usuario");
                System.out.println("❌ Error al cambiar estado: ID " + userId);
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
                System.out.println("✅ Contraseña cambiada: ID " + userId);
            } else {
                request.setAttribute("error", "Error al cambiar la contraseña");
                System.out.println("❌ Error al cambiar contraseña: ID " + userId);
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