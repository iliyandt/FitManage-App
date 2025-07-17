package demos.springdata.fitmanage.repository;

import demos.springdata.fitmanage.domain.entity.Gym;
import demos.springdata.fitmanage.domain.entity.PredefinedStaffRole;
import demos.springdata.fitmanage.domain.entity.StaffRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

public interface StaffRoleRepository extends JpaRepository<StaffRole, Long> {
    boolean existsByNameAndGym(String name, Gym gym);
    Optional<StaffRole> findByGymAndPredefinedStaffRole(Gym gym, PredefinedStaffRole role);
    Optional<StaffRole> findByNameAndGym(String customRoleName, Gym gym);
    Collection<StaffRole> findAllByGym(Gym gym);
}
