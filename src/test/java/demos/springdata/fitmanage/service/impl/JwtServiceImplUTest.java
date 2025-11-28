package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.exception.DamilSoftException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;
import java.security.Key;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class JwtServiceImplUTest {

    @InjectMocks
    private JwtServiceImpl jwtService;

    private final String TEST_SECRET_KEY = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private final long TEST_EXPIRATION = 1000 * 60 * 60;

    @BeforeEach
    void setUp() {

        ReflectionTestUtils.setField(jwtService, "secretKey", TEST_SECRET_KEY);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", TEST_EXPIRATION);
    }

    @Test
    void generateToken_ShouldReturnNonEmptyToken_WhenUserIsValid() {

        UserDetails user = createMockUser("user@gym.bg", "ROLE_USER");


        String token = jwtService.generateToken(user);

        assertNotNull(token);
        assertFalse(token.isEmpty());

        assertEquals(3, token.split("\\.").length);
    }

    @Test
    void extractUsername_ShouldReturnCorrectUsername_WhenTokenIsValid() {

        String expectedUsername = "admin@gym.bg";
        UserDetails user = createMockUser(expectedUsername, "ROLE_ADMIN");
        String token = jwtService.generateToken(user);

        String actualUsername = jwtService.extractUsername(token);

        assertEquals(expectedUsername, actualUsername);
    }

    @Test
    void isTokenValid_ShouldReturnTrue_WhenTokenIsValidAndUserMatches() {
        UserDetails user = createMockUser("valid@gym.bg", "ROLE_USER");
        String token = jwtService.generateToken(user);

        boolean isValid = jwtService.isTokenValid(token, user);

        assertTrue(isValid);
    }

    @Test
    void isTokenValid_ShouldReturnFalse_WhenUsernameDoesNotMatch() {

        UserDetails user1 = createMockUser("user1@gym.bg", "ROLE_USER");
        String token = jwtService.generateToken(user1);

        UserDetails user2 = createMockUser("user2@gym.bg", "ROLE_USER");

        boolean isValid = jwtService.isTokenValid(token, user2);

        assertFalse(isValid);
    }

    @Test
    void isTokenValid_ShouldReturnFalse_WhenTokenIsExpired() {

        Date pastDate = new Date(System.currentTimeMillis() - 10000);
        String expiredToken = Jwts.builder()
                .setSubject("expired@gym.bg")
                .setExpiration(pastDate)
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();

        UserDetails user = createMockUser("expired@gym.bg", "ROLE_USER");

        boolean isValid = jwtService.isTokenValid(expiredToken, user);

        assertFalse(isValid);
    }

    @Test
    void isTokenValid_ShouldReturnFalse_WhenTokenIsMalformed() {

        String malformedToken = "invalid.token.structure";
        UserDetails user = createMockUser("test@gym.bg", "ROLE_USER");

        boolean isValid = jwtService.isTokenValid(malformedToken, user);

        assertFalse(isValid);
    }

    @Test
    void extractUsername_ShouldThrowDamilSoftException_WhenTokenIsInvalid() {
        String invalidToken = "invalid.token";

        assertThrows(DamilSoftException.class, () -> {
            jwtService.extractUsername(invalidToken);
        });
    }


    private UserDetails createMockUser(String username, String role) {
        UserDetails user = mock(UserDetails.class);
        when(user.getUsername()).thenReturn(username);

        Collection authorities = List.of(new SimpleGrantedAuthority(role));
        when(user.getAuthorities()).thenReturn(authorities);

        return user;
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(TEST_SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}