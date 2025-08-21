package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.tenant.TenantResponseDto;
import demos.springdata.fitmanage.domain.dto.tenant.users.UserUpdateDto;
import demos.springdata.fitmanage.domain.entity.User;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User getUserOrElseThrow(String email);
    TenantResponseDto getUserSummaryByEmail(String email);
    void updateUserProfile(String email, UserUpdateDto dto);
    boolean existsByEmailAndTenant(String email, Long tenantId);
    boolean existsByPhoneAndTenant(String phone, Long tenantId);
    User save(User user);
    void delete(User user);
    User getByIdAndTenantId(Long memberId, Long tenantId);
    List<User> findMembersByFilter(Specification<User> spec);
    Optional<User> findFirstMemberByFilter(Specification<User> spec);
    User findMemberById(Long memberId);
}
