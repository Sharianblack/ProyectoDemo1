package util;

import org.mindrot.jbcrypt.BCrypt;
import java.security.SecureRandom;

/**
 * Utilidad para encriptar y verificar contraseñas usando BCrypt
 */
public class PasswordUtil {

    // Factor de trabajo (cost factor)
    // Valor entre 10-12 es recomendado (mayor = más seguro pero más lento)
    private static final int WORK_FACTOR = 12;

    // Caracteres permitidos para generar contraseñas seguras
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String NUMBERS = "0123456789";
    private static final String SPECIAL_CHARS = "!@#$%&*-_+=?";
    private static final String ALL_CHARS = UPPERCASE + LOWERCASE + NUMBERS + SPECIAL_CHARS;

    // ========================================================================
    // GENERAR CONTRASEÑA SEGURA
    // ========================================================================
    /**
     * Genera una contraseña segura aleatoria similar a las de Google
     * Incluye mayúsculas, minúsculas, números y caracteres especiales
     * @param length Longitud de la contraseña (mínimo 12, recomendado 16)
     * @return Contraseña segura generada
     */
    public static String generarPasswordSegura(int length) {
        if (length < 12) {
            length = 12; // Mínimo 12 caracteres para seguridad
        }

        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(length);

        // Asegurar que tenga al menos un carácter de cada tipo
        password.append(UPPERCASE.charAt(random.nextInt(UPPERCASE.length())));
        password.append(LOWERCASE.charAt(random.nextInt(LOWERCASE.length())));
        password.append(NUMBERS.charAt(random.nextInt(NUMBERS.length())));
        password.append(SPECIAL_CHARS.charAt(random.nextInt(SPECIAL_CHARS.length())));

        // Completar el resto de la contraseña
        for (int i = 4; i < length; i++) {
            password.append(ALL_CHARS.charAt(random.nextInt(ALL_CHARS.length())));
        }

        // Mezclar los caracteres para que no estén en orden predecible
        return mezclarCaracteres(password.toString(), random);
    }

    /**
     * Genera una contraseña segura con longitud por defecto de 16 caracteres
     * @return Contraseña segura generada
     */
    public static String generarPasswordSegura() {
        return generarPasswordSegura(16);
    }

    /**
     * Mezcla aleatoriamente los caracteres de un string
     */
    private static String mezclarCaracteres(String input, SecureRandom random) {
        char[] chars = input.toCharArray();
        for (int i = chars.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = chars[i];
            chars[i] = chars[j];
            chars[j] = temp;
        }
        return new String(chars);
    }

    // ========================================================================
    // ENCRIPTAR CONTRASEÑA
    // ========================================================================
    /**
     * Encripta una contraseña usando BCrypt
     * @param plainPassword Contraseña en texto plano
     * @return Hash de la contraseña
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía");
        }

        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(WORK_FACTOR));
    }

    // ========================================================================
    // VERIFICAR CONTRASEÑA
    // ========================================================================
    /**
     * Verifica si una contraseña coincide con su hash
     * @param plainPassword Contraseña en texto plano
     * @param hashedPassword Hash almacenado en la BD
     * @return true si coinciden, false si no
     */
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }

        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (IllegalArgumentException e) {
            // Hash inválido
            System.err.println("❌ Hash inválido: " + e.getMessage());
            return false;
        }
    }

    // ========================================================================
    // VERIFICAR SI ES UN HASH BCRYPT VÁLIDO
    // ========================================================================
    /**
     * Verifica si un string es un hash BCrypt válido
     * @param hash String a verificar
     * @return true si es un hash BCrypt válido
     */
    public static boolean isBCryptHash(String hash) {
        if (hash == null || hash.isEmpty()) {
            return false;
        }

        // Los hashes BCrypt empiezan con $2a$, $2b$, $2x$ o $2y$
        return hash.matches("^\\$2[aby]?\\$\\d{2}\\$.{53}$");
    }

    // ========================================================================
    // MÉTODO DE PRUEBA
    // ========================================================================
    /**
     * Método main para probar la encriptación
     */
    public static void main(String[] args) {
        System.out.println("=== PRUEBA DE ENCRIPTACIÓN BCrypt ===\n");

        // 1. Encriptar contraseña
        String passwordOriginal = "12345";
        System.out.println("Contraseña original: " + passwordOriginal);

        String hash1 = hashPassword(passwordOriginal);
        System.out.println("Hash 1: " + hash1);

        String hash2 = hashPassword(passwordOriginal);
        System.out.println("Hash 2: " + hash2);

        System.out.println("\n✅ Nota: Cada hash es diferente aunque la contraseña sea la misma (salt aleatorio)\n");

        // 2. Verificar contraseña correcta
        boolean esCorrecta = checkPassword(passwordOriginal, hash1);
        System.out.println("¿Contraseña correcta? " + esCorrecta);

        // 3. Verificar contraseña incorrecta
        boolean esIncorrecta = checkPassword("54321", hash1);
        System.out.println("¿Contraseña incorrecta? " + !esIncorrecta);

        // 4. Verificar si es un hash BCrypt
        System.out.println("\n¿Es hash BCrypt válido? " + isBCryptHash(hash1));
        System.out.println("¿'12345' es hash BCrypt? " + isBCryptHash("12345"));
    }
}