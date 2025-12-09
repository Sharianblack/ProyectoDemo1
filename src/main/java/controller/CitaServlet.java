package controller;

import model.Cita;
import dao.CitaDao;
import util.ValidacionUtil;
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

        // DEBUG: confirmar que la acción se invocó y desde qué usuario/rol
        try {
            System.out.println("→ listarCitasCliente invoked for userId=" + userId + " rol=" + session.getAttribute("rol"));
        } catch (Exception ignored) { }

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
            String idCitaStr = request.getParameter("idCita");
            String nuevoEstado = request.getParameter("nuevoEstado");

            // Validar parámetros
            if (!ValidacionUtil.noEstaVacio(idCitaStr)) {
                request.setAttribute("error", "ID de cita es requerido");
                listarCitas(request, response);
                return;
            }

            if (!ValidacionUtil.noEstaVacio(nuevoEstado)) {
                request.setAttribute("error", "Estado inválido");
                listarCitas(request, response);
                return;
            }

            // Validar que el estado sea uno válido
            if (!nuevoEstado.equals("Programada") && !nuevoEstado.equals("En Proceso") && 
                !nuevoEstado.equals("Completada") && !nuevoEstado.equals("Cancelada")) {
                request.setAttribute("error", "Estado no válido");
                listarCitas(request, response);
                return;
            }

            int idCita = Integer.parseInt(idCitaStr);

            if (!ValidacionUtil.esIdValido(idCita)) {
                request.setAttribute("error", "ID de cita inválido");
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
            String idCitaStr = request.getParameter("idCita");
            String observaciones = request.getParameter("observaciones");

            // Validar ID
            if (!ValidacionUtil.noEstaVacio(idCitaStr)) {
                request.setAttribute("error", "ID de cita es requerido");
                listarCitas(request, response);
                return;
            }

            int idCita = Integer.parseInt(idCitaStr);

            if (!ValidacionUtil.esIdValido(idCita)) {
                request.setAttribute("error", "ID de cita inválido");
                listarCitas(request, response);
                return;
            }

            // Sanitizar observaciones
            String observacionesSanitizadas = ValidacionUtil.sanitizar(observaciones != null ? observaciones : "");

            boolean actualizado = citaDao.actualizarObservaciones(idCita, observacionesSanitizadas);

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

            // Validar sesión
            if (vetId == null) {
                request.setAttribute("error", "Sesión inválida");
                response.sendRedirect("login.jsp");
                return;
            }

            // Obtener y validar parámetros
            String idClienteStr = request.getParameter("idCliente");
            String idMascotaStr = request.getParameter("idMascota");
            String idSucursalStr = request.getParameter("idSucursal");
            String fechaStr = request.getParameter("fecha");
            String horaStr = request.getParameter("hora");
            String observaciones = request.getParameter("observaciones");

            // Validar cliente
            if (!ValidacionUtil.noEstaVacio(idClienteStr)) {
                request.setAttribute("error", "Debe seleccionar un cliente");
                mostrarFormularioCrear(request, response);
                return;
            }

            // Validar que los campos obligatorios no estén vacíos
            if (!ValidacionUtil.noEstaVacio(idMascotaStr)) {
                request.setAttribute("error", "Debe seleccionar una mascota");
                mostrarFormularioCrear(request, response);
                return;
            }

            if (!ValidacionUtil.noEstaVacio(idSucursalStr)) {
                request.setAttribute("error", "Debe seleccionar una sucursal");
                mostrarFormularioCrear(request, response);
                return;
            }

            if (!ValidacionUtil.noEstaVacio(fechaStr)) {
                request.setAttribute("error", "La fecha es obligatoria");
                mostrarFormularioCrear(request, response);
                return;
            }

            if (!ValidacionUtil.noEstaVacio(horaStr)) {
                request.setAttribute("error", "La hora es obligatoria");
                mostrarFormularioCrear(request, response);
                return;
            }

            if (!ValidacionUtil.esFechaValida(fechaStr)) {
                request.setAttribute("error", "Fecha inválida. Use formato YYYY-MM-DD");
                mostrarFormularioCrear(request, response);
                return;
            }

            if (!ValidacionUtil.esHoraValida(horaStr)) {
                request.setAttribute("error", "Hora inválida. Use formato HH:MM");
                mostrarFormularioCrear(request, response);
                return;
            }

            // Convertir y validar IDs
            int idMascota = Integer.parseInt(idMascotaStr);
            int idSucursal = Integer.parseInt(idSucursalStr);

            if (!ValidacionUtil.esIdValido(idMascota) || !ValidacionUtil.esIdValido(idSucursal)) {
                request.setAttribute("error", "IDs inválidos");
                mostrarFormularioCrear(request, response);
                return;
            }

            // Sanitizar observaciones
            String observacionesSanitizadas = ValidacionUtil.sanitizar(observaciones != null ? observaciones : "");

            // Combinar fecha y hora
            String fechaHoraStr = fechaStr + " " + horaStr + ":00";
            Timestamp fechaCita = Timestamp.valueOf(fechaHoraStr);

            // Validar que la fecha no sea en el pasado
            Timestamp ahora = new Timestamp(System.currentTimeMillis());
            if (fechaCita.before(ahora)) {
                request.setAttribute("error", "No se pueden programar citas en el pasado. Seleccione una fecha y hora futuras");
                mostrarFormularioCrear(request, response);
                return;
            }

            // Crear objeto Cita
            Cita nuevaCita = new Cita();
            nuevaCita.setIdMascota(idMascota);
            nuevaCita.setIdVeterinario(vetId);
            nuevaCita.setIdSucursal(idSucursal);
            nuevaCita.setFechaCita(fechaCita);
            nuevaCita.setEstado("Programada");
            nuevaCita.setObservaciones(observacionesSanitizadas);

            boolean creada = citaDao.crearCita(nuevaCita);

            if (creada) {
                request.setAttribute("success", "Cita creada exitosamente");
                System.out.println("✓ Cita creada por veterinario ID: " + vetId);
            } else {
                request.setAttribute("error", "Error al crear la cita");
            }

        } catch (NumberFormatException e) {
            request.setAttribute("error", "Formato de número inválido");
            e.printStackTrace();
            mostrarFormularioCrear(request, response);
            return;
        } catch (Exception e) {
            request.setAttribute("error", "Error al procesar la cita: " + e.getMessage());
            e.printStackTrace();
            mostrarFormularioCrear(request, response);
            return;
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

            // Validar sesión
            if (vetId == null) {
                request.setAttribute("error", "Sesión inválida");
                response.sendRedirect("login.jsp");
                return;
            }

            // Obtener datos del formulario
            String idCitaStr = request.getParameter("idCita");
            String idMascotaStr = request.getParameter("idMascota");
            String idSucursalStr = request.getParameter("idSucursal");
            String fechaStr = request.getParameter("fecha");
            String horaStr = request.getParameter("hora");
            String estado = request.getParameter("estado");
            String observaciones = request.getParameter("observaciones");

            // Validar campos obligatorios
            if (!ValidacionUtil.noEstaVacio(idCitaStr) || !ValidacionUtil.noEstaVacio(idMascotaStr) ||
                !ValidacionUtil.noEstaVacio(idSucursalStr) || !ValidacionUtil.noEstaVacio(fechaStr) ||
                !ValidacionUtil.noEstaVacio(horaStr) || !ValidacionUtil.noEstaVacio(estado)) {
                request.setAttribute("error", "Todos los campos obligatorios deben estar completos");
                listarCitas(request, response);
                return;
            }

            // Validar formato de fecha y hora
            if (!ValidacionUtil.esFechaValida(fechaStr)) {
                request.setAttribute("error", "Fecha inválida");
                listarCitas(request, response);
                return;
            }

            if (!ValidacionUtil.esHoraValida(horaStr)) {
                request.setAttribute("error", "Hora inválida");
                listarCitas(request, response);
                return;
            }

            // Validar estado
            if (!estado.equals("Programada") && !estado.equals("En Proceso") &&
                !estado.equals("Completada") && !estado.equals("Cancelada")) {
                request.setAttribute("error", "Estado inválido");
                listarCitas(request, response);
                return;
            }

            int idCita = Integer.parseInt(idCitaStr);
            int idMascota = Integer.parseInt(idMascotaStr);
            int idSucursal = Integer.parseInt(idSucursalStr);

            // Validar IDs
            if (!ValidacionUtil.esIdValido(idCita) || !ValidacionUtil.esIdValido(idMascota) ||
                !ValidacionUtil.esIdValido(idSucursal)) {
                request.setAttribute("error", "IDs inválidos");
                listarCitas(request, response);
                return;
            }

            // Combinar fecha y hora
            String fechaHoraStr = fechaStr + " " + horaStr + ":00";
            Timestamp fechaCita = Timestamp.valueOf(fechaHoraStr);

            // Validar que la fecha no sea en el pasado (solo para citas en estado "Programada")
            if ("Programada".equals(estado)) {
                Timestamp ahora = new Timestamp(System.currentTimeMillis());
                if (fechaCita.before(ahora)) {
                    request.setAttribute("error", "No se pueden programar citas en el pasado. Seleccione una fecha y hora futuras");
                    listarCitas(request, response);
                    return;
                }
            }

            // Sanitizar observaciones
            String observacionesSanitizadas = ValidacionUtil.sanitizar(observaciones != null ? observaciones : "");

            // Crear objeto Cita con los nuevos datos
            Cita cita = new Cita();
            cita.setIdCita(idCita);
            cita.setIdMascota(idMascota);
            cita.setIdVeterinario(vetId);
            cita.setIdSucursal(idSucursal);
            cita.setFechaCita(fechaCita);
            cita.setEstado(estado);
            cita.setObservaciones(observacionesSanitizadas);

            boolean actualizada = citaDao.actualizarCita(cita);

            if (actualizada) {
                request.setAttribute("success", "Cita actualizada exitosamente");
                System.out.println("✓ Cita actualizada: ID " + idCita);
            } else {
                request.setAttribute("error", "Error al actualizar la cita");
            }

        } catch (NumberFormatException e) {
            request.setAttribute("error", "Formato de número inválido");
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            request.setAttribute("error", "Formato de fecha u hora inválido");
            e.printStackTrace();
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