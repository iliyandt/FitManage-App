package demos.springdata.fitmanage.init;

import demos.springdata.fitmanage.domain.entity.Role;
import demos.springdata.fitmanage.domain.entity.SuperAdminUser;
import demos.springdata.fitmanage.domain.enums.RoleType;
import demos.springdata.fitmanage.exception.FitManageAppException;
import demos.springdata.fitmanage.repository.SuperAdminRepository;
import demos.springdata.fitmanage.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Order(3)
public class SuperAdminDataInitializer implements ApplicationRunner {
    private final SuperAdminRepository superAdminRepository;
    private final RoleService roleService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Value("${SUPERADMIN_EMAIL}")
    private String superAdminEmail;

    @Value("${SUPERADMIN_USERNAME}")
    private String superAdminUsername;

    @Value("${SUPERADMIN_PASSWORD}")
    private String superAdminPassword;

    @Autowired
    public SuperAdminDataInitializer(SuperAdminRepository superAdminRepository, RoleService roleService, BCryptPasswordEncoder passwordEncoder) {
        this.superAdminRepository = superAdminRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    //todo: should it be transactional, add logger
    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (superAdminRepository.count() == 0) {
            Role superAdminRole;
            try {
                superAdminRole = roleService.findByName(RoleType.SUPER_ADMIN);
            } catch (FitManageAppException e) {
                superAdminRole = new Role();
                superAdminRole.setName(RoleType.SUPER_ADMIN);
                superAdminRole = roleService.save(superAdminRole);
            }

            SuperAdminUser superAdmin = new SuperAdminUser();
            superAdmin.setEmail(superAdminEmail);
            superAdmin.setUsername(superAdminUsername);
            superAdmin.setPassword(passwordEncoder.encode(superAdminPassword));
            superAdmin.setEnabled(true);
            superAdmin.getRoles().add(superAdminRole);

            superAdminRepository.save(superAdmin);
        }
    }
}
