package demos.springdata.fitmanage.domain.dto.auth.request;

import demos.springdata.fitmanage.domain.dto.tenant.TenantDto;
import jakarta.validation.Valid;

public record RegistrationRequestWrapper(
        @Valid
        RegisterRequest userDto,
        @Valid
        TenantDto tenantDto
) {

}
