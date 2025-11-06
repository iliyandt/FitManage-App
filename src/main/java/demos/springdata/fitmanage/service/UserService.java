package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.users.UserLookupDto;
import demos.springdata.fitmanage.domain.dto.users.UserResponseDto;
import demos.springdata.fitmanage.domain.dto.users.UserUpdateDto;
import demos.springdata.fitmanage.domain.entity.Role;
import demos.springdata.fitmanage.domain.entity.User;
import demos.springdata.fitmanage.domain.enums.Gender;
import demos.springdata.fitmanage.domain.enums.RoleType;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserService {
    User getCurrentUser();
    UserResponseDto getUserProfileByEmail(String email);
    UserResponseDto updateProfile(UserUpdateDto dto);
    List<User> findMembersByFilter(Specification<User> spec);
    List<User> findFirstMemberByFilter(Specification<User> spec);
    User getByIdAndTenantId(Long memberId, Long tenantId);
    User save(User user);
    User findUserById(Long memberId);
    boolean existsByEmailAndTenant(String email, Long tenantId);
    boolean existsByPhoneAndTenant(String phone, Long tenantId);
    void delete(User user);
    Long countByGenderForTenant(Gender gender);
    Long countAllUsersByTenant();
    User findByEmail(String email);
    List<UserLookupDto> findUsersWithRoles(Set<String> roleNames);
    boolean existsByEmail(String email);
}
