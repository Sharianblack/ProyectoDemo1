package controller;

import model.Cita;
import dao.CitaDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.sql.Timestamp;

import java.io.IOException;

import java.util.List;

@WebServlet("/CitaServlet")
public class CitaServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private CitaDao citaDao;

    @Override
    public void init() {
        citaDao = new CitaDao();
    }

    // ========================================================================
    // MÉTODO GET - Listar citas
    // ========================================================================
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Obtener acción y permitir acceso público para la acción 'misCitas' (clientes)
        String action = request.getParameter("action");
        if (action == null) action = "listar";

        // Si la acción solicitada es ver las citas del cliente, no forzamos el check de veterinario
        if ("misCitas".equals(action)) {
            listarCitasCliente(request, response);
            return;
        }

        // Verificar que sea Veterinario para el resto de acciones
        if (!esVeterinario(request, response)) {
            return;
        }

        switch (action) {
            case "listar":
                listarCitas(request, response);
                break;
            case "hoy":
                listarCitasHoy(request, response);
                break;
            case "filtrar":
                filtrarCitasPorEstado(request, response);
                break;
            case "formCrear":
                mostrarFormularioCrear(request, response);
                break;
            case "formEditar":
                mostrarFormularioEditar(request, response);
                break;
            case "getMascotas":
                obtenerMascotasJSON(request, response);
                break;
            default:
                listarCitas(request, response);
                break;
        }
    }

    // ========================================================================
    // MÉTODO POST - Actualizar citas
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
            response.sendRedirect("CitaServlet?action=listar");
            return;
        }

        switch (action) {
            case "crear":
                crearCita(request, response);
                break;
            case "actualizar":
                actualizarCita(request, response);
                break;
            case "eliminar":
                eliminarCita(request, response);
                break;
            case "cambiarEstado":
                cambiarEstadoCita(request, response);
                break;
            case "actualizarObservaciones":
                actualizarObservaciones(request, response);
                break;
            default:
                response.sendRedirect("CitaServlet?action=listar");
                break;
        }
    }

    // ========================================================================
    // LISTAR TODAS LAS CITAS DEL VETERINARIO
    // ========================================================================
    private void listarCitas(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");

        // Obtener todas las citas del veterinario
        List<Cita> citas = citaDao.obtenerCitasVeterinario(userId);

        // Contar citas por estado
        long programadas = citas.stream().filter(c -> c.getEstado().equalsIgnoreCase("Programada")).count();
        long enProceso = citas.stream().filter(c -> c.getEstado().equalsIgnoreCase("En Proceso")).count();
        long completadas = citas.stream().filter(c -> c.getEstado().equalsIgnoreCase("Completada")).count();
        long canceladas = citas.stream().filter(c -> c.getEstado().equalsIgnoreCase("Cancelada")).count();

        request.setAttribute("citas", citas);
        request.setAttribute("totalCitas", citas.size());
        request.setAttribute("programadas", programadas);
        request.setAttribute("enProceso", enProceso);
        request.setAttribute("completadas", completadas);
        request.setAttribute("canceladas", canceladas);

        request.getRequestDispatcher("misCitas.jsp").forward(request, response);
    }

    // ========================================================================
    // LISTAR CITAS DEL CLIENTE (MIS CITAS)
    // ========================================================================
    private void listarCitasCliente(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        List<Cita> citas = citaDao.obtenerCitasCliente(userId);

        request.setAttribute("citas", citas);
        request.setAttribute("totalCitas", citas.size());

        request.getRequestDispatcher("misCitas.jsp").forward(request, response);
    }

    // ========================================================================
    // LISTAR CITAS DE HOY
    // ========================================================================
    private void listarCitasHoy(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");

        List<Cita> citas = citaDao.obtenerCitasHoyVeterinario(userId);

        request.setAttribute("citas", citas);
        request.setAttribute("totalCitas", citas.size());
        request.setAttribute("filtro", "hoy");

        request.getRequestDispatcher("misCitas.jsp").forward(request, response);
    }

    // ========================================================================
    // FILTRAR CITAS POR ESTADO
    // ========================================================================
    private void filtrarCitasPorEstado(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        String estado = request.getParameter("estado");

        List<Cita> citas;

        if (estado != null && !estado.trim().isEmpty()) {
            citas = citaDao.obtenerCitasPorEstado(userId, estado);
            request.setAttribute("filtro", estado);
        } else {
            citas = citaDao.obtenerCitasVeterinario(userId);
        }

        request.setAttribute("citas", citas);
        request.setAttribute("totalCitas", citas.size());

        request.getRequestDispatcher("misCitas.jsp").forward(request, response);
    }

    // ========================================================================
    // CAMBIAR ESTADO DE CITA
    // ========================================================================
    private void cambiarEstadoCita(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int idCita = Integer.parseInt(request.getParameter("idCita"));
            String nuevoEstado = request.getParameter("nuevoEstado");

            if (nuevoEstado == null || nuevoEstado.trim().isEmpty()) {
                request.setAttribute("error", "Estado inválido");
                listarCitas(request, response);
                return;
            }

            boolean actualizado = citaDao.actualizarEstadoCita(idCita, nuevoEstado);

            if (actualizado) {
                request.setAttribute("success", "Estado de cita actualizado a: " + nuevoEstado);
                System.out.println("✓ Estado de cita actualizado: ID " + idCita);
            } else {
                request.setAttribute("error", "Error al actualizar el estado de la cita");
                System.out.println("✗ Error al actualizar estado de cita: ID " + idCita);
            }

        } catch (NumberFormatException e) {
            request.setAttribute("error", "ID de cita inválido");
            e.printStackTrace();
        } catch (Exception e) {
            request.setAttribute("error", "Error inesperado: " + e.getMessage());
            e.printStackTrace();
        }

        listarCitas(request, response);
    }

    // ========================================================================
    // ACTUALIZAR OBSERVACIONES
    // ========================================================================
    private void actualizarObservaciones(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int idCita = Integer.parseInt(request.getParameter("idCita"));
            String observaciones = request.getParameter("observaciones");

            boolean actualizado = citaDao.actualizarObservaciones(idCita, observaciones);

            if (actualizado) {
                request.setAttribute("success", "Observaciones actualizadas exitosamente");
                System.out.println("✓ Observaciones actualizadas: ID " + idCita);
            } else {
                request.setAttribute("error", "Error al actualizar observaciones");
                System.out.println("✗ Error al actualizar observaciones: ID " + idCita);
            }

        } catch (NumberFormatException e) {
            request.setAttribute("error", "ID de cita inválido");
            e.printStackTrace();
        } catch (Exception e) {
            request.setAttribute("error", "Error inesperado: " + e.getMessage());
            e.printStackTrace();
        }

        listarCitas(request, response);
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

    // ========================================================================
    // MOSTRAR FORMULARIO PARA CREAR CITA
    // ========================================================================
    private void mostrarFormularioCrear(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Obtener listas necesarias para el formulario
        List<String[]> clientes = citaDao.obtenerClientesConMascotas();
        List<String[]> sucursales = citaDao.obtenerSucursales();

        request.setAttribute("clientes", clientes);
        request.setAttribute("sucursales", sucursales);
        request.setAttribute("accion", "crear");

        request.getRequestDispatcher("formCita.jsp").forward(request, response);
    }

    // ========================================================================
    // MOSTRAR FORMULARIO PARA EDITAR CITA
    // ========================================================================
    private void mostrarFormularioEditar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int idCita = Integer.parseInt(request.getParameter("id"));

            Cita cita = citaDao.obtenerCitaPorId(idCita);

            if (cita == null) {
                request.setAttribute("error", "Cita no encontrada");
                listarCitas(request, response);
                return;
            }

            // Obtener listas necesarias
            List<String[]> clientes = citaDao.obtenerClientesConMascotas();
            List<String[]> sucursales = citaDao.obtenerSucursales();

            request.setAttribute("cita", cita);
            request.setAttribute("clientes", clientes);
            request.setAttribute("sucursales", sucursales);
            request.setAttribute("accion", "editar");

            request.getRequestDispatcher("formCita.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            request.setAttribute("error", "ID de cita inválido");
            listarCitas(request, response);
        }
    }

    // ========================================================================
    // CREAR NUEVA CITA
    // ========================================================================
    private void crearCita(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            HttpSession session = request.getSession();
            Integer vetId = (Integer) session.getAttribute("userId");

            // Obtener datos del formulario
            int idMascota = Integer.parseInt(request.getParameter("idMascota"));
            int idSucursal = Integer.parseInt(request.getParameter("idSucursal"));
            String fechaStr = request.getParameter("fecha");
            String horaStr = request.getParameter("hora");
            String observaciones = request.getParameter("observaciones");

            // Combinar fecha y hora
            String fechaHoraStr = fechaStr + " " + horaStr + ":00";
            Timestamp fechaCita = Timestamp.valueOf(fechaHoraStr);

            // Crear objeto Cita
            Cita nuevaCita = new Cita();
            nuevaCita.setIdMascota(idMascota);
            nuevaCita.setIdVeterinario(vetId);
            nuevaCita.setIdSucursal(idSucursal);
            nuevaCita.setFechaCita(fechaCita);
            nuevaCita.setEstado("Programada");
            nuevaCita.setObservaciones(observaciones);

            boolean creada = citaDao.crearCita(nuevaCita);

            if (creada) {
                request.setAttribute("success", "Cita creada exitosamente");
                System.out.println("✓ Cita creada por veterinario ID: " + vetId);
            } else {
                request.setAttribute("error", "Error al crear la cita");
            }

        } catch (Exception e) {
            request.setAttribute("error", "Error al procesar la cita: " + e.getMessage());
            e.printStackTrace();
        }

        listarCitas(request, response);
    }

    // ========================================================================
    // ACTUALIZAR CITA EXISTENTE
    // ========================================================================
    private void actualizarCita(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            HttpSession session = request.getSession();
            Integer vetId = (Integer) session.getAttribute("userId");

            // Obtener datos del formulario
            int idCita = Integer.parseInt(request.getParameter("idCita"));
            int idMascota = Integer.parseInt(request.getParameter("idMascota"));
            int idSucursal = Integer.parseInt(request.getParameter("idSucursal"));
            String fechaStr = request.getParameter("fecha");
            String horaStr = request.getParameter("hora");
            String estado = request.getParameter("estado");
            String observaciones = request.getParameter("observaciones");

            // Combinar fecha y hora
            String fechaHoraStr = fechaStr + " " + horaStr + ":00";
            Timestamp fechaCita = Timestamp.valueOf(fechaHoraStr);

            // Crear objeto Cita con los nuevos datos
            Cita cita = new Cita();
            cita.setIdCita(idCita);
            cita.setIdMascota(idMascota);
            cita.setIdVeterinario(vetId);
            cita.setIdSucursal(idSucursal);
            cita.setFechaCita(fechaCita);
            cita.setEstado(estado);
            cita.setObservaciones(observaciones);

            boolean actualizada = citaDao.actualizarCita(cita);

            if (actualizada) {
                request.setAttribute("success", "Cita actualizada exitosamente");
                System.out.println("✓ Cita actualizada: ID " + idCita);
            } else {
                request.setAttribute("error", "Error al actualizar la cita");
            }

        } catch (Exception e) {
            request.setAttribute("error", "Error al procesar la cita: " + e.getMessage());
            e.printStackTrace();
        }

        listarCitas(request, response);
    }

    // ========================================================================
    // ELIMINAR CITA
    // ========================================================================
    private void eliminarCita(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int idCita = Integer.parseInt(request.getParameter("idCita"));

            boolean eliminada = citaDao.eliminarCita(idCita);

            if (eliminada) {
                request.setAttribute("success", "Cita eliminada exitosamente");
                System.out.println("✓ Cita eliminada: ID " + idCita);
            } else {
                request.setAttribute("error", "Error al eliminar la cita");
            }

        } catch (NumberFormatException e) {
            request.setAttribute("error", "ID de cita inválido");
            e.printStackTrace();
        }

        listarCitas(request, response);
    }

    // ========================================================================
    // OBTENER MASCOTAS EN FORMATO JSON (AJAX)
    // ========================================================================
    private void obtenerMascotasJSON(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int idCliente = Integer.parseInt(request.getParameter("idCliente"));

            List<String[]> mascotas = citaDao.obtenerMascotasCliente(idCliente);

            // Crear JSON manualmente
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < mascotas.size(); i++) {
                String[] mascota = mascotas.get(i);
                if (i > 0) json.append(",");
                json.append("{")
                        .append("\"id\":").append(mascota[0]).append(",")
                        .append("\"nombre\":\"").append(mascota[1]).append("\",")
                        .append("\"especie\":\"").append(mascota[2]).append("\",")
                        .append("\"raza\":\"").append(mascota[3] != null ? mascota[3] : "").append("\"")
                        .append("}");
            }
            json.append("]");

            response.getWriter().write(json.toString());

        } catch (NumberFormatException e) {
            response.setContentType("application/json");
            response.getWriter().write("[]");
        }
    }
}