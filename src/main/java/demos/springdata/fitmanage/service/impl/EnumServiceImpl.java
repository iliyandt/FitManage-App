package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.common.EnumOption;
import demos.springdata.fitmanage.domain.entity.Gym;
import demos.springdata.fitmanage.domain.entity.PredefinedStaffRole;
import demos.springdata.fitmanage.exception.ApiErrorCode;
import demos.springdata.fitmanage.exception.FitManageAppException;
import demos.springdata.fitmanage.repository.GymRepository;
import demos.springdata.fitmanage.repository.PredefinedStaffRoleRepository;
import demos.springdata.fitmanage.repository.StaffRoleRepository;
import demos.springdata.fitmanage.service.EnumService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EnumServiceImpl implements EnumService {

    private final GymRepository gymRepository;
    private final StaffRoleRepository staffRoleRepository;
    private final PredefinedStaffRoleRepository predefinedStaffRoleRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(EnumServiceImpl.class);
    private static final String ENUM_PACKAGE = "demos.springdata.fitmanage.domain.enums.";

    @Autowired
    public EnumServiceImpl(GymRepository gymRepository, StaffRoleRepository staffRoleRepository, PredefinedStaffRoleRepository predefinedStaffRoleRepository) {
        this.gymRepository = gymRepository;
        this.staffRoleRepository = staffRoleRepository;
        this.predefinedStaffRoleRepository = predefinedStaffRoleRepository;
    }

    @Override
    public List<EnumOption> getEnumOptions(String enumName) {
        LOGGER.info("Enum requested: {}", enumName);
        try {
            Class<?> clazz = Class.forName(ENUM_PACKAGE + enumName);
            LOGGER.info("Resolved enum class: {}", clazz);

            if (!clazz.isEnum()) {
                throw new FitManageAppException("Provided type is not an enum", ApiErrorCode.BAD_REQUEST);
            }

            return Arrays.stream(clazz.getEnumConstants())
                    .map(constant -> {
                        String value = constant.toString();
                        String title = capitalize(value.toLowerCase().replace("_", " "));
                        return new EnumOption(title, value);
                    })
                    .collect(Collectors.toList());

        } catch (ClassNotFoundException e) {
            LOGGER.error("Enum not found: {}", enumName, e);
            throw new FitManageAppException("Enum not found: " + enumName, ApiErrorCode.NOT_FOUND);
        }
    }

    @Override
    public List<EnumOption> getAllStaffRoleOptionsForGym(String gymEmail) {
        Gym gym = gymRepository.findByEmail(gymEmail)
                .orElseThrow(() -> new FitManageAppException("Gym not found", ApiErrorCode.NOT_FOUND));

        List<EnumOption> options = new ArrayList<>();

        staffRoleRepository.findAllByGym(gym).forEach(role -> {
            String label = role.getPredefinedStaffRole() != null
                    ? role.getName() + " (Existing)"
                    : role.getName() + " (Custom)";
            options.add(new EnumOption(label, "existing:" + role.getId()));
        });

        List<PredefinedStaffRole> predefinedRoles = predefinedStaffRoleRepository.findAll();
        predefinedRoles.forEach(predefined -> {
            boolean alreadyExists = staffRoleRepository.findAllByGym(gym).stream()
                    .anyMatch(role -> role.getPredefinedStaffRole() != null &&
                            role.getPredefinedStaffRole().getId().equals(predefined.getId()));

            if (!alreadyExists) {
                options.add(new EnumOption(
                        capitalize(predefined.getName()) + " (System)",
                        "predefined:" + predefined.getId()
                ));
            }
        });

        options.add(new EnumOption("Create Custom Role", "custom:"));

        return options;
    }


    private String capitalize(String input) {
        String[] words = input.split(" ");
        return Arrays.stream(words)
                .map(w -> w.substring(0, 1).toUpperCase() + w.substring(1))
                .collect(Collectors.joining(" "));
    }
}
