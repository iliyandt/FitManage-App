package demos.springdata.fitmanage.config;

import demos.springdata.fitmanage.domain.dto.gym.GymMemberResponseDto;
import demos.springdata.fitmanage.domain.dto.gym.GymSummaryDto;
import demos.springdata.fitmanage.domain.entity.Gym;
import demos.springdata.fitmanage.domain.entity.GymMember;
import demos.springdata.fitmanage.repository.GymRepository;
import demos.springdata.fitmanage.service.CustomUserDetailsService;
import demos.springdata.fitmanage.util.ValidationUtil;
import demos.springdata.fitmanage.util.ValidationUtilImpl;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class ApplicationBeanConfiguration {
    private final GymRepository gymRepository;


    public ApplicationBeanConfiguration(GymRepository gymRepository) {
        this.gymRepository = gymRepository;
    }

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        configureGymMapper(modelMapper);
        return modelMapper;
    }

    private void configureGymMapper(ModelMapper modelMapper) {
        modelMapper.typeMap(Gym.class, GymSummaryDto.class)
                .addMappings(mapper -> mapper.map(Gym::getActualUsername, GymSummaryDto::setUsername));
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ValidationUtil validationUtil() {
        return new ValidationUtilImpl();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{
        return config.getAuthenticationManager();
    }

    @Bean
    AuthenticationProvider authenticationProvider(CustomUserDetailsService customUserDetailsService) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(bCryptPasswordEncoder());

        return authProvider;
    }
}
