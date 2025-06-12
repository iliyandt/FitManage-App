package demos.springdata.fitmanage.service.impl;
import demos.springdata.fitmanage.domain.entity.Gym;
import demos.springdata.fitmanage.domain.entity.RefreshToken;
import demos.springdata.fitmanage.exception.ApiErrorCode;
import demos.springdata.fitmanage.exception.FitManageAppException;
import demos.springdata.fitmanage.repository.GymRepository;
import demos.springdata.fitmanage.repository.RefreshTokenRepository;
import demos.springdata.fitmanage.service.RefreshTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;


@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final GymRepository gymRepository;
    private final static Logger LOGGER = LoggerFactory.getLogger(RefreshTokenServiceImpl.class);


    @Autowired
    public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository, GymRepository gymRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.gymRepository = gymRepository;

    }

    @Override
    public RefreshToken createRefreshToken(String email) {

        Gym gym = gymRepository.findByEmail(email)
                .orElseThrow(() -> new FitManageAppException("Gym not found", ApiErrorCode.NOT_FOUND));

        refreshTokenRepository.findByGym(gym).ifPresent(refreshTokenRepository::delete);

        RefreshToken refreshToken = RefreshToken.builder()
                .gym(gym)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(600_000))
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Override
    public RefreshToken verifyExpiration(RefreshToken token) {
        LOGGER.info("Verifying refresh token expiration for token: {}", token.getToken());
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            refreshTokenRepository.flush();
            LOGGER.info("Deleting expired refresh token for gym ID: {}", token.getGym());
            throw new FitManageAppException("Token is expired. Please make new sign in request", ApiErrorCode.CONFLICT);
        }

        return token;
    }
}
