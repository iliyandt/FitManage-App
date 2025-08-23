package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.tenant.UserResponseDto;
import demos.springdata.fitmanage.domain.entity.Tenant;

import java.util.List;
import java.util.Optional;

public interface TenantService {
    Optional<Tenant> getTenantByEmail(String email);
    List<UserResponseDto> getAllTenants();

}
