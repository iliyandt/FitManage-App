package demos.springdata.fitmanage.config;

import demos.springdata.fitmanage.domain.dto.GymAdminResponseDto;
import demos.springdata.fitmanage.domain.entity.Gym;
import demos.springdata.fitmanage.repository.GymRepository;
import demos.springdata.fitmanage.util.ValidationUtil;
import demos.springdata.fitmanage.util.ValidationUtilImpl;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
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

    @Bean
    UserDetailsService userDetailsService() {
        return username -> gymRepository.findByEmail(username)
                .orElseThrow(() -> new EntityNotFoundException("Gym not found."));
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{
        return config.getAuthenticationManager();
    }

    @Bean
    AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(bCryptPasswordEncoder());

        return authProvider;
    }
}
