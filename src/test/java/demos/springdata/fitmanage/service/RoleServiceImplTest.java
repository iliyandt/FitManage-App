package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.entity.Role;
import demos.springdata.fitmanage.domain.enums.RoleType;
import demos.springdata.fitmanage.exception.FitManageAppException;
import demos.springdata.fitmanage.repository.GymRoleRepository;
import demos.springdata.fitmanage.service.impl.RoleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RoleServiceImplTest {
    private GymRoleRepository gymRoleRepository;
    private RoleServiceImpl roleService;

    @BeforeEach
    void setUp() {
        gymRoleRepository = mock(GymRoleRepository.class);
        roleService = new RoleServiceImpl(gymRoleRepository);
    }


    @Test
    void findByName_ShouldReturnRole_WhenRoleExists() {
        Role role = new Role(RoleType.GYM_ADMIN);
        when(gymRoleRepository.findByName(RoleType.GYM_ADMIN)).thenReturn(Optional.of(role));

        Role roleByName = roleService.findByName(RoleType.GYM_ADMIN);
        assertEquals(role, roleByName);
    }

    @Test
    void findByName_ShouldThrowException_WhenRoleDoesNotExists() {
        when(gymRoleRepository.findByName(RoleType.GYM_ADMIN)).thenReturn(Optional.empty());

        FitManageAppException exception = assertThrows(
                FitManageAppException.class,
                () -> roleService.findByName(RoleType.GYM_ADMIN)
        );

        assertTrue(exception.getMessage().contains("Role with name GYM_ADMIN not found"));
    }

    @Test
    void createRole_shouldSaveNewRole() {
        roleService.createRole(RoleType.GYM_ADMIN);

        ArgumentCaptor<Role> captor = ArgumentCaptor.forClass(Role.class);
        verify(gymRoleRepository, times(1)).save(captor.capture());

        assertEquals(RoleType.GYM_ADMIN, captor.getValue().getName());
    }

    @Test
    void initRoles_shouldCreateAllRoles_whenRepositoryIsEmpty() {
        when(gymRoleRepository.count()).thenReturn(0L);

        roleService.initRoles();

        verify(gymRoleRepository, times(RoleType.values().length)).save(any(Role.class));
    }


    @Test
    void save_shouldSaveRoleInRoleRepository() {
        Role roleToSave = new Role(RoleType.GYM_ADMIN);
        Role savedRole = new Role(RoleType.GYM_ADMIN);
        savedRole.setId(1L);

        when(gymRoleRepository.save(any(Role.class))).thenReturn(savedRole);

        Role result = roleService.save(roleToSave);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(RoleType.GYM_ADMIN, result.getName());
    }
}
