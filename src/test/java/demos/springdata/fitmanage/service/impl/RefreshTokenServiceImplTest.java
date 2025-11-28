package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.entity.RefreshToken;
import demos.springdata.fitmanage.domain.entity.User;
import demos.springdata.fitmanage.exception.DamilSoftException;
import demos.springdata.fitmanage.repository.RefreshTokenRepository;
import demos.springdata.fitmanage.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceImplTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private RefreshTokenServiceImpl refreshTokenService;

    private final Long EXPIRATION_DURATION = 3600000L;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(refreshTokenService, "refreshTokenExpiration", EXPIRATION_DURATION);
    }


    @Test
    void createRefreshToken_ShouldDeleteOldAndCreateNew() {
        String email = "test@example.com";
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setEmail(email);

        when(userService.findByEmail(email)).thenReturn(user);
        when(refreshTokenRepository.nativeDeleteByUserId(userId)).thenReturn(1); // Симулираме, че е изтрит 1 стар токен

        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(i -> i.getArgument(0));

        RefreshToken result = refreshTokenService.createRefreshToken(email);

        assertNotNull(result);
        assertNotNull(result.getToken(), "Token string should be generated");
        assertEquals(user, result.getUser());

        assertTrue(result.getExpiryDate().isAfter(Instant.now()));

        verify(userService).findByEmail(email);
        verify(refreshTokenRepository).nativeDeleteByUserId(userId);
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }


    @Test
    void findByToken_ShouldReturnToken_WhenExists() {

        String tokenStr = UUID.randomUUID().toString();
        RefreshToken token = new RefreshToken();
        token.setToken(tokenStr);

        when(refreshTokenRepository.findByToken(tokenStr)).thenReturn(Optional.of(token));

        Optional<RefreshToken> result = refreshTokenService.findByToken(tokenStr);

        assertTrue(result.isPresent());
        assertEquals(tokenStr, result.get().getToken());
    }

    @Test
    void findByToken_ShouldReturnEmpty_WhenNotExists() {

        String tokenStr = "invalid-token";
        when(refreshTokenRepository.findByToken(tokenStr)).thenReturn(Optional.empty());

        Optional<RefreshToken> result = refreshTokenService.findByToken(tokenStr);

        assertTrue(result.isEmpty());
    }


    @Test
    void verifyExpiration_ShouldReturnToken_WhenNotExpired() {

        RefreshToken token = new RefreshToken();
        token.setExpiryDate(Instant.now().plusMillis(10000));

        RefreshToken result = refreshTokenService.verifyExpiration(token);

        assertEquals(token, result);

        verify(refreshTokenRepository, never()).delete(any());
    }

    @Test
    void verifyExpiration_ShouldThrowAndRemove_WhenExpired() {

        RefreshToken token = new RefreshToken();
        User user = new User();
        user.setId(UUID.randomUUID());
        token.setUser(user);

        token.setExpiryDate(Instant.now().minusMillis(10000));

        DamilSoftException ex = assertThrows(DamilSoftException.class, () -> {
            refreshTokenService.verifyExpiration(token);
        });

        assertEquals(HttpStatus.CONFLICT, ex.getErrorCode());
        assertEquals("Token is expired. Please make new sign in request", ex.getMessage());

        verify(refreshTokenRepository).delete(token);
        verify(refreshTokenRepository).flush();
    }
}