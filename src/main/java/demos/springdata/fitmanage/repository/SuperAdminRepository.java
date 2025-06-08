package demos.springdata.fitmanage.repository;

import demos.springdata.fitmanage.domain.entity.SuperAdminUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SuperAdminRepository extends JpaRepository<SuperAdminUser, Long> {
    Optional<SuperAdminUser> findByEmail(String email);
}
