package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.entity.Role;
import demos.springdata.fitmanage.domain.entity.User;
import demos.springdata.fitmanage.exception.DamilSoftException;
import demos.springdata.fitmanage.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

import static demos.springdata.fitmanage.domain.enums.RoleType.ADMIN;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceImplUTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsServiceImpl customUserDetailsService;

    @Test
    void loadUserByUsername_ShouldReturnUserDetails_WhenUserExists() {
        String email = "admin@gym.bg";
        UUID userId = UUID.randomUUID();

        Role adminRole = new Role();
        adminRole.setName(ADMIN);

        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setEmail(email);
        mockUser.setPassword("encodedPassword123");
        mockUser.setEnabled(true);
        mockUser.setRoles(Set.of(adminRole));

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));

        UserDetails result = customUserDetailsService.loadUserByUsername(email);

        assertNotNull(result);
        assertEquals(email, result.getUsername());
        assertEquals(mockUser.getPassword(), result.getPassword());
        assertTrue(result.isEnabled());

        assertFalse(result.getAuthorities().isEmpty());
    }

    @Test
    void loadUserByUsername_ShouldThrowException_WhenUserNotFound() {

        String email = "missing@gym.bg";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        
        DamilSoftException exception = assertThrows(DamilSoftException.class, () -> {
            customUserDetailsService.loadUserByUsername(email);
        });

        assertEquals("User not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getErrorCode());
    }
}