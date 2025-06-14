package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.superadmin.SuperAdminDto;

import java.util.Optional;


public interface SuperAdminService {
    Optional<SuperAdminDto> findByEmail(String email);
}
