package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.entity.Role;
import demos.springdata.fitmanage.domain.enums.RoleType;
import org.springframework.stereotype.Service;

import java.util.Set;

public interface RoleService {
    Role findByName(RoleType name);
    Set<Role> findByNameIn(Set<RoleType> roleTypes);
    void createRole(RoleType roleType);
    void initRoles();
    Role save(Role superAdminRole);
}