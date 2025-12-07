package controller;

import dao.EmailVerificationTokenDao;
import dao.UsuarioDao;
import model.Usuario;
import util.EmailUtil;
import util.EmailValidator;
import util.PasswordUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/EmailVerificationServlet")
public class EmailVerificationServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private EmailVerificationTokenDao tokenDao;
    private UsuarioDao usuarioDao;

    @Override
    public void init() {
        tokenDao = new EmailVerificationTokenDao();
        usuarioDao = new UsuarioDao();
    }

    // ========================================================================
    // GET - Procesar verificación de email con token
    // ========================================================================
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String token = request.getParameter("token");

        if (token == null || token.trim().isEmpty()) {
            request.setAttribute("error", "Token de verificación no válido");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }

        verificarEmail(request, response, token);
    }

    // ========================================================================
    // POST - Solicitar nueva verificación
    // ========================================================================
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("solicitar".equals(action)) {
            solicitarVerificacion(request, response);
        } else {
            response.sendRedirect("login.jsp");
        }
    }

    // ========================================================================
    // SOLICITAR VERIFICACIÓN (Registro inicial)
    // ========================================================================
    private void solicitarVerificacion(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Obtener datos del formulario
            String nombre = request.getParameter("nombre");
            String correo = request.getParameter("correo");
            String password = request.getParameter("password");
            String confirmarPassword = request.getParameter("confirmarPassword");
            String rol = request.getParameter("rol");
            String telefono = request.getParameter("telefono");
            String direccion = request.getParameter("direccion");

            // Si no hay rol, asumir Cliente
            if (rol == null || rol.trim().isEmpty()) {
                rol = "Cliente";
            }

            // Validaciones básicas
            if (nombre == null || nombre.trim().isEmpty() ||
                    correo == null || correo.trim().isEmpty() ||
                    password == null || password.trim().isEmpty()) {

                request.setAttribute("error", "Todos los campos obligatorios deben estar completos");
                request.getRequestDispatcher("registro.jsp").forward(request, response);
                return;
            }

            // Validar que las contraseñas coincidan
            if (confirmarPassword != null && !password.equals(confirmarPassword)) {
                request.setAttribute("error", "Las contraseñas no coinciden");
                request.getRequestDispatcher("registro.jsp").forward(request, response);
                return;
            }

            // Validar longitud de contraseña
            if (password.length() < 4) {
                request.setAttribute("error", "La contraseña debe tener al menos 4 caracteres");
                request.getRequestDispatcher("registro.jsp").forward(request, response);
                return;
            }

            // Validar formato de email
            String mensajeValidacion = EmailValidator.validarConMensaje(correo);
            if (mensajeValidacion != null) {
                request.setAttribute("error", "❌ " + mensajeValidacion);
                request.getRequestDispatcher("registro.jsp").forward(request, response);
                return;
            }

            // Rechazar emails temporales
            if (EmailValidator.esEmailTemporal(correo)) {
                request.setAttribute("error", "❌ No se permiten correos temporales o desechables");
                request.getRequestDispatcher("registro.jsp").forward(request, response);
                return;
            }

            // Normalizar correo
            correo = EmailValidator.normalizar(correo);

            // Verificar si el correo ya está registrado
            if (usuarioDao.correoExiste(correo)) {
                request.setAttribute("error", "El correo '" + correo + "' ya está registrado");
                request.getRequestDispatcher("registro.jsp").forward(request, response);
                return;
            }

            // Verificar si ya tiene un token pendiente
            if (tokenDao.tienTokenPendiente(correo)) {
                request.setAttribute("warning",
                        "Ya enviamos un correo de verificación a " + correo + ". " +
                                "Revisa tu bandeja de entrada (o SPAM). El enlace es válido por 24 horas.");
                request.getRequestDispatcher("registro.jsp").forward(request, response);
                return;
            }

            // Encriptar contraseña
            String passwordHash = PasswordUtil.hashPassword(password);

            // Obtener IP del cliente
            String ipCliente = request.getRemoteAddr();

            // Crear token de verificación
            String token = tokenDao.crearTokenVerificacion(
                    correo,
                    nombre.trim(),
                    passwordHash,
                    rol,
                    telefono != null ? telefono.trim() : "",
                    direccion != null ? direccion.trim() : "",
                    ipCliente
            );

            if (token != null) {
                // Construir URL base
                String urlBase = request.getScheme() + "://" +
                        request.getServerName() + ":" +
                        request.getServerPort() +
                        request.getContextPath();

                // Enviar correo de verificación
                boolean enviado = EmailUtil.enviarCorreoVerificacion(
                        correo,
                        nombre.trim(),
                        token,
                        urlBase
                );

                if (enviado) {
                    request.setAttribute("success",
                            "✅ ¡Registro casi completo! Hemos enviado un correo de verificación a <strong>" + correo + "</strong>. " +
                                    "Por favor revisa tu bandeja de entrada (o SPAM) y haz clic en el enlace para activar tu cuenta.");
                    System.out.println("✅ Correo de verificación enviado a: " + correo);
                } else {
                    request.setAttribute("error",
                            "Error al enviar el correo de verificación. Verifica tu dirección de email e inténtalo nuevamente.");
                    System.out.println("❌ Error al enviar correo a: " + correo);
                }
            } else {
                request.setAttribute("error", "Error al crear el token de verificación. Inténtalo nuevamente.");
            }

        } catch (Exception e) {
            request.setAttribute("error", "Error inesperado: " + e.getMessage());
            e.printStackTrace();
        }

        request.getRequestDispatcher("registro.jsp").forward(request, response);
    }

    // ========================================================================
    // VERIFICAR EMAIL CON TOKEN
    // ========================================================================
    private void verificarEmail(HttpServletRequest request, HttpServletResponse response, String token)
            throws ServletException, IOException {

        try {
            // Validar token y obtener datos del usuario
            String[] datos = tokenDao.validarYObtenerDatos(token);

            if (datos == null) {
                request.setAttribute("error",
                        "❌ El enlace de verificación ha expirado o ya fue utilizado. " +
                                "Por favor solicita uno nuevo desde la página de registro.");
                request.getRequestDispatcher("login.jsp").forward(request, response);
                return;
            }

            // Extraer datos
            String email = datos[0];
            String nombre = datos[1];
            String passwordHash = datos[2];
            String rol = datos[3];
            String telefono = datos[4];
            String direccion = datos[5];

            // Verificar nuevamente que el correo no exista (por si acaso)
            if (usuarioDao.correoExiste(email)) {
                request.setAttribute("error", "Este correo ya está registrado en el sistema");
                request.getRequestDispatcher("login.jsp").forward(request, response);
                return;
            }

            // Crear usuario en la base de datos
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setNombre(nombre);
            nuevoUsuario.setCorreo(email);
            nuevoUsuario.setPasswordHash(passwordHash); // Ya está hasheado
            nuevoUsuario.setRol(rol);
            nuevoUsuario.setTelefono(telefono);
            nuevoUsuario.setDireccion(direccion);
            nuevoUsuario.setActivo(true);

            // IMPORTANTE: Usar método especial que NO hashea de nuevo
            boolean creado = usuarioDao.crearUsuarioConHashExistente(nuevoUsuario);

            if (creado) {
                // Marcar token como usado
                tokenDao.marcarTokenUsado(token);

                // Enviar correo de confirmación
                String urlLogin = request.getScheme() + "://" +
                        request.getServerName() + ":" +
                        request.getServerPort() +
                        request.getContextPath() + "/login.jsp";

                EmailUtil.enviarCorreoEmailVerificado(email, nombre, urlLogin);

                // Mostrar mensaje de éxito
                request.setAttribute("success",
                        "🎉 ¡Tu correo ha sido verificado exitosamente! " +
                                "Tu cuenta está ahora activa. Puedes iniciar sesión con tu correo y contraseña.");

                System.out.println("✅ Usuario creado y email verificado: " + email);
            } else {
                request.setAttribute("error", "Error al crear tu cuenta. Por favor contacta al administrador.");
                System.out.println("❌ Error al crear usuario tras verificación: " + email);
            }

        } catch (Exception e) {
            request.setAttribute("error", "Error al procesar la verificación: " + e.getMessage());
            e.printStackTrace();
        }

        request.getRequestDispatcher("login.jsp").forward(request, response);
    }
}