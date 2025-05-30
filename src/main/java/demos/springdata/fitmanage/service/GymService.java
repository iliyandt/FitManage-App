package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.GymAdminResponseDto;

import java.util.List;

public interface GymService {
    List<GymAdminResponseDto> getAllGyms();
}
