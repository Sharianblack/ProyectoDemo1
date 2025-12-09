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
    <title>Login - Veterinaria Llano Grande</title>
    <link rel="stylesheet" type="text/css" href="estilos/login.css">
</head>
<body>

<div class="login-wrapper">

    <div class="welcome-section">
        <div>
            <h1>Veterinaria<br>"Llano Grande"</h1>
            <p>Sistema de gestión de citas para tus mascotas.</p>
            <p style="font-size: 0.95em; margin-top: 10px; opacity: 0.9;">Cuida la salud de tu compañero peludo con nosotros.</p>
        </div>

        <div class="rocket-image-placeholder">
            <div style="font-size: 6rem; font-weight: bold; color: #0077be;">VET</div>
        </div>

        <p style="font-size: 0.8em; opacity: 0.8;">Tu confianza, nuestro compromiso.</p>
    </div>

    <div class="login-container">
        <h2>LOGIN</h2>

        <%
            String errorMessage = (String) request.getAttribute("errorMessage");
            if (errorMessage != null) {
        %>
        <div class="error-message">
            <%= errorMessage %>
        </div>
        <% } %>

        <form action="LoginServlet" method="post">
            <div class="form-group">
                <label for="username">Usuario</label>
                <input
                        type="text"
                        id="username"
                        name="username"
                        placeholder="Correo Electrónico"
                        autocomplete="username">
            </div>

            <div class="form-group">
                <label for="password">Contraseña</label>
                <input
                        type="password"
                        id="password"
                        name="password"
                        placeholder="Contraseña"
                        autocomplete="current-password">
            </div>

            <button type="submit">INGRESAR</button>
        </form>

        <div class="forgot-password-link">
            <a href="solicitarRecuperacion.jsp">
                ¿Olvidaste tu contraseña?
            </a>
        </div>
    </div>
</div>

</body>
</html>