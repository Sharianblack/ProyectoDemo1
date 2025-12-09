<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="model.Cita" %>

<%
    // ========================================================================
    // BLOQUE DE SEGURIDAD - VETERINARIOS O CLIENTES (permitir que el cliente vea "Mis Citas")
    // ========================================================================
    if (session == null || session.getAttribute("user") == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    String rol = (String) session.getAttribute("rol");
    if (rol == null || !(rol.equalsIgnoreCase("Veterinario") || rol.equalsIgnoreCase("Cliente"))) {
        response.sendRedirect("login.jsp");
        return;
    }

    String nombreUsuario = (String) session.getAttribute("nombre");
    Integer userId = (Integer) session.getAttribute("userId");
    Integer vetId = null;
    if (rol.equalsIgnoreCase("Veterinario")) {
        vetId = userId;
    }

    // Obtener lista de citas
    @SuppressWarnings("unchecked")
    List<Cita> citas = (List<Cita>) request.getAttribute("citas");

    // Obtener mensajes y estadísticas
    String success = (String) request.getAttribute("success");
    String error = (String) request.getAttribute("error");
    String filtro = (String) request.getAttribute("filtro");

    Integer totalCitas = (Integer) request.getAttribute("totalCitas");
    Long programadas = (Long) request.getAttribute("programadas");
    Long enProceso = (Long) request.getAttribute("enProceso");
    Long completadas = (Long) request.getAttribute("completadas");
    Long canceladas = (Long) request.getAttribute("canceladas");
    // ========================================================================
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mis Citas</title>

    <link rel="stylesheet" type="text/css" href="estilos/veterinario_citas.css">

</head>
<body>
<nav class="navbar">
    <h1>Mis Citas</h1>
    <div class="user-info">
        <span><strong><%= nombreUsuario %></strong></span>
        <% if ("Veterinario".equalsIgnoreCase(rol)) { %>
            <span class="vet-badge">VETERINARIO</span>
            <a href="PIVeterinario.jsp" class="btn-volver">← Volver al Panel</a>
        <% } else { %>
            <span class="client-badge">CLIENTE</span>
            <a href="paginaInicio.jsp" class="btn-volver">← Volver al Panel</a>
        <% } %>
    </div>
</nav>

<div class="container">
    <div class="page-header">
        <div class="header-content">
            <div>
                <h2>Gestión de Citas Veterinarias</h2>
                <p style="color: #666; margin-top: 0.5rem;">Administra tus citas, actualiza estados y añade observaciones</p>
            </div>
            <% if ("Veterinario".equalsIgnoreCase(rol)) { %>
            <a href="CitaServlet?action=formCrear" class="btn-nueva-cita">
                + Nueva Cita
            </a>
            <% } %>
        </div>
    </div>

    <% if (programadas != null) { %>
    <div class="stats-grid">
        <div class="stat-card">
            <div class="stat-number stat-programada"><%= programadas %></div>
            <div class="stat-label">Programadas</div>
        </div>
        <div class="stat-card">
            <div class="stat-number stat-proceso"><%= enProceso %></div>
            <div class="stat-label">En Proceso</div>
        </div>
        <div class="stat-card">
            <div class="stat-number stat-completada"><%= completadas %></div>
            <div class="stat-label">Completadas</div>
        </div>
        <div class="stat-card">
            <div class="stat-number stat-cancelada"><%= canceladas %></div>
            <div class="stat-label">Canceladas</div>
        </div>
    </div>
    <% } %>

    <% if (success != null) { %>
    <div class="alert alert-success">
        <%= success %>
    </div>
    <% } %>

    <% if (error != null) { %>
    <div class="alert alert-error">
        <%= error %>
    </div>
    <% } %>

    <div class="table-container">
        <% if (citas != null && !citas.isEmpty()) { %>
        <table>
            <thead>
            <tr>
                <th>Fecha y Hora</th>
                <th>Mascota</th>
                <th>Cliente</th>
                <th>Contacto</th>
                <th>Sucursal</th>
                <th>Estado</th>
                <th>Acciones</th>
            </tr>
            </thead>
            <tbody>
            <% for (Cita c : citas) { %>
            <tr>
                <td>
                    <strong><%= c.getFechaSola() %></strong><br>
                    <span style="color: #666;"><%= c.getHora() %></span>
                    <% if (c.esHoy()) { %>
                    <br><span style="color: #f5576c; font-weight: bold;">HOY</span>
                    <% } %>
                </td>
                <td>
                    <strong><%= c.getNombreMascota() %></strong><br>
                    <span style="color: #666;"><%= c.getEspecieMascota() %></span>
                </td>
                <td><%= c.getNombreCliente() %></td>
                <td>
                    <%= c.getTelefonoCliente() != null ? c.getTelefonoCliente() : "-" %><br>
                    <small style="color: #666;"><%= c.getCorreoCliente() %></small>
                </td>
                <td><%= c.getNombreSucursal() %></td>
                <td><%= c.getEstadoBadge() %></td>
                <td>
                    <% if ("Veterinario".equalsIgnoreCase(rol)) { %>
                    <div class="action-buttons">
                        <% if (!"Completada".equals(c.getEstado()) && !"Cancelada".equals(c.getEstado())) { %>
                        <button class="btn-action btn-proceso"
                                onclick="cambiarEstado(<%= c.getIdCita() %>, &quot;En Proceso&quot;)">
                            En Proceso
                        </button>
                        <button class="btn-action btn-completar"
                                onclick="cambiarEstado(<%= c.getIdCita() %>, &quot;Completada&quot;)">
                            Completar
                        </button>
                        <% } %>
                        <a href="CitaServlet?action=formEditar&id=<%= c.getIdCita() %>"
                           class="btn-action btn-editar-yellow">
                            Editar
                        </a>
                        <%
                          String obsVal = c.getObservaciones() != null ? c.getObservaciones().replace("'", "\\'").replace("\n", "\\n").replace("\r", "") : "";
                        %>
                        <button class="btn-action btn-observar"
                                onclick="abrirModalObservaciones(<%= c.getIdCita() %>, '<%= obsVal %>')">
                            Observaciones
                        </button>
                        <% if ("Programada".equals(c.getEstado())) { %>
                        <button class="btn-action btn-cancelar"
                                onclick="if(confirm(&quot;¿Estás seguro de eliminar esta cita?&quot;)) window.location.href=&quot;CitaServlet?action=eliminar&amp;id=<%= c.getIdCita() %>&quot;">
                            Eliminar
                        </button>
                        <% } %>
                    </div>
                    <% } %>
                </td>
            </tr>
            <% } %>
            </tbody>
        </table>
        <% } else { %>
        <div class="empty-state">
            <div class="empty-icon">·</div>
            <h3>No hay citas registradas</h3>
            <p>No tienes citas <%= filtro != null ? "con el filtro seleccionado" : "en este momento" %></p>
        </div>
        <% } %>
    </div>

    <div class="citas-grid">
        <% if (citas != null && !citas.isEmpty()) { %>
        <% for (Cita c : citas) { %>
        <div class="cita-card">
            <div class="cita-header">
                <div class="cita-fecha">
                    <%= c.getFechaFormateada() %>
                    <% if (c.esHoy()) { %>
                    <br><span style="color: #f5576c;">HOY</span>
                    <% } %>
                </div>
                <%= c.getEstadoBadge() %>
            </div>
            <div class="cita-info">
                <strong>Mascota:</strong> <%= c.getNombreMascota() %> (<%= c.getEspecieMascota() %>)
            </div>
            <div class="cita-info">
                <strong>Cliente:</strong> <%= c.getNombreCliente() %>
            </div>
            <div class="cita-info">
                <strong>Teléfono:</strong> <%= c.getTelefonoCliente() != null ? c.getTelefonoCliente() : "-" %>
            </div>
            <div class="cita-info">
                <strong>Sucursal:</strong> <%= c.getNombreSucursal() %>
            </div>
            <% if ("Veterinario".equalsIgnoreCase(rol)) { %>
            <div class="action-buttons">
                <% if (!"Completada".equals(c.getEstado()) && !"Cancelada".equals(c.getEstado())) { %>
                <button class="btn-action btn-proceso"
                        onclick="cambiarEstado(<%= c.getIdCita() %>, 'En Proceso')">
                    En Proceso
                </button>
                <button class="btn-action btn-completar"
                        onclick="cambiarEstado(<%= c.getIdCita() %>, 'Completada')">
                    Completar
                </button>
                <% } %>
                <a href="CitaServlet?action=formEditar&id=<%= c.getIdCita() %>"
                   class="btn-action btn-editar-yellow">
                    Editar
                </a>
                <button class="btn-action btn-observar"
                        onclick="abrirModalObservaciones(<%= c.getIdCita() %>, '<%= c.getObservaciones() != null ? c.getObservaciones().replace("'", "\\'").replace("\n", "\\n") : "" %>')">
                    Observaciones
                </button>
                <% if ("Programada".equals(c.getEstado())) { %>
                <button class="btn-action btn-cancelar"
                        onclick="eliminarCita(<%= c.getIdCita() %>)">
                    Eliminar
                </button>
                <% } %>
            </div>
            <% } %>
        </div>
        <% } %>
        <% } %>
    </div>
</div>

<div id="modalObservaciones" class="modal">
    <div class="modal-content">
        <div class="modal-header">
            <h3>Observaciones de la Cita</h3>
            <span class="close" onclick="cerrarModal()">&times;</span>
        </div>
        <form action="CitaServlet" method="post">
            <input type="hidden" name="action" value="actualizarObservaciones">
            <input type="hidden" name="idCita" id="modalIdCita">

            <textarea name="observaciones" id="txtObservaciones"
                      placeholder="Escribe las observaciones de la cita aquí..."></textarea>

            <button type="submit" class="btn-submit">Guardar Observaciones</button>
        </form>
    </div>
</div>

<script>
    // Cambiar estado de cita
    function cambiarEstado(idCita, nuevoEstado) {
        if (confirm('¿Cambiar el estado de la cita a "' + nuevoEstado + '"?')) {
            const form = document.createElement('form');
            form.method = 'POST';
            form.action = 'CitaServlet';

            const actionInput = document.createElement('input');
            actionInput.type = 'hidden';
            actionInput.name = 'action';
            actionInput.value = 'cambiarEstado';

            const idInput = document.createElement('input');
            idInput.type = 'hidden';
            idInput.name = 'idCita';
            idInput.value = idCita;

            const estadoInput = document.createElement('input');
            estadoInput.type = 'hidden';
            estadoInput.name = 'nuevoEstado';
            estadoInput.value = nuevoEstado;

            form.appendChild(actionInput);
            form.appendChild(idInput);
            form.appendChild(estadoInput);

            document.body.appendChild(form);
            form.submit();
        }
    }

    // Eliminar cita
    function eliminarCita(idCita) {
        if (confirm('¿Estás seguro de eliminar esta cita? Esta acción no se puede deshacer.')) {
            const form = document.createElement('form');
            form.method = 'POST';
            form.action = 'CitaServlet';

            const actionInput = document.createElement('input');
            actionInput.type = 'hidden';
            actionInput.name = 'action';
            actionInput.value = 'eliminar';

            const idInput = document.createElement('input');
            idInput.type = 'hidden';
            idInput.name = 'idCita';
            idInput.value = idCita;

            form.appendChild(actionInput);
            form.appendChild(idInput);

            document.body.appendChild(form);
            form.submit();
        }
    }

    // Abrir modal de observaciones
    function abrirModalObservaciones(idCita, observaciones) {
        document.getElementById('modalIdCita').value = idCita;
        // Se usa el nuevo ID corregido
        document.getElementById('txtObservaciones').value = observaciones || '';
        document.getElementById('modalObservaciones').style.display = 'block';
    }

    // Cerrar modal
    function cerrarModal() {
        document.getElementById('modalObservaciones').style.display = 'none';
    }

    // Cerrar modal al hacer clic fuera
    window.onclick = function(event) {
        const modal = document.getElementById('modalObservaciones');
        if (event.target == modal) {
            cerrarModal();
        }
    }

    // Auto-cerrar alertas
    setTimeout(() => {
        const alerts = document.querySelectorAll('.alert');
        alerts.forEach(alert => {
            alert.style.opacity = '0';
            setTimeout(() => alert.remove(), 300);
        });
    }, 5000);
</script>
</body>
</html>