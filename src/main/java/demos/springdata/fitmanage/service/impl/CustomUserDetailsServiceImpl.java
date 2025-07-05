package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.exception.ApiErrorCode;
import demos.springdata.fitmanage.exception.FitManageAppException;
import demos.springdata.fitmanage.repository.GymMemberRepository;
import demos.springdata.fitmanage.repository.GymRepository;
import demos.springdata.fitmanage.repository.SuperAdminRepository;
import demos.springdata.fitmanage.service.CustomUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsServiceImpl implements CustomUserDetailsService {
    private final SuperAdminRepository superAdminRepository;
    private final GymRepository gymRepository;
    private final GymMemberRepository gymMemberRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomUserDetailsServiceImpl.class);

    @Autowired
    public CustomUserDetailsServiceImpl(SuperAdminRepository superAdminRepository, GymRepository gymRepository, GymMemberRepository gymMemberRepository) {
        this.superAdminRepository = superAdminRepository;
        this.gymRepository = gymRepository;
        this.gymMemberRepository = gymMemberRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        LOGGER.info("Attempting to load user details for email: {}", email);

        return superAdminRepository.findByEmail(email).<UserDetails>map(user -> {
                    LOGGER.info("SuperAdmin found with email: {}", email);
                    return user;
                })
                .or(() -> gymRepository.findByEmail(email).map(gym -> (UserDetails) gym))
                .or(() -> gymMemberRepository.findByEmail(email).map(member -> {
                    LOGGER.info("Gym account found with email: {}", email);
                    return (UserDetails) member;
                }))
                .orElseThrow(() -> {
                    LOGGER.warn("No user found with email: {}", email);
                    return new FitManageAppException("User not found", ApiErrorCode.NOT_FOUND);
                });
    }


}
