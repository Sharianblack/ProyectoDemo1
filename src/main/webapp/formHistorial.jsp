<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="model.HistorialClinico" %>
<%@ page import="java.text.SimpleDateFormat" %>

<%
    if (session == null || session.getAttribute("user") == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    String rol = (String) session.getAttribute("rol");
    if (rol == null || !rol.equalsIgnoreCase("Veterinario")) {
        response.sendRedirect("login.jsp");
        return;
    }

    String nombreUsuario = (String) session.getAttribute("nombre");
    String accion = (String) request.getAttribute("accion");
    HistorialClinico historial = (HistorialClinico) request.getAttribute("historial");
    
    @SuppressWarnings("unchecked")
    List<Object[]> pacientes = (List<Object[]>) request.getAttribute("pacientes");
    
    boolean esEditar = "editar".equals(accion);
    
    // Obtener errores y datos del formulario
    String error = (String) request.getAttribute("error");
    String formIdMascota = (String) request.getAttribute("formIdMascota");
    String formFechaConsulta = (String) request.getAttribute("formFechaConsulta");
    String formMotivoConsulta = (String) request.getAttribute("formMotivoConsulta");
    String formDiagnostico = (String) request.getAttribute("formDiagnostico");
    String formTratamiento = (String) request.getAttribute("formTratamiento");
    String formMedicamentos = (String) request.getAttribute("formMedicamentos");
    String formPeso = (String) request.getAttribute("formPeso");
    String formTemperatura = (String) request.getAttribute("formTemperatura");
    String formObservaciones = (String) request.getAttribute("formObservaciones");
    String formProximaCita = (String) request.getAttribute("formProximaCita");
    
    SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat sdfDateTime = new SimpleDateFormat("yyyy-MM-dd");
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= esEditar ? "Editar" : "Crear" %> Registro - Historial Clínico</title>
    <link rel="stylesheet" type="text/css" href="estilos/veterinario_form.css">
    <style>
        body {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
        }

        .form-container {
            max-width: 900px;
            margin: 2rem auto;
            background: white;
            border-radius: 15px;
            padding: 2.5rem;
            box-shadow: 0 10px 40px rgba(0,0,0,0.2);
        }

        .form-container h2 {
            color: #2c3e50;
            margin-bottom: 0.5rem;
            font-size: 2rem;
        }

        .form-container > p {
            color: #7f8c8d;
            margin-bottom: 2rem;
            font-size: 1rem;
        }

        .form-section {
            background: #f8f9fa;
            padding: 1.5rem;
            border-radius: 10px;
            margin-bottom: 1.5rem;
            border-left: 4px solid #667eea;
        }

        .form-section h3 {
            color: #34495e;
            margin: 0 0 1.5rem 0;
            font-size: 1.3rem;
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }

        .form-section p {
            margin: 0.5rem 0;
            color: #2c3e50;
        }

        .form-group {
            margin-bottom: 1.5rem;
        }

        .form-group label {
            display: block;
            margin-bottom: 0.5rem;
            color: #2c3e50;
            font-weight: 600;
            font-size: 0.95rem;
        }

        .form-group input[type="text"],
        .form-group input[type="date"],
        .form-group input[type="number"],
        .form-group select,
        .form-group textarea {
            width: 100%;
            padding: 0.75rem;
            border: 2px solid #e0e0e0;
            border-radius: 8px;
            font-size: 0.95rem;
            transition: all 0.3s;
            font-family: inherit;
        }

        .form-group input:focus,
        .form-group select:focus,
        .form-group textarea:focus {
            outline: none;
            border-color: #667eea;
            box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
        }

        .form-group textarea {
            resize: vertical;
            min-height: 100px;
        }

        .form-group small {
            display: block;
            margin-top: 0.25rem;
            color: #7f8c8d;
            font-size: 0.85rem;
        }

        .form-row {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 1.5rem;
        }

        .info-box {
            background: #e3f2fd;
            border-left: 4px solid #2196f3;
            padding: 1rem;
            border-radius: 5px;
            margin-bottom: 1.5rem;
        }

        .info-box p {
            margin: 0;
            color: #1976d2;
        }

        .form-actions {
            display: flex;
            gap: 1rem;
            justify-content: flex-end;
            margin-top: 2rem;
            padding-top: 2rem;
            border-top: 2px solid #ecf0f1;
        }

        .btn-cancel,
        .btn-submit {
            padding: 0.875rem 2rem;
            border: none;
            border-radius: 8px;
            font-size: 1rem;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s;
            text-decoration: none;
            display: inline-block;
        }

        .btn-cancel {
            background: #95a5a6;
            color: white;
        }

        .btn-cancel:hover {
            background: #7f8c8d;
            transform: translateY(-2px);
        }

        .btn-submit {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
        }

        .btn-submit:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
        }

        @media (max-width: 768px) {
            .form-container {
                margin: 1rem;
                padding: 1.5rem;
            }

            .form-row {
                grid-template-columns: 1fr;
            }

            .form-actions {
                flex-direction: column-reverse;
            }

            .btn-cancel,
            .btn-submit {
                width: 100%;
            }
        }
    </style>
</head>
<body>
    <nav class="navbar">
        <h1>🩺 Portal Veterinario</h1>
        <div class="user-info">
            <span>Dr./Dra. <strong><%= nombreUsuario != null ? nombreUsuario : "Usuario" %></strong></span>
            <span class="vet-badge">⚕️ VETERINARIO</span>
            <a href="LogoutServlet" class="logout-btn">Cerrar Sesión</a>
        </div>
    </nav>

    <div class="form-container">
        <h2><%= esEditar ? "📝 Editar Registro Médico" : "➕ Nuevo Registro Médico" %></h2>
        <p>Complete la información de la consulta veterinaria</p>

        <% if (error != null) { %>
            <div style="background: #fee; color: #c00; border-left: 4px solid #c00; padding: 1rem; border-radius: 5px; margin-bottom: 1.5rem;">
                <strong>⚠️ Error:</strong> <%= error %>
            </div>
        <% } %>

        <form action="HistorialClinicoServlet" method="post">
            <input type="hidden" name="action" value="<%= esEditar ? "actualizar" : "crear" %>">
            <% if (esEditar && historial != null) { %>
                <input type="hidden" name="idHistorial" value="<%= historial.getIdHistorial() %>">
            <% } %>

            <% if (!esEditar) { %>
            <div class="form-section">
                <h3>🐾 Paciente</h3>
                <div class="form-group">
                    <label for="idMascota">Seleccionar Mascota <span style="color: #f5576c;">*</span></label>
                    <select name="idMascota" id="idMascota">
                        <option value="">-- Seleccionar mascota --</option>
                        <% if (pacientes != null) {
                            for (Object[] p : pacientes) {
                                Integer idMascota = (Integer) p[0];
                                String nombre = (String) p[1];
                                String especie = (String) p[2];
                                String nombreProp = (String) p[6];
                                boolean selected = formIdMascota != null && formIdMascota.equals(String.valueOf(idMascota));
                        %>
                            <option value="<%= idMascota %>" <%= selected ? "selected" : "" %>>🐕 <%= nombre %> (<%= especie %>) - 👤 <%= nombreProp %></option>
                        <% }} %>
                    </select>
                </div>

                <div class="form-group">
                    <label for="fechaConsulta">Fecha de Consulta</label>
                    <input type="date" name="fechaConsulta" id="fechaConsulta" 
                           value="<%= formFechaConsulta != null ? formFechaConsulta : new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()) %>">
                    <small>Dejar vacío para usar la fecha actual</small>
                </div>
            </div>
            <% } else { %>
                <div class="form-section">
                    <h3>🐾 Información del Paciente</h3>
                    <p><strong>🐕 Mascota:</strong> <%= historial.getNombreMascota() %> (<%= historial.getEspecieMascota() %>)</p>
                    <p><strong>👤 Propietario:</strong> <%= historial.getNombrePropietario() %></p>
                    <p><strong>📅 Fecha de Consulta:</strong> <%= sdfDateTime.format(historial.getFechaConsulta()) %></p>
                </div>
            <% } %>

            <div class="form-section">
                <h3>📝 Información de la Consulta</h3>

                <div class="form-group">
                    <label for="motivoConsulta">Motivo de Consulta <span style="color: #f5576c;">*</span></label>
                    <textarea name="motivoConsulta" id="motivoConsulta" rows="3"
                              placeholder="Describir el motivo de la visita (ej: Control rutinario, vacunación, síntomas observados...)"><%= formMotivoConsulta != null ? formMotivoConsulta : (esEditar && historial != null ? historial.getMotivoConsulta() : "") %></textarea>
                </div>

                <div class="form-group">
                    <label for="diagnostico">Diagnóstico <span style="color: #f5576c;">*</span></label>
                    <textarea name="diagnostico" id="diagnostico" rows="4"
                              placeholder="Diagnóstico médico veterinario detallado..."><%= formDiagnostico != null ? formDiagnostico : (esEditar && historial != null ? historial.getDiagnostico() : "") %></textarea>
                </div>

                <div class="form-group">
                    <label for="tratamiento">Tratamiento Aplicado</label>
                    <textarea name="tratamiento" id="tratamiento" rows="4"
                              placeholder="Describir procedimientos, cirugías o tratamientos realizados (opcional)..."><%= formTratamiento != null ? formTratamiento : (esEditar && historial != null && historial.getTratamiento() != null ? historial.getTratamiento() : "") %></textarea>
                </div>

                <div class="form-group">
                    <label for="medicamentos">Medicamentos Recetados</label>
                    <textarea name="medicamentos" id="medicamentos" rows="3"
                              placeholder="Ejemplo: Amoxicilina 500mg cada 8h por 7 días, Carprofeno 75mg cada 12h por 5 días..."><%= formMedicamentos != null ? formMedicamentos : (esEditar && historial != null && historial.getMedicamentos() != null ? historial.getMedicamentos() : "") %></textarea>
                </div>
            </div>

            <div class="form-section">
                <h3>🩺 Signos Vitales</h3>

                <div class="form-row">
                    <div class="form-group">
                        <label for="peso">⚖️ Peso (kg)</label>
                        <input type="number" name="peso" id="peso" step="0.01"
                               placeholder="15.5"
                               value="<%= formPeso != null ? formPeso : (esEditar && historial != null && historial.getPesoKg() != null ? historial.getPesoKg() : "") %>">
                        <small>Peso en kilogramos</small>
                    </div>

                    <div class="form-group">
                        <label for="temperatura">🌡️ Temperatura (°C)</label>
                        <input type="number" name="temperatura" id="temperatura" step="0.1"
                               placeholder="38.5"
                               value="<%= formTemperatura != null ? formTemperatura : (esEditar && historial != null && historial.getTemperaturaC() != null ? historial.getTemperaturaC() : "") %>">
                        <small>Temperatura en grados Celsius</small>
                    </div>
                </div>
            </div>

            <div class="form-section">
                <h3>📅 Seguimiento</h3>

                <div class="form-group">
                    <label for="proximaCita">Próxima Cita de Seguimiento</label>
                    <input type="date" name="proximaCita" id="proximaCita"
                           value="<%= formProximaCita != null ? formProximaCita : (esEditar && historial != null && historial.getProximaCita() != null ? sdfDate.format(historial.getProximaCita()) : "") %>">
                    <small>Fecha sugerida para el próximo control</small>
                </div>

                <div class="form-group">
                    <label for="observaciones">Observaciones Adicionales</label>
                    <textarea name="observaciones" id="observaciones" rows="3"
                              placeholder="Notas adicionales, recomendaciones para el propietario, cuidados especiales..."><%= formObservaciones != null ? formObservaciones : (esEditar && historial != null && historial.getObservaciones() != null ? historial.getObservaciones() : "") %></textarea>
                </div>
            </div>

            <div class="info-box">
                <p><strong>ℹ️ Información:</strong> Los campos marcados con <span style="color: #f5576c;">*</span> son obligatorios. Los demás son opcionales pero recomendados para un historial completo.</p>
            </div>

            <div class="form-actions">
                <a href="HistorialClinicoServlet?action=listar" class="btn-cancel">✖ Cancelar</a>
                <button type="submit" class="btn-submit">
                    <%= esEditar ? "💾 Guardar Cambios" : "✓ Crear Registro" %>
                </button>
            </div>
        </form>
    </div>
</body>
</html>
