package demos.springdata.fitmanage.util;

import demos.springdata.fitmanage.domain.entity.Role;
import demos.springdata.fitmanage.domain.entity.User;
import demos.springdata.fitmanage.domain.enums.RoleType;
import lombok.experimental.UtilityClass;

import java.util.Set;
import java.util.stream.Collectors;

@UtilityClass
public class UserRoleHelper {

    public static boolean hasRole(User user, RoleType roleType) {
        return user.getRoles()
                .stream()
                .anyMatch(r -> r.getName().equals(roleType));
    }


    public static Set<RoleType> extractRoleTypes(User user) {
        return user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    }

    public static boolean isFacilityAdmin(User user) {
        return hasRole(user, RoleType.ADMIN);
    }


}
