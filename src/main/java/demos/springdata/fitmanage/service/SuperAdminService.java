package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.SuperAdminDto;


public interface SuperAdminService {
    SuperAdminDto findByEmail(String email);
}
