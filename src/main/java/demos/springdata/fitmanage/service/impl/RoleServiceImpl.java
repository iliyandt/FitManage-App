package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.entity.Role;
import demos.springdata.fitmanage.domain.enums.RoleType;
import demos.springdata.fitmanage.exception.DamilSoftException;
import demos.springdata.fitmanage.repository.RoleRepository;
import demos.springdata.fitmanage.service.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final static Logger LOGGER = LoggerFactory.getLogger(RoleServiceImpl.class);

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Role findByName(RoleType name) {
        LOGGER.info("Looking for role with name: {}", name);
        return roleRepository.findByName(name)
                .orElseThrow(() -> {
                    LOGGER.warn("Role with name {} not found", name);
                    return new DamilSoftException("Role with name " + name + " not found", HttpStatus.NOT_FOUND);
                });
    }

    @Override
    public Set<Role> findByNameIn(Set<RoleType> roleTypes) {
        return roleRepository.findByNameIn(roleTypes);
    }

    @Override
    public void createRole(RoleType roleType) {
        LOGGER.info("Creating new role with type: {}", roleType);
        Role role = new Role(roleType);
        roleRepository.save(role);
        LOGGER.info("Role {} successfully created", roleType);
    }

    @Override
    public void initRoles() {
        LOGGER.info("Initializing default roles...");
        for (RoleType roleType : RoleType.values()) {
            roleRepository.findByName(roleType)
                    .orElseGet(() -> {
                        createRole(roleType);
                        return roleRepository.findByName(roleType).orElse(null);
                    });
        }
        LOGGER.info("Default role initialization complete");
    }

    @Override
    public Role save(Role role) {
        LOGGER.info("Saving role: {}", role);
        return roleRepository.save(role);
    }
}