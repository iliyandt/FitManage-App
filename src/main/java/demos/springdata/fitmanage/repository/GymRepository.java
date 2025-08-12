package demos.springdata.fitmanage.repository;

import demos.springdata.fitmanage.domain.entity.Gym;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GymRepository extends JpaRepository<Gym, Long> {

    Optional<Gym> findByUsername(String username);
    Optional<Gym> findByEmail(String email);

    @Query("SELECT g FROM Gym g LEFT JOIN FETCH g.gymMembers WHERE g.email = :email")
    Optional<Gym> findByEmailWithMembers(@Param("email") String email);

}
