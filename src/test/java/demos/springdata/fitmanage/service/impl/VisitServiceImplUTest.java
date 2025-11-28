package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.visit.VisitDto;
import demos.springdata.fitmanage.domain.dto.visit.VisitTableResponse;
import demos.springdata.fitmanage.domain.entity.Membership;
import demos.springdata.fitmanage.domain.entity.Tenant;
import demos.springdata.fitmanage.domain.entity.User;
import demos.springdata.fitmanage.domain.entity.Visit;
import demos.springdata.fitmanage.domain.enums.SubscriptionPlan;
import demos.springdata.fitmanage.exception.DamilSoftException;
import demos.springdata.fitmanage.repository.VisitRepository;
import demos.springdata.fitmanage.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class VisitServiceImplUTest {
    @Mock
    private VisitRepository visitRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private VisitServiceImpl visitService;

    private User member;
    private User admin;
    private Tenant tenant;
    private Membership membership;
    private Visit visit;
    private UUID memberId;
    private UUID adminId;

    @BeforeEach
    void setUp() {
        memberId = UUID.randomUUID();
        adminId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();
        UUID membershipId = UUID.randomUUID();

        tenant = new Tenant();
        tenant.setId(tenantId);

        member = new User();
        member.setId(memberId);
        member.setFirstName("John");
        member.setLastName("Doe");
        member.setPhone("123456789");
        // Задаваме Tenant на потребителя, защото се използва в getVisitsInPeriod
        member.setTenant(tenant);

        admin = new User();
        admin.setId(adminId);
        admin.setTenant(tenant);

        membership = new Membership();
        membership.setId(membershipId);
        membership.setUser(member);
        membership.setTenant(tenant);
        membership.setSubscriptionPlan(SubscriptionPlan.MONTHLY);

        visit = new Visit();
        visit.setId(UUID.randomUUID());
        visit.setUser(member);
        visit.setMembership(membership);
        visit.setCheckInAt(LocalDateTime.now());
    }

    // --- Tests for findVisitsForMember ---

    @Test
    void findVisitsForMember_ShouldReturnListOfDtos_WhenVisitsExist() {
        // Arrange
        Mockito.when(visitRepository.findByUser_Id(memberId))
                .thenReturn(Optional.of(List.of(visit)));

        // Act
        List<VisitDto> result = visitService.findVisitsForMember(memberId);

        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(memberId, result.get(0).getUserId());
        Assertions.assertEquals(membership.getId(), result.get(0).getMembershipId());

        Mockito.verify(visitRepository).findByUser_Id(memberId);
    }

    @Test
    void findVisitsForMember_ShouldThrowException_WhenNoVisitsFound() {
        // Arrange
        // Тук симулираме Optional.empty(), защото кодът ти ползва .orElseThrow()
        Mockito.when(visitRepository.findByUser_Id(memberId))
                .thenReturn(Optional.empty());

        // Act & Assert
        DamilSoftException exception = Assertions.assertThrows(DamilSoftException.class, () -> {
            visitService.findVisitsForMember(memberId);
        });

        Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getErrorCode());
        Assertions.assertEquals("No visits for this member", exception.getMessage());
    }

    // --- Tests for checkIn ---

    @Test
    void checkIn_ShouldSaveVisitAndReturnIt() {
        // Arrange
        // Трябва да мокнем save метода.
        // ВАЖНО: Твоят код логва savedVisit.getCheckInAt(). Ако базата данни (JPA) слага датата автоматично,
        // в unit test-a тя ще е null, освен ако не я сетнем ръчно в мока.
        Mockito.when(visitRepository.save(any(Visit.class))).thenAnswer(invocation -> {
            Visit v = invocation.getArgument(0);
            v.setId(UUID.randomUUID());
            v.setCheckInAt(LocalDateTime.now()); // Симулираме @CreationTimestamp
            return v;
        });

        // Act
        Visit result = visitService.checkIn(membership, memberId);

        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getId());
        Assertions.assertNotNull(result.getCheckInAt());
        Assertions.assertEquals(member, result.getUser());
        Assertions.assertEquals(membership, result.getMembership());

        Mockito.verify(visitRepository).save(any(Visit.class));
    }

    // --- Tests for getVisitsInPeriod ---

    @Test
    void getVisitsInPeriod_ShouldReturnMappedResponses() {
        // Arrange
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        // 1. Мокваме намирането на админа, за да вземем неговия Tenant
        Mockito.when(userService.findUserById(adminId)).thenReturn(admin);

        // 2. Мокваме търсенето на визити
        Mockito.when(visitRepository.findByUserTenantAndCheckInAtBetween(tenant, start, end))
                .thenReturn(List.of(visit));

        // Act
        List<VisitTableResponse> result = visitService.getVisitsInPeriod(adminId, start, end);

        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());

        // Тук тестваме мапинга (manualMapDto метода)
        VisitTableResponse response = result.get(0);
        Assertions.assertEquals(memberId, response.getId());
        Assertions.assertEquals("John", response.getFirstName());
        Assertions.assertEquals("Doe", response.getLastName());
        Assertions.assertEquals("123456789", response.getPhone());
        Assertions.assertEquals(SubscriptionPlan.MONTHLY, response.getSubscriptionPlan());

        Mockito.verify(userService).findUserById(adminId);
        Mockito.verify(visitRepository).findByUserTenantAndCheckInAtBetween(tenant, start, end);
    }

    @Test
    void getVisitsInPeriod_ShouldReturnEmptyList_WhenNoVisitsInPeriod() {
        // Arrange
        LocalDateTime start = LocalDateTime.now().minusDays(10);
        LocalDateTime end = LocalDateTime.now().minusDays(9);

        Mockito.when(userService.findUserById(adminId)).thenReturn(admin);
        Mockito.when(visitRepository.findByUserTenantAndCheckInAtBetween(tenant, start, end))
                .thenReturn(List.of());

        // Act
        List<VisitTableResponse> result = visitService.getVisitsInPeriod(adminId, start, end);

        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
    }
}
