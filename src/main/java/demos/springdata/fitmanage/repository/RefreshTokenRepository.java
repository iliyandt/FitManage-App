package demos.springdata.fitmanage.repository;
import demos.springdata.fitmanage.domain.entity.RefreshToken;
import demos.springdata.fitmanage.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM refresh_token WHERE user_id = :userId", nativeQuery = true)
    int nativeDeleteByUserId(@Param("userId") Long userId);
}
