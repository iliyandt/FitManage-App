package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.tenant.TenantDto;
import demos.springdata.fitmanage.domain.dto.users.UserResponseDto;
import demos.springdata.fitmanage.domain.entity.Tenant;
import demos.springdata.fitmanage.domain.enums.Abonnement;

import java.util.List;

public interface TenantService {
    Tenant getTenantByEmail(String email);
    TenantDto getTenantDtoByEmail();
    List<UserResponseDto> getAllTenants();
    void createAbonnement(Long tenantId, Abonnement planName, String duration);

}
