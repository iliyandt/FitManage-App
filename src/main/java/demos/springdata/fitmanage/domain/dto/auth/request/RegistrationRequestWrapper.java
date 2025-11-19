package demos.springdata.fitmanage.domain.dto.auth.request;

import demos.springdata.fitmanage.domain.dto.tenant.TenantRegisterRequest;
import jakarta.validation.Valid;

public record RegistrationRequestWrapper(
        @Valid
        UserRegisterRequest userDto,
        @Valid
        TenantRegisterRequest tenantRequest
) {

}
