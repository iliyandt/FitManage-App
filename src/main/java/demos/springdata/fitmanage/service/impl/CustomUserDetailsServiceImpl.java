package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.exception.ApiErrorCode;
import demos.springdata.fitmanage.exception.FitManageAppException;
import demos.springdata.fitmanage.repository.GymRepository;
import demos.springdata.fitmanage.repository.SuperAdminRepository;
import demos.springdata.fitmanage.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsServiceImpl implements CustomUserDetailsService {
    private final SuperAdminRepository superAdminRepository;
    private final GymRepository gymRepository;

    @Autowired
    public CustomUserDetailsServiceImpl(SuperAdminRepository superAdminRepository, GymRepository gymRepository) {
        this.superAdminRepository = superAdminRepository;
        this.gymRepository = gymRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return superAdminRepository.findByEmail(email)
                .map(user -> (UserDetails) user)
                .orElseGet(() ->
                        gymRepository.findByEmail(email)
                                .map(gym -> (UserDetails) gym)
                                .orElseThrow(() -> new FitManageAppException("User/Gym not found", ApiErrorCode.NOT_FOUND)));
    }
}
