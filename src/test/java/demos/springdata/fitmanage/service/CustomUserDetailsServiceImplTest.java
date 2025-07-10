package demos.springdata.fitmanage.service;


import demos.springdata.fitmanage.domain.entity.Gym;
import demos.springdata.fitmanage.domain.entity.GymMember;
import demos.springdata.fitmanage.domain.entity.SuperAdminUser;
import demos.springdata.fitmanage.exception.ApiErrorCode;
import demos.springdata.fitmanage.exception.FitManageAppException;
import demos.springdata.fitmanage.repository.GymMemberRepository;
import demos.springdata.fitmanage.repository.GymRepository;
import demos.springdata.fitmanage.repository.SuperAdminRepository;
import demos.springdata.fitmanage.service.impl.CustomUserDetailsServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceImplTest {
    @Mock
    private SuperAdminRepository superAdminRepository;
    @Mock
    private GymRepository gymRepository;
    @Mock
    private GymMemberRepository gymMemberRepository;
    @InjectMocks
    private CustomUserDetailsServiceImpl customUserDetailsService;

    private final String email = "user@example.com";


    @Test
    void shouldReturnSuperAdmin_WhenEmailExistsInSuperAdminRepo() {
        SuperAdminUser mockedSuperAdminUser = Mockito.mock(SuperAdminUser.class);
        Mockito.when(superAdminRepository.findByEmail(email)).thenReturn(Optional.of(mockedSuperAdminUser));

        UserDetails result = customUserDetailsService.loadUserByUsername(email);

        Assertions.assertEquals(mockedSuperAdminUser, result);
        Mockito.verify(superAdminRepository).findByEmail(email);
        Mockito.verifyNoInteractions(gymRepository, gymMemberRepository);
    }

    @Test
    void shouldReturnGymMember_WhenEmailExistsInGymMemberRepo() {
        Mockito.when(superAdminRepository.findByEmail(email)).thenReturn(Optional.empty());
        Mockito.when(gymRepository.findByEmail(email)).thenReturn(Optional.empty());
        GymMember mockedGymMember = Mockito.mock(GymMember.class);
        Mockito.when(gymMemberRepository.findByEmail(email)).thenReturn(Optional.of(mockedGymMember));

        UserDetails result = customUserDetailsService.loadUserByUsername(email);

        Assertions.assertEquals(mockedGymMember, result);
    }

    @Test
    void shouldReturnGym_WhenEmailExistsInGymRepo() {
        Mockito.when(superAdminRepository.findByEmail(email)).thenReturn(Optional.empty());
        Gym mockedGym = Mockito.mock(Gym.class);
        Mockito.when(gymRepository.findByEmail(email)).thenReturn(Optional.of(mockedGym));

        UserDetails result = customUserDetailsService.loadUserByUsername(email);

        Assertions.assertEquals(mockedGym, result);
        Mockito.verify(superAdminRepository).findByEmail(email);
        Mockito.verify(gymRepository).findByEmail(email);
        Mockito.verifyNoInteractions(gymMemberRepository);

    }

    @Test
    void shouldThrowException_WhenEmailNotFound() {
        Mockito.when(superAdminRepository.findByEmail(email)).thenReturn(Optional.empty());
        Mockito.when(gymRepository.findByEmail(email)).thenReturn(Optional.empty());
        Mockito.when(gymMemberRepository.findByEmail(email)).thenReturn(Optional.empty());

        FitManageAppException exception = Assertions.assertThrows(FitManageAppException.class, () ->
                customUserDetailsService.loadUserByUsername(email)
        );

        Assertions.assertEquals(ApiErrorCode.NOT_FOUND, exception.getErrorCode());
    }

}
