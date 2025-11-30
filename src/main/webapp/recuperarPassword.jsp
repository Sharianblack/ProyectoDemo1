<%--
  Created by IntelliJ IDEA.
  User: PC-01
  Date: 30/11/2025
  Time: 12:57
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="dao.RecoveryTokenDao" %>
<%
    String token = request.getParameter("token");
    String error = (String) request.getAttribute("error");

    // Si no hay token en la URL, redirigir
    if (token == null || token.trim().isEmpty()) {
        response.sendRedirect("login.jsp");
        return;
    }

    // Validar que el token sea válido
    RecoveryTokenDao tokenDao = new RecoveryTokenDao();
    int userId = tokenDao.validarToken(token);

    if (userId == -1) {
        // Token inválido o expirado
        request.setAttribute("error", "El enlace ha expirado o ya fue utilizado. Solicita uno nuevo.");
        request.getRequestDispatcher("solicitarRecuperacion.jsp").forward(request, response);
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Nueva Contraseña - Veterinaria Bellavista</title>

    <link rel="stylesheet" type="text/css" href="estilos/password.css">
</head>
<body>
<div class="container">
    <div class="header">
        <div class="header-icon">🔐</div>
        <h1>Nueva Contraseña</h1>
        <p>Crea una contraseña segura para tu cuenta</p>
    </div>

    <div class="content">
        <% if (error != null) { %>
        <div class="alert-error">
            <strong>✗ Error:</strong><br>
            <%= error %>
        </div>
        <% } %>

        <div class="info-box">
            <p><strong>⏰ Este enlace expirará en 1 hora</strong></p>
            <p>Por seguridad, completa el proceso lo antes posible.</p>
        </div>

        <form action="PasswordRecoveryServlet" method="post" id="resetForm">
            <input type="hidden" name="action" value="resetear">
            <input type="hidden" name="token" value="<%= token %>">

            <div class="form-group">
                <label for="nuevaPassword">🔒 Nueva Contraseña</label>
                <input
                        type="password"
                        id="nuevaPassword"
                        name="nuevaPassword"
                        placeholder="Ingresa tu nueva contraseña"
                        required
                        minlength="4"
                        autocomplete="new-password">
                <div class="password-strength" id="strengthBar" style="display: none;">
                    <div class="password-strength-bar" id="strengthLevel"></div>
                </div>
            </div>

            <div class="form-group">
                <label for="confirmarPassword">🔒 Confirmar Contraseña</label>
                <input
                        type="password"
                        id="confirmarPassword"
                        name="confirmarPassword"
                        placeholder="Confirma tu nueva contraseña"
                        required
                        minlength="4"
                        autocomplete="new-password">
            </div>

            <div class="password-requirements">
                <h3>Requisitos de la contraseña:</h3>
                <ul>
                    <li>Mínimo 4 caracteres</li>
                    <li>Se recomienda usar letras, números y símbolos</li>
                    <li>No uses contraseñas comunes o fáciles de adivinar</li>
                </ul>
            </div>

            <button type="submit" class="btn-submit" id="submitBtn">
                ✓ Cambiar Contraseña
            </button>
        </form>

        <div class="footer-links">
            <p>
                <a href="login.jsp">← Volver al Login</a>
            </p>
        </div>
    </div>
</div>

<script>
    const passwordInput = document.getElementById('nuevaPassword');
    const confirmInput = document.getElementById('confirmarPassword');
    const strengthBar = document.getElementById('strengthBar');
    const strengthLevel = document.getElementById('strengthLevel');
    const submitBtn = document.getElementById('submitBtn');
    const form = document.getElementById('resetForm');

    // Validar fortaleza de contraseña
    passwordInput.addEventListener('input', function() {
        const password = this.value;
        strengthBar.style.display = password.length > 0 ? 'block' : 'none';

        // Calcular fortaleza
        let strength = 0;
        if (password.length >= 4) strength++;
        if (password.length >= 8) strength++;
        if (/[a-z]/.test(password) && /[A-Z]/.test(password)) strength++;
        if (/\d/.test(password)) strength++;
        if (/[^a-zA-Z0-9]/.test(password)) strength++;

        // Aplicar clase según fortaleza
        strengthLevel.className = 'password-strength-bar';
        if (strength <= 2) {
            strengthLevel.classList.add('strength-weak');
        } else if (strength <= 4) {
            strengthLevel.classList.add('strength-medium');
        } else {
            strengthLevel.classList.add('strength-strong');
        }
    });

    // Validar que las contraseñas coincidan antes de enviar
    form.addEventListener('submit', function(e) {
        const password = passwordInput.value;
        const confirm = confirmInput.value;

        if (password !== confirm) {
            e.preventDefault();
            alert('❌ Las contraseñas no coinciden. Por favor, verifícalas.');
            confirmInput.focus();
            return false;
        }

        if (password.length < 4) {
            e.preventDefault();
            alert('❌ La contraseña debe tener al menos 4 caracteres.');
            passwordInput.focus();
            return false;
        }

        return true;
    });
</script>
</body>
</html>
