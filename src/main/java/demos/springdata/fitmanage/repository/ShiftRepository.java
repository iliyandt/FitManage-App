package demos.springdata.fitmanage.repository;

import demos.springdata.fitmanage.domain.dto.shift.ShiftResponseDto;
import demos.springdata.fitmanage.domain.entity.Shift;
import demos.springdata.fitmanage.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, Long> {
    List<Shift> findByEmployee_User(User user);
}
