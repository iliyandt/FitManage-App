package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.tenant.TenantDto;
import demos.springdata.fitmanage.domain.dto.users.UserBaseResponseDto;
import demos.springdata.fitmanage.domain.entity.Tenant;

import java.util.List;
import java.util.Optional;

public interface TenantService {
    Tenant getTenantByEmail(String email);
    TenantDto getTenantDtoByEmail();
    List<UserBaseResponseDto> getAllTenants();

}
