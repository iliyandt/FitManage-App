package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.SuperAdminDto;
import demos.springdata.fitmanage.domain.entity.SuperAdminUser;

import java.util.Optional;

public interface SuperAdminService {
    SuperAdminDto findByEmail(String email);
}
