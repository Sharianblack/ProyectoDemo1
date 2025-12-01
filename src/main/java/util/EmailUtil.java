package util;

// ========================================================================
// IMPORTS DE JAKARTA (NO javax)
// ========================================================================
import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;
import java.util.Date;

/**
 * Utilidad para enviar correos electrónicos usando Jakarta Mail
 * Configurado para usar Gmail como servidor SMTP
 *
 * IMPORTANTE: Usa Jakarta Mail (jakarta.mail.*) en lugar de javax.mail
 */
public class EmailUtil {

    // ========================================================================
    // CONFIGURACIÓN DEL SERVIDOR DE CORREO
    // ========================================================================
    // ⚠️ IMPORTANTE: Cambia estos valores con tu cuenta de Gmail
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "465"; // 👈 PUERTO 465 (SSL) en lugar de 587 (TLS)
    private static final String EMAIL_FROM = "narvateo2021@gmail.com"; // 👈 CAMBIAR AQUÍ
    private static final String EMAIL_PASSWORD = "nsexbaddwooiywvb"; // 👈 CAMBIAR AQUÍ (contraseña de app de 16 caracteres)
    private static final String EMAIL_FROM_NAME = "Veterinaria Bellavista";

    // ========================================================================
    // MÉTODO PRINCIPAL PARA ENVIAR CORREO
    // ========================================================================
    /**
     * Envía un correo electrónico
     * @param destinatario Correo del destinatario
     * @param asunto Asunto del correo
     * @param mensaje Contenido del correo (puede ser HTML)
     * @return true si se envió correctamente, false si hubo error
     */
    public static boolean enviarCorreo(String destinatario, String asunto, String mensaje) {
        try {
            // Configurar propiedades del servidor SMTP
            Properties props = new Properties();
            props.put("mail.smtp.host", SMTP_HOST);
            props.put("mail.smtp.port", SMTP_PORT);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.ssl.enable", "true"); // 👈 SSL en lugar de STARTTLS
            props.put("mail.smtp.ssl.protocols", "TLSv1.2");
            props.put("mail.smtp.ssl.trust", SMTP_HOST); // Confiar en el servidor Gmail

            // Crear sesión con autenticación
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(EMAIL_FROM, EMAIL_PASSWORD);
                }
            });

            // Habilitar debug (opcional, comentar en producción)
            // session.setDebug(true);

            // Crear mensaje
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(EMAIL_FROM, EMAIL_FROM_NAME));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            msg.setSubject(asunto);
            msg.setContent(mensaje, "text/html; charset=utf-8");
            msg.setSentDate(new Date());

            // Enviar correo
            Transport.send(msg);

            System.out.println("✓ Correo enviado exitosamente a: " + destinatario);
            return true;

        } catch (MessagingException e) {
            System.err.println("✗ Error de mensajería al enviar correo a " + destinatario);
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("✗ Error general al enviar correo a " + destinatario);
            e.printStackTrace();
            return false;
        }
    }

    // ========================================================================
    // MÉTODO PARA ENVIAR CORREO DE RECUPERACIÓN DE CONTRASEÑA
    // ========================================================================
    /**
     * Envía un correo con el enlace de recuperación de contraseña
     * @param destinatario Correo del usuario
     * @param nombreUsuario Nombre del usuario
     * @param token Token de recuperación generado
     * @param urlBase URL base de tu aplicación (ej: http://localhost:8080/ProyectoDemo1)
     * @return true si se envió correctamente
     */
    public static boolean enviarCorreoRecuperacion(String destinatario, String nombreUsuario, String token, String urlBase) {
        String asunto = "Recuperación de Contraseña - Veterinaria Bellavista";

        // Crear el enlace de recuperación
        String enlaceRecuperacion = urlBase + "/recuperarPassword.jsp?token=" + token;

        // Crear mensaje HTML
        String mensaje = crearMensajeRecuperacionHTML(nombreUsuario, enlaceRecuperacion);

        return enviarCorreo(destinatario, asunto, mensaje);
    }

    // ========================================================================
    // PLANTILLA HTML PARA CORREO DE RECUPERACIÓN
    // ========================================================================
    private static String crearMensajeRecuperacionHTML(String nombreUsuario, String enlaceRecuperacion) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "    <meta charset='UTF-8'>" +
                "    <style>" +
                "        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
                "        .container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
                "        .header { background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }" +
                "        .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }" +
                "        .button { display: inline-block; padding: 15px 30px; background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); color: white; text-decoration: none; border-radius: 5px; font-weight: bold; margin: 20px 0; }" +
                "        .warning { background: #fff3cd; border-left: 4px solid #ffc107; padding: 15px; margin: 20px 0; }" +
                "        .footer { text-align: center; margin-top: 20px; font-size: 12px; color: #666; }" +
                "    </style>" +
                "</head>" +
                "<body>" +
                "    <div class='container'>" +
                "        <div class='header'>" +
                "            <h1>🐾 Veterinaria Bellavista</h1>" +
                "            <p>Recuperación de Contraseña</p>" +
                "        </div>" +
                "        <div class='content'>" +
                "            <h2>Hola, " + nombreUsuario + "</h2>" +
                "            <p>Hemos recibido una solicitud para restablecer tu contraseña.</p>" +
                "            <p>Para crear una nueva contraseña, haz clic en el siguiente botón:</p>" +
                "            <center>" +
                "                <a href='" + enlaceRecuperacion + "' class='button'>🔑 Restablecer Contraseña</a>" +
                "            </center>" +
                "            <p>O copia y pega este enlace en tu navegador:</p>" +
                "            <p style='word-break: break-all; background: white; padding: 10px; border-radius: 5px;'>" +
                "                <a href='" + enlaceRecuperacion + "'>" + enlaceRecuperacion + "</a>" +
                "            </p>" +
                "            <div class='warning'>" +
                "                <strong>⚠️ Importante:</strong>" +
                "                <ul>" +
                "                    <li>Este enlace expirará en <strong>1 hora</strong></li>" +
                "                    <li>Solo puede usarse <strong>una vez</strong></li>" +
                "                    <li>Si no solicitaste este cambio, ignora este correo</li>" +
                "                </ul>" +
                "            </div>" +
                "        </div>" +
                "        <div class='footer'>" +
                "            <p>Este es un correo automático, por favor no responder.</p>" +
                "            <p>&copy; 2025 Veterinaria Bellavista - Todos los derechos reservados</p>" +
                "        </div>" +
                "    </div>" +
                "</body>" +
                "</html>";
    }

    // ========================================================================
    // MÉTODO PARA ENVIAR CONFIRMACIÓN DE CAMBIO DE CONTRASEÑA
    // ========================================================================
    /**
     * Envía un correo confirmando que la contraseña fue cambiada exitosamente
     */
    public static boolean enviarCorreoConfirmacion(String destinatario, String nombreUsuario) {
        String asunto = "Contraseña Cambiada Exitosamente - Veterinaria Bellavista";

        String mensaje = "<!DOCTYPE html>" +
                "<html>" +
                "<head><meta charset='UTF-8'></head>" +
                "<body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>" +
                "    <div style='max-width: 600px; margin: 0 auto; padding: 20px;'>" +
                "        <div style='background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%); color: white; padding: 30px; text-align: center; border-radius: 10px;'>" +
                "            <h1>✓ Contraseña Actualizada</h1>" +
                "        </div>" +
                "        <div style='background: #f9f9f9; padding: 30px; margin-top: 20px; border-radius: 10px;'>" +
                "            <h2>Hola, " + nombreUsuario + "</h2>" +
                "            <p>Tu contraseña ha sido cambiada exitosamente.</p>" +
                "            <p>Si no realizaste este cambio, por favor contacta a nuestro equipo de soporte inmediatamente.</p>" +
                "            <p style='margin-top: 30px;'>Saludos,<br><strong>Equipo de Veterinaria Bellavista</strong></p>" +
                "        </div>" +
                "    </div>" +
                "</body>" +
                "</html>";

        return enviarCorreo(destinatario, asunto, mensaje);
    }

    // ========================================================================
    // MÉTODO PARA ENVIAR CORREO DE BIENVENIDA (NUEVO USUARIO CREADO)
    // ========================================================================
    /**
     * Envía un correo de bienvenida cuando un administrador crea un nuevo usuario
     * @param destinatario Correo del nuevo usuario
     * @param nombreUsuario Nombre del usuario
     * @param rol Rol asignado (Admin, Cliente, Veterinario)
     * @param passwordTemporal Contraseña temporal asignada
     * @param urlLogin URL del login de la aplicación
     * @return true si se envió correctamente
     */
    public static boolean enviarCorreoBienvenida(String destinatario, String nombreUsuario,
                                                 String rol, String passwordTemporal, String urlLogin) {
        String asunto = "¡Bienvenido a Veterinaria Bellavista! - Cuenta Creada";

        // Emoji según el rol
        String emojiRol = "";
        String descripcionRol = "";
        String colorRol = "#667eea";

        switch (rol.toLowerCase()) {
            case "admin":
                emojiRol = "⚡";
                descripcionRol = "Administrador del Sistema";
                colorRol = "#f5576c";
                break;
            case "cliente":
                emojiRol = "👤";
                descripcionRol = "Cliente";
                colorRol = "#667eea";
                break;
            case "veterinario":
                emojiRol = "⚕️";
                descripcionRol = "Veterinario";
                colorRol = "#38f9d7";
                break;
            default:
                emojiRol = "✓";
                descripcionRol = rol;
                break;
        }

        String mensaje = "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "    <meta charset='UTF-8'>" +
                "    <style>" +
                "        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
                "        .container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
                "        .header { background: linear-gradient(135deg, " + colorRol + " 0%, #764ba2 100%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }" +
                "        .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }" +
                "        .credentials-box { background: white; border-left: 4px solid " + colorRol + "; padding: 20px; margin: 20px 0; border-radius: 5px; }" +
                "        .button { display: inline-block; padding: 15px 30px; background: linear-gradient(135deg, " + colorRol + " 0%, #764ba2 100%); color: white; text-decoration: none; border-radius: 5px; font-weight: bold; margin: 20px 0; }" +
                "        .warning { background: #fff3cd; border-left: 4px solid #ffc107; padding: 15px; margin: 20px 0; border-radius: 5px; }" +
                "        .footer { text-align: center; margin-top: 20px; font-size: 12px; color: #666; }" +
                "        .badge { display: inline-block; background: " + colorRol + "; color: white; padding: 5px 15px; border-radius: 20px; font-weight: bold; }" +
                "    </style>" +
                "</head>" +
                "<body>" +
                "    <div class='container'>" +
                "        <div class='header'>" +
                "            <h1>🐾 Veterinaria Bellavista</h1>" +
                "            <p>¡Bienvenido al equipo!</p>" +
                "        </div>" +
                "        <div class='content'>" +
                "            <h2>¡Felicidades, " + nombreUsuario + "!</h2>" +
                "            <p>Un administrador ha creado una cuenta para ti en nuestro sistema.</p>" +
                "            <p>Tu rol asignado es: <span class='badge'>" + emojiRol + " " + descripcionRol + "</span></p>" +
                "            " +
                "            <div class='credentials-box'>" +
                "                <h3 style='color: " + colorRol + "; margin-top: 0;'>📧 Tus Credenciales de Acceso</h3>" +
                "                <p><strong>Usuario (Correo):</strong> " + destinatario + "</p>" +
                "                <p><strong>Contraseña Temporal:</strong> <code style='background: #f0f0f0; padding: 5px 10px; border-radius: 3px;'>" + passwordTemporal + "</code></p>" +
                "            </div>" +
                "            " +
                "            <center>" +
                "                <a href='" + urlLogin + "' class='button'>🔐 Iniciar Sesión Ahora</a>" +
                "            </center>" +
                "            " +
                "            <div class='warning'>" +
                "                <strong>⚠️ Importante - Seguridad de tu cuenta:</strong>" +
                "                <ul style='margin: 10px 0;'>" +
                "                    <li>Esta es una contraseña temporal</li>" +
                "                    <li>Te recomendamos cambiarla después de iniciar sesión</li>" +
                "                    <li>No compartas tus credenciales con nadie</li>" +
                "                    <li>Si no solicitaste esta cuenta, contacta al administrador</li>" +
                "                </ul>" +
                "            </div>" +
                "            " +
                "            <h3 style='color: " + colorRol + ";'>¿Qué puedes hacer con tu cuenta?</h3>";

        // Agregar características según el rol
        if (rol.equalsIgnoreCase("Admin")) {
            mensaje += "            <ul>" +
                    "                <li>✓ Gestionar usuarios del sistema</li>" +
                    "                <li>✓ Administrar sucursales y servicios</li>" +
                    "                <li>✓ Ver reportes y estadísticas</li>" +
                    "                <li>✓ Configurar el sistema</li>" +
                    "            </ul>";
        } else if (rol.equalsIgnoreCase("Cliente")) {
            mensaje += "            <ul>" +
                    "                <li>✓ Registrar tus mascotas</li>" +
                    "                <li>✓ Agendar citas veterinarias</li>" +
                    "                <li>✓ Ver el historial clínico de tus mascotas</li>" +
                    "                <li>✓ Consultar calendario de vacunación</li>" +
                    "            </ul>";
        } else if (rol.equalsIgnoreCase("Veterinario")) {
            mensaje += "            <ul>" +
                    "                <li>✓ Gestionar tus citas asignadas</li>" +
                    "                <li>✓ Registrar diagnósticos y tratamientos</li>" +
                    "                <li>✓ Actualizar historiales clínicos</li>" +
                    "                <li>✓ Registrar vacunaciones</li>" +
                    "            </ul>";
        }

        mensaje += "            <p style='margin-top: 30px;'>Si tienes alguna duda, no dudes en contactarnos.</p>" +
                "            <p>¡Bienvenido a bordo!</p>" +
                "            <p style='margin-top: 30px;'>Saludos,<br><strong>Equipo de Veterinaria Bellavista</strong></p>" +
                "        </div>" +
                "        <div class='footer'>" +
                "            <p>Este es un correo automático, por favor no responder.</p>" +
                "            <p>&copy; 2025 Veterinaria Bellavista - Todos los derechos reservados</p>" +
                "        </div>" +
                "    </div>" +
                "</body>" +
                "</html>";

        return enviarCorreo(destinatario, asunto, mensaje);
    }

    // ========================================================================
    // MÉTODO DE PRUEBA (para verificar configuración)
    // ========================================================================
    /**
     * Método main para probar el envío de correos
     * Ejecuta esta clase directamente para verificar que la configuración funciona
     */
   public static void main(String[] args) {
        System.out.println("=== PRUEBA DE ENVÍO DE CORREO ===");
        System.out.println("Usando Jakarta Mail API");
        System.out.println("");

        // 👇 CAMBIAR ESTE CORREO PARA PROBAR
        String destinatarioPrueba = "dsmn2005gmail.com";

        System.out.println("Enviando correo de prueba a: " + destinatarioPrueba);
        System.out.println("Desde: " + EMAIL_FROM);
        System.out.println("");

        boolean enviado = enviarCorreo(
                destinatarioPrueba,
                "Prueba de Correo - Veterinaria Bellavista",
                "<html><body>" +
                        "<h1 style='color: #f5576c;'>¡Hola desde Jakarta Mail!</h1>" +
                        "<p>Este es un correo de prueba desde Java usando Jakarta Mail API.</p>" +
                        "<p>Si recibes este correo, la configuración está funcionando correctamente. ✓</p>" +
                        "</body></html>"
        );

        System.out.println("");
        if (enviado) {
            System.out.println("✓✓✓ ÉXITO: Correo de prueba enviado correctamente");
            System.out.println("Revisa tu bandeja de entrada (o SPAM)");
        } else {
            System.out.println("✗✗✗ ERROR: No se pudo enviar el correo");
            System.out.println("");
            System.out.println("Verifica:");
            System.out.println("1. Que EMAIL_FROM sea tu correo de Gmail");
            System.out.println("2. Que EMAIL_PASSWORD sea la contraseña de aplicación (16 caracteres)");
            System.out.println("3. Que tengas habilitada la verificación en 2 pasos en Gmail");
            System.out.println("4. Que las librerías Jakarta Mail estén en el classpath");
        }
    }
}