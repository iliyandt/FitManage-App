package demos.springdata.fitmanage.init;

import demos.springdata.fitmanage.domain.entity.Role;
import demos.springdata.fitmanage.domain.entity.SuperAdminUser;
import demos.springdata.fitmanage.domain.enums.RoleType;
import demos.springdata.fitmanage.repository.SuperAdminRepository;
import demos.springdata.fitmanage.service.RoleService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class SuperAdminDataInitializer {
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

    @PostConstruct
    public void init() {
        if (superAdminRepository.count() == 0) {
            SuperAdminUser superAdmin = new SuperAdminUser();
            superAdmin.setEmail(superAdminEmail);
            superAdmin.setUsername(superAdminUsername);
            superAdmin.setPassword(passwordEncoder.encode(superAdminPassword));
            superAdmin.setEnabled(true);

            Role superAdminRole = roleService.findByName(RoleType.SUPER_ADMIN);
            superAdmin.getRoles().add(superAdminRole);

            superAdminRepository.save(superAdmin);
        }
    }
}
