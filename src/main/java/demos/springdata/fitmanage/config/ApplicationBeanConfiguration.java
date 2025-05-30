package demos.springdata.fitmanage.config;

import demos.springdata.fitmanage.domain.dto.GymAdminResponseDto;
import demos.springdata.fitmanage.domain.entity.Gym;
import demos.springdata.fitmanage.util.ValidationUtil;
import demos.springdata.fitmanage.util.ValidationUtilImpl;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.Collections;
import java.util.stream.Collectors;

@Configuration
public class ApplicationBeanConfiguration {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        configureGymMapper(modelMapper);
        return modelMapper;
    }

    private void configureGymMapper(ModelMapper modelMapper) {
        modelMapper.typeMap(Gym.class, GymAdminResponseDto.class)
                .addMappings(mapping -> {
                    mapping.map(src -> src.getRoles() != null ? src.getRoles().stream()
                            .map(role -> role.getName().name())
                            .collect(Collectors.toSet()) : Collections.emptySet(),
                            GymAdminResponseDto::setRoles);

                    mapping.map(src -> src.getSubscriptionValidUntil() != null &&
                                    src.getSubscriptionValidUntil().isAfter(LocalDate.now()),
                            GymAdminResponseDto::setSubscriptionActive);
                });
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ValidationUtil validationUtil() {
        return new ValidationUtilImpl();
    }
}
