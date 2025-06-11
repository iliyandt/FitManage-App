package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.superadmin.SuperAdminDto;


public interface SuperAdminService {
    SuperAdminDto findByEmail(String email);
}
