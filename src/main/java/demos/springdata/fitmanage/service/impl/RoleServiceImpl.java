package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.entity.Role;
import demos.springdata.fitmanage.domain.enums.RoleType;
import demos.springdata.fitmanage.exception.ApiErrorCode;
import demos.springdata.fitmanage.exception.FitManageAppException;
import demos.springdata.fitmanage.repository.GymRoleRepository;
import demos.springdata.fitmanage.service.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {

    private final GymRoleRepository gymRoleRepository;
    private final static Logger LOGGER = LoggerFactory.getLogger(RoleServiceImpl.class);

    @Autowired
    public RoleServiceImpl(GymRoleRepository gymRoleRepository) {
        this.gymRoleRepository = gymRoleRepository;
    }

    @Override
    public Role findByName(RoleType name) {
        LOGGER.info("Looking for role with name: {}", name);
        return gymRoleRepository.findByName(name)
                .orElseThrow(() -> {
                    LOGGER.warn("Role with name {} not found", name);
                    return new FitManageAppException("Role with name " + name + " not found", ApiErrorCode.NOT_FOUND);
                });
    }

    @Override
    public void createRole(RoleType roleType) {
        LOGGER.info("Creating new role with type: {}", roleType);
        Role role = new Role(roleType);
        gymRoleRepository.save(role);
        LOGGER.info("Role {} successfully created", roleType);
    }

    @Override
    public void initRoles() {
        LOGGER.info("Initializing default roles...");
        for (RoleType roleType : RoleType.values()) {
            gymRoleRepository.findByName(roleType)
                    .orElseGet(() -> gymRoleRepository.save(new Role(roleType)));
        }
        LOGGER.info("Default role initialization complete");
    }

    @Override
    public Role save(Role role) {
        LOGGER.info("Saving role: {}", role);
        return gymRoleRepository.save(role);
    }
}