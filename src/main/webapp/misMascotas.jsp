<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="model.Mascota" %>

<%
  // BLOQUE DE SEGURIDAD
  if (session == null || session.getAttribute("user") == null) {
    response.sendRedirect("login.jsp");
    return;
  }

  String rol = (String) session.getAttribute("rol");
  if (rol == null || !rol.equalsIgnoreCase("Cliente")) {
    response.sendRedirect("login.jsp");
    return;
  }

  String nombreUsuario = (String) session.getAttribute("nombre");

  @SuppressWarnings("unchecked")
  List<Mascota> mascotas = (List<Mascota>) request.getAttribute("mascotas");

  String success = (String) request.getAttribute("success");
  String error = (String) request.getAttribute("error");
%>

<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Mis Mascotas - Cliente</title>

  <link rel="stylesheet" href="estilos/mimascota.css">
</head>
<body>

<nav class="navbar">
  <h1>Mis Mascotas</h1>
  <div class="user-info">
    <span>Hola, <strong><%= nombreUsuario %></strong></span>
    <span class="info-badge">Cliente</span>
    <a href="paginaInicio.jsp" class="logout-btn">Volver</a>
  </div>
</nav>

<div class="content">

  <div class="section-header">
    <div class="section-title">
      <h2>Mis Mascotas Registradas</h2>
      <p>Gestiona el perfil de tus compañeros peludos</p>
    </div>
    <button class="btn-nuevo" onclick="abrirModalCrear()">
      Nueva Mascota
    </button>
  </div>

  <% if (success != null) { %>
  <div class="alert alert-success"><%= success %></div>
  <% } %>

  <% if (error != null) { %>
  <div class="alert alert-error"><%= error %></div>
  <% } %>

  <div class="mascotas-grid">
    <% if (mascotas != null && !mascotas.isEmpty()) {
      for (Mascota m : mascotas) { %>

    <div class="mascota-card">
      <div class="card-header">
        <span class="card-icon"><%= m.getIconoEspecie() %></span>
        <h3 class="card-title"><%= m.getNombre() %></h3>
      </div>

      <div class="card-body">
        <div class="info-row">
          <span class="info-label">Especie</span>
          <span class="info-value"><%= m.getEspecie() %></span>
        </div>
        <div class="info-row">
          <span class="info-label">Raza</span>
          <span class="info-value"><%= m.getRaza() != null && !m.getRaza().isEmpty() ? m.getRaza() : "-" %></span>
        </div>
        <div class="info-row">
          <span class="info-label">Sexo</span>
          <span class="info-value"><%= m.getSexoCompleto() %></span>
        </div>
        <div class="info-row">
          <span class="info-label">Edad</span>
          <span class="info-value"><%= m.getEdad() > 0 ? m.getEdad() + " años" : "< 1 año" %></span>
        </div>
      </div>

      <div class="card-footer">
        <button class="btn-icon btn-edit"
                onclick="abrirModalEditar(<%= m.getIdMascota() %>, '<%= m.getNombre().replace("'", "\\'"  ) %>', '<%= m.getEspecie() %>', '<%= m.getRaza() != null ? m.getRaza() : "" %>', '<%= m.getSexo() != null ? m.getSexo() : "" %>', '<%= m.getFechaNacimiento() != null ? m.getFechaNacimiento().toString() : "" %>')">
          Editar
        </button>
        <button class="btn-icon btn-delete"
                onclick="eliminarMascota(<%= m.getIdMascota() %>, '<%= m.getNombre().replace("'", "\\'") %>')">
          Eliminar
        </button>
      </div>
    </div>

    <% }
    } else { %>
    <div class="empty-state">
      <div class="empty-text">
        <h3>Aún no tienes mascotas</h3>
        <p>Haz clic en "Nueva Mascota" para registrar a tu primer amigo.</p>
      </div>
    </div>
    <% } %>
  </div>
</div>

<div id="modalCrear" class="modal">
  <div class="modal-content">
    <div class="modal-header">
      <h3>Registrar Mascota</h3>
    </div>
    
    <% if (error != null && "crear".equals(request.getAttribute("mostrarModal"))) { %>
    <div class="alert alert-error" style="margin: 0 0 1rem 0;">
      <%= error %>
    </div>
    <% } %>
    
    <form action="MascotaServlet" method="post">
      <input type="hidden" name="action" value="crear">

      <div class="form-group">
        <label>Nombre *</label>
        <input type="text" name="nombre" placeholder="Ej: Firulais">
      </div>

      <div class="form-group">
        <label>Especie *</label>
        <select name="especie">
          <option value="">Seleccionar...</option>
          <option value="Perro">Perro</option>
          <option value="Gato">Gato</option>
          <option value="Ave">Ave</option>
          <option value="Conejo">Conejo</option>
          <option value="Hamster">Hamster</option>
          <option value="Otro">Otro</option>
        </select>
      </div>

      <div class="form-group">
        <label>Raza</label>
        <input type="text" name="raza" placeholder="Ej: Labrador">
      </div>

      <div class="form-group">
        <label>Sexo</label>
        <select name="sexo">
          <option value="">No especificado</option>
          <option value="M">Macho</option>
          <option value="H">Hembra</option>
        </select>
      </div>

      <div class="form-group">
        <label>Fecha Nacimiento</label>
        <input type="date" name="fechaNacimiento">
      </div>

      <div class="modal-actions">
        <button type="button" class="btn-cancel" onclick="cerrarModal('modalCrear')">Cancelar</button>
        <button type="submit" class="btn-save">Guardar</button>
      </div>
    </form>
  </div>
</div>

<div id="modalEditar" class="modal">
  <div class="modal-content">
    <div class="modal-header">
      <h3>Editar Mascota</h3>
    </div>
    
    <% if (error != null && "editar".equals(request.getAttribute("mostrarModal"))) { %>
    <div class="alert alert-error" style="margin: 0 0 1rem 0;">
      <%= error %>
    </div>
    <% } %>
    
    <form action="MascotaServlet" method="post">
      <input type="hidden" name="action" value="actualizar">
      <input type="hidden" name="idMascota" id="editIdMascota">

      <div class="form-group">
        <label>Nombre *</label>
        <input type="text" name="nombre" id="editNombre">
      </div>

      <div class="form-group">
        <label>Especie *</label>
        <select name="especie" id="editEspecie">
          <option value="Perro">Perro</option>
          <option value="Gato">Gato</option>
          <option value="Ave">Ave</option>
          <option value="Conejo">Conejo</option>
          <option value="Hamster">Hamster</option>
          <option value="Otro">Otro</option>
        </select>
      </div>

      <div class="form-group">
        <label>Raza</label>
        <input type="text" name="raza" id="editRaza">
      </div>

      <div class="form-group">
        <label>Sexo *</label>
        <select name="sexo" id="editSexo">
          <option value="">Seleccionar...</option>
          <option value="M">Macho</option>
          <option value="H">Hembra</option>
        </select>
      </div>

      <div class="form-group">
        <label>Fecha Nacimiento</label>
        <input type="date" name="fechaNacimiento" id="editFechaNac">
      </div>

      <div class="modal-actions">
        <button type="button" class="btn-cancel" onclick="cerrarModal('modalEditar')">Cancelar</button>
        <button type="submit" class="btn-save">Guardar Cambios</button>
      </div>
    </form>
  </div>
</div>

<script>
  // Funciones del Modal
  function abrirModalCrear() {
    document.getElementById('modalCrear').style.display = 'block';
  }

  function abrirModalEditar(id, nombre, especie, raza, sexo, fechaNac) {
    document.getElementById('editIdMascota').value = id;
    document.getElementById('editNombre').value = nombre;
    document.getElementById('editEspecie').value = especie;
    document.getElementById('editRaza').value = raza;
    document.getElementById('editSexo').value = sexo;
    document.getElementById('editFechaNac').value = fechaNac;
    document.getElementById('modalEditar').style.display = 'block';
  }

  function cerrarModal(modalId) {
    document.getElementById(modalId).style.display = 'none';
  }

  // Eliminar con confirmación
  function eliminarMascota(id, nombre) {
    if (confirm('¿Seguro que deseas eliminar a ' + nombre + '?')) {
      const form = document.createElement('form');
      form.method = 'POST';
      form.action = 'MascotaServlet';

      const actInput = document.createElement('input');
      actInput.type = 'hidden';
      actInput.name = 'action';
      actInput.value = 'eliminar';

      const idInput = document.createElement('input');
      idInput.type = 'hidden';
      idInput.name = 'idMascota';
      idInput.value = id;

      form.appendChild(actInput);
      form.appendChild(idInput);
      document.body.appendChild(form);
      form.submit();
    }
  }

  // Cerrar modal al hacer clic fuera
  window.onclick = function(event) {
    if (event.target.className === 'modal') {
      event.target.style.display = 'none';
    }
  }

  // Auto-abrir modal si hay error de validación
  window.onload = function() {
    <% if ("crear".equals(request.getAttribute("mostrarModal"))) { %>
      document.getElementById('modalCrear').style.display = 'block';
    <% } else if ("editar".equals(request.getAttribute("mostrarModal"))) { %>
      document.getElementById('modalEditar').style.display = 'block';
    <% } %>
  };

  // Ocultar alertas automáticamente
  setTimeout(() => {
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(alert => {
      alert.style.opacity = '0';
      setTimeout(() => alert.remove(), 300);
    });
  }, 4000);
</script>
</body>
</html>