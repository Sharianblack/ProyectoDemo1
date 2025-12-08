<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Date" %>

<%
    // Verificar sesión y rol de veterinario
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
    Integer userId = (Integer) session.getAttribute("userId");

    @SuppressWarnings("unchecked")
    List<Object[]> pacientes = (List<Object[]>) request.getAttribute("pacientes");
    Integer totalPacientes = (Integer) request.getAttribute("totalPacientes");

    if (pacientes == null) pacientes = new java.util.ArrayList<>();
    if (totalPacientes == null) totalPacientes = 0;

    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    SimpleDateFormat sdfDateTime = new SimpleDateFormat("dd/MM/yyyy HH:mm");
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mis Pacientes - Sistema Veterinaria</title>
    <link rel="stylesheet" type="text/css" href="estilos/veterinario.css">
    <style>
        .patients-container {
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

        .header-section h2 {
            color: #2c3e50;
            margin: 0 0 0.5rem 0;
        }

        .stats-bar {
            display: flex;
            gap: 2rem;
            margin-top: 1rem;
            padding-top: 1rem;
            border-top: 2px solid #ecf0f1;
        }

        .stat-item {
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }

        .stat-number {
            font-size: 1.5rem;
            font-weight: bold;
            color: #27ae60;
        }

        .patients-table {
            background: white;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            overflow: hidden;
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
            font-size: 0.9rem;
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
            display: flex;
            flex-direction: column;
            gap: 0.25rem;
        }

        .mascota-nombre {
            font-weight: bold;
            color: #2c3e50;
            font-size: 1rem;
        }

        .mascota-detalles {
            color: #7f8c8d;
            font-size: 0.85rem;
        }

        .propietario-info {
            display: flex;
            flex-direction: column;
            gap: 0.25rem;
        }

        .propietario-nombre {
            font-weight: 600;
            color: #34495e;
        }

        .propietario-contacto {
            color: #7f8c8d;
            font-size: 0.85rem;
        }

        .badge {
            display: inline-block;
            padding: 0.25rem 0.75rem;
            border-radius: 12px;
            font-size: 0.8rem;
            font-weight: 600;
        }

        .badge-macho {
            background: #e3f2fd;
            color: #1976d2;
        }

        .badge-hembra {
            background: #fce4ec;
            color: #c2185b;
        }

        .citas-count {
            display: flex;
            align-items: center;
            gap: 0.5rem;
            color: #27ae60;
            font-weight: 600;
        }

        .no-data {
            text-align: center;
            padding: 3rem;
            color: #95a5a6;
        }

        .btn-back {
            display: inline-block;
            padding: 0.75rem 1.5rem;
            background: #95a5a6;
            color: white;
            text-decoration: none;
            border-radius: 5px;
            transition: background 0.3s;
        }

        .btn-back:hover {
            background: #7f8c8d;
        }

        @media (max-width: 768px) {
            .patients-container {
                padding: 0 1rem;
            }

            .stats-bar {
                flex-direction: column;
                gap: 1rem;
            }

            table {
                font-size: 0.8rem;
            }

            thead th, tbody td {
                padding: 0.5rem;
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

    <div class="patients-container">
        <div class="header-section">
            <h2>🐕 Mis Pacientes</h2>
            <p>Listado de todas las mascotas que han registrado citas contigo</p>
            
            <div class="stats-bar">
                <div class="stat-item">
                    <span>📊 Total de pacientes:</span>
                    <span class="stat-number"><%= totalPacientes %></span>
                </div>
            </div>
        </div>

        <% if (pacientes.isEmpty()) { %>
            <div class="patients-table">
                <div class="no-data">
                    <h3>No hay pacientes registrados</h3>
                    <p>Aún no tienes mascotas con citas asignadas</p>
                </div>
            </div>
        <% } else { %>
            <div class="patients-table">
                <table>
                    <thead>
                        <tr>
                            <th>Mascota</th>
                            <th>Sexo</th>
                            <th>Fecha de Nacimiento</th>
                            <th>Propietario</th>
                            <th>Citas</th>
                            <th>Última Visita</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (Object[] paciente : pacientes) {
                            Integer idMascota = (Integer) paciente[0];
                            String nombre = (String) paciente[1];
                            String especie = (String) paciente[2];
                            String raza = (String) paciente[3];
                            String sexo = (String) paciente[4];
                            Date fechaNacimiento = (Date) paciente[5];
                            String nombrePropietario = (String) paciente[6];
                            String correoPropietario = (String) paciente[7];
                            String telefonoPropietario = (String) paciente[8];
                            Integer totalCitas = (Integer) paciente[9];
                            Date ultimaCita = (Date) paciente[10];

                            String sexoDisplay = "";
                            String sexoClass = "";
                            if ("M".equals(sexo)) {
                                sexoDisplay = "♂ Macho";
                                sexoClass = "badge-macho";
                            } else if ("H".equals(sexo) || "F".equals(sexo)) {
                                sexoDisplay = "♀ Hembra";
                                sexoClass = "badge-hembra";
                            }
                        %>
                        <tr>
                            <td>
                                <div class="mascota-info">
                                    <span class="mascota-nombre"><%= nombre %></span>
                                    <span class="mascota-detalles">
                                        <%= especie %><% if (raza != null && !raza.isEmpty()) { %> - <%= raza %><% } %>
                                    </span>
                                </div>
                            </td>
                            <td>
                                <% if (!sexoDisplay.isEmpty()) { %>
                                    <span class="badge <%= sexoClass %>"><%= sexoDisplay %></span>
                                <% } %>
                            </td>
                            <td>
                                <%= fechaNacimiento != null ? sdf.format(fechaNacimiento) : "N/A" %>
                            </td>
                            <td>
                                <div class="propietario-info">
                                    <span class="propietario-nombre"><%= nombrePropietario %></span>
                                    <span class="propietario-contacto">
                                        📧 <%= correoPropietario != null ? correoPropietario : "" %>
                                    </span>
                                    <% if (telefonoPropietario != null && !telefonoPropietario.isEmpty()) { %>
                                        <span class="propietario-contacto">
                                            📱 <%= telefonoPropietario %>
                                        </span>
                                    <% } %>
                                </div>
                            </td>
                            <td>
                                <div class="citas-count">
                                    <span>📅</span>
                                    <span><%= totalCitas %> cita<%= totalCitas != 1 ? "s" : "" %></span>
                                </div>
                            </td>
                            <td>
                                <%= ultimaCita != null ? sdfDateTime.format(ultimaCita) : "N/A" %>
                            </td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        <% } %>

        <div style="margin-top: 2rem;">
            <a href="PIVeterinario.jsp" class="btn-back">← Volver al Panel</a>
        </div>
    </div>
</body>
</html>
