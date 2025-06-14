package demos.springdata.fitmanage.service.impl;
import demos.springdata.fitmanage.domain.dto.gym.GymSummaryDto;
import demos.springdata.fitmanage.domain.dto.superadmin.SuperAdminDto;
import demos.springdata.fitmanage.domain.entity.Gym;
import demos.springdata.fitmanage.domain.entity.RefreshToken;
import demos.springdata.fitmanage.domain.entity.SuperAdminUser;
import demos.springdata.fitmanage.exception.ApiErrorCode;
import demos.springdata.fitmanage.exception.FitManageAppException;
import demos.springdata.fitmanage.repository.GymRepository;
import demos.springdata.fitmanage.repository.RefreshTokenRepository;
import demos.springdata.fitmanage.repository.SuperAdminRepository;
import demos.springdata.fitmanage.service.GymService;
import demos.springdata.fitmanage.service.RefreshTokenService;
import demos.springdata.fitmanage.service.SuperAdminService;
import org.modelmapper.ModelMapper;
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
    private final GymService gymService;
    private final SuperAdminService superAdminService;
    private final ModelMapper modelMapper;
    private final static Logger LOGGER = LoggerFactory.getLogger(RefreshTokenServiceImpl.class);


    @Autowired
    public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository, GymService gymService, SuperAdminService superAdminService, ModelMapper modelMapper) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.gymService = gymService;
        this.superAdminService = superAdminService;
        this.modelMapper = modelMapper;
    }

    @Override
    public RefreshToken createRefreshToken(String email) {

        Optional<GymSummaryDto> gymDtoOpt = gymService.getGymByEmail(email);
        Optional<SuperAdminDto> adminDtoOpt = superAdminService.findByEmail(email);

        if (gymDtoOpt.isPresent()) {
            Gym gym = modelMapper.map(gymDtoOpt.get(), Gym.class);

            refreshTokenRepository.findByGym(gym).ifPresent(refreshTokenRepository::delete);

            RefreshToken refreshToken = RefreshToken.builder()
                    .gym(gym)
                    .token(UUID.randomUUID().toString())
                    .expiryDate(Instant.now().plusMillis(604_800_000))
                    .build();

            return refreshTokenRepository.save(refreshToken);

        } else if (adminDtoOpt.isPresent()) {
            SuperAdminUser superAdmin = modelMapper.map(adminDtoOpt.get(), SuperAdminUser.class);

            refreshTokenRepository.findBySuperAdminUser(superAdmin).ifPresent(refreshTokenRepository::delete);

            RefreshToken refreshToken = RefreshToken.builder()
                    .superAdminUser(superAdmin)
                    .token(UUID.randomUUID().toString())
                    .expiryDate(Instant.now().plusMillis(604_800_000))
                    .build();

            return refreshTokenRepository.save(refreshToken);

        } else {
            throw new FitManageAppException("User not found with email: " + email, ApiErrorCode.NOT_FOUND);
        }
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
