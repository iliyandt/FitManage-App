package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.mapper.UserMapper;
import demos.springdata.fitmanage.domain.dto.member.request.SubscriptionRequest;
import demos.springdata.fitmanage.domain.dto.users.UserResponse;
import demos.springdata.fitmanage.domain.entity.Membership;
import demos.springdata.fitmanage.domain.entity.Tenant;
import demos.springdata.fitmanage.domain.entity.User;
import demos.springdata.fitmanage.domain.enums.Employment;
import demos.springdata.fitmanage.domain.enums.SubscriptionPlan;
import demos.springdata.fitmanage.domain.enums.SubscriptionStatus;
import demos.springdata.fitmanage.exception.DamilSoftException;
import demos.springdata.fitmanage.repository.MembershipRepository;
import demos.springdata.fitmanage.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MembershipServiceImplUTest {

    @Mock
    private MembershipRepository membershipRepository;
    @Mock
    private UserService userService;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private MembershipServiceImpl membershipService;

    @Captor
    private ArgumentCaptor<Membership> membershipCaptor;


    @Test
    void setupMembershipPlan_ShouldActivateTimeBasedSubscription() {
        // Arrange
        UUID memberId = UUID.randomUUID();
        Tenant tenant = new Tenant();
        User user = new User();
        user.setTenant(tenant);
        user.setMemberships(new HashSet<>());

        Membership existingMembership = new Membership();
        existingMembership.setSubscriptionStatus(SubscriptionStatus.INACTIVE);

        SubscriptionRequest request = new SubscriptionRequest(null, SubscriptionPlan.MONTHLY, Employment.REGULAR);

        when(userService.findUserById(memberId)).thenReturn(user);
        when(membershipRepository.findByUserAndTenant(user, tenant)).thenReturn(Optional.of(existingMembership));
        when(membershipRepository.save(any(Membership.class))).thenAnswer(i -> i.getArguments()[0]);
        when(userMapper.toResponse((Membership) any(), any())).thenReturn(new UserResponse());

        membershipService.setupMembershipPlan(memberId, request);

        verify(membershipRepository).save(membershipCaptor.capture());
        Membership saved = membershipCaptor.getValue();

        assertEquals(SubscriptionStatus.ACTIVE, saved.getSubscriptionStatus());
        assertEquals(SubscriptionPlan.MONTHLY, saved.getSubscriptionPlan());
        assertNotNull(saved.getSubscriptionStartDate());
        assertNotNull(saved.getSubscriptionEndDate());
        assertTrue(saved.getSubscriptionEndDate().isAfter(saved.getSubscriptionStartDate()));
    }

    @Test
    void setupMembershipPlan_ShouldActivateVisitBasedSubscription() {
        UUID memberId = UUID.randomUUID();
        Tenant tenant = new Tenant();
        User user = new User();
        user.setTenant(tenant);
        user.setMemberships(new HashSet<>());

        Membership existingMembership = new Membership();
        existingMembership.setSubscriptionStatus(SubscriptionStatus.INACTIVE);

        SubscriptionRequest request = new SubscriptionRequest(10, SubscriptionPlan.VISIT_PASS, Employment.STUDENT);

        when(userService.findUserById(memberId)).thenReturn(user);
        when(membershipRepository.findByUserAndTenant(user, tenant)).thenReturn(Optional.of(existingMembership));
        when(membershipRepository.save(any(Membership.class))).thenAnswer(i -> i.getArguments()[0]);
        when(userMapper.toResponse((Membership) any(), any())).thenReturn(new UserResponse());

        membershipService.setupMembershipPlan(memberId, request);

        verify(membershipRepository).save(membershipCaptor.capture());
        Membership saved = membershipCaptor.getValue();

        assertEquals(SubscriptionStatus.ACTIVE, saved.getSubscriptionStatus());
        assertEquals(SubscriptionPlan.VISIT_PASS, saved.getSubscriptionPlan());
        assertEquals(10, saved.getRemainingVisits());
        assertEquals(10, saved.getAllowedVisits());
        assertNull(saved.getSubscriptionEndDate());
    }

    @Test
    void setupMembershipPlan_ShouldThrow_WhenChangingTimeBasedBeforeExpiry() {
        UUID memberId = UUID.randomUUID();
        User user = new User();
        user.setTenant(new Tenant());

        Membership activeMembership = new Membership();
        activeMembership.setSubscriptionPlan(SubscriptionPlan.MONTHLY);
        activeMembership.setSubscriptionStatus(SubscriptionStatus.ACTIVE);

        activeMembership.setSubscriptionEndDate(LocalDateTime.now().plusDays(10));

        SubscriptionRequest request = new SubscriptionRequest(null, SubscriptionPlan.ANNUAL, Employment.REGULAR);

        when(userService.findUserById(memberId)).thenReturn(user);
        when(membershipRepository.findByUserAndTenant(any(), any())).thenReturn(Optional.of(activeMembership));

        DamilSoftException ex = assertThrows(DamilSoftException.class, () ->
                membershipService.setupMembershipPlan(memberId, request)
        );
        assertEquals(HttpStatus.UNAUTHORIZED, ex.getErrorCode());
        assertTrue(ex.getMessage().contains("Cannot change time-based plan"));
    }

    @Test
    void setupMembershipPlan_ShouldThrow_WhenChangingVisitBasedBeforeUsed() {
        UUID memberId = UUID.randomUUID();
        User user = new User();
        user.setTenant(new Tenant());

        Membership activeMembership = new Membership();
        activeMembership.setSubscriptionPlan(SubscriptionPlan.VISIT_PASS);
        activeMembership.setSubscriptionStatus(SubscriptionStatus.ACTIVE);

        activeMembership.setRemainingVisits(5);

        SubscriptionRequest request = new SubscriptionRequest(null, SubscriptionPlan.MONTHLY, Employment.REGULAR);

        when(userService.findUserById(memberId)).thenReturn(user);
        when(membershipRepository.findByUserAndTenant(any(), any())).thenReturn(Optional.of(activeMembership));

        DamilSoftException ex = assertThrows(DamilSoftException.class, () ->
                membershipService.setupMembershipPlan(memberId, request)
        );
        assertEquals(HttpStatus.UNAUTHORIZED, ex.getErrorCode());
        assertTrue(ex.getMessage().contains("until all visits are used"));
    }


    @Test
    void checkIn_ShouldUpdateLastCheckIn_WhenTimeBasedAndActive() {
        Membership membership = new Membership();
        membership.setSubscriptionPlan(SubscriptionPlan.MONTHLY);
        membership.setSubscriptionStatus(SubscriptionStatus.ACTIVE);

        membership.setSubscriptionEndDate(LocalDateTime.now().plusDays(1));


        membershipService.checkIn(membership);


        verify(membershipRepository).save(membershipCaptor.capture());
        Membership saved = membershipCaptor.getValue();

        assertEquals(SubscriptionStatus.ACTIVE, saved.getSubscriptionStatus());
        assertNotNull(saved.getLastCheckInAt());
    }

    @Test
    void checkIn_ShouldExpire_WhenTimeBasedAndDatePassed() {

        Membership membership = new Membership();
        membership.setSubscriptionPlan(SubscriptionPlan.MONTHLY);
        membership.setSubscriptionStatus(SubscriptionStatus.ACTIVE);

        membership.setSubscriptionEndDate(LocalDateTime.now().minusDays(1));


        membershipService.checkIn(membership);

        verify(membershipRepository).save(membershipCaptor.capture());
        Membership saved = membershipCaptor.getValue();

        assertEquals(SubscriptionStatus.INACTIVE, saved.getSubscriptionStatus());
        assertNull(saved.getSubscriptionPlan()); // Логиката ти зачиства плана при expire
    }

    @Test
    void checkIn_ShouldDecrementVisits_WhenVisitBasedAndHasVisits() {

        Membership membership = new Membership();
        membership.setSubscriptionPlan(SubscriptionPlan.VISIT_PASS);
        membership.setSubscriptionStatus(SubscriptionStatus.ACTIVE);
        membership.setRemainingVisits(5);


        membershipService.checkIn(membership);


        verify(membershipRepository).save(membershipCaptor.capture());
        Membership saved = membershipCaptor.getValue();

        assertEquals(4, saved.getRemainingVisits());
        assertNotNull(saved.getLastCheckInAt());
        assertEquals(SubscriptionStatus.ACTIVE, saved.getSubscriptionStatus());
    }

    @Test
    void checkIn_ShouldExpire_WhenVisitBasedAndNoVisitsLeft() {

        Membership membership = new Membership();
        membership.setSubscriptionPlan(SubscriptionPlan.VISIT_PASS);
        membership.setSubscriptionStatus(SubscriptionStatus.ACTIVE);
        membership.setRemainingVisits(0);

        membershipService.checkIn(membership);

        verify(membershipRepository).save(membershipCaptor.capture());
        Membership saved = membershipCaptor.getValue();

        assertEquals(SubscriptionStatus.INACTIVE, saved.getSubscriptionStatus());
        assertNull(saved.getSubscriptionPlan());
    }



    @Test
    void getRequiredActiveMembership_ShouldReturnActive() {

        Membership inactive = new Membership();
        inactive.setId(UUID.randomUUID());
        inactive.setSubscriptionStatus(SubscriptionStatus.INACTIVE);

        Membership active = new Membership();
        active.setId(UUID.randomUUID());
        active.setSubscriptionStatus(SubscriptionStatus.ACTIVE);

        Set<Membership> memberships = Set.of(inactive, active);


        Membership result = membershipService.getRequiredActiveMembership(memberships);

        assertEquals(active, result);
    }

    @Test
    void getRequiredActiveMembership_ShouldThrow_WhenNoneActive() {

        Membership inactive = new Membership();
        inactive.setSubscriptionStatus(SubscriptionStatus.INACTIVE);
        Set<Membership> memberships = Set.of(inactive);

        DamilSoftException ex = assertThrows(DamilSoftException.class, () ->
                membershipService.getRequiredActiveMembership(memberships)
        );
        assertEquals(HttpStatus.NOT_FOUND, ex.getErrorCode());
    }
}