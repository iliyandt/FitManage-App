package demos.springdata.fitmanage.repository;

import demos.springdata.fitmanage.domain.entity.GymAccountSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GymAccountSettingsRepository extends JpaRepository<GymAccountSettings, Long> {
    Optional<GymAccountSettings> findByUserId(Long id);
}
