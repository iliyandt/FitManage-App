package demos.springdata.fitmanage.util;

import demos.springdata.fitmanage.domain.entity.Tenant;
import demos.springdata.fitmanage.domain.entity.User;
import demos.springdata.fitmanage.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthContext {
    private final UserService userService;
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthContext.class);

    @Autowired
    public AuthContext(UserService userService) {
        this.userService = userService;
    }

    private String getAuthenticatedUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        LOGGER.info("Authenticated user email: {}", email);
        return email;
    }

    public User getAuthenticatedUser() {
        return userService.getUserOrElseThrow(getAuthenticatedUserEmail());
    }

    public Tenant getAuthenticatedTenant() {
        return getAuthenticatedUser().getTenant();
    }
}
