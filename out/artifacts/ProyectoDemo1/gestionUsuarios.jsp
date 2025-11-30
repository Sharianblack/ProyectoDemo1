<%--
  Created by IntelliJ IDEA.
  User: PC-01
  Date: 30/11/2025
  Time: 11:30
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="model.Usuario" %>

<%
  // ========================================================================
  // BLOQUE DE SEGURIDAD - SOLO ADMINISTRADORES
  // ========================================================================
  if (session == null || session.getAttribute("user") == null) {
    response.sendRedirect("login.jsp");
    return;
  }

  String rol = (String) session.getAttribute("rol");
  if (rol == null || !rol.equalsIgnoreCase("Admin")) {
    response.sendRedirect("login.jsp");
    return;
  }

  String nombreUsuario = (String) session.getAttribute("nombre");
  Integer adminId = (Integer) session.getAttribute("userId");

  // Obtener lista de usuarios
  @SuppressWarnings("unchecked")
  List<Usuario> usuarios = (List<Usuario>) request.getAttribute("usuarios");

  // Obtener mensajes
  String success = (String) request.getAttribute("success");
  String error = (String) request.getAttribute("error");
  String criterio = (String) request.getAttribute("criterio");
  // ========================================================================
%>

<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Gestión de Usuarios - Admin</title>

  <%-- INCLUIMOS EL ARCHIVO CSS EXTERNO --%>
  <link rel="stylesheet" type="text/css" href="estilos/creacion_usuario.css">

</head>
<body>
<nav class="navbar">
  <h1>👥 Gestión de Usuarios</h1>
  <div class="user-info">
    <span>Admin: <strong><%= nombreUsuario %></strong></span>
    <span class="admin-badge">⚡ ADMIN</span>
    <a href="PIAdmin.jsp" class="btn-volver">← Volver al Panel</a>
  </div>
</nav>

<div class="container">
  <div class="page-header">
    <h2>📋 Lista de Usuarios del Sistema</h2>
    <button class="btn-nuevo" onclick="abrirModalCrear()">➕ Nuevo Usuario</button>
  </div>

  <div class="search-bar">
    <form action="UsuarioServlet" method="get" class="search-form">
      <input type="hidden" name="action" value="listar">
      <input
              type="text"
              name="criterio"
              placeholder="🔍 Buscar por nombre, correo o rol..."
              value="<%= criterio != null ? criterio : "" %>">
      <button type="submit" class="btn-buscar">Buscar</button>
      <% if (criterio != null && !criterio.isEmpty()) { %>
      <a href="UsuarioServlet?action=listar" class="btn-limpiar" style="text-decoration: none; display: inline-block;">Limpiar</a>
      <% } %>
    </form>
  </div>

  <% if (success != null) { %>
  <div class="alert alert-success">
    ✓ <%= success %>
  </div>
  <% } %>

  <% if (error != null) { %>
  <div class="alert alert-error">
    ✗ <%= error %>
  </div>
  <% } %>

  <div class="table-container">
    <% if (usuarios != null && !usuarios.isEmpty()) { %>
    <table>
      <thead>
      <tr>
        <th>ID</th>
        <th>Nombre</th>
        <th>Correo</th>
        <th>Rol</th>
        <th>Teléfono</th>
        <th>Estado</th>
        <th>Acciones</th>
      </tr>
      </thead>
      <tbody>
      <% for (Usuario u : usuarios) { %>
      <tr>
        <td><strong>#<%= u.getId() %></strong></td>
        <td><%= u.getNombre() %></td>
        <td><%= u.getCorreo() %></td>
        <td><span class="badge badge-rol"><%= u.getRol() %></span></td>
        <td><%= u.getTelefono() != null ? u.getTelefono() : "-" %></td>
        <td>
          <% if (u.isActivo()) { %>
          <span class="badge badge-activo">✓ Activo</span>
          <% } else { %>
          <span class="badge badge-inactivo">✗ Inactivo</span>
          <% } %>
        </td>
        <td>
          <div class="action-buttons">
            <button class="btn-action btn-edit"
                    onclick="abrirModalEditar(<%= u.getId() %>, '<%= u.getNombre().replace("'", "\\'") %>', '<%= u.getCorreo() %>', '<%= u.getRol() %>', '<%= u.getTelefono() != null ? u.getTelefono() : "" %>', '<%= u.getDireccion() != null ? u.getDireccion().replace("'", "\\'") : "" %>', <%= u.isActivo() %>)">
              ✏️ Editar
            </button>

            <button class="btn-action btn-password"
                    onclick="abrirModalPassword(<%= u.getId() %>, '<%= u.getNombre().replace("'", "\\'") %>')">
              🔑 Cambiar Clave
            </button>

            <% if (u.isActivo()) { %>
            <% if (adminId == null || adminId != u.getId()) { %>
            <button class="btn-action btn-toggle"
                    onclick="cambiarEstado(<%= u.getId() %>, false, '<%= u.getNombre().replace("'", "\\'") %>')">
              🔒 Desactivar
            </button>
            <% } %>
            <% } else { %>
            <button class="btn-action btn-activate"
                    onclick="cambiarEstado(<%= u.getId() %>, true, '<%= u.getNombre().replace("'", "\\'") %>')">
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
      Total de usuarios: <strong><%= usuarios.size() %></strong>
    </p>
    <% } else { %>
    <div class="empty-state">
      <div class="empty-state-icon">📭</div>
      <h3>No hay usuarios registrados</h3>
      <p>Comienza creando tu primer usuario con el botón "Nuevo Usuario"</p>
    </div>
    <% } %>
  </div>
</div>

<div id="modalCrear" class="modal">
  <div class="modal-content">
    <div class="modal-header">
      <h3>➕ Crear Nuevo Usuario</h3>
      <span class="close" onclick="cerrarModal('modalCrear')">&times;</span>
    </div>
    <form action="UsuarioServlet" method="post">
      <input type="hidden" name="action" value="crear">

      <div class="form-group">
        <label>Nombre Completo <span class="required">*</span></label>
        <input type="text" name="nombre" required placeholder="Ej: Juan Pérez">
      </div>

      <div class="form-row">
        <div class="form-group">
          <label>Correo Electrónico <span class="required">*</span></label>
          <input type="email" name="correo" required placeholder="usuario@ejemplo.com">
        </div>

        <div class="form-group">
          <label>Contraseña <span class="required">*</span></label>
          <input type="password" name="password" required placeholder="Mínimo 4 caracteres" minlength="4">
        </div>
      </div>

      <div class="form-row">
        <div class="form-group">
          <label>Rol <span class="required">*</span></label>
          <select name="rol" required>
            <option value="">Seleccionar...</option>
            <option value="Admin">Administrador</option>
            <option value="Cliente">Cliente</option>
            <option value="Veterinario">Veterinario</option>
          </select>
        </div>

        <div class="form-group">
          <label>Teléfono</label>
          <input type="tel" name="telefono" placeholder="0999999999">
        </div>
      </div>

      <div class="form-group">
        <label>Dirección</label>
        <input type="text" name="direccion" placeholder="Ej: Av. Principal 123">
      </div>

      <div class="form-actions">
        <button type="button" class="btn-cancel" onclick="cerrarModal('modalCrear')">Cancelar</button>
        <button type="submit" class="btn-submit">✓ Crear Usuario</button>
      </div>
    </form>
  </div>
</div>

<div id="modalEditar" class="modal">
  <div class="modal-content">
    <div class="modal-header">
      <h3>✏️ Editar Usuario</h3>
      <span class="close" onclick="cerrarModal('modalEditar')">&times;</span>
    </div>
    <form action="UsuarioServlet" method="post">
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
          <label>Rol <span class="required">*</span></label>
          <select name="rol" id="editRol" required>
            <option value="Admin">Administrador</option>
            <option value="Cliente">Cliente</option>
            <option value="Veterinario">Veterinario</option>
          </select>
        </div>
      </div>

      <div class="form-row">
        <div class="form-group">
          <label>Teléfono</label>
          <input type="tel" name="telefono" id="editTelefono">
        </div>

        <div class="form-group">
          <label>Dirección</label>
          <input type="text" name="direccion" id="editDireccion">
        </div>
      </div>

      <div class="form-group">
        <div class="checkbox-group">
          <input type="checkbox" name="activo" id="editActivo" value="true">
          <label for="editActivo" style="margin: 0;">Usuario Activo</label>
        </div>
      </div>

      <div class="form-actions">
        <button type="button" class="btn-cancel" onclick="cerrarModal('modalEditar')">Cancelar</button>
        <button type="submit" class="btn-submit">✓ Guardar Cambios</button>
      </div>
    </form>
  </div>
</div>

<div id="modalPassword" class="modal">
  <div class="modal-content">
    <div class="modal-header">
      <h3>🔑 Cambiar Contraseña</h3>
      <span class="close" onclick="cerrarModal('modalPassword')">&times;</span>
    </div>
    <form action="UsuarioServlet" method="post">
      <input type="hidden" name="action" value="cambiarPassword">
      <input type="hidden" name="userId" id="passUserId">

      <p style="margin-bottom: 1rem; color: #6c757d;">
        Usuario: <strong id="passNombre"></strong>
      </p>

      <div class="form-group">
        <label>Nueva Contraseña <span class="required">*</span></label>
        <input type="password" name="nuevaPassword" required placeholder="Mínimo 4 caracteres" minlength="4">
      </div>

      <div class="form-actions">
        <button type="button" class="btn-cancel" onclick="cerrarModal('modalPassword')">Cancelar</button>
        <button type="submit" class="btn-submit">✓ Cambiar Contraseña</button>
      </div>
    </form>
  </div>
</div>

<script>
  // ABRIR MODAL CREAR
  function abrirModalCrear() {
    document.getElementById('modalCrear').style.display = 'block';
  }

  // ABRIR MODAL EDITAR
  function abrirModalEditar(id, nombre, correo, rol, telefono, direccion, activo) {
    document.getElementById('editUserId').value = id;
    document.getElementById('editNombre').value = nombre;
    document.getElementById('editCorreo').value = correo;
    document.getElementById('editRol').value = rol;
    document.getElementById('editTelefono').value = telefono;
    document.getElementById('editDireccion').value = direccion;
    document.getElementById('editActivo').checked = activo;
    document.getElementById('modalEditar').style.display = 'block';
  }

  // ABRIR MODAL CAMBIAR CONTRASEÑA
  function abrirModalPassword(id, nombre) {
    document.getElementById('passUserId').value = id;
    document.getElementById('passNombre').textContent = nombre;
    document.getElementById('modalPassword').style.display = 'block';
  }

  // CERRAR MODAL
  function cerrarModal(modalId) {
    document.getElementById(modalId).style.display = 'none';
  }

  // CAMBIAR ESTADO (Activar/Desactivar)
  function cambiarEstado(id, nuevoEstado, nombre) {
    const accion = nuevoEstado ? 'activar' : 'desactivar';
    const mensaje = '¿Estás seguro de ' + accion + ' al usuario "' + nombre + '"?';

    if (confirm(mensaje)) {
      const form = document.createElement('form');
      form.method = 'POST';
      form.action = 'UsuarioServlet';

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

  // CERRAR MODAL AL HACER CLIC FUERA
  window.onclick = function(event) {
    const modals = ['modalCrear', 'modalEditar', 'modalPassword'];
    modals.forEach(modalId => {
      const modal = document.getElementById(modalId);
      if (event.target == modal) {
        cerrarModal(modalId);
      }
    });
  }

  // AUTO-CERRAR ALERTAS DESPUÉS DE 5 SEGUNDOS
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
