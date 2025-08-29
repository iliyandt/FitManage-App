package demos.springdata.fitmanage.repository;

import demos.springdata.fitmanage.domain.entity.StaffProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StaffRepository extends JpaRepository<StaffProfile, Long> {
}
