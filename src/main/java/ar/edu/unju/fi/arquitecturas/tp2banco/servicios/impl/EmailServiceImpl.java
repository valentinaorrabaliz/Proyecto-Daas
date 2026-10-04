package ar.edu.unju.fi.arquitecturas.tp2banco.servicios.impl;

import ar.edu.unju.fi.arquitecturas.tp2banco.servicios.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void enviarEmailActivacion(String destinatario, String nombreCliente, String tokenActivacion) {
        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

            String urlActivacion = "http://localhost:8080/api/v1/clientes/activar?token=" + tokenActivacion;

            String contenidoHtml = "<html>" +
                    "<body style='font-family: Arial, sans-serif; text-align: center; color: #333;'>" +
                    "<h2>¡Bienvenido al Sistema Bancario, " + nombreCliente + "!</h2>" +
                    "<p>Gracias por registrarte. Para comenzar a operar, confirma tu cuenta haciendo clic en el botón:</p>" +
                    "<a href='" + urlActivacion + "' style='background-color: #0056b3; color: white; padding: 12px 24px; text-decoration: none; border-radius: 5px; display: inline-block;'>Activar Mi Cuenta</a>" +
                    "<p style='margin-top: 20px; font-size: 12px; color: #777;'>Este enlace tiene una validez de 24 horas.</p>" +
                    "</body>" +
                    "</html>";

            helper.setTo(destinatario);
            helper.setSubject("Activación de Cuenta Bancaria");
            helper.setText(contenidoHtml, true);

            mailSender.send(mensaje);
        } catch (MessagingException e) {
            throw new RuntimeException("Error al enviar correo de activación", e);
        }
    }
}