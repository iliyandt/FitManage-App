package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.entity.User;
import demos.springdata.fitmanage.exception.DamilSoftException;
import demos.springdata.fitmanage.repository.UserRepository;
import demos.springdata.fitmanage.security.UserData;
import demos.springdata.fitmanage.service.CustomUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
        User user = userRepository.findByEmail(email).orElseThrow(() -> new DamilSoftException("User not found", HttpStatus.NOT_FOUND));
        return new UserData(user.getId(), user.getEmail(), user.getPassword(), user.getRoles(), user.isEnabled());
    }
}
