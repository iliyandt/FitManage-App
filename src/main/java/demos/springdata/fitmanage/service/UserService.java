package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.users.UserResponseDto;
import demos.springdata.fitmanage.domain.dto.users.UserUpdateDto;
import demos.springdata.fitmanage.domain.entity.User;
import demos.springdata.fitmanage.domain.enums.Gender;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

public interface UserService {
    UserResponseDto getUserProfileByEmail(String email);
    UserResponseDto updateProfile(UserUpdateDto dto);
    List<User> findMembersByFilter(Specification<User> spec);
    List<User> findFirstMemberByFilter(Specification<User> spec);
    Optional<User> findByQrToken(String qrToken);
    User getByIdAndTenantId(Long memberId, Long tenantId);
    User save(User user);
    User findUserById(Long memberId);
    boolean existsByEmailAndTenant(String email, Long tenantId);
    boolean existsByPhoneAndTenant(String phone, Long tenantId);
    void delete(User user);
    Double countByGenderForTenant(Gender gender);

    Double countAllUsersByTenant();


}
