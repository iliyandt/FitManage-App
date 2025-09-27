package demos.springdata.fitmanage.repository;
import demos.springdata.fitmanage.domain.entity.Tenant;
import demos.springdata.fitmanage.domain.entity.User;
import demos.springdata.fitmanage.domain.enums.Gender;
import demos.springdata.fitmanage.domain.enums.RoleType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByIdAndTenantId(Long id, Long tenantId);
    boolean existsByPhoneAndTenant_Id(String phone, Long tenantId);
    boolean existsByEmailAndTenant_Id(String email, Long id);
    boolean existsByRoles_Name(RoleType roleType);
    List<User> findAll(Specification<User> spec);
    Optional<User> findByQrToken(String qrToken);

    Double countByGender_AndTenant(Gender genderAfter, Tenant tenant);
    Double countAllByTenant(Tenant tenant);
}
