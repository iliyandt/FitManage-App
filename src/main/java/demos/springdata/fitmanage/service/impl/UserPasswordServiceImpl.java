package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.entity.User;
import demos.springdata.fitmanage.service.EmailService;
import demos.springdata.fitmanage.service.UserPasswordService;
import demos.springdata.fitmanage.util.SecurityCodeGenerator;
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

        emailService.sendInitialPassword(user, initialPassword);

        user.setPassword(passwordEncoder.encode(initialPassword));
        user.setUpdatedAt(LocalDateTime.now());
    }
}
