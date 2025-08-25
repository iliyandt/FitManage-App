package demos.springdata.fitmanage.config;

import com.google.gson.Gson;
import demos.springdata.fitmanage.domain.dto.tenant.users.UserBaseResponseDto;
import demos.springdata.fitmanage.domain.entity.User;
import demos.springdata.fitmanage.service.impl.CustomUserDetailsServiceImpl;
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


    public ApplicationBeanConfiguration() {
    }

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        configureUserMapper(modelMapper);
//        configureStaffMemberMapper(modelMapper);
        return modelMapper;
    }

    private void configureUserMapper(ModelMapper modelMapper) {
        modelMapper.typeMap(User.class, UserBaseResponseDto.class)
                .addMappings(mapper -> mapper.map(User::getUsername, UserBaseResponseDto::setUsername));
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
    AuthenticationProvider authenticationProvider(CustomUserDetailsServiceImpl customUserDetailsService) {
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
