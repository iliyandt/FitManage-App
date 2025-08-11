package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.gym.GymBasicInfoDto;
import demos.springdata.fitmanage.domain.dto.gym.GymSummaryDto;
import demos.springdata.fitmanage.domain.entity.Gym;

import java.util.List;
import java.util.Optional;

public interface GymService {
    Optional<Gym> findGymEntityByEmail(String email);
    List<GymSummaryDto> getAllGyms();
    Optional<GymSummaryDto> getGymByEmail(String email);
    void updateGymBasicInfo(String email, GymBasicInfoDto dto);
}
