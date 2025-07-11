package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {
    private JavaMailSender emailSender;
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Autowired
    public EmailServiceImpl(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void sendUserVerificationEmail(String to, String subject, String text) throws MessagingException {
        LOGGER.info("Preparing to send verification email to: {}, subject: {}", to, subject);

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text, true);

        try {
            emailSender.send(message);
            LOGGER.info("Verification email successfully sent to: {}", to);
        } catch (MailException ex) {
            LOGGER.error("Failed to send verification email to: {}", to, ex);
            throw ex;
        }

    }
}
