package demos.springdata.fitmanage.util;

import demos.springdata.fitmanage.domain.entity.User;
import demos.springdata.fitmanage.domain.enums.RoleType;
import demos.springdata.fitmanage.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserUtils {

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        return principal.getUser();
    }

    public boolean hasRole(User user, RoleType roleType) {
        return user.getRoles()
                .stream()
                .anyMatch(r -> r.getName().equals(roleType));
    }

    public boolean isFacilityAdmin(User user) {
        return hasRole(user, RoleType.FACILITY_ADMIN);
    }
}
