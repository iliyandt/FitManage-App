package demos.springdata.fitmanage.repository;

import demos.springdata.fitmanage.domain.entity.PredefinedStaffRole;
import demos.springdata.fitmanage.domain.enums.StaffPosition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PredefinedStaffRoleRepository extends JpaRepository<PredefinedStaffRole, Long> {
    boolean existsByPosition(StaffPosition staffPosition);
}
