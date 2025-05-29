package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.entity.Role;
import demos.springdata.fitmanage.domain.enums.RoleType;
import demos.springdata.fitmanage.exception.ApiErrorCode;
import demos.springdata.fitmanage.exception.FitManageAppException;
import demos.springdata.fitmanage.repository.RoleRepository;
import demos.springdata.fitmanage.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Role findByName(RoleType name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new FitManageAppException("Role with name " + name + " not found", ApiErrorCode.NOT_FOUND));
    }

    @Override
    public Role createRole(RoleType roleType) {
        Role role = new Role(roleType);
        return roleRepository.save(role);
    }

    @Override
    public void initRoles() {
        if (roleRepository.count() == 0) {
            for (RoleType roleType : RoleType.values()) {
                createRole(roleType);
            }
        }
    }
}