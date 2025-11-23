package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.exception.MultipleValidationException;
import demos.springdata.fitmanage.service.UserService;
import demos.springdata.fitmanage.service.UserValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class UserValidationServiceImpl implements UserValidationService {


    private final UserService userService;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserValidationServiceImpl.class);

    @Autowired
    public UserValidationServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void validateTenantScopedCredentials(String email, String phone, UUID tenantId) {
        Map<String, String> errors = new HashMap<>();

        checkEmailInTenant(email, tenantId, errors);
        checkPhoneInTenant(phone, tenantId, errors);

        if (!errors.isEmpty()) {
            throw new MultipleValidationException(errors);
        }
    }

    @Override
    public void validateGlobalAndTenantScopedCredentials(String email, String phone, UUID tenantId) {
        Map<String, String> errors = new HashMap<>();

        if (userService.existsByEmail(email)) {
            LOGGER.warn("User with email {} already exists globally", email);
            errors.put("email", "Email is already registered");
        }

        checkEmailInTenant(email, tenantId, errors);
        checkPhoneInTenant(phone, tenantId, errors);

        if (!errors.isEmpty()) {
            throw new MultipleValidationException(errors);
        }
    }


    private void checkEmailInTenant(String email, UUID tenantId, Map<String, String> errors) {
        if (!errors.containsKey("email") && userService.existsByEmailAndTenant(email, tenantId)) {
            LOGGER.warn("User with email {} already exists in tenant {}", email, tenantId);
            errors.put("email", "Email is already registered in this tenant");
        }
    }

    private void checkPhoneInTenant(String phone, UUID tenantId, Map<String, String> errors) {
        if (userService.existsByPhoneAndTenant(phone, tenantId)) {
            LOGGER.warn("User with phone {} already exists in tenant {}", phone, tenantId);
            errors.put("phone", "Phone used from another user");
        }
    }
}
