package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.mapper.TenantMapper;
import demos.springdata.fitmanage.domain.dto.tenant.TenantDto;
import demos.springdata.fitmanage.domain.dto.tenant.TenantLookUp;
import demos.springdata.fitmanage.domain.entity.Tenant;
import demos.springdata.fitmanage.domain.enums.Abonnement;
import demos.springdata.fitmanage.domain.enums.AbonnementDuration;
import demos.springdata.fitmanage.exception.DamilSoftException;
import demos.springdata.fitmanage.repository.TenantRepository;
import demos.springdata.fitmanage.service.TenantService;
import jakarta.transaction.Transactional;
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
    private final static Logger LOGGER = LoggerFactory.getLogger(TenantServiceImpl.class);
    private final TenantMapper tenantMapper;

    @Autowired
    public TenantServiceImpl(TenantRepository tenantRepository, TenantMapper tenantMapper) {
        this.tenantRepository = tenantRepository;
        this.tenantMapper = tenantMapper;
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
                .map(tenantMapper::toResponse)
                .toList();
    }

    @Override
    public List<TenantLookUp> getShortInfoForAllTenants() {
        return this.tenantRepository.findAll()
                .stream()
                .map(tenantMapper::lookUp)
                .toList();
    }

    @Override
    public TenantDto getTenantDtoByEmail() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Tenant tenant = getTenantByEmail(email);

        return tenantMapper.toResponse(tenant);
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
