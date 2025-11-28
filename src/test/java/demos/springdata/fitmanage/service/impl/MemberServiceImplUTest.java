package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.mapper.UserMapper;
import demos.springdata.fitmanage.domain.dto.member.request.MemberFilter;
import demos.springdata.fitmanage.domain.dto.member.response.MemberTableDto;
import demos.springdata.fitmanage.domain.dto.users.CreateUser;
import demos.springdata.fitmanage.domain.dto.users.UserResponse;
import demos.springdata.fitmanage.domain.dto.users.UserUpdate;
import demos.springdata.fitmanage.domain.entity.*;
import demos.springdata.fitmanage.domain.enums.RoleType;
import demos.springdata.fitmanage.domain.enums.SubscriptionStatus;
import demos.springdata.fitmanage.exception.DamilSoftException;
import demos.springdata.fitmanage.service.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MemberServiceImplUTest {

    @Mock
    private UserService userService;
    @Mock
    private UserPasswordService userPasswordService;
    @Mock
    private UserValidationService userValidationService;
    @Mock
    private RoleService roleService;
    @Mock
    private MembershipService membershipService;
    @Mock
    private VisitService visitService;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private MemberServiceImpl memberService;

    @Captor
    private ArgumentCaptor<Membership> membershipCaptor;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    // --- CREATE TESTS ---

    @Test
    void create_ShouldCreateMemberAndInactiveMembership() {
        // Arrange
        CreateUser request = new CreateUser();
        request.setEmail("member@gym.bg");
        request.setPhone("0888123456");

        Tenant tenant = new Tenant();
        tenant.setId(UUID.randomUUID());
        tenant.setName("My Gym");

        User adminUser = new User();
        adminUser.setTenant(tenant);

        User newMember = new User();
        newMember.setId(UUID.randomUUID());
        // Важно: Инициализираме списъка, защото сървисът прави .add()
        newMember.setMemberships(new HashSet<>());

        when(userService.getCurrentUser()).thenReturn(adminUser);
        when(userMapper.toMember(tenant, request)).thenReturn(newMember);
        when(userMapper.toResponse(any(Membership.class), eq(newMember))).thenReturn(new UserResponse());

        UserResponse result = memberService.create(request);

        assertNotNull(result);

        verify(userValidationService).validateGlobalAndTenantScopedCredentials(request.getEmail(), request.getPhone(), tenant.getId());

        verify(userPasswordService).setupMemberInitialPassword(newMember);

        verify(membershipService).save(membershipCaptor.capture());
        Membership capturedMembership = membershipCaptor.getValue();

        assertEquals(SubscriptionStatus.INACTIVE, capturedMembership.getSubscriptionStatus());
        assertEquals(0, capturedMembership.getAllowedVisits());
        assertEquals(newMember, capturedMembership.getUser());
        assertEquals(tenant, capturedMembership.getTenant());

        verify(userService).save(newMember);
    }


    @Test
    void checkInMember_ShouldLogVisit_WhenMembershipIsActiveAfterCheckIn() {

        UUID memberId = UUID.randomUUID();
        User member = new User();
        Set<Membership> memberships = new HashSet<>();
        member.setMemberships(memberships);

        Membership activeMembership = new Membership();
        activeMembership.setId(UUID.randomUUID());

        Membership updatedMembership = new Membership();
        updatedMembership.setSubscriptionStatus(SubscriptionStatus.ACTIVE);

        when(userService.findUserById(memberId)).thenReturn(member);
        when(membershipService.getRequiredActiveMembership(memberships)).thenReturn(activeMembership);
        when(membershipService.checkIn(activeMembership)).thenReturn(updatedMembership);
        when(userMapper.toResponse(updatedMembership, member)).thenReturn(new UserResponse());

        memberService.checkInMember(memberId);

        verify(visitService).checkIn(updatedMembership, memberId);
    }

    @Test
    void checkInMember_ShouldNOTLogVisit_WhenMembershipIsNotActive() {

        UUID memberId = UUID.randomUUID();
        User member = new User();
        Set<Membership> memberships = new HashSet<>();

        Membership activeMembership = new Membership();

        Membership updatedMembership = new Membership();
        updatedMembership.setSubscriptionStatus(SubscriptionStatus.INACTIVE);

        when(userService.findUserById(memberId)).thenReturn(member);
        when(membershipService.getRequiredActiveMembership(any())).thenReturn(activeMembership);
        when(membershipService.checkIn(activeMembership)).thenReturn(updatedMembership);

        memberService.checkInMember(memberId);

        verify(visitService, never()).checkIn(any(), any());
    }

    @Test
    void findMembersTableView_ShouldReturnMappedUsers() {

        Tenant tenant = new Tenant();
        User memberWithMembership = new User();
        memberWithMembership.setFirstName("John");
        Membership m = new Membership();
        m.setSubscriptionStatus(SubscriptionStatus.ACTIVE);
        memberWithMembership.setMemberships(Set.of(m));

        User userWithoutMembership = new User();
        userWithoutMembership.setMemberships(Collections.emptySet());

        tenant.setUsers(List.of(memberWithMembership, userWithoutMembership));

        User admin = new User();
        admin.setTenant(tenant);
        when(userService.getCurrentUser()).thenReturn(admin);


        List<MemberTableDto> result = memberService.findMembersTableView();

        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getFirstName());
        assertEquals(SubscriptionStatus.ACTIVE, result.get(0).getSubscriptionStatus());
    }

    @Test
    void getMembersByFilter_ShouldReturnMembers_WhenFound() {

        MemberFilter filter = new MemberFilter();
        Tenant tenant = new Tenant();
        User admin = new User();
        admin.setTenant(tenant);

        Role memberRole = new Role();
        memberRole.setName(RoleType.MEMBER);

        User john = new User();
        john.setFirstName("John");
        john.setRoles(Set.of(memberRole));
        Membership m = new Membership();
        m.setSubscriptionStatus(SubscriptionStatus.ACTIVE);
        john.setMemberships(Set.of(m));

        when(userService.getCurrentUser()).thenReturn(admin);
        when(userService.findMembersByFilter(any(Specification.class))).thenReturn(List.of(john));
        when(roleService.findByName(RoleType.MEMBER)).thenReturn(memberRole);

        List<MemberTableDto> result = memberService.getMembersByFilter(filter);

        assertFalse(result.isEmpty());
        assertEquals("John", result.get(0).getFirstName());
    }

    @Test
    void getMembersByFilter_ShouldThrowException_WhenListIsEmpty() {

        MemberFilter filter = new MemberFilter();
        Tenant tenant = new Tenant();
        User admin = new User();
        admin.setTenant(tenant);

        when(userService.getCurrentUser()).thenReturn(admin);
        when(userService.findMembersByFilter(any(Specification.class))).thenReturn(Collections.emptyList());

        DamilSoftException ex = assertThrows(DamilSoftException.class, () ->
                memberService.getMembersByFilter(filter)
        );
        assertEquals(HttpStatus.NOT_FOUND, ex.getErrorCode());
        assertEquals("No members found for the given filter", ex.getMessage());
    }


    @Test
    void updateMember_ShouldUpdateUserAndMembershipFields() {

        UUID memberId = UUID.randomUUID();
        UserUpdate updateRequest = new UserUpdate();

        User member = new User();
        Membership currentMembership = new Membership();
        member.setMemberships(Set.of(currentMembership));

        when(userService.findUserById(memberId)).thenReturn(member);


        memberService.updateMember(memberId, updateRequest);


        verify(userMapper).updateUserFields(updateRequest, member);
        verify(userMapper).updateMembershipFields(updateRequest, currentMembership);
        verify(userMapper).toResponse(currentMembership, member);
    }

    @Test
    void deleteMember_ShouldCallDeleteOnUserService() {

        UUID memberId = UUID.randomUUID();
        Tenant tenant = new Tenant();
        tenant.setId(UUID.randomUUID());

        User admin = new User();
        admin.setTenant(tenant);

        User memberToDelete = new User();

        when(userService.getCurrentUser()).thenReturn(admin);
        when(userService.getByIdAndTenantId(memberId, tenant.getId())).thenReturn(memberToDelete);


        memberService.deleteMember(memberId);


        verify(userService).delete(memberToDelete);
    }
}