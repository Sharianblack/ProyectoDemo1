package util;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Utilidad para validar correos electrónicos
 */
public class EmailValidator {

    // Patrón RFC 5322 simplificado para validar emails
    private static final String EMAIL_PATTERN =
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
                    "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    private static final Pattern pattern = Pattern.compile(EMAIL_PATTERN);

    /**
     * Valida si un email tiene formato válido
     * @param email Email a validar
     * @return true si es válido, false si no
     */
    public static boolean esEmailValido(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        // Normalizar (quitar espacios y minúsculas)
        email = email.trim().toLowerCase();

        // Verificar longitud razonable
        if (email.length() > 254) { // RFC 5321
            return false;
        }

        // Verificar formato con regex
        Matcher matcher = pattern.matcher(email);
        if (!matcher.matches()) {
            return false;
        }

        // Verificaciones adicionales
        String[] parts = email.split("@");
        if (parts.length != 2) {
            return false;
        }

        String localPart = parts[0]; // Parte antes del @
        String domainPart = parts[1]; // Parte después del @

        // Validar parte local (antes del @)
        if (localPart.length() > 64) { // RFC 5321
            return false;
        }

        // No puede empezar o terminar con punto
        if (localPart.startsWith(".") || localPart.endsWith(".")) {
            return false;
        }

        // No puede tener puntos consecutivos
        if (localPart.contains("..")) {
            return false;
        }

        // Validar dominio
        if (domainPart.length() > 255) {
            return false;
        }

        // El dominio debe tener al menos un punto
        if (!domainPart.contains(".")) {
            return false;
        }

        return true;
    }

    /**
     * Valida si un email pertenece a dominios populares y confiables
     * @param email Email a validar
     * @return true si es de un dominio conocido
     */
    public static boolean esDominioConfiable(String email) {
        if (!esEmailValido(email)) {
            return false;
        }

        email = email.toLowerCase().trim();
        String dominio = email.substring(email.indexOf("@") + 1);

        // Lista de dominios populares y confiables
        String[] dominiosConfiables = {
                "gmail.com", "yahoo.com", "hotmail.com", "outlook.com",
                "live.com", "icloud.com", "aol.com", "protonmail.com",
                "zoho.com", "mail.com", "gmx.com", "yandex.com",
                "msn.com", "hotmail.es", "yahoo.es", "outlook.es"
        };

        for (String dominioConfiable : dominiosConfiables) {
            if (dominio.equals(dominioConfiable)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Detecta emails temporales/desechables comunes
     * @param email Email a validar
     * @return true si parece ser temporal
     */
    public static boolean esEmailTemporal(String email) {
        if (!esEmailValido(email)) {
            return false;
        }

        email = email.toLowerCase().trim();
        String dominio = email.substring(email.indexOf("@") + 1);

        // Lista de dominios temporales conocidos
        String[] dominiosTemporales = {
                "10minutemail.com", "guerrillamail.com", "mailinator.com",
                "tempmail.com", "throwaway.email", "maildrop.cc",
                "sharklasers.com", "yopmail.com", "temp-mail.org",
                "fakeinbox.com", "trashmail.com", "getnada.com"
        };

        for (String temporal : dominiosTemporales) {
            if (dominio.equals(temporal) || dominio.endsWith("." + temporal)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Valida y proporciona feedback detallado
     * @param email Email a validar
     * @return Mensaje descriptivo del problema, o null si es válido
     */
    public static String validarConMensaje(String email) {
        if (email == null || email.trim().isEmpty()) {
            return "El correo no puede estar vacío";
        }

        email = email.trim();

        if (email.length() > 254) {
            return "El correo es demasiado largo (máximo 254 caracteres)";
        }

        if (!email.contains("@")) {
            return "El correo debe contener el símbolo @";
        }

        String[] parts = email.split("@");
        if (parts.length != 2) {
            return "El correo debe tener exactamente un símbolo @";
        }

        String localPart = parts[0];
        String domainPart = parts[1];

        if (localPart.isEmpty()) {
            return "El correo debe tener una parte antes del @";
        }

        if (domainPart.isEmpty()) {
            return "El correo debe tener un dominio después del @";
        }

        if (!domainPart.contains(".")) {
            return "El dominio debe contener al menos un punto (ej: gmail.com)";
        }

        if (localPart.startsWith(".") || localPart.endsWith(".")) {
            return "El correo no puede empezar o terminar con punto antes del @";
        }

        if (localPart.contains("..")) {
            return "El correo no puede tener puntos consecutivos";
        }

        if (!pattern.matcher(email).matches()) {
            return "El formato del correo no es válido";
        }

        if (esEmailTemporal(email)) {
            return "No se permiten correos temporales o desechables";
        }

        return null; // Email válido
    }

    /**
     * Normaliza un email (minúsculas, sin espacios)
     * @param email Email a normalizar
     * @return Email normalizado
     */
    public static String normalizar(String email) {
        if (email == null) {
            return null;
        }
        return email.trim().toLowerCase();
    }

    // ========================================================================
    // MÉTODO DE PRUEBA
    // ========================================================================
    public static void main(String[] args) {
        System.out.println("=== PRUEBA DE VALIDACIÓN DE EMAILS ===\n");

        String[] testEmails = {
                "usuario@gmail.com",          // ✅ Válido
                "test.user@yahoo.com",        // ✅ Válido
                "invalid@",                   // ❌ Sin dominio
                "@gmail.com",                 // ❌ Sin usuario
                "no-arroba.com",              // ❌ Sin @
                "..test@gmail.com",           // ❌ Puntos consecutivos
                "test@dominio",               // ❌ Sin extensión
                "user@10minutemail.com",      // ❌ Email temporal
                "MiEmail@GMAIL.COM",          // ✅ Válido (se normaliza)
                "a".repeat(300) + "@test.com" // ❌ Muy largo
        };

        for (String email : testEmails) {
            System.out.println("Email: " + email);
            System.out.println("  ✓ Es válido: " + esEmailValido(email));

            String mensaje = validarConMensaje(email);
            if (mensaje != null) {
                System.out.println("  ❌ Problema: " + mensaje);
            } else {
                System.out.println("  ✅ Email válido");
                System.out.println("  📧 Normalizado: " + normalizar(email));
                System.out.println("  🔒 Dominio confiable: " + esDominioConfiable(email));
            }
            System.out.println();
        }
    }
}