<%--
   Vista Principal para ADMINISTRADORES
   Solo usuarios con rol "Admin" pueden acceder a esta página
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%
  // ========================================================================
  // BLOQUE DE SEGURIDAD - SOLO ADMINISTRADORES
  // ========================================================================

  // Verificar sesión activa
  if (session == null || session.getAttribute("user") == null) {
    response.sendRedirect("login.jsp");
    return;
  }

  // Verificar que el rol sea "Admin"
  String rol = (String) session.getAttribute("rol");
  if (rol == null || !rol.equalsIgnoreCase("Admin")) {
    // Si no es Admin, NO puede acceder
    response.sendRedirect("login.jsp");
    return;
  }

  // Recuperar datos del administrador
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
  <title>Panel Administrador - Sistema Veterinaria</title>

  <link rel="stylesheet" type="text/css" href="estilos/admin.css">
</head>
<body>
<nav class="navbar">
  <h1>Panel de Administración</h1>
  <div class="user-info">
    <span>Administrador: <strong><%= nombreUsuario != null ? nombreUsuario : username %></strong></span>
    <span class="admin-badge">ADMIN</span>
    <a href="LogoutServlet" class="logout-btn">Cerrar Sesión</a>
  </div>
</nav>

<div class="content">
  <div class="welcome-card">
    <h2>Panel de Control del Administrador</h2>
    <p style="font-size: 1.1em; margin-bottom: 15px; color: #2c3e50;">
      Como administrador, tu rol es fundamental para el funcionamiento óptimo del sistema. 
      Tienes la responsabilidad y el privilegio de gestionar usuarios, supervisar operaciones, 
      administrar sucursales y mantener la integridad de toda la plataforma.
    </p>
    <p style="font-style: italic; color: #7f8c8d; border-left: 4px solid #3498db; padding-left: 15px; margin-top: 20px;">
      "El liderazgo efectivo no se trata de tomar decisiones por los demás, 
      sino de crear las condiciones para que todos prosperen."
    </p>
    <p style="margin-top: 20px;"><strong>ID de Usuario:</strong> <%= userId %> | <strong>Nivel de Acceso:</strong> Completo</p>
  </div>

  <div class="admin-grid" style="justify-content: center;">
    <div class="admin-card" onclick="location.href='UsuarioServlet?action=listar'" style="cursor: pointer;">
      <h3>Gestión de Usuarios</h3>
      <p>Crear, editar y eliminar usuarios del sistema</p>
    </div>

    <div class="admin-card" onclick="location.href='SucursalServlet?action=listar'" style="cursor: pointer;">
      <h3>Sucursales</h3>
      <p>Administrar sucursales y sus ubicaciones</p>
    </div>
  </div>
</div>
</body>
</html>