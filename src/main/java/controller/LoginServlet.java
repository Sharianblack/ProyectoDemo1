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

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UsuarioDao userDAO;

    public void init() {
        userDAO = new UsuarioDao();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Obtener parámetros del formulario
        String correo = request.getParameter("username");
        String password = request.getParameter("password");

        System.out.println("Intento de login - Correo: " + correo);

        // Validar que los campos no estén vacíos
        if (correo == null || correo.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Por favor complete todos los campos");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }

        // Validar usuario con la base de datos
        Usuario user = userDAO.validateUser(correo, password);

        if (user != null) {
            // Login exitoso
            System.out.println("✓ Login exitoso para: " + correo + " - Rol: " + user.getRol());

            // Crear sesión y guardar datos del usuario
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            session.setAttribute("username", user.getCorreo());
            session.setAttribute("nombre", user.getNombre());
            session.setAttribute("userId", user.getId());
            session.setAttribute("rol", user.getRol());

            // Redirección según el rol del usuario
            String rol = user.getRol().trim().toLowerCase();

            switch (rol) {
                case "admin":
                    System.out.println("→ Redirigiendo a Panel de Administrador");
                    response.sendRedirect("PIAdmin.jsp");
                    break;

                case "cliente":
                    System.out.println("→ Redirigiendo a Panel de Cliente");
                    response.sendRedirect("paginaInicio.jsp");
                    break;

                case "veterinario":
                    System.out.println("→ Redirigiendo a Panel de Veterinario");
                    response.sendRedirect("PIVeterinario.jsp");
                    break;

                default:
                    // Si el rol no coincide con ninguno esperado
                    System.out.println("✗ Rol desconocido: '" + user.getRol() + "'");
                    session.invalidate();
                    request.setAttribute("errorMessage", "El rol '" + user.getRol() + "' no está configurado. Contacte al administrador.");
                    request.getRequestDispatcher("login.jsp").forward(request, response);
                    break;
            }
        }
        else {
            // Login fallido
            System.out.println("✗ Login fallido para: " + correo);
            request.setAttribute("errorMessage", "Usuario o contraseña incorrectos");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect("login.jsp");
    }
}