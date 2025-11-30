<%--
  Created by IntelliJ IDEA.
  User: Usuario
  Date: 17/11/2025
  Time: 9:25
  To change this template use File | Settings | File Templates.
--%>
<%--
  Página de Login
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%
    // ========================================================================
    // VALIDACIÓN DE SESIÓN (Lógica Java intacta)
    // ========================================================================
    if (session != null && session.getAttribute("user") != null) {
        String rolActual = (String) session.getAttribute("rol");
        if (rolActual != null) {
            String rolLower = rolActual.trim().toLowerCase();
            switch (rolLower) {
                case "admin": response.sendRedirect("PJAdmin.jsp"); return;
                case "cliente": response.sendRedirect("paginaInicio.jsp"); return;
                case "veterinario": response.sendRedirect("PJVeterinario.jsp"); return;
            }
        }
    }
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - Veterinaria Bellavista</title>
    <link rel="stylesheet" type="text/css" href="estilos/login.css">
</head>
<body>

<div class="login-wrapper">

    <div class="welcome-section">
        <div>
            <h1>Veterinaria<br>"Bellavista"</h1>
            <p>Gestión integral para el cuidado de tus mascotas.</p>
        </div>

        <div class="rocket-image-placeholder">
            <div style="font-size: 6rem;">🐾</div>
        </div>

        <p style="font-size: 0.8em; opacity: 0.8;">Tu confianza, nuestro compromiso.</p>
    </div>

    <div class="login-container">
        <h2>USER LOGIN</h2>

        <%
            String errorMessage = (String) request.getAttribute("errorMessage");
            if (errorMessage != null) {
        %>
        <div class="error-message">
            ⚠️ <%= errorMessage %>
        </div>
        <% } %>

        <form action="LoginServlet" method="post">
            <div class="form-group">
                <label for="username">Usuario</label>
                <input
                        type="text"
                        id="username"
                        name="username"
                        placeholder="📧 Correo Electrónico"
                        required
                        autocomplete="username">
            </div>

            <div class="form-group">
                <label for="password">Contraseña</label>
                <input
                        type="password"
                        id="password"
                        name="password"
                        placeholder="🔒 Contraseña"
                        required
                        autocomplete="current-password">
            </div>

            <button type="submit">INGRESAR</button>
        </form>

        <div class="forgot-password-link">
            <a href="solicitarRecuperacion.jsp">
                ¿Olvidaste tu contraseña?
            </a>
        </div>

        <div class="test-users">
            <p>Usuarios de prueba:</p>
            <div style="margin-top: 5px;">
                Admin: <code>admin</code> / <code>12345</code><br>
                Vet: <code>vet@test.com</code> / <code>12345</code>
            </div>
        </div>
    </div>
</div>

</body>
</html>