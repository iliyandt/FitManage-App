package demos.springdata.fitmanage.repository;
import demos.springdata.fitmanage.domain.entity.Gym;
import demos.springdata.fitmanage.domain.entity.PredefinedStaffRole;
import demos.springdata.fitmanage.domain.entity.StaffRole;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;


public interface StaffRoleRepository extends JpaRepository<StaffRole, Long> {
    List<StaffRole> findAllByGym(Gym gym);
    Optional<StaffRole> findByPredefinedStaffRoleAndGym(PredefinedStaffRole template, Gym gym);
    Optional<StaffRole> findByNameAndGym(String name, Gym gym);

}
