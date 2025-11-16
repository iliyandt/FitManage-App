package demos.springdata.fitmanage.repository;

import demos.springdata.fitmanage.domain.entity.Tenant;
import demos.springdata.fitmanage.domain.entity.Training;
import demos.springdata.fitmanage.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface TrainingRepository extends JpaRepository<Training, Long> {
    List<Training> findAllByTenant(Tenant tenant);
    Optional<Training> findByIdAndTenant(Long id, Tenant tenant);

    Set<Training> findAllByTrainer(User trainer);
}
