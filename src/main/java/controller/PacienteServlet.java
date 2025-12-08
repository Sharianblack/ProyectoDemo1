package controller;

import dao.CitaDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/PacienteServlet")
public class PacienteServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private CitaDao citaDao;

    @Override
    public void init() {
        citaDao = new CitaDao();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Verificar que sea Veterinario
        if (!esVeterinario(request, response)) {
            return;
        }

        String action = request.getParameter("action");
        if (action == null) action = "listar";

        if ("listar".equals(action)) {
            listarPacientes(request, response);
        } else {
            listarPacientes(request, response);
        }
    }

    private void listarPacientes(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Integer vetId = (Integer) session.getAttribute("userId");

        // Obtener todos los pacientes (mascotas) que han tenido citas con este veterinario
        List<Object[]> pacientes = citaDao.obtenerPacientesVeterinario(vetId);

        request.setAttribute("pacientes", pacientes);
        request.setAttribute("totalPacientes", pacientes.size());

        request.getRequestDispatcher("pacientes.jsp").forward(request, response);
    }

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
