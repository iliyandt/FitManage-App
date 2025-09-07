package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.users.UserProfileDto;
import demos.springdata.fitmanage.domain.entity.RefreshToken;
import demos.springdata.fitmanage.domain.entity.User;
import demos.springdata.fitmanage.exception.ApiErrorCode;
import demos.springdata.fitmanage.exception.FitManageAppException;
import demos.springdata.fitmanage.repository.RefreshTokenRepository;
import demos.springdata.fitmanage.service.RefreshTokenService;
import demos.springdata.fitmanage.service.UserService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;


@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;
    private final static Logger LOGGER = LoggerFactory.getLogger(RefreshTokenServiceImpl.class);

    @Value("${security.jwt.refresh-token-expiration}")
    private Long refreshTokenExpiration;

    @Autowired
    public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository, UserService userService, ModelMapper modelMapper) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @Override
    public RefreshToken createRefreshToken(String email) {


        LOGGER.info("Creating refresh token for email: {}", email);
        UserProfileDto userBaseResponseDto = userService.getUserProfileByEmail(email);

        User user = modelMapper.map(userBaseResponseDto, User.class);

        refreshTokenRepository.findByUser(user).ifPresent(refreshTokenRepository::delete);

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
            throw new FitManageAppException("Token is expired. Please make new sign in request", ApiErrorCode.CONFLICT);
        }

        return token;
    }
}
