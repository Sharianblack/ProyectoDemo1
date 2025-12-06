package controller;

import model.Usuario;
import dao.UsuarioDao;
import util.EmailUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/VeterinarioClienteServlet")
public class VeterinarioClienteServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UsuarioDao usuarioDao;

    @Override
    public void init() {
        usuarioDao = new UsuarioDao();
    }

    // ========================================================================
    // MÉTODO GET - Para listar y buscar clientes
    // ========================================================================
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Verificar que sea Veterinario
        if (!esVeterinario(request, response)) {
            return;
        }

        String action = request.getParameter("action");

        if (action == null) {
            action = "listar";
        }

        switch (action) {
            case "listar":
                listarClientes(request, response);
                break;
            case "buscar":
                buscarClientes(request, response);
                break;
            default:
                listarClientes(request, response);
                break;
        }
    }

    // ========================================================================
    // MÉTODO POST - Para crear y actualizar clientes
    // ========================================================================
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Verificar que sea Veterinario
        if (!esVeterinario(request, response)) {
            return;
        }

        String action = request.getParameter("action");

        if (action == null) {
            response.sendRedirect("VeterinarioClienteServlet?action=listar");
            return;
        }

        switch (action) {
            case "crear":
                crearCliente(request, response);
                break;
            case "actualizar":
                actualizarCliente(request, response);
                break;
            case "cambiarEstado":
                cambiarEstadoCliente(request, response);
                break;
            default:
                response.sendRedirect("VeterinarioClienteServlet?action=listar");
                break;
        }
    }

    // ========================================================================
    // LISTAR TODOS LOS CLIENTES
    // ========================================================================
    private void listarClientes(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Obtener todos los usuarios y filtrar solo los clientes
        List<Usuario> todosUsuarios = usuarioDao.listarTodos();
        List<Usuario> clientes = todosUsuarios.stream()
                .filter(u -> "Cliente".equalsIgnoreCase(u.getRol()))
                .collect(Collectors.toList());

        request.setAttribute("clientes", clientes);
        request.getRequestDispatcher("gestionClientesVet.jsp").forward(request, response);
    }

    // ========================================================================
    // BUSCAR CLIENTES
    // ========================================================================
    private void buscarClientes(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String criterio = request.getParameter("criterio");
        List<Usuario> clientes;

        if (criterio != null && !criterio.trim().isEmpty()) {
            // Buscar y filtrar solo clientes
            List<Usuario> resultados = usuarioDao.buscarUsuarios(criterio);
            clientes = resultados.stream()
                    .filter(u -> "Cliente".equalsIgnoreCase(u.getRol()))
                    .collect(Collectors.toList());
            request.setAttribute("criterio", criterio);
        } else {
            // Si no hay criterio, listar todos los clientes
            List<Usuario> todosUsuarios = usuarioDao.listarTodos();
            clientes = todosUsuarios.stream()
                    .filter(u -> "Cliente".equalsIgnoreCase(u.getRol()))
                    .collect(Collectors.toList());
        }

        request.setAttribute("clientes", clientes);
        request.getRequestDispatcher("gestionClientesVet.jsp").forward(request, response);
    }

    // ========================================================================
    // CREAR NUEVO CLIENTE
    // ========================================================================
    private void crearCliente(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Obtener datos del formulario
            String nombre = request.getParameter("nombre");
            String correo = request.getParameter("correo");
            String password = request.getParameter("password");
            String telefono = request.getParameter("telefono");
            String direccion = request.getParameter("direccion");

            // Validaciones básicas
            if (nombre == null || nombre.trim().isEmpty() ||
                    correo == null || correo.trim().isEmpty() ||
                    password == null || password.trim().isEmpty()) {

                request.setAttribute("error", "Nombre, correo y contraseña son obligatorios");
                listarClientes(request, response);
                return;
            }

            // Verificar si el correo ya existe
            if (usuarioDao.correoExiste(correo)) {
                request.setAttribute("error", "El correo '" + correo + "' ya está registrado");
                listarClientes(request, response);
                return;
            }

            // Crear cliente (ROL FIJO: Cliente)
            Usuario nuevoCliente = new Usuario();
            nuevoCliente.setNombre(nombre.trim());
            nuevoCliente.setCorreo(correo.trim().toLowerCase());
            nuevoCliente.setPasswordHash(password);
            nuevoCliente.setRol("Cliente"); // 🔒 ROL FIJO - Solo puede crear clientes
            nuevoCliente.setTelefono(telefono != null ? telefono.trim() : "");
            nuevoCliente.setDireccion(direccion != null ? direccion.trim() : "");
            nuevoCliente.setActivo(true);

            boolean creado = usuarioDao.crearUsuario(nuevoCliente);

            if (creado) {
                // Enviar correo de bienvenida
                try {
                    String urlLogin = request.getScheme() + "://" +
                            request.getServerName() + ":" +
                            request.getServerPort() +
                            request.getContextPath() + "/login.jsp";

                    boolean correoEnviado = EmailUtil.enviarCorreoBienvenida(
                            correo.trim().toLowerCase(),
                            nombre.trim(),
                            "Cliente",
                            password,
                            urlLogin
                    );

                    if (correoEnviado) {
                        request.setAttribute("success",
                                "✅ Cliente '" + nombre + "' creado exitosamente. " +
                                        "📧 Se envió un correo de bienvenida a " + correo);
                        System.out.println("✅ Correo de bienvenida enviado a: " + correo);
                    } else {
                        request.setAttribute("success",
                                "✅ Cliente '" + nombre + "' creado exitosamente. " +
                                        "⚠️ No se pudo enviar el correo de bienvenida.");
                        System.out.println("❌ Error al enviar correo de bienvenida a: " + correo);
                    }
                } catch (Exception emailEx) {
                    System.err.println("❌ Error al enviar correo: " + emailEx.getMessage());
                    request.setAttribute("success",
                            "✅ Cliente '" + nombre + "' creado exitosamente. " +
                                    "⚠️ No se pudo enviar el correo de bienvenida.");
                }

                System.out.println("✅ Cliente creado por veterinario: " + correo);
            } else {
                request.setAttribute("error", "Error al crear el cliente");
                System.out.println("❌ Error al crear cliente: " + correo);
            }

        } catch (Exception e) {
            request.setAttribute("error", "Error inesperado: " + e.getMessage());
            e.printStackTrace();
        }

        listarClientes(request, response);
    }

    // ========================================================================
    // ACTUALIZAR CLIENTE EXISTENTE
    // ========================================================================
    private void actualizarCliente(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int userId = Integer.parseInt(request.getParameter("userId"));

            // Verificar que el usuario a editar sea un cliente
            Usuario usuarioExistente = usuarioDao.obtenerPorId(userId);
            if (usuarioExistente == null || !"Cliente".equalsIgnoreCase(usuarioExistente.getRol())) {
                request.setAttribute("error", "Solo puedes editar clientes");
                listarClientes(request, response);
                return;
            }

            // Obtener datos del formulario
            String nombre = request.getParameter("nombre");
            String correo = request.getParameter("correo");
            String telefono = request.getParameter("telefono");
            String direccion = request.getParameter("direccion");
            boolean activo = request.getParameter("activo") != null;

            // Validaciones básicas
            if (nombre == null || nombre.trim().isEmpty() ||
                    correo == null || correo.trim().isEmpty()) {

                request.setAttribute("error", "Nombre y correo son obligatorios");
                listarClientes(request, response);
                return;
            }

            // Verificar si el correo ya existe (excepto el usuario actual)
            if (usuarioDao.correoExisteExceptoUsuario(correo, userId)) {
                request.setAttribute("error", "El correo '" + correo + "' ya está registrado");
                listarClientes(request, response);
                return;
            }

            // Actualizar cliente
            Usuario cliente = new Usuario();
            cliente.setId(userId);
            cliente.setNombre(nombre.trim());
            cliente.setCorreo(correo.trim().toLowerCase());
            cliente.setRol("Cliente"); // 🔒 Mantener rol Cliente
            cliente.setTelefono(telefono != null ? telefono.trim() : "");
            cliente.setDireccion(direccion != null ? direccion.trim() : "");
            cliente.setActivo(activo);

            boolean actualizado = usuarioDao.actualizarUsuario(cliente);

            if (actualizado) {
                request.setAttribute("success", "✅ Cliente '" + nombre + "' actualizado exitosamente");
                System.out.println("✅ Cliente actualizado: ID " + userId);
            } else {
                request.setAttribute("error", "Error al actualizar el cliente");
                System.out.println("❌ Error al actualizar cliente: ID " + userId);
            }

        } catch (NumberFormatException e) {
            request.setAttribute("error", "ID de cliente inválido");
            e.printStackTrace();
        } catch (Exception e) {
            request.setAttribute("error", "Error inesperado: " + e.getMessage());
            e.printStackTrace();
        }

        listarClientes(request, response);
    }

    // ========================================================================
    // CAMBIAR ESTADO DEL CLIENTE (Activar/Desactivar)
    // ========================================================================
    private void cambiarEstadoCliente(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int userId = Integer.parseInt(request.getParameter("userId"));
            boolean nuevoEstado = Boolean.parseBoolean(request.getParameter("estado"));

            // Verificar que sea un cliente
            Usuario usuario = usuarioDao.obtenerPorId(userId);
            if (usuario == null || !"Cliente".equalsIgnoreCase(usuario.getRol())) {
                request.setAttribute("error", "Solo puedes gestionar clientes");
                listarClientes(request, response);
                return;
            }

            boolean cambiado = usuarioDao.cambiarEstado(userId, nuevoEstado);

            if (cambiado) {
                String accion = nuevoEstado ? "activado" : "desactivado";
                request.setAttribute("success", "✅ Cliente '" + usuario.getNombre() + "' " + accion + " exitosamente");
                System.out.println("✅ Cliente " + accion + ": ID " + userId);
            } else {
                request.setAttribute("error", "Error al cambiar el estado del cliente");
                System.out.println("❌ Error al cambiar estado: ID " + userId);
            }

        } catch (NumberFormatException e) {
            request.setAttribute("error", "ID de cliente inválido");
            e.printStackTrace();
        } catch (Exception e) {
            request.setAttribute("error", "Error inesperado: " + e.getMessage());
            e.printStackTrace();
        }

        listarClientes(request, response);
    }

    // ========================================================================
    // VERIFICAR QUE EL USUARIO SEA VETERINARIO
    // ========================================================================
    private boolean esVeterinario(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login.jsp");
            return false;
        }

        String rol = (String) session.getAttribute("rol");

        if (rol == null || !rol.equalsIgnoreCase("Veterinario")) {
            response.sendRedirect("login.jsp");
            return false;
        }

        return true;
    }
}