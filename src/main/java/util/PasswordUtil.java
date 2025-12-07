package util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utilidad para encriptar y verificar contraseñas usando BCrypt
 */
public class PasswordUtil {

    // Factor de trabajo (cost factor)
    // Valor entre 10-12 es recomendado (mayor = más seguro pero más lento)
    private static final int WORK_FACTOR = 12;

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