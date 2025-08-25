package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.tenant.users.UserBaseResponseDto;
import demos.springdata.fitmanage.domain.dto.tenant.users.UserProfileDto;
import demos.springdata.fitmanage.domain.dto.tenant.users.UserUpdateDto;
import demos.springdata.fitmanage.domain.entity.User;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserService {
    UserProfileDto getUserProfileByEmail(String email);
    UserProfileDto updateProfile(Long id, UserUpdateDto dto);
    boolean existsByEmailAndTenant(String email, Long tenantId);
    boolean existsByPhoneAndTenant(String phone, Long tenantId);
    User save(User user);
    void delete(User user);
    User getByIdAndTenantId(Long memberId, Long tenantId);
    List<User> findMembersByFilter(Specification<User> spec);
    Optional<User> findFirstMemberByFilter(Specification<User> spec);
    User findUserById(Long memberId);
}
