package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.entity.Role;
import demos.springdata.fitmanage.domain.enums.RoleType;

public interface RoleService {
    Role findByName(RoleType name);
    void createRole(RoleType roleType);
    void initRoles();
    Role save(Role superAdminRole);
}