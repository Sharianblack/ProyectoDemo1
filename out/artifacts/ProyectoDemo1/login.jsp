<%--
  Created by IntelliJ IDEA.
  User: Usuario
  Date: 17/11/2025
  Time: 9:25
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - Sistema</title>


</head>
<body>
<div class="login-container">
    <h2>Iniciar Sesión</h2>

    <%
        String errorMessage = (String) request.getAttribute("errorMessage");
        if (errorMessage != null) {
    %>
    <div class="error-message"><%= errorMessage %></div>
    <% } %>

    <form action="LoginServlet" method="post">
        <div class="form-group">
            <label for="username">Usuario:</label>
            <input type="text" id="username" name="username" required>
        </div>

        <div class="form-group">
            <label for="password">Contraseña:</label>
            <input type="password" id="password" name="password" required>
        </div>

        <button type="submit">Ingresar</button>
    </form>
</div>
</body>
</html>