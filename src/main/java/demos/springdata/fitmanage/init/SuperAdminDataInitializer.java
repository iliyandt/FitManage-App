package demos.springdata.fitmanage.init;
import demos.springdata.fitmanage.domain.entity.Role;
import demos.springdata.fitmanage.domain.entity.User;
import demos.springdata.fitmanage.domain.enums.RoleType;
import demos.springdata.fitmanage.repository.UserRepository;
import demos.springdata.fitmanage.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Component
@Order(2)
public class SuperAdminDataInitializer implements ApplicationRunner {
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Value("${SUPERADMIN_EMAIL}")
    private String email;

    @Value("${SUPERADMIN_USERNAME}")
    private String username;

    @Value("${SUPERADMIN_PASSWORD}")
    private String password;

    @Autowired
    public SuperAdminDataInitializer(UserRepository userRepository, RoleService roleService, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    @Override
    public void run(ApplicationArguments args) {
        if (!userRepository.existsByRoles_Name(RoleType.ADMINISTRATOR)) {

            User administratorUser = User.builder()
                    .firstName("Iliyan")
                    .lastName("Todorov")
                    .email(email)
                    .username(username)
                    .password(passwordEncoder.encode(password))
                    .verificationCode(null)
                    .verificationCodeExpiresAt(null)
                    .enabled(true)
                    .roles(Set.of(roleService.findByName(RoleType.ADMINISTRATOR)))
                    .build();

            userRepository.save(administratorUser);
        }
    }
}
