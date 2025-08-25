package demos.springdata.fitmanage.domain.dto.auth.request;

import demos.springdata.fitmanage.domain.dto.tenant.TenantDto;
import jakarta.validation.Valid;

public class RegistrationRequestWrapper {
    @Valid
    private RegistrationRequestDto userDto;
    @Valid
    private TenantDto tenantDto;

    public RegistrationRequestWrapper() {
    }

    public RegistrationRequestDto getUserDto() {
        return userDto;
    }

    public RegistrationRequestWrapper setUserDto(RegistrationRequestDto userDto) {
        this.userDto = userDto;
        return this;
    }

    public TenantDto getTenantDto() {
        return tenantDto;
    }

    public RegistrationRequestWrapper setTenantDto(TenantDto tenantDto) {
        this.tenantDto = tenantDto;
        return this;
    }
}
