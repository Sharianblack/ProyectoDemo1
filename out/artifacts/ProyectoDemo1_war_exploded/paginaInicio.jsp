<%--
  Vista Principal para CLIENTES
  Solo usuarios con rol "Cliente" pueden acceder a esta página
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%
    // ========================================================================
    // BLOQUE DE SEGURIDAD - VERIFICACIÓN DE SESIÓN Y ROL
    // ========================================================================
    // Este bloque SIEMPRE debe ir al inicio de cada JSP protegida
    // Su función es verificar que el usuario:
    // 1. Tenga una sesión activa (esté logueado)
    // 2. Tenga el ROL correcto para acceder a esta página

    // PASO 1: Verificar si existe una sesión activa
    // Si no hay sesión (usuario no logueado), redirigir al login
    if (session == null || session.getAttribute("user") == null) {
        // response.sendRedirect() → envía al usuario a otra página
        response.sendRedirect("login.jsp");
        // return → detiene la ejecución del JSP para evitar mostrar contenido
        return;
    }

    // PASO 2: Obtener el rol del usuario desde la sesión
    // Cuando el usuario se loguea, guardamos su rol en la sesión
    String rol = (String) session.getAttribute("rol");

    // PASO 3: Verificar que el rol sea "Cliente"
    // Si el rol NO es "Cliente", significa que es un Admin o Veterinario
    // intentando acceder a una página que no le corresponde
    if (rol == null || !rol.equalsIgnoreCase("Cliente")) {
        // Lo enviamos de vuelta al login (también podrías crear una página de "Acceso Denegado")
        response.sendRedirect("login.jsp");
        return;
    }

    // PASO 4: Si llegamos aquí, el usuario SÍ es un Cliente válido
    // Recuperamos sus datos de la sesión para mostrarlos en la página
    String username = (String) session.getAttribute("username");  // El correo
    String nombreUsuario = (String) session.getAttribute("nombre"); // El nombre completo
    Integer userId = (Integer) session.getAttribute("userId");      // El ID del usuario

    // NOTA IMPORTANTE: ¿Por qué necesitamos esto?
    // --------------------------------------------------
    // Sin esta validación, cualquier persona podría escribir directamente en el navegador:
    // http://localhost:8080/paginaInicio.jsp
    // Y accedería sin estar logueada, o peor aún, un Admin podría ver contenido de Cliente
    //
    // Con esta validación:
    // ✓ Solo usuarios logueados pueden ver la página
    // ✓ Solo usuarios con rol "Cliente" pueden acceder
    // ✓ Si alguien intenta acceder sin permiso, es redirigido al login
    // ========================================================================
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Panel Cliente - Sistema Veterinaria</title>

    <%-- INCLUIMOS EL ARCHIVO CSS EXTERNO --%>
    <link rel="stylesheet" href="estilos/cliente.css">

</head>
<body>
<nav class="navbar">
    <h1>Sistema Veterinaria</h1>
    <div class="user-info">
        <span>Bienvenido, <strong><%= nombreUsuario != null ? nombreUsuario : username %></strong></span>
        <span class="info-badge">Cliente</span>
        <a href="LogoutServlet" class="logout-btn">Cerrar Sesión</a>
    </div>
</nav>

<div class="content">
    <div class="welcome-card">
        <h2>¡Bienvenido al Portal de Clientes!</h2>
        <p>Somos la mejor clínica veterinaria de la región. Nos dedicamos al cuidado y bienestar de tu mascota con amor, profesionalismo y tecnología de punta.</p>
        <p>Tu compañero peludo merece lo mejor, y estamos aquí para brindarle lo. ¡Confía en nosotros para cuidar de quien más quieres!</p>
    </div>

    <div class="features-grid">

        <a href="<%=request.getContextPath()%>/CitaServlet?action=misCitas" style="text-decoration: none; color: inherit;">
            <div class="feature-card" style="cursor: pointer;">
                <h3>Mis Citas</h3>
                <p>Agenda y gestiona las citas veterinarias de tus mascotas</p>
            </div>
        </a>
        <div class="feature-card" onclick="location.href='<%=request.getContextPath()%>/MascotaServlet?action=listar'" style="cursor: pointer;">
            <h3>Mis Mascotas</h3>
            <p>Administra la información de tus mascotas registradas</p>
        </div>

        <div class="feature-card">
            <h3>Mi Perfil</h3>
            <p>Actualiza tu información personal</p>
        </div>
    </div>
</div>
</body>
</html>