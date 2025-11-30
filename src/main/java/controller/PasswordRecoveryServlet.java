package controller;

import dao.UsuarioDao;
import dao.RecoveryTokenDao;
import model.Usuario;
import util.EmailUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/PasswordRecoveryServlet")
public class PasswordRecoveryServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UsuarioDao usuarioDao;
    private RecoveryTokenDao tokenDao;

    @Override
    public void init() {
        usuarioDao = new UsuarioDao();
        tokenDao = new RecoveryTokenDao();
    }

    // ========================================================================
    // MÉTODO POST - Procesar solicitudes
    // ========================================================================
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if (action == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        switch (action) {
            case "solicitar":
                solicitarRecuperacion(request, response);
                break;
            case "resetear":
                resetearPassword(request, response);
                break;
            default:
                response.sendRedirect("login.jsp");
                break;
        }
    }

    // ========================================================================
    // SOLICITAR RECUPERACIÓN DE CONTRASEÑA
    // ========================================================================
    private void solicitarRecuperacion(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String correo = request.getParameter("correo");

        // Validar que el correo no esté vacío
        if (correo == null || correo.trim().isEmpty()) {
            request.setAttribute("error", "Por favor ingresa tu correo electrónico");
            request.getRequestDispatcher("solicitarRecuperacion.jsp").forward(request, response);
            return;
        }

        correo = correo.trim().toLowerCase();

        try {
            // Buscar usuario por correo
            Usuario usuario = usuarioDao.obtenerPorCorreo(correo);

            // IMPORTANTE: Por seguridad, siempre mostrar el mismo mensaje
            // sin revelar si el correo existe o no en la BD
            String mensajeExito = "Si el correo existe en nuestro sistema, recibirás un enlace de recuperación en los próximos minutos.";

            if (usuario != null && usuario.isActivo()) {
                // Usuario encontrado y activo - generar token y enviar correo

                // Obtener IP del cliente
                String ipCliente = request.getRemoteAddr();

                // Crear token
                String token = tokenDao.crearToken(usuario.getId(), ipCliente);

                if (token != null) {
                    // Construir URL base de la aplicación
                    String urlBase = request.getScheme() + "://" +
                            request.getServerName() + ":" +
                            request.getServerPort() +
                            request.getContextPath();

                    // Enviar correo
                    boolean enviado = EmailUtil.enviarCorreoRecuperacion(
                            usuario.getCorreo(),
                            usuario.getNombre(),
                            token,
                            urlBase
                    );

                    if (enviado) {
                        System.out.println("✓ Correo de recuperación enviado a: " + correo);
                    } else {
                        System.err.println("✗ Error al enviar correo a: " + correo);
                        // Pero no mostramos el error al usuario por seguridad
                    }
                } else {
                    System.err.println("✗ Error al crear token para: " + correo);
                }
            } else {
                // Usuario no existe o está inactivo
                System.out.println("✗ Intento de recuperación para correo inexistente: " + correo);
                // Pero mostramos el mismo mensaje por seguridad
            }

            // Mostrar mensaje de éxito (siempre el mismo por seguridad)
            request.setAttribute("success", mensajeExito);
            request.setAttribute("correo", correo);
            request.getRequestDispatcher("solicitarRecuperacion.jsp").forward(request, response);

        } catch (Exception e) {
            System.err.println("✗ Error en solicitarRecuperacion");
            e.printStackTrace();
            request.setAttribute("error", "Ocurrió un error al procesar tu solicitud. Inténtalo de nuevo.");
            request.getRequestDispatcher("solicitarRecuperacion.jsp").forward(request, response);
        }
    }

    // ========================================================================
    // RESETEAR CONTRASEÑA
    // ========================================================================
    private void resetearPassword(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String token = request.getParameter("token");
        String nuevaPassword = request.getParameter("nuevaPassword");
        String confirmarPassword = request.getParameter("confirmarPassword");

        // Validaciones básicas
        if (token == null || token.trim().isEmpty()) {
            request.setAttribute("error", "Token inválido");
            response.sendRedirect("login.jsp");
            return;
        }

        if (nuevaPassword == null || nuevaPassword.trim().isEmpty()) {
            request.setAttribute("error", "La contraseña no puede estar vacía");
            request.setAttribute("token", token);
            request.getRequestDispatcher("recuperarPassword.jsp").forward(request, response);
            return;
        }

        if (nuevaPassword.length() < 4) {
            request.setAttribute("error", "La contraseña debe tener al menos 4 caracteres");
            request.setAttribute("token", token);
            request.getRequestDispatcher("recuperarPassword.jsp").forward(request, response);
            return;
        }

        if (!nuevaPassword.equals(confirmarPassword)) {
            request.setAttribute("error", "Las contraseñas no coinciden");
            request.setAttribute("token", token);
            request.getRequestDispatcher("recuperarPassword.jsp").forward(request, response);
            return;
        }

        try {
            // Validar token
            int userId = tokenDao.validarToken(token);

            if (userId == -1) {
                request.setAttribute("error", "El enlace ha expirado o ya fue utilizado. Solicita uno nuevo.");
                request.getRequestDispatcher("solicitarRecuperacion.jsp").forward(request, response);
                return;
            }

            // Obtener usuario
            Usuario usuario = usuarioDao.obtenerPorId(userId);

            if (usuario == null) {
                request.setAttribute("error", "Usuario no encontrado");
                response.sendRedirect("login.jsp");
                return;
            }

            // Actualizar contraseña
            boolean actualizado = usuarioDao.actualizarPassword(userId, nuevaPassword);

            if (actualizado) {
                // Marcar token como usado
                tokenDao.marcarTokenUsado(token);

                // Enviar correo de confirmación
                EmailUtil.enviarCorreoConfirmacion(usuario.getCorreo(), usuario.getNombre());

                System.out.println("✓ Contraseña restablecida para usuario: " + usuario.getCorreo());

                // Redirigir al login con mensaje de éxito
                request.setAttribute("success", "Tu contraseña ha sido restablecida exitosamente. Ya puedes iniciar sesión.");
                request.getRequestDispatcher("login.jsp").forward(request, response);
            } else {
                request.setAttribute("error", "Error al actualizar la contraseña. Inténtalo de nuevo.");
                request.setAttribute("token", token);
                request.getRequestDispatcher("recuperarPassword.jsp").forward(request, response);
            }

        } catch (Exception e) {
            System.err.println("✗ Error en resetearPassword");
            e.printStackTrace();
            request.setAttribute("error", "Ocurrió un error al procesar tu solicitud. Inténtalo de nuevo.");
            request.setAttribute("token", token);
            request.getRequestDispatcher("recuperarPassword.jsp").forward(request, response);
        }
    }
}