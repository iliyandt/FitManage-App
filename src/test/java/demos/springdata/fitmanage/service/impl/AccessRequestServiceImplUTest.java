package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.mapper.UserMapper;
import demos.springdata.fitmanage.domain.dto.users.CreateUser;
import demos.springdata.fitmanage.domain.dto.users.UserResponse;
import demos.springdata.fitmanage.domain.entity.Membership;
import demos.springdata.fitmanage.domain.entity.Tenant;
import demos.springdata.fitmanage.domain.entity.User;
import demos.springdata.fitmanage.domain.enums.SubscriptionStatus;
import demos.springdata.fitmanage.exception.DamilSoftException;
import demos.springdata.fitmanage.service.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccessRequestServiceImplUTest {
    @Mock
    private TenantService tenantService;
    @Mock
    private MembershipService membershipService;
    @Mock
    private UserService userService;
    @Mock
    private UserValidationService userValidationService;
    @Mock
    private UserPasswordService userPasswordService;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private AccessRequestServiceImpl accessRequestService;



    @Test
    void requestAccess_ShouldCreateUserAndPendingMembership_WhenDataIsValid() {
        UUID tenantId = UUID.randomUUID();
        CreateUser request = new CreateUser();
        request.setEmail("new@member.com");
        request.setPhone("0888123456");

        Tenant mockTenant = new Tenant();
        mockTenant.setId(tenantId);
        mockTenant.setName("Power Gym");

        User mockMember = new User();
        mockMember.setEmail(request.getEmail());
        mockMember.setMemberships(new HashSet<>());

        UserResponse mockResponse = new UserResponse();
        mockResponse.setEmail(request.getEmail());

        when(tenantService.getTenantById(tenantId)).thenReturn(mockTenant);

        when(userMapper.toMember(mockTenant, request)).thenReturn(mockMember);

        when(userMapper.toResponse(any(Membership.class), eq(mockMember))).thenReturn(mockResponse);

        UserResponse result = accessRequestService.requestAccess(tenantId, request);

        assertNotNull(result);
        assertEquals(request.getEmail(), result.getEmail());

        verify(userValidationService).validateTenantScopedCredentials(request.getEmail(), request.getPhone(), tenantId);

        verify(userPasswordService).setupMemberInitialPassword(mockMember);

        verify(userService).save(mockMember);
        verify(membershipService).save(any(Membership.class));

        assertFalse(mockMember.getMemberships().isEmpty());
        Membership createdMembership = mockMember.getMemberships().iterator().next();
        assertEquals(SubscriptionStatus.PENDING, createdMembership.getSubscriptionStatus());
        assertEquals(mockTenant, createdMembership.getTenant());
    }

    @Test
    void processAccessRequest_ShouldApproveAndEnableUser_WhenActionIsApprove() {
        UUID userId = UUID.randomUUID();
        User mockMember = new User();
        mockMember.setId(userId);
        mockMember.setEnabled(false);

        Membership pendingMembership = new Membership();
        pendingMembership.setSubscriptionStatus(SubscriptionStatus.PENDING);

        Set<Membership> memberships = new HashSet<>();
        memberships.add(pendingMembership);
        mockMember.setMemberships(memberships);

        when(userService.findUserById(userId)).thenReturn(mockMember);
        when(userMapper.toResponse(pendingMembership, mockMember)).thenReturn(new UserResponse());

        accessRequestService.processAccessRequest(userId, true);
        assertTrue(mockMember.isEnabled());

        assertEquals(SubscriptionStatus.INACTIVE, pendingMembership.getSubscriptionStatus());

        verify(userService).save(mockMember);
        verify(membershipService).save(pendingMembership);
    }

    @Test
    void processAccessRequest_ShouldReject_WhenActionIsReject() {
        UUID userId = UUID.randomUUID();
        User mockMember = new User();

        Membership pendingMembership = new Membership();
        pendingMembership.setSubscriptionStatus(SubscriptionStatus.PENDING);

        mockMember.setMemberships(Set.of(pendingMembership));

        when(userService.findUserById(userId)).thenReturn(mockMember);
        when(userMapper.toResponse(pendingMembership, mockMember)).thenReturn(new UserResponse());

        accessRequestService.processAccessRequest(userId, false);

        assertEquals(SubscriptionStatus.CANCELLED, pendingMembership.getSubscriptionStatus());

        assertFalse(mockMember.isEnabled());

        verify(membershipService).save(pendingMembership);
    }

    @Test
    void processAccessRequest_ShouldThrowException_WhenStatusIsNotPending() {
        UUID userId = UUID.randomUUID();
        User mockMember = new User();

        Membership activeMembership = new Membership();
        activeMembership.setSubscriptionStatus(SubscriptionStatus.ACTIVE);

        mockMember.setMemberships(Set.of(activeMembership));

        when(userService.findUserById(userId)).thenReturn(mockMember);

        DamilSoftException exception = assertThrows(DamilSoftException.class, () -> {
            accessRequestService.processAccessRequest(userId, true);
        });

        assertEquals("Membership request already processed.", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getErrorCode());

        verify(userService, never()).save(any());
        verify(membershipService, never()).save(any());
    }

}
