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
    
    @SuppressWarnings("unchecked")
    List<HistorialClinico> historiales = (List<HistorialClinico>) request.getAttribute("historiales");
    Integer totalRegistros = (Integer) request.getAttribute("totalRegistros");
    String criterio = (String) request.getAttribute("criterio");
    String error = (String) request.getAttribute("error");
    String success = (String) request.getAttribute("success");

    if (historiales == null) historiales = new java.util.ArrayList<>();
    if (totalRegistros == null) totalRegistros = 0;

    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Historial Clínico - Sistema Veterinaria</title>
    <link rel="stylesheet" type="text/css" href="estilos/veterinario.css">
    <style>
        .historial-container {
            max-width: 1400px;
            margin: 2rem auto;
            padding: 0 2rem;
        }

        .header-section {
            background: white;
            padding: 2rem;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            margin-bottom: 2rem;
        }

        .search-section {
            display: flex;
            gap: 1rem;
            margin-top: 1.5rem;
            align-items: center;
        }

        .search-input {
            flex: 1;
            padding: 0.75rem;
            border: 2px solid #e0e0e0;
            border-radius: 5px;
            font-size: 0.95rem;
        }

        .btn {
            padding: 0.75rem 1.5rem;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-weight: 600;
            transition: all 0.3s;
            text-decoration: none;
            display: inline-block;
        }

        .btn-primary {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
        }

        .btn-primary:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
        }

        .btn-secondary {
            background: #95a5a6;
            color: white;
        }

        .btn-secondary:hover {
            background: #7f8c8d;
        }

        .btn-success {
            background: #27ae60;
            color: white;
        }

        .btn-edit {
            background: #3498db;
            color: white;
            padding: 0.5rem 1rem;
            font-size: 0.85rem;
        }

        .historial-table {
            background: white;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            overflow: hidden;
            margin-top: 2rem;
        }

        table {
            width: 100%;
            border-collapse: collapse;
        }

        thead {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
        }

        thead th {
            padding: 1rem;
            text-align: left;
            font-weight: 600;
        }

        tbody tr {
            border-bottom: 1px solid #ecf0f1;
            transition: background-color 0.2s;
        }

        tbody tr:hover {
            background-color: #f8f9fa;
        }

        tbody td {
            padding: 1rem;
            font-size: 0.9rem;
        }

        .mascota-info {
            font-weight: bold;
            color: #2c3e50;
        }

        .propietario {
            color: #7f8c8d;
            font-size: 0.85rem;
        }

        .diagnostico-preview {
            max-width: 300px;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
        }

        .alert {
            padding: 1rem;
            border-radius: 5px;
            margin-bottom: 1rem;
        }

        .alert-error {
            background: #fee;
            color: #c00;
            border-left: 4px solid #c00;
        }

        .alert-success {
            background: #efe;
            color: #060;
            border-left: 4px solid #060;
        }

        .no-data {
            text-align: center;
            padding: 3rem;
            color: #95a5a6;
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

    <div class="historial-container">
        <div class="header-section">
            <h2>📋 Historial Clínico</h2>
            <p>Registro de todas las consultas y tratamientos veterinarios</p>

            <% if (error != null) { %>
                <div class="alert alert-error"><%= error %></div>
            <% } %>
            
            <% if (success != null) { %>
                <div class="alert alert-success"><%= success %></div>
            <% } %>

            <div class="search-section">
                <form action="HistorialClinicoServlet" method="get" style="display: flex; gap: 1rem; flex: 1;">
                    <input type="hidden" name="action" value="buscar">
                    <input type="text" name="criterio" class="search-input" 
                           placeholder="🔍 Buscar por mascota, propietario, diagnóstico o motivo..." 
                           value="<%= criterio != null ? criterio : "" %>">
                    <button type="submit" class="btn btn-primary">Buscar</button>
                    <% if (criterio != null) { %>
                        <a href="HistorialClinicoServlet?action=listar" class="btn btn-secondary">Limpiar</a>
                    <% } %>
                </form>
                <a href="HistorialClinicoServlet?action=formCrear" class="btn btn-success">+ Nuevo Registro</a>
            </div>

            <p style="margin-top: 1rem; color: #7f8c8d;">
                Total de registros: <strong><%= totalRegistros %></strong>
            </p>
        </div>

        <% if (historiales.isEmpty()) { %>
            <div class="historial-table">
                <div class="no-data">
                    <h3>No hay registros en el historial clínico</h3>
                    <p>Comienza agregando consultas médicas de tus pacientes</p>
                </div>
            </div>
        <% } else { %>
            <div class="historial-table">
                <table>
                    <thead>
                        <tr>
                            <th>Fecha</th>
                            <th>Paciente</th>
                            <th>Propietario</th>
                            <th>Motivo</th>
                            <th>Diagnóstico</th>
                            <th>Signos Vitales</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (HistorialClinico h : historiales) { %>
                        <tr>
                            <td><%= sdf.format(h.getFechaConsulta()) %></td>
                            <td>
                                <div class="mascota-info"><%= h.getNombreMascota() %></div>
                                <div class="propietario"><%= h.getEspecieMascota() %><% if (h.getRazaMascota() != null) { %> - <%= h.getRazaMascota() %><% } %></div>
                            </td>
                            <td><%= h.getNombrePropietario() %></td>
                            <td><div class="diagnostico-preview"><%= h.getMotivoConsulta() %></div></td>
                            <td><div class="diagnostico-preview"><%= h.getDiagnostico() %></div></td>
                            <td>
                                <% if (h.getPesoKg() != null) { %>
                                    ⚖️ <%= h.getPesoKg() %> kg<br>
                                <% } %>
                                <% if (h.getTemperaturaC() != null) { %>
                                    🌡️ <%= h.getTemperaturaC() %> °C
                                <% } %>
                                <% if (h.getPesoKg() == null && h.getTemperaturaC() == null) { %>
                                    -
                                <% } %>
                            </td>
                            <td>
                                <a href="HistorialClinicoServlet?action=verDetalle&id=<%= h.getIdHistorial() %>" 
                                   class="btn btn-primary" style="padding: 0.5rem 1rem; font-size: 0.85rem; margin-right: 0.5rem;">Ver</a>
                                <a href="HistorialClinicoServlet?action=formEditar&id=<%= h.getIdHistorial() %>" 
                                   class="btn btn-edit">Editar</a>
                            </td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        <% } %>

        <div style="margin-top: 2rem;">
            <a href="PIVeterinario.jsp" class="btn btn-secondary">← Volver al Panel</a>
        </div>
    </div>

    <script>
        // Auto-cerrar alertas
        setTimeout(() => {
            const alerts = document.querySelectorAll('.alert');
            alerts.forEach(alert => {
                alert.style.transition = 'opacity 0.5s';
                alert.style.opacity = '0';
                setTimeout(() => alert.remove(), 500);
            });
        }, 5000);
    </script>
</body>
</html>
