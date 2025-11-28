
package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.entity.User;
import demos.springdata.fitmanage.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserPasswordServiceImplTest {

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserPasswordServiceImpl userPasswordService;

    @Captor
    private ArgumentCaptor<String> passwordCaptor;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("test@damilsoft.com");
    }

    @Test
    void setupMemberInitialPassword_ShouldGenerateSendAndSetEncodedPassword() {

        String expectedEncodedPassword = "encoded_random_password";

        when(passwordEncoder.encode(anyString())).thenReturn(expectedEncodedPassword);


        userPasswordService.setupMemberInitialPassword(user);

        assertEquals(expectedEncodedPassword, user.getPassword(), "User password should be the encoded one");
        assertNotNull(user.getUpdatedAt(), "Updated At timestamp should be set");
        assertTrue(user.getUpdatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));

        verify(emailService).sendInitialPassword(eq(user), passwordCaptor.capture());
        String generatedRawPassword = passwordCaptor.getValue();

        assertNotNull(generatedRawPassword);
        assertFalse(generatedRawPassword.isEmpty());

        verify(passwordEncoder).encode(generatedRawPassword);

        System.out.println("Captured Raw Password: " + generatedRawPassword);
        System.out.println("Encoded Password set to User: " + user.getPassword());
    }
}