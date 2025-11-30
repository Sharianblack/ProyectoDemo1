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
  <h1>🛡️ Panel de Administración</h1>
  <div class="user-info">
    <span>Administrador: <strong><%= nombreUsuario != null ? nombreUsuario : username %></strong></span>
    <span class="admin-badge">⚡ ADMIN</span>
    <a href="LogoutServlet" class="logout-btn">Cerrar Sesión</a>
  </div>
</nav>

<div class="content">
  <div class="welcome-card">
    <h2>🎯 Panel de Control del Administrador</h2>
    <p>Tienes acceso completo al sistema. Desde aquí puedes gestionar usuarios, sucursales, servicios y más.</p>
    <p><strong>Tu ID:</strong> <%= userId %> | <strong>Rol:</strong> Administrador</p>
  </div>

  <div class="admin-grid">
    <div class="admin-card" onclick="location.href='UsuarioServlet?action=listar'">
      <div class="icon">👥</div>
      <h3>Gestión de Usuarios</h3>
      <p>Crear, editar y eliminar usuarios del sistema</p>
    </div>

    <div class="admin-card">
      <div class="icon">🏥</div>
      <h3>Sucursales</h3>
      <p>Administrar sucursales y sus ubicaciones</p>
    </div>

    <div class="admin-card">
      <div class="icon">💼</div>
      <h3>Servicios</h3>
      <p>Gestionar servicios veterinarios y precios</p>
    </div>

    <div class="admin-card">
      <div class="icon">⚕️</div>
      <h3>Veterinarios</h3>
      <p>Administrar el personal veterinario</p>
    </div>

    <div class="admin-card">
      <div class="icon">📅</div>
      <h3>Citas</h3>
      <p>Ver y gestionar todas las citas del sistema</p>
    </div>

    <div class="admin-card">
      <div class="icon">💰</div>
      <h3>Reportes Financieros</h3>
      <p>Consultar ingresos y pagos</p>
    </div>

    <div class="admin-card">
      <div class="icon">📊</div>
      <h3>Estadísticas</h3>
      <p>Ver métricas y análisis del sistema</p>
    </div>

    <div class="admin-card">
      <div class="icon">⚙️</div>
      <h3>Configuración</h3>
      <p>Ajustes generales del sistema</p>
    </div>
  </div>
</div>
</body>
</html>