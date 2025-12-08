<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="model.Sucursal" %>
<%@ page import="java.text.SimpleDateFormat" %>

<%
  // Verificación de seguridad
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
  List<Sucursal> sucursales = (List<Sucursal>) request.getAttribute("sucursales");
  SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
%>

<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Gestión de Sucursales - Sistema Veterinaria</title>
  <link rel="stylesheet" href="estilos/creacion_usuario.css">
  <style>
    .horario-badge {
      display: inline-block;
      background: #e7f3ff;
      color: #0056b3;
      padding: 3px 8px;
      border-radius: 4px;
      font-size: 0.85rem;
      margin: 0 2px;
    }
    .ciudad-tag {
      display: inline-block;
      background: #f0f0f0;
      color: #666;
      padding: 3px 10px;
      border-radius: 12px;
      font-size: 0.8rem;
      margin-left: 5px;
    }
  </style>
</head>
<body>

<!-- NAVBAR -->
<nav class="navbar">
  <h1>🏥 Gestión de Sucursales</h1>
  <div class="user-info">
    <span>Administrador: <strong><%= nombreUsuario %></strong></span>
    <span class="admin-badge">⚡ ADMIN</span>
    <a href="PIAdmin.jsp" class="btn-volver">← Volver al Panel</a>
  </div>
</nav>

<!-- CONTENEDOR PRINCIPAL -->
<div class="container">

  <!-- HEADER CON BOTÓN NUEVO -->
  <div class="page-header">
    <h2>📍 Administración de Sucursales</h2>
    <button class="btn-nuevo" onclick="abrirModal('modalCrear')">➕ Nueva Sucursal</button>
  </div>

  <!-- BARRA DE BÚSQUEDA -->
  <div class="search-bar">
    <form action="SucursalServlet" method="get" class="search-form">
      <input type="hidden" name="action" value="listar">
      <input
              type="text"
              name="criterio"
              placeholder="🔍 Buscar por nombre, ciudad o dirección..."
              value="<%= request.getAttribute("criterio") != null ? request.getAttribute("criterio") : "" %>">
      <button type="submit" class="btn-buscar">Buscar</button>
      <% if (request.getAttribute("criterio") != null && !request.getAttribute("criterio").toString().isEmpty()) { %>
      <a href="SucursalServlet?action=listar" class="btn-limpiar" style="text-decoration: none; display: inline-block;">Limpiar</a>
      <% } %>
    </form>
  </div>

  <!-- ALERTAS -->
  <% if (request.getAttribute("success") != null) { %>
  <div class="alert alert-success">
    <%= request.getAttribute("success") %>
  </div>
  <% } %>

  <% if (request.getAttribute("error") != null && request.getAttribute("mostrarModal") == null) { %>
  <div class="alert alert-error">
    <%= request.getAttribute("error") %>
  </div>
  <% } %>

  <!-- TABLA DE SUCURSALES -->
  <div class="table-container">
    <table class="data-table">
      <thead>
      <tr>
        <th>ID</th>
        <th>Nombre</th>
        <th>Ciudad</th>
        <th>Dirección</th>
        <th>Teléfono</th>
        <th>Correo</th>
        <th>Horario</th>
        <th>Estado</th>
        <th>Acciones</th>
      </tr>
      </thead>
      <tbody>
      <%
        if (sucursales != null && !sucursales.isEmpty()) {
          for (Sucursal s : sucursales) {
            String estadoClase = s.isActivo() ? "badge-activo" : "badge-inactivo";
            String estadoTexto = s.isActivo() ? "✓ Activa" : "✗ Inactiva";
            String horarioTexto = "";
            if (s.getHorarioApertura() != null && s.getHorarioCierre() != null) {
              horarioTexto = timeFormat.format(s.getHorarioApertura()) + " - " + timeFormat.format(s.getHorarioCierre());
            }
      %>
      <tr>
        <td><%= s.getId() %></td>
        <td><strong><%= s.getNombre() %></strong></td>
        <td>
          <% if (s.getCiudad() != null && !s.getCiudad().isEmpty()) { %>
          <span class="ciudad-tag"><%= s.getCiudad() %></span>
          <% } else { %>
          <span class="ciudad-tag">N/A</span>
          <% } %>
        </td>
        <td><%= s.getDireccion() != null ? s.getDireccion() : "N/A" %></td>
        <td><%= s.getTelefono() != null && !s.getTelefono().isEmpty() ? s.getTelefono() : "N/A" %></td>
        <td><%= s.getCorreo() != null && !s.getCorreo().isEmpty() ? s.getCorreo() : "N/A" %></td>
        <td>
          <% if (!horarioTexto.isEmpty()) { %>
          <span class="horario-badge">🕒 <%= horarioTexto %></span>
          <% } else { %>
          N/A
          <% } %>
        </td>
        <td><span class="<%= estadoClase %>"><%= estadoTexto %></span></td>
        <td class="actions">
          <button class="btn-action btn-edit" 
                  onclick="abrirModalEditar(<%= s.getId() %>, 
                  '<%= s.getNombre().replace("'", "\\'") %>', 
                  '<%= s.getDireccion() != null ? s.getDireccion().replace("'", "\\'") : "" %>', 
                  '<%= s.getTelefono() != null ? s.getTelefono() : "" %>', 
                  '<%= s.getCorreo() != null ? s.getCorreo() : "" %>', 
                  '<%= s.getCiudad() != null ? s.getCiudad() : "" %>', 
                  '<%= s.getHorarioApertura() != null ? timeFormat.format(s.getHorarioApertura()) : "" %>', 
                  '<%= s.getHorarioCierre() != null ? timeFormat.format(s.getHorarioCierre()) : "" %>', 
                  <%= s.isActivo() %>)">✏️ Editar</button>

          <button class="btn-action btn-delete" 
                  onclick="eliminarSucursal(<%= s.getId() %>, '<%= s.getNombre() %>')">🗑️ Eliminar</button>

          <%
            if (s.isActivo()) {
          %>
          <button class="btn-action btn-disable" 
                  onclick="cambiarEstado(<%= s.getId() %>, false, '<%= s.getNombre() %>')">⏸️ Desactivar</button>
          <%
          } else {
          %>
          <button class="btn-action btn-enable" 
                  onclick="cambiarEstado(<%= s.getId() %>, true, '<%= s.getNombre() %>')">▶️ Activar</button>
          <%
            }
          %>
        </td>
      </tr>
      <%
        }
      } else {
      %>
      <tr>
        <td colspan="9" class="no-data">No hay sucursales registradas</td>
      </tr>
      <%
        }
      %>
      </tbody>
    </table>
  </div>

</div>

<!-- MODAL CREAR SUCURSAL -->
<div id="modalCrear" class="modal">
  <div class="modal-content">
    <div class="modal-header">
      <h3>➕ Nueva Sucursal</h3>
      <span class="close" onclick="cerrarModal('modalCrear')">&times;</span>
    </div>

    <% if (request.getAttribute("error") != null && "crear".equals(request.getAttribute("mostrarModal"))) { %>
    <div class="alert alert-error">
      <%= request.getAttribute("error") %>
    </div>
    <% } %>

    <form action="SucursalServlet" method="post">
      <input type="hidden" name="action" value="crear">

      <div class="form-group">
        <label>Nombre de la Sucursal <span class="required">*</span></label>
        <input type="text" name="nombre" placeholder="Ej: Sucursal Centro" 
               value="<%= request.getAttribute("formNombre") != null ? request.getAttribute("formNombre") : "" %>">
      </div>

      <div class="form-row">
        <div class="form-group">
          <label>Ciudad</label>
          <input type="text" name="ciudad" placeholder="Ej: Quito" 
                 value="<%= request.getAttribute("formCiudad") != null ? request.getAttribute("formCiudad") : "" %>">
        </div>

        <div class="form-group">
          <label>Teléfono (10 dígitos)</label>
          <input type="text" name="telefono" placeholder="0999999999" maxlength="10"
                 value="<%= request.getAttribute("formTelefono") != null ? request.getAttribute("formTelefono") : "" %>"
                 onkeypress="return soloNumeros(event)" onpaste="return false">
        </div>
      </div>

      <div class="form-group">
        <label>Dirección <span class="required">*</span></label>
        <input type="text" name="direccion" placeholder="Ej: Av. Principal #123" 
               value="<%= request.getAttribute("formDireccion") != null ? request.getAttribute("formDireccion") : "" %>">
      </div>

      <div class="form-group">
        <label>Correo Electrónico <span class="required">*</span></label>
        <input type="text" name="correo" placeholder="sucursal@veterinaria.com" 
               value="<%= request.getAttribute("formCorreo") != null ? request.getAttribute("formCorreo") : "" %>">
      </div>

      <div class="form-row">
        <div class="form-group">
          <label>Horario Apertura <span class="required">*</span></label>
          <input type="time" name="horarioApertura" 
                 value="<%= request.getAttribute("formHorarioApertura") != null ? request.getAttribute("formHorarioApertura") : "" %>">
        </div>

        <div class="form-group">
          <label>Horario Cierre <span class="required">*</span></label>
          <input type="time" name="horarioCierre" 
                 value="<%= request.getAttribute("formHorarioCierre") != null ? request.getAttribute("formHorarioCierre") : "" %>">
        </div>
      </div>

      <div class="form-actions">
        <button type="button" class="btn-cancel" onclick="cerrarModal('modalCrear')">Cancelar</button>
        <button type="submit" class="btn-submit">✓ Crear Sucursal</button>
      </div>
    </form>
  </div>
</div>

<!-- MODAL EDITAR SUCURSAL -->
<div id="modalEditar" class="modal">
  <div class="modal-content">
    <div class="modal-header">
      <h3>✏️ Editar Sucursal</h3>
      <span class="close" onclick="cerrarModal('modalEditar')">&times;</span>
    </div>

    <% if (request.getAttribute("error") != null && "editar".equals(request.getAttribute("mostrarModal"))) { %>
    <div class="alert alert-error">
      <%= request.getAttribute("error") %>
    </div>
    <% } %>

    <form action="SucursalServlet" method="post">
      <input type="hidden" name="action" value="actualizar">
      <input type="hidden" name="sucursalId" id="editSucursalId">

      <div class="form-group">
        <label>Nombre de la Sucursal <span class="required">*</span></label>
        <input type="text" name="nombre" id="editNombre">
      </div>

      <div class="form-row">
        <div class="form-group">
          <label>Ciudad</label>
          <input type="text" name="ciudad" id="editCiudad">
        </div>

        <div class="form-group">
          <label>Teléfono (10 dígitos)</label>
          <input type="text" name="telefono" id="editTelefono" maxlength="10"
                 onkeypress="return soloNumeros(event)" onpaste="return false">
        </div>
      </div>

      <div class="form-group">
        <label>Dirección <span class="required">*</span></label>
        <input type="text" name="direccion" id="editDireccion">
      </div>

      <div class="form-group">
        <label>Correo Electrónico <span class="required">*</span></label>
        <input type="text" name="correo" id="editCorreo">
      </div>

      <div class="form-row">
        <div class="form-group">
          <label>Horario Apertura <span class="required">*</span></label>
          <input type="time" name="horarioApertura" id="editHorarioApertura">
        </div>

        <div class="form-group">
          <label>Horario Cierre <span class="required">*</span></label>
          <input type="time" name="horarioCierre" id="editHorarioCierre">
        </div>
      </div>

      <div class="form-group">
        <div class="checkbox-group">
          <input type="checkbox" name="activo" id="editActivo" value="true">
          <label for="editActivo" style="margin: 0;">Sucursal Activa</label>
        </div>
      </div>

      <div class="form-actions">
        <button type="button" class="btn-cancel" onclick="cerrarModal('modalEditar')">Cancelar</button>
        <button type="submit" class="btn-submit">✓ Guardar Cambios</button>
      </div>
    </form>
  </div>
</div>

<!-- JAVASCRIPT -->
<script>
  // ABRIR MODAL
  function abrirModal(modalId) {
    document.getElementById(modalId).style.display = 'block';
  }

  // CERRAR MODAL
  function cerrarModal(modalId) {
    document.getElementById(modalId).style.display = 'none';
  }

  // ABRIR MODAL EDITAR
  function abrirModalEditar(id, nombre, direccion, telefono, correo, ciudad, horarioApertura, horarioCierre, activo) {
    document.getElementById('editSucursalId').value = id;
    document.getElementById('editNombre').value = nombre;
    document.getElementById('editDireccion').value = direccion;
    document.getElementById('editTelefono').value = telefono;
    document.getElementById('editCorreo').value = correo;
    document.getElementById('editCiudad').value = ciudad;
    document.getElementById('editHorarioApertura').value = horarioApertura;
    document.getElementById('editHorarioCierre').value = horarioCierre;
    document.getElementById('editActivo').checked = activo;
    document.getElementById('modalEditar').style.display = 'block';
  }

  // ELIMINAR SUCURSAL
  function eliminarSucursal(id, nombre) {
    if (confirm('¿Estás seguro de eliminar la sucursal "' + nombre + '"?')) {
      const form = document.createElement('form');
      form.method = 'POST';
      form.action = 'SucursalServlet';

      const actionInput = document.createElement('input');
      actionInput.type = 'hidden';
      actionInput.name = 'action';
      actionInput.value = 'eliminar';

      const idInput = document.createElement('input');
      idInput.type = 'hidden';
      idInput.name = 'sucursalId';
      idInput.value = id;

      form.appendChild(actionInput);
      form.appendChild(idInput);
      document.body.appendChild(form);
      form.submit();
    }
  }

  // CAMBIAR ESTADO
  function cambiarEstado(id, nuevoEstado, nombre) {
    const accion = nuevoEstado ? 'activar' : 'desactivar';
    if (confirm('¿Estás seguro de ' + accion + ' la sucursal "' + nombre + '"?')) {
      const form = document.createElement('form');
      form.method = 'POST';
      form.action = 'SucursalServlet';

      const actionInput = document.createElement('input');
      actionInput.type = 'hidden';
      actionInput.name = 'action';
      actionInput.value = 'cambiarEstado';

      const idInput = document.createElement('input');
      idInput.type = 'hidden';
      idInput.name = 'sucursalId';
      idInput.value = id;

      const estadoInput = document.createElement('input');
      estadoInput.type = 'hidden';
      estadoInput.name = 'estado';
      estadoInput.value = nuevoEstado;

      form.appendChild(actionInput);
      form.appendChild(idInput);
      form.appendChild(estadoInput);
      document.body.appendChild(form);
      form.submit();
    }
  }

  // SOLO NÚMEROS
  function soloNumeros(event) {
    const char = event.key;
    const regex = /^[0-9]$/;
    if (!regex.test(char)) {
      event.preventDefault();
      return false;
    }
    return true;
  }

  // CERRAR MODAL AL HACER CLIC FUERA
  window.onclick = function(event) {
    const modals = ['modalCrear', 'modalEditar'];
    modals.forEach(modalId => {
      const modal = document.getElementById(modalId);
      if (event.target == modal) {
        cerrarModal(modalId);
      }
    });
  }

  // AUTO-CERRAR ALERTAS
  setTimeout(() => {
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(alert => {
      alert.style.opacity = '0';
      setTimeout(() => alert.remove(), 300);
    });
  }, 5000);

  // AUTO-ABRIR MODAL SI HAY ERROR
  window.onload = function() {
    <% if ("crear".equals(request.getAttribute("mostrarModal"))) { %>
      document.getElementById('modalCrear').style.display = 'block';
    <% } else if ("editar".equals(request.getAttribute("mostrarModal"))) { %>
      <%
        Integer sucursalId = (Integer) request.getAttribute("formSucursalId");
        if (sucursalId != null) {
      %>
        abrirModalEditar(
          <%= sucursalId %>,
          "<%= request.getAttribute("formNombre") != null ? request.getAttribute("formNombre") : "" %>",
          "<%= request.getAttribute("formDireccion") != null ? request.getAttribute("formDireccion") : "" %>",
          "<%= request.getAttribute("formTelefono") != null ? request.getAttribute("formTelefono") : "" %>",
          "<%= request.getAttribute("formCorreo") != null ? request.getAttribute("formCorreo") : "" %>",
          "<%= request.getAttribute("formCiudad") != null ? request.getAttribute("formCiudad") : "" %>",
          "<%= request.getAttribute("formHorarioApertura") != null ? request.getAttribute("formHorarioApertura") : "" %>",
          "<%= request.getAttribute("formHorarioCierre") != null ? request.getAttribute("formHorarioCierre") : "" %>",
          <%= request.getAttribute("formActivo") != null ? request.getAttribute("formActivo") : "false" %>
        );
      <% } %>
    <% } %>
  };
</script>

</body>
</html>
