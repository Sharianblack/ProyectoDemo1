package util;

/**
 * Clase utilitaria para validaciones centralizadas
 * Proporciona métodos estáticos para validar datos de entrada
 */
public class ValidacionUtil {
    
    /**
     * Valida si un email tiene formato correcto
     * @param email Email a validar
     * @return true si el email es válido
     */
    public static boolean esEmailValido(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }
    
    /**
     * Valida si un teléfono tiene formato correcto (exactamente 10 dígitos)
     * @param telefono Teléfono a validar
     * @return true si el teléfono es válido
     */
    public static boolean esTelefonoValido(String telefono) {
        if (telefono == null || telefono.trim().isEmpty()) {
            return false;
        }
        return telefono.matches("^\\d{10}$");
    }
    
    /**
     * Valida si un texto cumple con los requisitos de longitud
     * @param texto Texto a validar
     * @param minLength Longitud mínima
     * @param maxLength Longitud máxima
     * @return true si el texto es válido
     */
    public static boolean esTextoValido(String texto, int minLength, int maxLength) {
        if (texto == null || texto.trim().isEmpty()) {
            return false;
        }
        int length = texto.trim().length();
        return length >= minLength && length <= maxLength;
    }
    
    /**
     * Valida si un texto no está vacío
     * @param texto Texto a validar
     * @return true si el texto no está vacío
     */
    public static boolean noEstaVacio(String texto) {
        return texto != null && !texto.trim().isEmpty();
    }
    
    /**
     * Valida si un nombre solo contiene letras y espacios (sin números)
     * @param nombre Nombre a validar
     * @return true si el nombre es válido
     */
    public static boolean esNombreValido(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return false;
        }
        // Solo letras (incluyendo acentos), espacios y apóstrofes
        return nombre.matches("^[a-zA-ZÀ-ÿ\\s']+$");
    }
    
    /**
     * Sanitiza una cadena para prevenir XSS
     * @param input Cadena de entrada
     * @return Cadena sanitizada
     */
    public static String sanitizar(String input) {
        if (input == null) {
            return "";
        }
        return input.trim()
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#x27;")
                   .replace("&", "&amp;");
    }
    
    /**
     * Valida si una contraseña cumple con los requisitos mínimos
     * @param password Contraseña a validar
     * @return true si la contraseña es válida
     */
    public static boolean esPasswordValido(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        // Mínimo 6 caracteres
        return password.length() >= 6;
    }
    
    /**
     * Valida si un rol es válido
     * @param rol Rol a validar
     * @return true si el rol es válido
     */
    public static boolean esRolValido(String rol) {
        if (rol == null) {
            return false;
        }
        return rol.equals("Admin") || rol.equals("Veterinario") || rol.equals("Cliente");
    }
    
    /**
     * Valida si un ID es válido (mayor a 0)
     * @param id ID a validar
     * @return true si el ID es válido
     */
    public static boolean esIdValido(int id) {
        return id > 0;
    }
    
    /**
     * Valida si una fecha tiene formato válido (yyyy-MM-dd)
     * @param fecha Fecha a validar
     * @return true si la fecha es válida
     */
    public static boolean esFechaValida(String fecha) {
        if (fecha == null || fecha.trim().isEmpty()) {
            return false;
        }
        return fecha.matches("^\\d{4}-\\d{2}-\\d{2}$");
    }
    
    /**
     * Valida si una hora tiene formato válido (HH:mm)
     * @param hora Hora a validar
     * @return true si la hora es válida
     */
    public static boolean esHoraValida(String hora) {
        if (hora == null || hora.trim().isEmpty()) {
            return false;
        }
        return hora.matches("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$");
    }
}
