<%--
  Created by IntelliJ IDEA.
  User: Usuario
  Date: 01/12/2025
  Time: 9:26
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="model.Usuario" %>

<%
    // ========================================================================
    // BLOQUE DE SEGURIDAD Y LÓGICA (Sin cambios)
    // ========================================================================
    if (session == null || session.getAttribute("user") == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    String rol = (String) session.getAttribute("rol");
    if (rol == null || !rol.equalsIgnoreCase("Veterinario")) {
        response.sendRedirect("login.jsp");
        return;
    }

    String nombreVeterinario = (String) session.getAttribute("nombre");

    @SuppressWarnings("unchecked")
    List<Usuario> clientes = (List<Usuario>) request.getAttribute("clientes");

    String success = (String) request.getAttribute("success");
    String error = (String) request.getAttribute("error");
    String criterio = (String) request.getAttribute("criterio");
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestión de Clientes - Veterinario</title>

    <%-- 1. CSS Base (el que ya tenías) --%>
    <link rel="stylesheet" type="text/css" href="estilos/creacion_usuario.css">

    <%-- 2. CSS Específico del Veterinario (NUEVO LINK) --%>
    <link rel="stylesheet" type="text/css" href="estilos/veterinario_custom.css">

</head>
<body>
<nav class="navbar">
    <h1>👥 Gestión de Clientes</h1>
    <div class="user-info">
        <span>Dr./Dra.: <strong><%= nombreVeterinario %></strong></span>
        <span class="vet-badge">⚕️ VETERINARIO</span>
        <a href="PIVeterinario.jsp" class="btn-volver">← Volver al Panel</a>
    </div>
</nav>

<div class="container">
    <div class="page-header">
        <h2>📋 Lista de Clientes Registrados</h2>
        <button class="btn-nuevo" onclick="abrirModalCrear()">➕ Nuevo Cliente</button>
    </div>

    <%-- Resto del HTML (Tablas, Modals, Scripts) se mantiene igual --%>

    <div class="search-bar">
        <form action="VeterinarioClienteServlet" method="get" class="search-form">
            <input type="hidden" name="action" value="buscar">
            <input
                    type="text"
                    name="criterio"
                    placeholder="🔍 Buscar por nombre, correo o teléfono..."
                    value="<%= criterio != null ? criterio : "" %>">
            <button type="submit" class="btn-buscar">Buscar</button>
            <% if (criterio != null && !criterio.isEmpty()) { %>
            <a href="VeterinarioClienteServlet?action=listar" class="btn-limpiar" style="text-decoration: none; display: inline-block;">Limpiar</a>
            <% } %>
        </form>
    </div>

    <% if (success != null) { %>
    <div class="alert alert-success">
        ✅ <%= success %>
    </div>
    <% } %>

    <% if (error != null) { %>
    <div class="alert alert-error">
        ❌ <%= error %>
    </div>
    <% } %>

    <div class="table-container">
        <% if (clientes != null && !clientes.isEmpty()) { %>
        <table>
            <thead>
            <tr>
                <th>ID</th>
                <th>Nombre</th>
                <th>Correo</th>
                <th>Teléfono</th>
                <th>Dirección</th>
                <th>Estado</th>
                <th>Acciones</th>
            </tr>
            </thead>
            <tbody>
            <% for (Usuario c : clientes) { %>
            <tr>
                <td><strong>#<%= c.getId() %></strong></td>
                <td><%= c.getNombre() %></td>
                <td><%= c.getCorreo() %></td>
                <td><%= c.getTelefono() != null && !c.getTelefono().isEmpty() ? c.getTelefono() : "-" %></td>
                <td><%= c.getDireccion() != null && !c.getDireccion().isEmpty() ? c.getDireccion() : "-" %></td>
                <td>
                    <% if (c.isActivo()) { %>
                    <span class="badge badge-activo">✅ Activo</span>
                    <% } else { %>
                    <span class="badge badge-inactivo">❌ Inactivo</span>
                    <% } %>
                </td>
                <td>
                    <div class="action-buttons">
                        <button class="btn-action btn-edit"
                                onclick="abrirModalEditar(<%= c.getId() %>, '<%= c.getNombre().replace("'", "\\'") %>', '<%= c.getCorreo() %>', '<%= c.getTelefono() != null ? c.getTelefono() : "" %>', '<%= c.getDireccion() != null ? c.getDireccion().replace("'", "\\'") : "" %>', <%= c.isActivo() %>)">
                            ✏️ Editar
                        </button>

                        <% if (c.isActivo()) { %>
                        <button class="btn-action btn-toggle"
                                onclick="cambiarEstado(<%= c.getId() %>, false, '<%= c.getNombre().replace("'", "\\'") %>')">
                            🔒 Desactivar
                        </button>
                        <% } else { %>
                        <button class="btn-action btn-activate"
                                onclick="cambiarEstado(<%= c.getId() %>, true, '<%= c.getNombre().replace("'", "\\'") %>')">
                            🔓 Activar
                        </button>
                        <% } %>
                    </div>
                </td>
            </tr>
            <% } %>
            </tbody>
        </table>

        <p style="margin-top: 1rem; color: #6c757d; text-align: center;">
            Total de clientes: <strong><%= clientes.size() %></strong>
        </p>
        <% } else { %>
        <div class="empty-state">
            <div class="empty-state-icon">🔭</div>
            <h3>No hay clientes registrados</h3>
            <p>Comienza creando tu primer cliente con el botón "Nuevo Cliente"</p>
        </div>
        <% } %>
    </div>
</div>

<div id="modalCrear" class="modal">
    <div class="modal-content">
        <div class="modal-header">
            <h3>➕ Registrar Nuevo Cliente</h3>
            <span class="close" onclick="cerrarModal('modalCrear')">&times;</span>
        </div>
        <form action="VeterinarioClienteServlet" method="post">
            <input type="hidden" name="action" value="crear">

            <div class="form-group">
                <label>Nombre Completo <span class="required">*</span></label>
                <input type="text" name="nombre" required placeholder="Ej: Juan Pérez">
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label>Correo Electrónico <span class="required">*</span></label>
                    <input type="email" name="correo" required placeholder="cliente@ejemplo.com">
                </div>

                <div class="form-group">
                    <label>Contraseña <span class="required">*</span></label>
                    <input type="password" name="password" required placeholder="Mínimo 4 caracteres" minlength="4">
                </div>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label>Teléfono</label>
                    <input type="tel" name="telefono" placeholder="0999999999">
                </div>

                <div class="form-group">
                    <label>Dirección</label>
                    <input type="text" name="direccion" placeholder="Ej: Av. Principal 123">
                </div>
            </div>

            <div class="form-actions">
                <button type="button" class="btn-cancel" onclick="cerrarModal('modalCrear')">Cancelar</button>
                <button type="submit" class="btn-submit">✅ Crear Cliente</button>
            </div>
        </form>
    </div>
</div>

<div id="modalEditar" class="modal">
    <div class="modal-content">
        <div class="modal-header">
            <h3>✏️ Editar Cliente</h3>
            <span class="close" onclick="cerrarModal('modalEditar')">&times;</span>
        </div>
        <form action="VeterinarioClienteServlet" method="post">
            <input type="hidden" name="action" value="actualizar">
            <input type="hidden" name="userId" id="editUserId">

            <div class="form-group">
                <label>Nombre Completo <span class="required">*</span></label>
                <input type="text" name="nombre" id="editNombre" required>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label>Correo Electrónico <span class="required">*</span></label>
                    <input type="email" name="correo" id="editCorreo" required>
                </div>

                <div class="form-group">
                    <label>Teléfono</label>
                    <input type="tel" name="telefono" id="editTelefono">
                </div>
            </div>

            <div class="form-group">
                <label>Dirección</label>
                <input type="text" name="direccion" id="editDireccion">
            </div>

            <div class="form-group">
                <div class="checkbox-group">
                    <input type="checkbox" name="activo" id="editActivo" value="true">
                    <label for="editActivo" style="margin: 0;">Cliente Activo</label>
                </div>
            </div>

            <div class="form-actions">
                <button type="button" class="btn-cancel" onclick="cerrarModal('modalEditar')">Cancelar</button>
                <button type="submit" class="btn-submit">✅ Guardar Cambios</button>
            </div>
        </form>
    </div>
</div>

<script>
    // El Javascript se mantiene idéntico al original, no requiere cambios
    function abrirModalCrear() {
        document.getElementById('modalCrear').style.display = 'block';
    }

    function abrirModalEditar(id, nombre, correo, telefono, direccion, activo) {
        document.getElementById('editUserId').value = id;
        document.getElementById('editNombre').value = nombre;
        document.getElementById('editCorreo').value = correo;
        document.getElementById('editTelefono').value = telefono;
        document.getElementById('editDireccion').value = direccion;
        document.getElementById('editActivo').checked = activo;
        document.getElementById('modalEditar').style.display = 'block';
    }

    function cerrarModal(modalId) {
        document.getElementById(modalId).style.display = 'none';
    }

    function cambiarEstado(id, nuevoEstado, nombre) {
        const accion = nuevoEstado ? 'activar' : 'desactivar';
        const mensaje = '¿Estás seguro de ' + accion + ' al cliente "' + nombre + '"?';

        if (confirm(mensaje)) {
            const form = document.createElement('form');
            form.method = 'POST';
            form.action = 'VeterinarioClienteServlet';

            const actionInput = document.createElement('input');
            actionInput.type = 'hidden';
            actionInput.name = 'action';
            actionInput.value = 'cambiarEstado';

            const userIdInput = document.createElement('input');
            userIdInput.type = 'hidden';
            userIdInput.name = 'userId';
            userIdInput.value = id;

            const estadoInput = document.createElement('input');
            estadoInput.type = 'hidden';
            estadoInput.name = 'estado';
            estadoInput.value = nuevoEstado;

            form.appendChild(actionInput);
            form.appendChild(userIdInput);
            form.appendChild(estadoInput);

            document.body.appendChild(form);
            form.submit();
        }
    }

    window.onclick = function(event) {
        const modals = ['modalCrear', 'modalEditar'];
        modals.forEach(modalId => {
            const modal = document.getElementById(modalId);
            if (event.target == modal) {
                cerrarModal(modalId);
            }
        });
    }

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