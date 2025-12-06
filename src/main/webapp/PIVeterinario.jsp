<%--
  Vista Principal para VETERINARIOS
  Solo usuarios con rol "Veterinario" pueden acceder a esta página
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%
    // ========================================================================
    // BLOQUE DE SEGURIDAD - SOLO VETERINARIOS
    // ========================================================================

    // Verificar sesión activa
    if (session == null || session.getAttribute("user") == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    // Verificar que el rol sea "Veterinario"
    String rol = (String) session.getAttribute("rol");
    if (rol == null || !rol.equalsIgnoreCase("Veterinario")) {
        // Si no es Veterinario, NO puede acceder
        response.sendRedirect("login.jsp");
        return;
    }

    // Recuperar datos del veterinario
    String username = (String) session.getAttribute("username");
    String nombreUsuario = (String) session.getAttribute("nombre");
    Integer userId = (Integer) session.getAttribute("userId");
    // ========================================================================
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Panel Veterinario - Sistema Veterinaria</title>

    <link rel="stylesheet" type="text/css" href="estilos/veterinario.css">

</head>
<body>
<nav class="navbar">
    <h1>🩺 Portal Veterinario</h1>
    <div class="user-info">
        <span>Dr./Dra. <strong><%= nombreUsuario != null ? nombreUsuario : username %></strong></span>
        <span class="vet-badge">⚕️ VETERINARIO</span>
        <a href="LogoutServlet" class="logout-btn">Cerrar Sesión</a>
    </div>
</nav>

<div class="content">
    <div class="welcome-card">
        <h2>👨‍⚕️ Panel de Atención Veterinaria</h2>
        <p>Bienvenido al portal veterinario. Gestiona tus citas, pacientes y registros clínicos.</p>
        <p><strong>Tu ID:</strong> <%= userId %> | <strong>Rol:</strong> Veterinario</p>
    </div>

    <div class="vet-grid">
        <div class="vet-card" onclick="location.href='CitaServlet?action=listar'">
            <div class="icon">📅</div>
            <h3>Mis Citas</h3>
            <p>Ver y gestionar citas asignadas</p>
        </div>

        <div class="vet-card" onclick="location.href='VeterinarioClienteServlet?action=listar'" style="cursor: pointer;">
            <div class="icon">👥</div>
            <h3>Gestión de Clientes</h3>
            <p>Registrar y gestionar clientes</p>
        </div>

        <div class="vet-card">
            <div class="icon">🐕</div>
            <h3>Pacientes</h3>
            <p>Consultar información de mascotas</p>
        </div>

        <div class="vet-card">
            <div class="icon">📋</div>
            <h3>Historial Clínico</h3>
            <p>Registrar diagnósticos y tratamientos</p>
        </div>

        <div class="vet-card">
            <div class="icon">💉</div>
            <h3>Vacunación</h3>
            <p>Registrar aplicación de vacunas</p>
        </div>

        <div class="vet-card">
            <div class="icon">📝</div>
            <h3>Recetas</h3>
            <p>Generar recetas médicas</p>
        </div>

        <div class="vet-card">
            <div class="icon">🕐</div>
            <h3>Mi Horario</h3>
            <p>Consultar disponibilidad y horarios</p>
        </div>

        <div class="vet-card">
            <div class="icon">📊</div>
            <h3>Mis Estadísticas</h3>
            <p>Ver métricas de atención</p>
        </div>

        <div class="vet-card">
            <div class="icon">⚙️</div>
            <h3>Mi Perfil</h3>
            <p>Actualizar información personal</p>
        </div>
    </div>
</div>
</body>
</html>