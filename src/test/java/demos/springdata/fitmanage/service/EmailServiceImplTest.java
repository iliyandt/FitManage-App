package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.service.impl.EmailServiceImpl;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.Mockito.doThrow;
import static org.modelmapper.internal.bytebuddy.matcher.ElementMatchers.any;


@ExtendWith(MockitoExtension.class)
public class EmailServiceImplTest {
    @Mock
    private JavaMailSender mailSender;
    @Mock
    private MimeMessage mimeMessage;
    @InjectMocks
    private EmailServiceImpl emailService;

    private String to;
    private String subject;
    private String body;

    @BeforeEach
    void setup() {
        to = "user@example.com";
        subject = "Email Subject";
        body = "<p>Email content</p>";
    }


    @Test
    void shouldSendEmailSuccessfully() throws MessagingException {
        Mockito.when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        emailService.sendVerificationEmail(to, subject, body);
        Mockito.verify(mailSender).send(mimeMessage);
    }


}
