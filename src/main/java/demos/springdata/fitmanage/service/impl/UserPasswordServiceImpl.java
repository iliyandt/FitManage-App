package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.entity.User;
import demos.springdata.fitmanage.exception.ApiErrorCode;
import demos.springdata.fitmanage.exception.FitManageAppException;
import demos.springdata.fitmanage.service.EmailService;
import demos.springdata.fitmanage.service.UserPasswordService;
import demos.springdata.fitmanage.util.SecurityCodeGenerator;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserPasswordServiceImpl implements UserPasswordService {

    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserPasswordServiceImpl.class);


    @Autowired
    public UserPasswordServiceImpl(BCryptPasswordEncoder passwordEncoder, EmailService emailService) {
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }


    @Override
    public void setupMemberInitialPassword(User user) {
        LOGGER.info("Initial password for user with email: {} will be created", user.getEmail());
        String initialPassword = SecurityCodeGenerator.generateDefaultPassword();

        LOGGER.debug("Initial password {}", initialPassword);

        sendInitialPassword(user, initialPassword);

        user.setPassword(passwordEncoder.encode(initialPassword)).setUpdatedAt(LocalDateTime.now());
    }


    private void sendInitialPassword(User user, String initialPassword) {
        String subject = "Password";
        String password = "PASSWORD " + initialPassword;
        String htmlMessage = "<html>"
                + "<body style=\"font-family: Arial, sans-serif;\">"
                + "<div style=\"background-color: #f5f5f5; padding: 20px;\">"
                + "<h2 style=\"color: #333;\">Welcome to our app!</h2>"
                + "<p style=\"font-size: 16px;\">Please use the password bellow for initial login</p>"
                + "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">"
                + "<h3 style=\"color: #333;\">Password:</h3>"
                + "<p style=\"font-size: 18px; font-weight: bold; color: #007bff;\">" + password + "</p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";

        try {
            LOGGER.info("Sending initial password to: {}", user.getEmail());
            emailService.sendUserVerificationEmail(user.getEmail(), subject, htmlMessage);
        } catch (MessagingException e) {
            LOGGER.error("Failed to send password to: {}", user.getEmail(), e);
            throw new FitManageAppException("Failed to send password to user", ApiErrorCode.INTERNAL_ERROR);
        }
    }
}
