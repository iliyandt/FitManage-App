package demos.springdata.fitmanage.init;

import demos.springdata.fitmanage.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class RoleInit implements ApplicationRunner {

    private final RoleService roleService;

    @Autowired
    public RoleInit(RoleService roleService) {
        this.roleService = roleService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        roleService.initRoles();
    }
}