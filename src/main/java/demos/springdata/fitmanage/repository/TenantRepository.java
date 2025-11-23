package demos.springdata.fitmanage.repository;

import demos.springdata.fitmanage.domain.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, UUID> {
    @Query("SELECT t FROM Tenant t JOIN t.users u WHERE u.email = :email")
    Tenant findTenantByUserEmail(@Param("email") String email);

}
