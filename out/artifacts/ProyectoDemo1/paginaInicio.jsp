<%--
  Created by IntelliJ IDEA.
  User: Usuario
  Date: 17/11/2025
  Time: 9:25
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    // Verificar si hay sesión activa
    if (session.getAttribute("username") == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    String username = (String) session.getAttribute("username");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Bienvenido - Sistema</title>

</head>
<body>
<nav class="navbar">
    <h1>Mi Sistema</h1>
    <div class="user-info">
        <span> Bienvenido, <strong><%= username %></strong></span>
        <a href="ProyectoDemo1/LogoutServlet" class="logout-btn">Cerrar Sesión</a>
    </div>
</nav>

<div class="content">
    <div class="welcome-card">
        <h2>¡Bienvenido al Sistema!</h2>
        <p>Has iniciado sesión exitosamente.</p>
        <p>Tu sesión está activa y puedes comenzar a usar la aplicación.</p>
    </div>
</div>
</body>
</html>