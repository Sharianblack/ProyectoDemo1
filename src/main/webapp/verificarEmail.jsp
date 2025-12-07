<%--
  Created by IntelliJ IDEA.
  User: PC-01
  Date: 7/12/2025
  Time: 8:13
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Verificando Email...</title>

    <link rel="stylesheet" href="estilos/V_E.css">
</head>
<body>
<div class="verificacion-container">
    <div class="icon">📧</div>
    <h2>Verificando tu correo<span class="dots"></span></h2>
    <div class="spinner"></div>
    <p>Por favor espera mientras procesamos tu verificación.</p>
    <p><strong>Serás redirigido automáticamente.</strong></p>
    <div class="progress-bar">
        <div class="progress-fill"></div>
    </div>
</div>

<script>
    // Obtener el token de la URL
    const urlParams = new URLSearchParams(window.location.search);
    const token = urlParams.get('token');

    // Validar que exista el token
    if (!token || token.trim() === '') {
        alert('❌ Token de verificación no válido');
        window.location.href = 'login.jsp';
    } else {
        // Redirigir al servlet después de 2 segundos (tiempo de la animación)
        setTimeout(function() {
            window.location.href = 'EmailVerificationServlet?token=' + encodeURIComponent(token);
        }, 2000);
    }
</script>
</body>
</html>