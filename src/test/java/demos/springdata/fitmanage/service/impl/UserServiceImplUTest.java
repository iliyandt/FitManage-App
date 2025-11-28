package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.mapper.UserMapper;
import demos.springdata.fitmanage.domain.dto.users.UserResponse;
import demos.springdata.fitmanage.domain.dto.users.UserUpdate;
import demos.springdata.fitmanage.domain.entity.*;
import demos.springdata.fitmanage.domain.enums.Gender;
import demos.springdata.fitmanage.domain.enums.RoleType;
import demos.springdata.fitmanage.exception.DamilSoftException;
import demos.springdata.fitmanage.repository.UserRepository;
import demos.springdata.fitmanage.security.UserData;
import demos.springdata.fitmanage.service.RoleService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleService roleService;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private Tenant tenant;
    private UUID userId;
    private UUID tenantId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        tenantId = UUID.randomUUID();

        tenant = new Tenant();
        tenant.setId(tenantId);

        user = new User();
        user.setId(userId);
        user.setEmail("test@damilsoft.com");
        user.setTenant(tenant);
        user.setRoles(new HashSet<>());
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void mockSecurityContext(User userEntity) {
        UserData userData = mock(UserData.class);
        when(userData.getId()).thenReturn(userEntity.getId());

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userData);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findById(userEntity.getId())).thenReturn(Optional.of(userEntity));
    }


    @Test
    void getCurrentUser_ShouldReturnUser_WhenAuthenticated() {
        mockSecurityContext(user);

        User result = userService.getCurrentUser();

        assertNotNull(result);
        assertEquals(userId, result.getId());
    }


    @Test
    void getUserProfileByEmail_ShouldReturnMemberProfile_WhenUserIsMember() {

        String email = "member@test.com";
        user.setEmail(email);

        Role memberRole = new Role(RoleType.MEMBER);
        user.getRoles().add(memberRole);

        Membership membership = new Membership();
        user.setMemberships(Set.of(membership));

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userMapper.toResponse(membership, user)).thenReturn(new UserResponse());


        UserResponse response = userService.getUserProfileByEmail(email);

        assertNotNull(response);
        verify(userMapper).toResponse(membership, user);
    }

    @Test
    void getUserProfileByEmail_ShouldReturnEmployeeProfile_WhenUserIsStaff() {
        String email = "staff@test.com";
        user.setEmail(email);

        Role staffRole = new Role(RoleType.STAFF);
        user.getRoles().add(staffRole);

        Employee employee = new Employee();
        user.setEmployees(List.of(employee));

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userMapper.toResponse(employee, user)).thenReturn(new UserResponse());

        UserResponse response = userService.getUserProfileByEmail(email);

        assertNotNull(response);
        verify(userMapper).toResponse(employee, user);
    }

    @Test
    void getUserProfileByEmail_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findByEmail("missing@test.com")).thenReturn(Optional.empty());

        assertThrows(DamilSoftException.class, () ->
                userService.getUserProfileByEmail("missing@test.com"));
    }



    @Test
    void updateProfile_ShouldUpdateAndReturnResponse() {

        mockSecurityContext(user);

        UserUpdate updateDto = new UserUpdate();
        updateDto.setFirstName("NewName");

        when(userMapper.updateUserFields(updateDto, user)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(new UserResponse());

        UserResponse response = userService.updateProfile(updateDto);

        assertNotNull(response);
        verify(userRepository).save(user);
    }

    @Test
    void countByGenderForTenant_ShouldCountOnlyMembers() {
        mockSecurityContext(user);

        User member1 = new User();
        member1.setRoles(Set.of(new Role(RoleType.MEMBER)));

        User member2 = new User();
        member2.setRoles(Set.of(new Role(RoleType.MEMBER)));

        User adminUser = new User();
        adminUser.setRoles(Set.of(new Role(RoleType.ADMIN)));

        List<User> usersInDb = List.of(member1, member2, adminUser);

        when(userRepository.findByGender_AndTenant(Gender.MALE, tenant)).thenReturn(usersInDb);

        Long count = userService.countByGenderForTenant(Gender.MALE);

        assertEquals(2L, count);
    }


    @Test
    void findUsersWithRoles_ShouldConvertStringsToRolesAndSearch() {
        // Arrange
        mockSecurityContext(user);

        Set<String> roleNames = Set.of("ADMIN", "STAFF");
        Role adminRole = new Role(RoleType.ADMIN);
        adminRole.setId(UUID.randomUUID());

        Role staffRole = new Role(RoleType.STAFF);
        staffRole.setId(UUID.randomUUID());

        Set<Role> rolesEntities = Set.of(adminRole, staffRole);

        when(roleService.findByNameIn(anySet())).thenReturn(rolesEntities);

        when(userRepository.findUsersByRolesAndTenant(rolesEntities, tenantId))
                .thenReturn(List.of(user));


        List<User> result = userService.findUsersWithRoles(roleNames);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(roleService).findByNameIn(anySet());
        verify(userRepository).findUsersByRolesAndTenant(rolesEntities, tenantId);
    }

    @Test
    void findUserById_ShouldThrowException_WhenIdNotFound() {
        UUID missingId = UUID.randomUUID();
        when(userRepository.findById(missingId)).thenReturn(Optional.empty());

        DamilSoftException ex = assertThrows(DamilSoftException.class, () ->
                userService.findUserById(missingId));

        assertEquals(HttpStatus.NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void findMembersByFilter_ShouldCallRepository() {
        Specification<User> spec = mock(Specification.class);
        when(userRepository.findAll(spec)).thenReturn(List.of(user));

        List<User> result = userService.findMembersByFilter(spec);

        assertEquals(1, result.size());
        verify(userRepository).findAll(spec);
    }
}