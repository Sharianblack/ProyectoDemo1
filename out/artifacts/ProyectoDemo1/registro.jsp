<%--
  Created by IntelliJ IDEA.
  User: PC-01
  Date: 7/12/2025
  Time: 8:16
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Registro - Veterinaria Bellavista</title>

    <link rel="stylesheet" href="estilos/registro.css">
</head>
<body>
<div class="registro-container">
    <div class="logo">
        <h1>🐾 Registro</h1>
        <p>Veterinaria Bellavista</p>
    </div>

    <% String success = (String) request.getAttribute("success"); %>
    <% if (success != null) { %>
    <div class="alert alert-success">
        <%= success %>
    </div>
    <% } %>

    <% String error = (String) request.getAttribute("error"); %>
    <% if (error != null) { %>
    <div class="alert alert-error">
        <%= error %>
    </div>
    <% } %>

    <% String warning = (String) request.getAttribute("warning"); %>
    <% if (warning != null) { %>
    <div class="alert alert-warning">
        <%= warning %>
    </div>
    <% } %>

    <div class="info-box">
        📧 <strong>Importante:</strong> Te enviaremos un correo de verificación.
        Debes confirmar tu email para activar tu cuenta.
    </div>

    <form action="EmailVerificationServlet" method="post" onsubmit="return validarFormulario()">
        <input type="hidden" name="action" value="solicitar">

        <div class="form-group">
            <label>Nombre Completo <span class="required">*</span></label>
            <input type="text" name="nombre" id="nombre" placeholder="Juan Pérez" required>
        </div>

        <div class="form-group">
            <label>Correo Electrónico <span class="required">*</span></label>
            <input type="email" name="correo" id="correo" placeholder="ejemplo@gmail.com" required>
        </div>

        <div class="form-group">
            <label>Contraseña <span class="required">*</span></label>
            <input type="password" name="password" id="password"
                   placeholder="Mínimo 4 caracteres" minlength="4"
                   required onkeyup="verificarFuerza()">
            <div class="password-strength">
                <div id="strengthBar" class="password-strength-bar"></div>
            </div>
            <small id="strengthText" style="font-size: 12px; color: #666;"></small>
        </div>

        <div class="form-group">
            <label>Confirmar Contraseña <span class="required">*</span></label>
            <input type="password" name="confirmarPassword" id="confirmarPassword"
                   placeholder="Repite tu contraseña" minlength="4" required>
        </div>

        <div class="form-group">
            <label>Teléfono</label>
            <input type="tel" name="telefono" id="telefono" placeholder="0987654321">
        </div>

        <div class="form-group">
            <label>Dirección</label>
            <input type="text" name="direccion" id="direccion" placeholder="Calle Principal 123">
        </div>

        <button type="submit" class="btn-registro">
            📧 Crear Cuenta
        </button>
    </form>

    <div class="login-link">
        ¿Ya tienes cuenta? <a href="login.jsp">Inicia sesión aquí</a>
    </div>
</div>

<script>
    function verificarFuerza() {
        const password = document.getElementById('password').value;
        const strengthBar = document.getElementById('strengthBar');
        const strengthText = document.getElementById('strengthText');

        let strength = 0;

        if (password.length >= 4) strength++;
        if (password.length >= 8) strength++;
        if (/[A-Z]/.test(password)) strength++;
        if (/[0-9]/.test(password)) strength++;
        if (/[^A-Za-z0-9]/.test(password)) strength++;

        strengthBar.className = 'password-strength-bar';

        if (strength <= 2) {
            strengthBar.classList.add('strength-weak');
            strengthText.textContent = 'Débil';
            strengthText.style.color = '#dc3545';
        } else if (strength <= 3) {
            strengthBar.classList.add('strength-medium');
            strengthText.textContent = 'Media';
            strengthText.style.color = '#ffc107';
        } else {
            strengthBar.classList.add('strength-strong');
            strengthText.textContent = 'Fuerte';
            strengthText.style.color = '#28a745';
        }
    }

    function validarFormulario() {
        const password = document.getElementById('password').value;
        const confirmar = document.getElementById('confirmarPassword').value;

        if (password !== confirmar) {
            alert('❌ Las contraseñas no coinciden');
            return false;
        }

        if (password.length < 4) {
            alert('❌ La contraseña debe tener al menos 4 caracteres');
            return false;
        }

        return true;
    }
</script>
</body>
</html>