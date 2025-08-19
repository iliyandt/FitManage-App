package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.tenant.TenantResponseDto;
import demos.springdata.fitmanage.domain.dto.tenant.users.UserUpdateDto;
import demos.springdata.fitmanage.domain.entity.User;

public interface UserService {
    TenantResponseDto getUserSummaryByEmail(String email);
    void updateUserProfile(String email, UserUpdateDto dto);
    boolean existsByEmailAndTenant(String email, Long tenantId);
    boolean existsByPhoneAndTenant(String phone, Long tenantId);
    User save(User user);
    void delete(User user);
    User getByIdAndTenantId(Long memberId, Long tenantId);
}
