package com.inmobix.backend.service;

import com.postmarkapp.postmark.Postmark;
import com.postmarkapp.postmark.client.ApiClient;
import com.postmarkapp.postmark.client.data.model.message.Message;
import com.postmarkapp.postmark.client.data.model.message.MessageResponse;
import com.postmarkapp.postmark.client.exception.PostmarkException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EmailService {

    private final ApiClient postmarkClient;

    @Value("${postmark.from.email}")
    private String fromEmail;

    @Value("${postmark.from.name}")
    private String fromName;

    public EmailService(@Value("${postmark.api.token:}") String apiToken) {
        if (apiToken == null || apiToken.trim().isEmpty()) {
            throw new IllegalStateException(
                    "POSTMARK_API_TOKEN no está configurado. " +
                            "Por favor configura la variable de entorno POSTMARK_API_TOKEN o " +
                            "agrega 'postmark.api.token' en application.properties");
        }
        this.postmarkClient = Postmark.getApiClient(apiToken);
        System.out.println("✅ EmailService inicializado correctamente con Postmark");
    }

    public void sendHtmlEmail(String to, String subject, String htmlBody) {
        try {
            // Limpiar HTML: eliminar espacios y líneas en blanco al inicio
            String cleanHtml = htmlBody.trim();

            // Extraer versión texto del HTML
            String textBody = htmlBodyToText(cleanHtml, subject);

            // Crear mensaje con ambas versiones
            Message message = new Message(
                    fromName + " <" + fromEmail + ">",
                    to,
                    subject,
                    textBody, // Texto plano
                    cleanHtml // HTML limpio
            );

            // Configurar Reply-To
            message.setReplyTo("noreply@inmobix.com");

            // Forzar que se use el formato HTML
            message.setHtmlBody(cleanHtml);

            MessageResponse response = postmarkClient.deliverMessage(message);

            System.out.println("✅ Email enviado exitosamente - MessageID: " + response.getMessageId());

        } catch (PostmarkException | IOException e) {
            System.err.println("⚠️ Error al enviar correo (continuando): " + e.getMessage());
            // No lanzar excepción para permitir que el registro continúe
        }
    }

    /**
     * Convierte HTML a texto plano básico para clientes que no soportan HTML
     */
    private String htmlBodyToText(String html, String subject) {
        // Extraer el código de 6 dígitos si existe
        String code = "";
        if (html.contains("letter-spacing:8px;")) {
            int start = html.indexOf("letter-spacing:8px;\">") + "letter-spacing:8px;\">".length();
            int end = html.indexOf("</h1>", start);
            if (start > 0 && end > start) {
                code = html.substring(start, end).trim();
            }
        }

        // Construir versión texto según el tipo de email
        if (subject.contains("Verifica tu cuenta") || subject.contains("verificación")) {
            return String.format(
                    "Inmobix - %s\n\n" +
                            "Gracias por registrarte en Inmobix.\n\n" +
                            "Para activar tu cuenta, utiliza el siguiente código de verificación:\n\n" +
                            "CÓDIGO: %s\n\n" +
                            "Este código expira en 5 minutos.\n\n" +
                            "Si no creaste esta cuenta, ignora este correo.\n\n" +
                            "Equipo de Inmobix",
                    subject, code
            );
        } else if (subject.contains("Restablecer contraseña") || subject.contains("Recuperar")) {
            return String.format(
                    "Inmobix - %s\n\n" +
                            "Recibimos una solicitud para restablecer tu contraseña.\n\n" +
                            "Utiliza el siguiente código:\n\n" +
                            "CÓDIGO: %s\n\n" +
                            "Este código expira en 5 minutos.\n\n" +
                            "Si no solicitaste este cambio, ignora este correo.\n\n" +
                            "Equipo de Inmobix",
                    subject, code
            );
        } else if (subject.contains("verificada") || subject.contains("Verificación Exitosa")) {
            return "Inmobix - Cuenta Verificada\n\n" +
                    "¡Excelentes noticias! Tu cuenta ha sido verificada exitosamente.\n\n" +
                    "✓ Tu email está confirmado\n" +
                    "✓ Ya puedes iniciar sesión\n" +
                    "✓ Tu cuenta está activa\n\n" +
                    "Ahora puedes acceder a todas las funcionalidades de Inmobix.\n\n" +
                    "¡Bienvenido a la comunidad Inmobix!\n\n" +
                    "Equipo de Inmobix";
        } else if (subject.contains("Contraseña actualizada")) {
            return "Inmobix - Contraseña Actualizada\n\n" +
                    "Tu contraseña ha sido restablecida exitosamente.\n\n" +
                    "✓ Contraseña actualizada\n" +
                    "✓ Tu cuenta está segura\n\n" +
                    "Ya puedes iniciar sesión con tu nueva contraseña.\n\n" +
                    "⚠️ AVISO DE SEGURIDAD:\n" +
                    "Si no solicitaste este cambio, tu cuenta podría estar comprometida.\n" +
                    "Por favor, contacta a soporte inmediatamente.\n\n" +
                    "Equipo de Inmobix";
        } else {
            // Fallback genérico
            return subject + "\n\nMensaje de Inmobix.\n\nRevisa este correo en un cliente que soporte HTML.";
        }
    }
}