package demos.springdata.fitmanage.util;

import demos.springdata.fitmanage.domain.entity.Role;
import demos.springdata.fitmanage.domain.entity.User;
import demos.springdata.fitmanage.domain.enums.RoleType;

import java.util.Set;
import java.util.stream.Collectors;

public class RoleUtils {

    public RoleUtils() {
    }

    public static Set<RoleType> extractRoleTypes(User user) {
        return user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    }

}
