package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.users.UserResponse;
import demos.springdata.fitmanage.domain.dto.users.UserUpdate;
import demos.springdata.fitmanage.domain.entity.User;
import demos.springdata.fitmanage.domain.enums.Gender;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface UserService {
    User getCurrentUser();
    UserResponse getUserProfileByEmail(String email);
    UserResponse updateProfile(UserUpdate dto);
    List<User> findMembersByFilter(Specification<User> spec);
    List<User> findFirstMemberByFilter(Specification<User> spec);
    User getByIdAndTenantId(UUID memberId, UUID tenantId);
    User save(User user);
    User findUserById(UUID memberId);
    boolean existsByEmailAndTenant(String email, UUID tenantId);
    boolean existsByPhoneAndTenant(String phone, UUID tenantId);
    void delete(User user);
    Long countByGenderForTenant(Gender gender);
    Long countAllUsersByTenant();
    User findByEmail(String email);
    List<User> findUsersWithRoles(Set<String> roleNames);
    boolean existsByEmail(String email);
}
