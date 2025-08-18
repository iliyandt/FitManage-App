package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.gym.GymBasicInfoDto;
import demos.springdata.fitmanage.domain.dto.gym.GymSummaryDto;
import demos.springdata.fitmanage.domain.entity.Tenant;

import java.util.List;
import java.util.Optional;

public interface TenantService {
    Optional<Tenant> findGymEntityByEmail(String email);
    List<GymSummaryDto> getAllGyms();
    Optional<GymSummaryDto> getGymByEmail(String email);
    void updateTenantBasicInfo(String email, GymBasicInfoDto dto);
}
