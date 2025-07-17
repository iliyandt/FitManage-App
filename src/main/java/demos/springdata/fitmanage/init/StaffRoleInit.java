package demos.springdata.fitmanage.init;

import demos.springdata.fitmanage.domain.entity.Gym;
import demos.springdata.fitmanage.domain.entity.PredefinedStaffRole;
import demos.springdata.fitmanage.domain.entity.StaffRole;
import demos.springdata.fitmanage.domain.enums.StaffPosition;
import demos.springdata.fitmanage.repository.GymRepository;
import demos.springdata.fitmanage.repository.PredefinedStaffRoleRepository;
import demos.springdata.fitmanage.repository.StaffRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(2)
public class StaffRoleInit implements ApplicationRunner {
    private final PredefinedStaffRoleRepository predefinedStaffRoleRepository;

    @Autowired
    public StaffRoleInit(PredefinedStaffRoleRepository predefinedStaffRoleRepository) {
        this.predefinedStaffRoleRepository = predefinedStaffRoleRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        for (StaffPosition position : StaffPosition.values()) {
            boolean exists = predefinedStaffRoleRepository.existsByPosition(position);
            if (!exists) {
                PredefinedStaffRole role = new PredefinedStaffRole();
                role.setName(position.name());
                role.setPosition(position);
                predefinedStaffRoleRepository.save(role);
            }
        }
    }
}
