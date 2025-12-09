<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
    HistorialClinico historial = (HistorialClinico) request.getAttribute("historial");

    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Detalle del Historial - Sistema Veterinaria</title>
    <link rel="stylesheet" type="text/css" href="estilos/veterinario.css">
    <style>
        .detalle-container {
            max-width: 900px;
            margin: 2rem auto;
            padding: 0 2rem;
        }

        .detalle-card {
            background: white;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            padding: 2rem;
            margin-bottom: 1.5rem;
        }

        .detalle-header {
            border-bottom: 3px solid #667eea;
            padding-bottom: 1rem;
            margin-bottom: 1.5rem;
        }

        .detalle-header h2 {
            color: #2c3e50;
            margin: 0 0 0.5rem 0;
        }

        .detalle-meta {
            color: #7f8c8d;
            font-size: 0.9rem;
        }

        .info-section {
            margin-bottom: 2rem;
        }

        .info-section h3 {
            color: #34495e;
            border-left: 4px solid #667eea;
            padding-left: 1rem;
            margin-bottom: 1rem;
        }

        .info-row {
            display: grid;
            grid-template-columns: 150px 1fr;
            padding: 0.75rem 0;
            border-bottom: 1px solid #ecf0f1;
        }

        .info-label {
            font-weight: 600;
            color: #7f8c8d;
        }

        .info-value {
            color: #2c3e50;
        }

        .text-content {
            background: #f8f9fa;
            padding: 1rem;
            border-radius: 5px;
            border-left: 4px solid #3498db;
            margin-top: 0.5rem;
            line-height: 1.6;
            white-space: pre-wrap;
        }

        .vital-signs {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 1rem;
            margin-top: 1rem;
        }

        .vital-card {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 1.5rem;
            border-radius: 8px;
            text-align: center;
        }

        .vital-icon {
            font-size: 2rem;
            margin-bottom: 0.5rem;
        }

        .vital-value {
            font-size: 1.5rem;
            font-weight: bold;
        }

        .vital-label {
            font-size: 0.85rem;
            opacity: 0.9;
        }

        .btn {
            display: inline-block;
            padding: 0.75rem 1.5rem;
            border-radius: 5px;
            text-decoration: none;
            font-weight: 600;
            transition: all 0.3s;
            margin-right: 0.5rem;
        }

        .btn-back {
            background: #95a5a6;
            color: white;
        }

        .btn-edit {
            background: #3498db;
            color: white;
        }

        .btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(0,0,0,0.2);
        }

        .actions {
            margin-top: 2rem;
            padding-top: 1.5rem;
            border-top: 2px solid #ecf0f1;
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

    <div class="detalle-container">
        <% if (historial == null) { %>
            <div class="detalle-card">
                <h2 style="color: #e74c3c;">Registro no encontrado</h2>
                <p>El registro solicitado no existe o no tienes permiso para verlo.</p>
                <a href="HistorialClinicoServlet?action=listar" class="btn btn-back">← Volver</a>
            </div>
        <% } else { %>

            <div class="detalle-card">
                <div class="detalle-header">
                    <h2>📋 Registro Médico - Historial Clínico</h2>
                    <div class="detalle-meta">
                        Fecha de consulta: <strong><%= sdf.format(historial.getFechaConsulta()) %></strong> | 
                        Registro ID: <strong>#<%= historial.getIdHistorial() %></strong>
                    </div>
                </div>

                <div class="info-section">
                    <h3>🐾 Información del Paciente</h3>
                    <div class="info-row">
                        <div class="info-label">Mascota:</div>
                        <div class="info-value"><strong><%= historial.getNombreMascota() %></strong></div>
                    </div>
                    <div class="info-row">
                        <div class="info-label">Especie/Raza:</div>
                        <div class="info-value">
                            <%= historial.getEspecieMascota() %>
                            <% if (historial.getRazaMascota() != null && !historial.getRazaMascota().isEmpty()) { %>
                                - <%= historial.getRazaMascota() %>
                            <% } %>
                        </div>
                    </div>
                    <div class="info-row">
                        <div class="info-label">Propietario:</div>
                        <div class="info-value"><%= historial.getNombrePropietario() %></div>
                    </div>
                </div>

                <div class="info-section">
                    <h3>🩺 Signos Vitales</h3>
                    <div class="vital-signs">
                        <% if (historial.getPesoKg() != null) { %>
                            <div class="vital-card">
                                <div class="vital-icon">⚖️</div>
                                <div class="vital-value"><%= historial.getPesoKg() %> kg</div>
                                <div class="vital-label">Peso</div>
                            </div>
                        <% } %>
                        
                        <% if (historial.getTemperaturaC() != null) { %>
                            <div class="vital-card">
                                <div class="vital-icon">🌡️</div>
                                <div class="vital-value"><%= historial.getTemperaturaC() %> °C</div>
                                <div class="vital-label">Temperatura</div>
                            </div>
                        <% } %>
                        
                        <% if (historial.getPesoKg() == null && historial.getTemperaturaC() == null) { %>
                            <p style="color: #95a5a6;">No se registraron signos vitales</p>
                        <% } %>
                    </div>
                </div>

                <div class="info-section">
                    <h3>📝 Motivo de Consulta</h3>
                    <div class="text-content"><%= historial.getMotivoConsulta() %></div>
                </div>

                <div class="info-section">
                    <h3>🔬 Diagnóstico</h3>
                    <div class="text-content"><%= historial.getDiagnostico() %></div>
                </div>

                <% if (historial.getTratamiento() != null && !historial.getTratamiento().isEmpty()) { %>
                <div class="info-section">
                    <h3>💉 Tratamiento Aplicado</h3>
                    <div class="text-content"><%= historial.getTratamiento() %></div>
                </div>
                <% } %>

                <% if (historial.getMedicamentos() != null && !historial.getMedicamentos().isEmpty()) { %>
                <div class="info-section">
                    <h3>💊 Medicamentos Recetados</h3>
                    <div class="text-content"><%= historial.getMedicamentos() %></div>
                </div>
                <% } %>

                <% if (historial.getObservaciones() != null && !historial.getObservaciones().isEmpty()) { %>
                <div class="info-section">
                    <h3>📌 Observaciones Adicionales</h3>
                    <div class="text-content"><%= historial.getObservaciones() %></div>
                </div>
                <% } %>

                <% if (historial.getProximaCita() != null) { %>
                <div class="info-section">
                    <h3>📅 Seguimiento</h3>
                    <div class="info-row">
                        <div class="info-label">Próxima Cita:</div>
                        <div class="info-value"><strong><%= sdfDate.format(historial.getProximaCita()) %></strong></div>
                    </div>
                </div>
                <% } %>

                <div class="info-section">
                    <h3>👨‍⚕️ Veterinario Responsable</h3>
                    <div class="info-row">
                        <div class="info-label">Dr./Dra.:</div>
                        <div class="info-value"><%= historial.getNombreVeterinario() %></div>
                    </div>
                </div>

                <div class="actions">
                    <a href="HistorialClinicoServlet?action=listar" class="btn btn-back">← Volver al Historial</a>
                    <a href="HistorialClinicoServlet?action=formEditar&id=<%= historial.getIdHistorial() %>" class="btn btn-edit">✏️ Editar Registro</a>
                </div>
            </div>

        <% } %>
    </div>
</body>
</html>
