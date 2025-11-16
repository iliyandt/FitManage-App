package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.tenant.TenantDto;
import demos.springdata.fitmanage.domain.dto.tenant.TenantLookUp;
import demos.springdata.fitmanage.domain.entity.Tenant;
import demos.springdata.fitmanage.domain.enums.Abonnement;

import java.util.List;

public interface TenantService {
    Tenant getTenantById(Long tenantId);
    Tenant getTenantByEmail(String email);
    TenantDto getTenantDtoByEmail();
    List<TenantDto> getAllTenants();
    List<TenantLookUp> getShortInfoForAllTenants();
    void createAbonnement(Long tenantId, Abonnement planName, String duration);
}
