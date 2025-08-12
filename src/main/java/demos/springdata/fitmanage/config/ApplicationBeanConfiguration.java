package demos.springdata.fitmanage.config;

import com.google.gson.Gson;
import demos.springdata.fitmanage.domain.dto.gym.GymSummaryDto;
import demos.springdata.fitmanage.domain.dto.team.response.StaffMemberResponseDto;
import demos.springdata.fitmanage.domain.dto.visit.VisitDto;
import demos.springdata.fitmanage.domain.entity.Gym;
import demos.springdata.fitmanage.domain.entity.StaffMember;
import demos.springdata.fitmanage.domain.entity.Visit;
import demos.springdata.fitmanage.repository.GymRepository;
import demos.springdata.fitmanage.service.CustomUserDetailsService;
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
        configureStaffMemberMapper(modelMapper);
        return modelMapper;
    }

    private void configureGymMapper(ModelMapper modelMapper) {
        modelMapper.typeMap(Gym.class, GymSummaryDto.class)
                .addMappings(mapper -> mapper.map(Gym::getActualUsername, GymSummaryDto::setUsername));
    }

    private void configureStaffMemberMapper(ModelMapper modelMapper) {
        modelMapper.typeMap(StaffMember.class, StaffMemberResponseDto.class)
                .addMappings(mapper -> {
                    mapper.map(src -> src.getStaffRole().getName(), StaffMemberResponseDto::setRoleName);
                    mapper.map(src -> src.getGym().getActualUsername(), StaffMemberResponseDto::setGymName);
                });
    }


    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
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

    @Bean
    public Gson gson() {
        return new Gson();
    }

}
