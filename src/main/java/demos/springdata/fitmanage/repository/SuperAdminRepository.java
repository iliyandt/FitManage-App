package demos.springdata.fitmanage.repository;
import demos.springdata.fitmanage.domain.entity.SuperAdminUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SuperAdminRepository extends JpaRepository<SuperAdminUser, Long> {
    Optional<SuperAdminUser> findByEmail(String email);
}
