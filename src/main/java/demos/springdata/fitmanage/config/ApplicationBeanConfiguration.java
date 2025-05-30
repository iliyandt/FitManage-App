package demos.springdata.fitmanage.config;

import demos.springdata.fitmanage.domain.dto.GymAdminResponseDto;
import demos.springdata.fitmanage.domain.entity.Gym;
import demos.springdata.fitmanage.util.ValidationUtil;
import demos.springdata.fitmanage.util.ValidationUtilImpl;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
                .addMapping(Gym::getRoles, GymAdminResponseDto::setRoles);
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
