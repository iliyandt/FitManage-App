package demos.springdata.fitmanage.service;

import jakarta.mail.MessagingException;

public interface EmailService {
    void sendUserVerificationEmail(String to, String subject, String text) throws MessagingException;
}
