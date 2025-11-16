package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.tenant.TenantDto;
import demos.springdata.fitmanage.domain.dto.tenant.TenantLookUp;
import demos.springdata.fitmanage.domain.entity.Tenant;
import demos.springdata.fitmanage.domain.enums.Abonnement;
import demos.springdata.fitmanage.domain.enums.AbonnementDuration;
import demos.springdata.fitmanage.domain.enums.RoleType;
import demos.springdata.fitmanage.exception.DamilSoftException;
import demos.springdata.fitmanage.repository.TenantRepository;
import demos.springdata.fitmanage.service.TenantService;
import demos.springdata.fitmanage.util.UserRoleHelper;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class TenantServiceImpl implements TenantService {

    private final TenantRepository tenantRepository;
    private final ModelMapper modelMapper;
    private final static Logger LOGGER = LoggerFactory.getLogger(TenantServiceImpl.class);

    @Autowired
    public TenantServiceImpl(TenantRepository tenantRepository, ModelMapper modelMapper) {
        this.tenantRepository = tenantRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public Tenant getTenantById(Long tenantId) {
        return this.tenantRepository.findById(tenantId).orElseThrow(()-> new DamilSoftException("Tenant not found", HttpStatus.NOT_FOUND));
    }

    @Override
    public Tenant getTenantByEmail(String email) {
        return tenantRepository.findTenantByUserEmail(email);
    }

    @Transactional
    @Override
    public List<TenantDto> getAllTenants() {
        LOGGER.info("Retrieving all tenants..");
        return this.tenantRepository.findAll()
                .stream()
                .map(tenant -> {
                    return TenantDto.builder()
                            .id(tenant.getId())
                            .stripeAccountId(tenant.getStripeAccountId() == null ? null : tenant.getStripeAccountId())
                            .name(tenant.getName())
                            .businessEmail(tenant.getBusinessEmail())
                            .address(tenant.getAddress())
                            .city(tenant.getCity())
                            .membersCount(getCountOfUsersWithRoleMemberWithinATenant(tenant))
                            .abonnement(tenant.getAbonnement() == null ? null : tenant.getAbonnement().name())
                            .abonnementDuration(tenant.getAbonnementDuration() == null ? null : tenant.getAbonnementDuration().name())
                            .build();
                }).toList();

    }

    @Override
    public List<TenantLookUp> getShortInfoForAllTenants() {
        return this.tenantRepository.findAll()
                .stream()
                .map(tenant -> this.modelMapper.map(tenant, TenantLookUp.class))
                .toList();
    }

    @Override
    public TenantDto getTenantDtoByEmail() {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Tenant tenant = getTenantByEmail(email);

        return TenantDto.builder()
                .id(tenant.getId())
                .stripeAccountId(tenant.getStripeAccountId() == null ? null : tenant.getStripeAccountId())
                .name(tenant.getName())
                .businessEmail(tenant.getBusinessEmail())
                .address(tenant.getAddress())
                .city(tenant.getCity())
                .membersCount(getCountOfUsersWithRoleMemberWithinATenant(tenant))
                .abonnement(tenant.getAbonnement() == null ? null : tenant.getAbonnement().name())
                .abonnementDuration(tenant.getAbonnementDuration() == null ? null : tenant.getAbonnementDuration().name())
                .build();
    }

    private Long getCountOfUsersWithRoleMemberWithinATenant(Tenant tenant) {
        return tenant.getUsers().stream()
                .filter(user -> UserRoleHelper.hasRole(user, RoleType.MEMBER))
                .count();
    }


    @Override
    public void createAbonnement(Long tenantId, Abonnement planName, String duration) {

        Tenant tenant = tenantRepository.findById(tenantId).orElseThrow(() -> new DamilSoftException("Not found", HttpStatus.NOT_FOUND));
        LOGGER.info("Tenant found");

        if (tenant.getAbonnement() != null) {
            throw new DamilSoftException("Tenant already has active subscription", HttpStatus.CONFLICT);
        } else {
            tenant.setAbonnement(planName);
            tenant.setAbonnementDuration(AbonnementDuration.valueOf(duration));

            switch (tenant.getAbonnementDuration()) {
                case MONTHLY -> tenant.setSubscriptionValidUntil(LocalDate.now().plusMonths(1));
                case ANNUALLY -> tenant.setSubscriptionValidUntil(LocalDate.now().plusYears(1));
            }

            tenantRepository.save(tenant);
            LOGGER.info("Abonnement saved");
        }

    }
}
