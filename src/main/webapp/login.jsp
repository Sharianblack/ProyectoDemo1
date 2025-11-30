<%--
  Created by IntelliJ IDEA.
  User: Usuario
  Date: 17/11/2025
  Time: 9:25
  To change this template use File | Settings | File Templates.
--%>
<%
    // ========================================================================
    // VALIDACIÓN: Si ya hay sesión activa, redirigir a su panel correspondiente
    // ========================================================================
    // NOTA: Esto es DIFERENTE a las otras páginas
    // Aquí NO bloqueamos el acceso, sino que REDIRIGIMOS si ya está logueado

    if (session != null && session.getAttribute("user") != null) {
        // El usuario YA está logueado, obtener su rol
        String rolActual = (String) session.getAttribute("rol");

        if (rolActual != null) {
            // Redirigir según su rol
            String rolLower = rolActual.trim().toLowerCase();

            switch (rolLower) {
                case "admin":
                    response.sendRedirect("PIAdmin.jsp");
                    return;
                case "cliente":
                    response.sendRedirect("paginaInicio.jsp");
                    return;
                case "veterinario":
                    response.sendRedirect("PIVeterinario.jsp");
                    return;
            }
        }
    }

    // Si llegamos aquí, NO hay sesión activa → Mostrar formulario de login
    // ========================================================================
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - Sistema</title>
    <link rel="stylesheet" type="text/css" href="estilos/login.css">
</head>
<body>
<div class="login-wrapper">

    <div class="welcome-section">
        <h1>Bienvenido Veteriniaria "Bellavista"</h1>
        <p>Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.</p>

        <div class="rocket-image-placeholder">
            <img src="tu-imagen-de-cohete.png" alt="Cohete de Lanzamiento" style="width: 150px; height: auto;">
        </div>
    </div>

    <div class="login-container">
        <h2>USER LOGIN</h2> <%
        String errorMessage = (String) request.getAttribute("errorMessage");
        if (errorMessage != null) {
    %>
        <div class="error-message"><%= errorMessage %></div>
        <% } %>

        <form action="LoginServlet" method="post">
            <div class="form-group">
                <label for="username">Usuario:</label>
                <input type="text" id="username" name="username" placeholder="Username" required>
            </div>

            <div class="form-group">
                <label for="password">Contraseña:</label>
                <input type="password" id="password" name="password" placeholder="Password" required>
            </div>

            <button type="submit">Login</button> </form>
    </div>
</div>
</body>
</html>