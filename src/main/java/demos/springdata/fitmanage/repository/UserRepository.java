package demos.springdata.fitmanage.repository;
import demos.springdata.fitmanage.domain.entity.Role;
import demos.springdata.fitmanage.domain.entity.Tenant;
import demos.springdata.fitmanage.domain.entity.User;
import demos.springdata.fitmanage.domain.enums.Gender;
import demos.springdata.fitmanage.domain.enums.RoleType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    Optional<User> findByIdAndTenantId(UUID id, UUID tenantId);
    boolean existsByPhoneAndTenant_Id(String phone, UUID tenantId);
    boolean existsByEmailAndTenant_Id(String email, UUID id);
    boolean existsByEmail(String email);
    boolean existsByRoles_Name(RoleType roleType);
    List<User> findAll(Specification<User> spec);
    Optional<User> findByQrToken(String qrToken);
    List<User> findByGender_AndTenant(Gender genderAfter, Tenant tenant);

    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE u.tenant = :tenant AND r.name = :roleType")
    Long countByTenantAndRoleType(@Param("tenant") Tenant tenant, @Param("roleType") RoleType roleType);

    @Query("SELECT DISTINCT u FROM User u " +
            "LEFT JOIN u.roles r " +
            "WHERE u.tenant.id = :tenantId " +
            "AND (" +
            "    u.id IN :ids OR r.name IN :roleTypes" +
            ")")
    Set<User> findAllByIdsOrRoleTypesAndTenant
            (@Param("ids") Set<UUID> ids,
             @Param("roleTypes") Set<RoleType> roles,
             @Param("tenantId") UUID tenantId);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r IN :roles AND u.tenant.id = :tenantId")
    List<User> findUsersByRolesAndTenant(@Param("roles") Set<Role> roles, @Param("tenantId") UUID tenantId);




}
