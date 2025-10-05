package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.tenant.TenantDto;
import demos.springdata.fitmanage.domain.dto.users.UserResponseDto;
import demos.springdata.fitmanage.domain.entity.Role;
import demos.springdata.fitmanage.domain.entity.Tenant;
import demos.springdata.fitmanage.domain.entity.User;
import demos.springdata.fitmanage.domain.enums.Abonnement;
import demos.springdata.fitmanage.domain.enums.AbonnementDuration;
import demos.springdata.fitmanage.domain.enums.RoleType;
import demos.springdata.fitmanage.exception.ApiErrorCode;
import demos.springdata.fitmanage.exception.FitManageAppException;
import demos.springdata.fitmanage.repository.TenantRepository;
import demos.springdata.fitmanage.service.TenantService;
import demos.springdata.fitmanage.util.CurrentUserUtils;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class TenantServiceImpl implements TenantService {

    private final TenantRepository tenantRepository;
    private final CurrentUserUtils currentUserUtils;
    private final ModelMapper modelMapper;
    private final static Logger LOGGER = LoggerFactory.getLogger(TenantServiceImpl.class);

    @Autowired
    public TenantServiceImpl(TenantRepository tenantRepository, CurrentUserUtils currentUserUtils, ModelMapper modelMapper) {
        this.tenantRepository = tenantRepository;
        this.currentUserUtils = currentUserUtils;
        this.modelMapper = modelMapper;
    }

    @Override
    public Tenant getTenantByEmail(String email) {
        return tenantRepository.findTenantByUserEmail(email);
    }

    @Transactional
    @Override
    public List<UserResponseDto> getAllTenants() {
        LOGGER.info("Retrieving all tenants..");
        return this.tenantRepository.findAll()
                .stream()
                .map(tenant -> this.modelMapper.map(tenant, UserResponseDto.class))
                .toList();
    }

    @Override
    public TenantDto getTenantDtoByEmail() {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Tenant tenant = getTenantByEmail(email);

        Long countUsersWithRoleMember = tenant.getUsers().stream()
                .filter(user -> currentUserUtils.hasRole(user, RoleType.FACILITY_MEMBER))
                .count();

        TenantDto dto = modelMapper.map(tenant, TenantDto.class).setMembersCount(countUsersWithRoleMember);

        if (tenant.getAbonnement() != null) {
            dto.setAbonnement(tenant.getAbonnement().name());
            dto.setAbonnementDuration(tenant.getAbonnementDuration().getDisplayName());
        }

        return dto;
    }


    @Override
    public void createAbonnement(Long tenantId, Abonnement planName, String duration) {
        Tenant tenant = tenantRepository.findById(tenantId).orElseThrow(() -> new FitManageAppException("Not found", ApiErrorCode.NOT_FOUND));

        tenant.setAbonnement(planName);
        tenant.setAbonnementDuration(AbonnementDuration.valueOf(duration));

        switch (tenant.getAbonnementDuration()) {
            case MONTHLY -> tenant.setSubscriptionValidUntil(LocalDate.now().plusYears(1));
            case ANNUALLY -> tenant.setSubscriptionValidUntil(LocalDate.now().plusMonths(1));
        }

        tenantRepository.save(tenant);
    }
}
