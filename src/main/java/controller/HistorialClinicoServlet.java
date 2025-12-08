package controller;

import model.HistorialClinico;
import dao.HistorialClinicoDao;
import dao.CitaDao;
import util.ValidacionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

@WebServlet("/HistorialClinicoServlet")
public class HistorialClinicoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private HistorialClinicoDao historialDao;
    private CitaDao citaDao;

    @Override
    public void init() {
        historialDao = new HistorialClinicoDao();
        citaDao = new CitaDao();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!esVeterinario(request, response)) {
            return;
        }

        String action = request.getParameter("action");
        if (action == null) action = "listar";

        switch (action) {
            case "listar":
                listarHistorial(request, response);
                break;
            case "buscar":
                buscarHistorial(request, response);
                break;
            case "verDetalle":
                verDetalle(request, response);
                break;
            case "formCrear":
                mostrarFormularioCrear(request, response);
                break;
            case "formEditar":
                mostrarFormularioEditar(request, response);
                break;
            default:
                listarHistorial(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!esVeterinario(request, response)) {
            return;
        }

        String action = request.getParameter("action");

        if (action == null) {
            response.sendRedirect("HistorialClinicoServlet?action=listar");
            return;
        }

        switch (action) {
            case "crear":
                crearHistorial(request, response);
                break;
            case "actualizar":
                actualizarHistorial(request, response);
                break;
            default:
                response.sendRedirect("HistorialClinicoServlet?action=listar");
                break;
        }
    }

    private void listarHistorial(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Integer vetId = (Integer) session.getAttribute("userId");

        List<HistorialClinico> historiales = historialDao.obtenerHistorialVeterinario(vetId);

        request.setAttribute("historiales", historiales);
        request.setAttribute("totalRegistros", historiales.size());

        request.getRequestDispatcher("historialClinico.jsp").forward(request, response);
    }

    private void buscarHistorial(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Integer vetId = (Integer) session.getAttribute("userId");
        String criterio = request.getParameter("criterio");

        List<HistorialClinico> historiales;

        if (criterio != null && !criterio.trim().isEmpty()) {
            historiales = historialDao.buscarHistorial(vetId, criterio);
            request.setAttribute("criterio", criterio);
        } else {
            historiales = historialDao.obtenerHistorialVeterinario(vetId);
        }

        request.setAttribute("historiales", historiales);
        request.setAttribute("totalRegistros", historiales.size());

        request.getRequestDispatcher("historialClinico.jsp").forward(request, response);
    }

    private void verDetalle(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int idHistorial = Integer.parseInt(request.getParameter("id"));
            HistorialClinico historial = historialDao.obtenerHistorialPorId(idHistorial);

            if (historial == null) {
                request.setAttribute("error", "Registro no encontrado");
                listarHistorial(request, response);
                return;
            }

            request.setAttribute("historial", historial);
            request.getRequestDispatcher("detalleHistorial.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            request.setAttribute("error", "ID inválido");
            listarHistorial(request, response);
        }
    }

    private void mostrarFormularioCrear(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Integer vetId = (Integer) session.getAttribute("userId");

        // Obtener lista de pacientes del veterinario
        List<Object[]> pacientes = citaDao.obtenerPacientesVeterinario(vetId);

        request.setAttribute("pacientes", pacientes);
        request.setAttribute("accion", "crear");

        request.getRequestDispatcher("formHistorial.jsp").forward(request, response);
    }

    private void mostrarFormularioEditar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int idHistorial = Integer.parseInt(request.getParameter("id"));
            HistorialClinico historial = historialDao.obtenerHistorialPorId(idHistorial);

            if (historial == null) {
                request.setAttribute("error", "Registro no encontrado");
                listarHistorial(request, response);
                return;
            }

            request.setAttribute("historial", historial);
            request.setAttribute("accion", "editar");

            request.getRequestDispatcher("formHistorial.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            request.setAttribute("error", "ID inválido");
            listarHistorial(request, response);
        }
    }

    private void crearHistorial(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            HttpSession session = request.getSession();
            Integer vetId = (Integer) session.getAttribute("userId");

            // Obtener parámetros
            String idCitaStr = request.getParameter("idCita");
            String idMascotaStr = request.getParameter("idMascota");
            String fechaConsultaStr = request.getParameter("fechaConsulta");
            String motivoConsulta = request.getParameter("motivoConsulta");
            String diagnostico = request.getParameter("diagnostico");
            String tratamiento = request.getParameter("tratamiento");
            String medicamentos = request.getParameter("medicamentos");
            String pesoStr = request.getParameter("peso");
            String temperaturaStr = request.getParameter("temperatura");
            String observaciones = request.getParameter("observaciones");
            String proximaCitaStr = request.getParameter("proximaCita");

            // Validar campos obligatorios
            if (!ValidacionUtil.noEstaVacio(idMascotaStr)) {
                request.setAttribute("error", "Debe seleccionar una mascota");
                preservarDatosFormulario(request, idMascotaStr, fechaConsultaStr, motivoConsulta, 
                    diagnostico, tratamiento, medicamentos, pesoStr, temperaturaStr, observaciones, proximaCitaStr);
                mostrarFormularioCrear(request, response);
                return;
            }

            if (!ValidacionUtil.noEstaVacio(motivoConsulta)) {
                request.setAttribute("error", "El motivo de consulta es requerido");
                preservarDatosFormulario(request, idMascotaStr, fechaConsultaStr, motivoConsulta, 
                    diagnostico, tratamiento, medicamentos, pesoStr, temperaturaStr, observaciones, proximaCitaStr);
                mostrarFormularioCrear(request, response);
                return;
            }

            if (!ValidacionUtil.esTextoValido(motivoConsulta, 5, 1000)) {
                request.setAttribute("error", "El motivo de consulta debe tener entre 5 y 1000 caracteres");
                preservarDatosFormulario(request, idMascotaStr, fechaConsultaStr, motivoConsulta, 
                    diagnostico, tratamiento, medicamentos, pesoStr, temperaturaStr, observaciones, proximaCitaStr);
                mostrarFormularioCrear(request, response);
                return;
            }

            if (!ValidacionUtil.noEstaVacio(diagnostico)) {
                request.setAttribute("error", "El diagnóstico es requerido");
                preservarDatosFormulario(request, idMascotaStr, fechaConsultaStr, motivoConsulta, 
                    diagnostico, tratamiento, medicamentos, pesoStr, temperaturaStr, observaciones, proximaCitaStr);
                mostrarFormularioCrear(request, response);
                return;
            }

            if (!ValidacionUtil.esTextoValido(diagnostico, 5, 2000)) {
                request.setAttribute("error", "El diagnóstico debe tener entre 5 y 2000 caracteres");
                preservarDatosFormulario(request, idMascotaStr, fechaConsultaStr, motivoConsulta, 
                    diagnostico, tratamiento, medicamentos, pesoStr, temperaturaStr, observaciones, proximaCitaStr);
                mostrarFormularioCrear(request, response);
                return;
            }

            int idMascota = Integer.parseInt(idMascotaStr);
            
            if (!ValidacionUtil.esIdValido(idMascota)) {
                request.setAttribute("error", "ID de mascota inválido");
                preservarDatosFormulario(request, idMascotaStr, fechaConsultaStr, motivoConsulta, 
                    diagnostico, tratamiento, medicamentos, pesoStr, temperaturaStr, observaciones, proximaCitaStr);
                mostrarFormularioCrear(request, response);
                return;
            }
            
            // Obtener idCita o poner 0 si no se proporciona
            int idCita = 0;
            if (ValidacionUtil.noEstaVacio(idCitaStr)) {
                idCita = Integer.parseInt(idCitaStr);
            }

            // Crear objeto HistorialClinico
            HistorialClinico historial = new HistorialClinico();
            historial.setIdCita(idCita);
            historial.setIdMascota(idMascota);
            historial.setIdVeterinario(vetId);
            
            // Fecha de consulta (usar actual si no se proporciona)
            if (ValidacionUtil.noEstaVacio(fechaConsultaStr)) {
                historial.setFechaConsulta(Timestamp.valueOf(fechaConsultaStr + " 00:00:00"));
            } else {
                historial.setFechaConsulta(new Timestamp(System.currentTimeMillis()));
            }
            
            historial.setMotivoConsulta(ValidacionUtil.sanitizar(motivoConsulta));
            historial.setDiagnostico(ValidacionUtil.sanitizar(diagnostico));
            historial.setTratamiento(ValidacionUtil.sanitizar(tratamiento));
            historial.setMedicamentos(ValidacionUtil.sanitizar(medicamentos));
            
            // Peso
            if (ValidacionUtil.noEstaVacio(pesoStr)) {
                try {
                    BigDecimal peso = new BigDecimal(pesoStr);
                    if (peso.compareTo(BigDecimal.ZERO) > 0 && peso.compareTo(new BigDecimal("500")) <= 0) {
                        historial.setPesoKg(peso);
                    } else {
                        request.setAttribute("error", "El peso debe estar entre 0 y 500 kg");
                        preservarDatosFormulario(request, idMascotaStr, fechaConsultaStr, motivoConsulta, 
                            diagnostico, tratamiento, medicamentos, pesoStr, temperaturaStr, observaciones, proximaCitaStr);
                        mostrarFormularioCrear(request, response);
                        return;
                    }
                } catch (NumberFormatException e) {
                    request.setAttribute("error", "El peso debe ser un número válido");
                    preservarDatosFormulario(request, idMascotaStr, fechaConsultaStr, motivoConsulta, 
                        diagnostico, tratamiento, medicamentos, pesoStr, temperaturaStr, observaciones, proximaCitaStr);
                    mostrarFormularioCrear(request, response);
                    return;
                }
            }
            
            // Temperatura
            if (ValidacionUtil.noEstaVacio(temperaturaStr)) {
                try {
                    BigDecimal temperatura = new BigDecimal(temperaturaStr);
                    if (temperatura.compareTo(new BigDecimal("30")) >= 0 && temperatura.compareTo(new BigDecimal("45")) <= 0) {
                        historial.setTemperaturaC(temperatura);
                    } else {
                        request.setAttribute("error", "La temperatura debe estar entre 30 y 45 °C");
                        preservarDatosFormulario(request, idMascotaStr, fechaConsultaStr, motivoConsulta, 
                            diagnostico, tratamiento, medicamentos, pesoStr, temperaturaStr, observaciones, proximaCitaStr);
                        mostrarFormularioCrear(request, response);
                        return;
                    }
                } catch (NumberFormatException e) {
                    request.setAttribute("error", "La temperatura debe ser un número válido");
                    preservarDatosFormulario(request, idMascotaStr, fechaConsultaStr, motivoConsulta, 
                        diagnostico, tratamiento, medicamentos, pesoStr, temperaturaStr, observaciones, proximaCitaStr);
                    mostrarFormularioCrear(request, response);
                    return;
                }
            }
            
            historial.setObservaciones(ValidacionUtil.sanitizar(observaciones));
            
            // Próxima cita
            if (ValidacionUtil.esFechaValida(proximaCitaStr)) {
                historial.setProximaCita(Date.valueOf(proximaCitaStr));
            }

            boolean creado = historialDao.crearHistorial(historial);

            if (creado) {
                request.setAttribute("success", "Registro creado exitosamente en el historial clínico");
                limpiarAtributosFormulario(request);
                System.out.println("✓ Historial clínico creado por veterinario ID: " + vetId);
            } else {
                request.setAttribute("error", "Error al crear el registro");
            }

        } catch (NumberFormatException e) {
            request.setAttribute("error", "Formato de número inválido");
            e.printStackTrace();
            mostrarFormularioCrear(request, response);
            return;
        } catch (Exception e) {
            request.setAttribute("error", "Error al procesar el historial: " + e.getMessage());
            e.printStackTrace();
        }

        listarHistorial(request, response);
    }

    private void actualizarHistorial(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int idHistorial = Integer.parseInt(request.getParameter("idHistorial"));
            String motivoConsulta = request.getParameter("motivoConsulta");
            String diagnostico = request.getParameter("diagnostico");
            String tratamiento = request.getParameter("tratamiento");
            String medicamentos = request.getParameter("medicamentos");
            String pesoStr = request.getParameter("peso");
            String temperaturaStr = request.getParameter("temperatura");
            String observaciones = request.getParameter("observaciones");
            String proximaCitaStr = request.getParameter("proximaCita");

            // Validar campos obligatorios
            if (!ValidacionUtil.noEstaVacio(motivoConsulta)) {
                request.setAttribute("error", "El motivo de consulta es requerido");
                mostrarFormularioEditar(request, response);
                return;
            }

            if (!ValidacionUtil.esTextoValido(motivoConsulta, 5, 1000)) {
                request.setAttribute("error", "El motivo de consulta debe tener entre 5 y 1000 caracteres");
                mostrarFormularioEditar(request, response);
                return;
            }

            if (!ValidacionUtil.noEstaVacio(diagnostico)) {
                request.setAttribute("error", "El diagnóstico es requerido");
                mostrarFormularioEditar(request, response);
                return;
            }

            if (!ValidacionUtil.esTextoValido(diagnostico, 5, 2000)) {
                request.setAttribute("error", "El diagnóstico debe tener entre 5 y 2000 caracteres");
                mostrarFormularioEditar(request, response);
                return;
            }

            HistorialClinico historial = new HistorialClinico();
            historial.setIdHistorial(idHistorial);
            historial.setMotivoConsulta(ValidacionUtil.sanitizar(motivoConsulta));
            historial.setDiagnostico(ValidacionUtil.sanitizar(diagnostico));
            historial.setTratamiento(ValidacionUtil.sanitizar(tratamiento));
            historial.setMedicamentos(ValidacionUtil.sanitizar(medicamentos));
            
            // Peso
            if (ValidacionUtil.noEstaVacio(pesoStr)) {
                try {
                    historial.setPesoKg(new BigDecimal(pesoStr));
                } catch (NumberFormatException e) {
                    historial.setPesoKg(null);
                }
            }
            
            // Temperatura
            if (ValidacionUtil.noEstaVacio(temperaturaStr)) {
                try {
                    historial.setTemperaturaC(new BigDecimal(temperaturaStr));
                } catch (NumberFormatException e) {
                    historial.setTemperaturaC(null);
                }
            }
            
            historial.setObservaciones(ValidacionUtil.sanitizar(observaciones));
            
            if (ValidacionUtil.esFechaValida(proximaCitaStr)) {
                historial.setProximaCita(Date.valueOf(proximaCitaStr));
            }

            boolean actualizado = historialDao.actualizarHistorial(historial);

            if (actualizado) {
                request.setAttribute("success", "Registro actualizado exitosamente");
                limpiarAtributosFormulario(request);
                System.out.println("✓ Historial actualizado: ID " + idHistorial);
            } else {
                request.setAttribute("error", "Error al actualizar el registro");
            }

        } catch (Exception e) {
            request.setAttribute("error", "Error al procesar la actualización: " + e.getMessage());
            e.printStackTrace();
        }

        listarHistorial(request, response);
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

    private void preservarDatosFormulario(HttpServletRequest request, String idMascota, String fechaConsulta,
            String motivoConsulta, String diagnostico, String tratamiento, String medicamentos,
            String peso, String temperatura, String observaciones, String proximaCita) {
        
        request.setAttribute("formIdMascota", idMascota);
        request.setAttribute("formFechaConsulta", fechaConsulta);
        request.setAttribute("formMotivoConsulta", motivoConsulta);
        request.setAttribute("formDiagnostico", diagnostico);
        request.setAttribute("formTratamiento", tratamiento);
        request.setAttribute("formMedicamentos", medicamentos);
        request.setAttribute("formPeso", peso);
        request.setAttribute("formTemperatura", temperatura);
        request.setAttribute("formObservaciones", observaciones);
        request.setAttribute("formProximaCita", proximaCita);
    }

    private void limpiarAtributosFormulario(HttpServletRequest request) {
        request.removeAttribute("formIdMascota");
        request.removeAttribute("formFechaConsulta");
        request.removeAttribute("formMotivoConsulta");
        request.removeAttribute("formDiagnostico");
        request.removeAttribute("formTratamiento");
        request.removeAttribute("formMedicamentos");
        request.removeAttribute("formPeso");
        request.removeAttribute("formTemperatura");
        request.removeAttribute("formObservaciones");
        request.removeAttribute("formProximaCita");
    }
}
