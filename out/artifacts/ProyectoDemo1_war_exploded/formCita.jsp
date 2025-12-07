<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="model.Cita" %>
<%@ page import="java.text.SimpleDateFormat" %>

<%
    // ========================================================================
    // BLOQUE DE SEGURIDAD - SOLO VETERINARIOS
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

    String nombreUsuario = (String) session.getAttribute("nombre");

    // Obtener datos del formulario
    String accion = (String) request.getAttribute("accion");
    Cita cita = (Cita) request.getAttribute("cita");

    @SuppressWarnings("unchecked")
    List<String[]> clientes = (List<String[]>) request.getAttribute("clientes");

    @SuppressWarnings("unchecked")
    List<String[]> sucursales = (List<String[]>) request.getAttribute("sucursales");

    boolean esEditar = "editar".equals(accion);

    // Formatear fechas si es editar
    String fechaFormateada = "";
    String horaFormateada = "";
    if (esEditar && cita != null && cita.getFechaCita() != null) {
        SimpleDateFormat sdfFecha = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm");
        fechaFormateada = sdfFecha.format(cita.getFechaCita());
        horaFormateada = sdfHora.format(cita.getFechaCita());
    }
    // ========================================================================
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= esEditar ? "Editar" : "Nueva" %> Cita - Veterinario</title>

    <link rel="stylesheet" type="text/css" href="estilos/veterinario_form.css">

</head>
<body>
<nav class="navbar">
    <h1><%= esEditar ? "✏️ Editar Cita" : "➕ Nueva Cita" %></h1>
    <div class="user-info">
        <span>Dr./Dra. <strong><%= nombreUsuario %></strong></span>
        <a href="CitaServlet?action=listar" class="btn-volver">← Volver a Mis Citas</a>
    </div>
</nav>

<div class="container">
    <div class="form-card">
        <div class="form-header">
            <h2><%= esEditar ? "Editar Cita Existente" : "Programar Nueva Cita" %></h2>
            <p style="color: #666; margin-top: 0.5rem;">
                Completa la información de la cita veterinaria
            </p>
        </div>

        <form action="CitaServlet" method="post" onsubmit="return validarFormulario()">
            <input type="hidden" name="action" value="<%= esEditar ? "actualizar" : "crear" %>">
            <% if (esEditar && cita != null) { %>
            <input type="hidden" name="idCita" value="<%= cita.getIdCita() %>">
            <% } %>

            <div class="form-section">
                <h3>📋 Información del Cliente</h3>

                <div class="form-group">
                    <label for="idCliente">Cliente <span class="required">*</span></label>
                    <select name="idCliente" id="idCliente" required onchange="cargarMascotas()">
                        <option value="">Selecciona un cliente...</option>
                        <% if (clientes != null) {
                            for (String[] cliente : clientes) {
                                // cliente[0]=id_cliente (puede ser ""), cliente[5]=id_usuario (puede ser null), cliente[4]=num_mascotas
                                String idClienteVal = cliente.length > 0 && cliente[0] != null ? cliente[0] : "";
                                String idUsuarioVal = cliente.length > 5 && cliente[5] != null ? cliente[5] : (idClienteVal.isEmpty() ? "" : idClienteVal);
                                String nombre = cliente.length > 1 ? cliente[1] : "";
                                String correo = cliente.length > 2 ? cliente[2] : "";
                                String numMascotas = cliente.length > 4 ? cliente[4] : "0";
                        %>
                        <option value="<%= (idClienteVal.isEmpty() ? idUsuarioVal : idClienteVal) %>"
                                data-idcliente="<%= idClienteVal %>"
                                data-idusuario="<%= idUsuarioVal %>"
                                data-mascotas="<%= numMascotas %>">
                            <%= nombre %> - <%= correo %> (<%= numMascotas %> mascotas)
                        </option>
                        <% }
                        } %>
                    </select>
                </div>

                <div class="form-group" id="mascotasContainer">
                    <label for="idMascota">Mascota <span class="required">*</span></label>
                    <select name="idMascota" id="idMascota" required>
                        <option value="">Primero selecciona un cliente</option>
                    </select>
                </div>
            </div>

            <div class="form-section">
                <h3>📅 Fecha y Hora de la Cita</h3>

                <div class="form-row">
                    <div class="form-group">
                        <label for="fecha">Fecha <span class="required">*</span></label>
                        <input type="date"
                               name="fecha"
                               id="fecha"
                               value="<%= esEditar ? fechaFormateada : "" %>"
                               min="<%= new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()) %>"
                               required>
                    </div>

                    <div class="form-group">
                        <label for="hora">Hora <span class="required">*</span></label>
                        <input type="time"
                               name="hora"
                               id="hora"
                               value="<%= esEditar ? horaFormateada : "" %>"
                               required>
                    </div>
                </div>
            </div>

            <div class="form-section">
                <h3>🏥 Ubicación</h3>

                <div class="form-group">
                    <label for="idSucursal">Sucursal <span class="required">*</span></label>
                    <select name="idSucursal" id="idSucursal" required>
                        <option value="">Selecciona una sucursal...</option>
                        <% if (sucursales != null) {
                            for (String[] sucursal : sucursales) {
                                boolean selected = esEditar && cita != null && cita.getIdSucursal() == Integer.parseInt(sucursal[0]);
                        %>
                        <option value="<%= sucursal[0] %>" <%= selected ? "selected" : "" %>>
                            <%= sucursal[1] %> - <%= sucursal[2] %>
                        </option>
                        <% }
                        } %>
                    </select>
                </div>
            </div>

            <% if (esEditar) { %>
            <div class="form-section">
                <h3>📊 Estado de la Cita</h3>

                <div class="form-group">
                    <label for="estado">Estado <span class="required">*</span></label>
                    <select name="estado" id="estado" required>
                        <option value="Programada" <%= "Programada".equals(cita.getEstado()) ? "selected" : "" %>>📅 Programada</option>
                        <option value="En Proceso" <%= "En Proceso".equals(cita.getEstado()) ? "selected" : "" %>>⏳ En Proceso</option>
                        <option value="Completada" <%= "Completada".equals(cita.getEstado()) ? "selected" : "" %>>✓ Completada</option>
                        <option value="Cancelada" <%= "Cancelada".equals(cita.getEstado()) ? "selected" : "" %>>✗ Cancelada</option>
                    </select>
                </div>
            </div>
            <% } %>

            <div class="form-section">
                <h3>📝 Observaciones</h3>

                <div class="form-group">
                    <label for="observaciones">Notas adicionales</label>
                    <textarea name="observaciones"
                              id="observaciones"
                              rows="4"
                              placeholder="Motivo de la consulta, síntomas, notas especiales..."><%= esEditar && cita != null && cita.getObservaciones() != null ? cita.getObservaciones() : "" %></textarea>
                </div>
            </div>

            <div class="info-box">
                <p><strong>ℹ️ Recuerda:</strong> Todos los campos marcados con <span style="color: #f5576c;">*</span> son obligatorios</p>
            </div>

            <div class="form-actions">
                <a href="CitaServlet?action=listar" class="btn-cancel">Cancelar</a>
                <button type="submit" class="btn-submit">
                    <%= esEditar ? "💾 Guardar Cambios" : "✓ Crear Cita" %>
                </button>
            </div>
        </form>
    </div>
</div>

<script>
    // Cargar mascotas cuando se selecciona un cliente
    function cargarMascotas() {
        const clienteSelect = document.getElementById('idCliente');
        const mascotasContainer = document.getElementById('mascotasContainer');
        const mascotasSelect = document.getElementById('idMascota');

        // Preferimos enviar id_cliente si está disponible, si no usamos id_usuario
        const selectedOption = clienteSelect.options[clienteSelect.selectedIndex];
        const idClienteAttr = selectedOption ? selectedOption.dataset.idcliente : '';
        const idUsuarioAttr = selectedOption ? selectedOption.dataset.idusuario : '';
        const idToSend = (idClienteAttr && idClienteAttr !== '') ? idClienteAttr : idUsuarioAttr;

        if (!idToSend) {
            mascotasContainer.style.display = 'none';
            mascotasSelect.innerHTML = '<option value="">Primero selecciona un cliente</option>';
            return;
        }

        // Llamar al servidor para obtener mascotas
        fetch('CitaServlet?action=getMascotas&idCliente=' + idToSend)
            .then(response => response.json())
            .then(mascotas => {
                mascotasSelect.innerHTML = '<option value="">Selecciona una mascota...</option>';

                if (mascotas && mascotas.length > 0) {
                    mascotas.forEach(mascota => {
                        const option = document.createElement('option');
                        option.value = mascota.id;
                        option.textContent = mascota.nombre + ' (' + mascota.especie + ' - ' + mascota.raza + ')';
                        mascotasSelect.appendChild(option);
                    });
                    mascotasContainer.style.display = 'block';
                } else {
                    mascotasSelect.innerHTML = '<option value="">Este cliente no tiene mascotas registradas</option>';
                    mascotasContainer.style.display = 'block';
                    alert('Este cliente no tiene mascotas registradas. Debe registrar una mascota primero.');
                }
            })
            .catch(error => {
                console.error('Error al cargar mascotas:', error);
                alert('Error al cargar las mascotas. Intenta de nuevo.');
            });
    }

    // Validar formulario antes de enviar
    function validarFormulario() {
        const fecha = document.getElementById('fecha').value;
        const hora = document.getElementById('hora').value;

        if (!fecha || !hora) {
            alert('Por favor, completa la fecha y hora de la cita.');
            return false;
        }

        // Validar que la fecha no sea en el pasado
        const fechaSeleccionada = new Date(fecha + 'T' + hora);
        const ahora = new Date();

        if (fechaSeleccionada < ahora) {
            alert('No puedes programar una cita en el pasado. Selecciona una fecha y hora futuras.');
            return false;
        }

        return true;
    }

    <% if (esEditar && cita != null) { %>
    // Si estamos editando, cargar las mascotas del cliente automáticamente
    window.onload = function() {
        // Aquí necesitaríamos el ID del cliente, lo cual requiere una consulta adicional
        // Por ahora, mostrar el contenedor de mascotas
        document.getElementById('mascotasContainer').style.display = 'block';
        document.getElementById('idMascota').innerHTML = '<option value="<%= cita.getIdMascota() %>" selected><%= cita.getNombreMascota() %></option>';
    };
    <% } %>
</script>
</body>
</html>