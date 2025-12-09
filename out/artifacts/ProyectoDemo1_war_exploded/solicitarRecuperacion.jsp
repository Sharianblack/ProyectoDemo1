<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
  String success = (String) request.getAttribute("success");
  String error = (String) request.getAttribute("error");
  String correo = (String) request.getAttribute("correo");
%>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Recuperar Contraseña - Veterinaria Bellavista</title>

  <link rel="stylesheet" type="text/css" href="estilos/solicitar.css">
</head>
<body>
<div class="container">
  <div class="header">
    <h1>Recuperar Contraseña</h1>
    <p>Ingresa tu correo electrónico registrado</p>
  </div>

  <div class="content">
    <% if (success != null) { %>
    <div class="alert alert-success">
      <strong>¡Listo!</strong><br>
      <%= success %>
    </div>

    <div class="steps">
      <h3>Pasos a seguir:</h3>
      <ol>
        <li>Revisa tu bandeja de entrada de <strong><%= correo %></strong></li>
        <li>Busca el correo de "Veterinaria Bellavista"</li>
        <li>Haz clic en el enlace de recuperación</li>
        <li>Si no llega en 5 minutos, revisa la carpeta de SPAM</li>
      </ol>
    </div>
    <% } else { %>

    <% if (error != null) { %>
    <div class="alert alert-error">
      <strong>Error:</strong><br>
      <%= error %>
    </div>
    <% } %>

    <div class="info-box">
      <p><strong>¿Olvidaste tu contraseña?</strong></p>
      <p>No te preocupes, te enviaremos un enlace a tu correo para restablecerla.</p>
    </div>

    <form action="PasswordRecoveryServlet" method="post">
      <input type="hidden" name="action" value="solicitar">

      <div class="form-group">
        <label for="correo">Correo Electrónico</label>
        <input
                type="email"
                id="correo"
                name="correo"
                placeholder="tucorreo@ejemplo.com"
                required
                autocomplete="email">
      </div>

      <button type="submit" class="btn-submit">
        Enviar Enlace de Recuperación
      </button>
    </form>
    <% } %>

    <div class="footer-links">
      <p>
        ¿Recordaste tu contraseña?
        <a href="login.jsp">Volver al Login</a>
      </p>
    </div>
  </div>
</div>
</body>
</html>