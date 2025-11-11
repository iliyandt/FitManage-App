package demos.springdata.fitmanage.repository;

import demos.springdata.fitmanage.domain.entity.Tenant;
import demos.springdata.fitmanage.domain.entity.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrainingRepository extends JpaRepository<Training, Long> {
    List<Training> findAllByTenant(Tenant tenant);
}
