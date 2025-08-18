package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.exception.ApiErrorCode;
import demos.springdata.fitmanage.exception.FitManageAppException;

import demos.springdata.fitmanage.repository.UserRepository;
import demos.springdata.fitmanage.service.CustomUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsServiceImpl implements CustomUserDetailsService {
    private final UserRepository userRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomUserDetailsServiceImpl.class);

    @Autowired
    public CustomUserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        LOGGER.info("Attempting to load user details for email: {}", email);

        return userRepository.findByEmail(email).<UserDetails>map(user -> {
                    LOGGER.info("SuperAdmin found with email: {}", email);
                    return user;
                })
                .orElseThrow(() -> {
                    LOGGER.warn("No user found with email: {}", email);
                    return new FitManageAppException("User not found", ApiErrorCode.NOT_FOUND);
                });
    }


}
