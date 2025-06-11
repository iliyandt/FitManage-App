package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.gym.GymBasicInfoDto;
import demos.springdata.fitmanage.domain.dto.gym.GymSummaryDto;

import java.util.List;

public interface GymService {
    List<GymSummaryDto> getAllGyms();
    GymSummaryDto getGymByEmail(String email);

}
