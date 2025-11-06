package demos.springdata.fitmanage.service;

public interface UserValidationService {
    void validateTenantScopedCredentials(String email, String phone, Long tenantId);
    void validateGlobalAndTenantScopedCredentials(String email, String phone, Long tenantId);
}
