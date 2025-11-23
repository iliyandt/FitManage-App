package demos.springdata.fitmanage.service;

import java.util.UUID;

public interface UserValidationService {
    void validateTenantScopedCredentials(String email, String phone, UUID tenantId);
    void validateGlobalAndTenantScopedCredentials(String email, String phone, UUID tenantId);
}
