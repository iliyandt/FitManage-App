package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.entity.Role;
import demos.springdata.fitmanage.domain.enums.RoleType;
import demos.springdata.fitmanage.exception.ApiErrorCode;
import demos.springdata.fitmanage.exception.FitManageAppException;
import demos.springdata.fitmanage.repository.GymRoleRepository;
import demos.springdata.fitmanage.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {

    private final GymRoleRepository gymRoleRepository;

    @Autowired
    public RoleServiceImpl(GymRoleRepository gymRoleRepository) {
        this.gymRoleRepository = gymRoleRepository;
    }

    @Override
    public Role findByName(RoleType name) {
        return gymRoleRepository.findByName(name)
                .orElseThrow(() -> new FitManageAppException("Role with name " + name + " not found", ApiErrorCode.NOT_FOUND));
    }

    @Override
    public void createRole(RoleType roleType) {
        Role role = new Role(roleType);
        gymRoleRepository.save(role);
    }

    @Override
    public void initRoles() {
        if (gymRoleRepository.count() == 0) {
            for (RoleType roleType : RoleType.values()) {
                createRole(roleType);
            }
        }
    }
}