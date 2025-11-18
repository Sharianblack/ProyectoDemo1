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
        // NOTA: Asumiendo que el campo del formulario sigue llamándose "username"
        // Si tu formulario usa "correo", cambia "username" en la siguiente línea.
        String correo = request.getParameter("username"); // <-- Variable renombrada a 'correo'
        String password = request.getParameter("password");

        System.out.println("Intento de login - Correo: " + correo); // <-- Usando 'correo'

        // Validar que los campos no estén vacíos
        if (correo == null || correo.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
            request.setAttribute("errorMessage", " Por favor complete todos los campos");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }

        // Validar usuario con la base de datos (UsuarioDao ya fue corregido para usar Correo y PasswordHash)
        Usuario user = userDAO.validateUser(correo, password);

        if (user != null) {
            // Login exitoso
            System.out.println(" Login exitoso para: " + correo);
            HttpSession session = request.getSession();
            session.setAttribute("user", user);

            // CAMBIO CLAVE: Usamos getCorreo() en lugar de getUsername()
            session.setAttribute("username", user.getCorreo());

            // El ID sigue siendo válido ya que se mantuvo el nombre 'id' en el modelo
            session.setAttribute("userId", user.getId());

            // También puedes guardar el Rol si lo necesitas:
            session.setAttribute("rol", user.getRol());

            response.sendRedirect("paginaInicio.jsp");
        }
        else
        {
            // Login fallido
            System.out.println(" Login fallido para: " + correo);
            request.setAttribute("errorMessage", " Usuario o contraseña incorrectos");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect("login.jsp");
    }
}