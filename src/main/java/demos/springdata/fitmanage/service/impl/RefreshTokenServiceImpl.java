package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.entity.RefreshToken;
import demos.springdata.fitmanage.domain.entity.User;
import org.springframework.http.HttpStatus;
import demos.springdata.fitmanage.exception.DamilSoftException;
import demos.springdata.fitmanage.repository.RefreshTokenRepository;
import demos.springdata.fitmanage.service.RefreshTokenService;
import demos.springdata.fitmanage.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;


@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;
    private final static Logger LOGGER = LoggerFactory.getLogger(RefreshTokenServiceImpl.class);

    @Value("${security.jwt.refresh-token-expiration}")
    private Long refreshTokenExpiration;

    @Autowired
    public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository, UserService userService) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userService = userService;
    }

    @Override
    @Transactional
    public RefreshToken createRefreshToken(String email) {

        LOGGER.info("Creating refresh token for email: {}", email);

        User user = userService.findByEmail(email);

        int deletedCount = refreshTokenRepository.nativeDeleteByUserId(user.getId());
        LOGGER.warn("Deleted existing tokens via Native Query: {}", deletedCount);

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshTokenExpiration))
                .build();
        RefreshToken saved = refreshTokenRepository.save(refreshToken);
        LOGGER.info("New refresh token created");

        return saved;
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Override
    public RefreshToken verifyExpiration(RefreshToken token) {
        LOGGER.info("Verifying refresh token expiration.");
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            refreshTokenRepository.flush();
            LOGGER.info("Deleting expired refresh token for gym ID: {}", token.getUser());
            throw new DamilSoftException("Token is expired. Please make new sign in request", HttpStatus.CONFLICT);
        }

        return token;
    }
}
