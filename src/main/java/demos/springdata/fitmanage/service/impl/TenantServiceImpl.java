package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.gym.GymBasicInfoDto;
import demos.springdata.fitmanage.domain.dto.gym.GymSummaryDto;
import demos.springdata.fitmanage.domain.entity.Tenant;
import demos.springdata.fitmanage.domain.entity.User;
import demos.springdata.fitmanage.exception.ApiErrorCode;
import demos.springdata.fitmanage.exception.FitManageAppException;
import demos.springdata.fitmanage.exception.MultipleValidationException;
import demos.springdata.fitmanage.repository.TenantRepository;
import demos.springdata.fitmanage.repository.UserRepository;
import demos.springdata.fitmanage.service.TenantService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class TenantServiceImpl implements TenantService {

    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final static Logger LOGGER = LoggerFactory.getLogger(TenantServiceImpl.class);

    @Autowired
    public TenantServiceImpl(TenantRepository tenantRepository, UserRepository userRepository, ModelMapper modelMapper) {
        this.tenantRepository = tenantRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }




    @Override
    public Optional<Tenant> findGymEntityByEmail(String email) {
        return tenantRepository.findTenantByUserEmail(email);
    }

    @Transactional
    @Override
    public List<GymSummaryDto> getAllGyms() {
        LOGGER.info("Retrieving all gyms");
        return this.tenantRepository.findAll()
                .stream()
                .map(gym -> this.modelMapper.map(gym, GymSummaryDto.class))
                .toList();
    }

    @Transactional
    @Override
    public Optional<GymSummaryDto> getGymByEmail(String email) {
        LOGGER.info("Fetching gym with email: {}", email);
        User user = userRepository
                .findByEmail(email).orElseThrow(() -> new FitManageAppException("User not found", ApiErrorCode.NOT_FOUND));

        GymSummaryDto dto = modelMapper.map(user, GymSummaryDto.class);

        int membersCount = user.getMemberships().size();
        dto.setMembersCount(membersCount);

        return Optional.of(dto);
    }



    @Override
    public void updateTenantBasicInfo(String email, GymBasicInfoDto dto) {
        LOGGER.info("Updating basic info for gym with email: {}", email);
        Map<String, String> errors = new HashMap<>();
        Tenant tenant = getTenantOrElseThrow(email);
        updateNameIfChanged(tenant, dto.getUsername(), errors);
        updateGymDetails(dto, tenant);

        if (!errors.isEmpty()) {
            throw new MultipleValidationException(errors);
        }

        tenantRepository.save(tenant);
        LOGGER.info("Updated basic info for gym: {}", email);
    }




    private void updateGymDetails(GymBasicInfoDto gymDto, Tenant tenant) {
        modelMapper.map(gymDto, tenant);
    }

    private void updateNameIfChanged(Tenant tenant, String name, Map<String, String> errors) {
        if (!tenant.getName().equals(name)) {
            LOGGER.info("Gym username change detected: {} -> {}", tenant.getName(), name);
            validateUsernameUniqueness(name, tenant.getId(), errors);
            if (!errors.containsKey("username")) {
                tenant.setName(name);
                LOGGER.info("Gym username updated to {}", name);
            }
        }
    }

    private void validateUsernameUniqueness(String username, Long currentGymId, Map<String, String> errors) {
        Optional<Tenant> existing = tenantRepository.findByName(username);
        if (existing.isPresent() && !existing.get().getId().equals(currentGymId)) {
            errors.put("username", "Username is taken");
        }
    }

    private Tenant getTenantOrElseThrow(String email) {
        return tenantRepository.findTenantByUserEmail(email)
                .orElseThrow(() -> {
                    LOGGER.warn("User with email {} not found", email);
                    return new FitManageAppException("User not found", ApiErrorCode.NOT_FOUND);
                });
    }

}
