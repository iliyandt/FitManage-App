package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.mapper.TenantMapper;
import demos.springdata.fitmanage.domain.dto.tenant.TenantDto;
import demos.springdata.fitmanage.domain.dto.tenant.TenantLookUp;
import demos.springdata.fitmanage.domain.entity.Tenant;
import demos.springdata.fitmanage.domain.enums.Abonnement;
import demos.springdata.fitmanage.domain.enums.AbonnementDuration;
import demos.springdata.fitmanage.exception.DamilSoftException;
import demos.springdata.fitmanage.repository.TenantRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TenantServiceImplTest {

    @Mock
    private TenantRepository tenantRepository;

    @Mock
    private TenantMapper tenantMapper;

    @InjectMocks
    private TenantServiceImpl tenantService;

    private Tenant tenant;
    private UUID tenantId;

    @BeforeEach
    void setUp() {
        tenantId = UUID.randomUUID();
        tenant = new Tenant();
        tenant.setId(tenantId);
        tenant.setName("Damilsoft Gym");
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }


    @Test
    void getTenantById_ShouldReturnTenant_WhenExists() {
        when(tenantRepository.findById(tenantId)).thenReturn(Optional.of(tenant));

        Tenant result = tenantService.getTenantById(tenantId);

        assertNotNull(result);
        assertEquals(tenantId, result.getId());
    }

    @Test
    void getTenantById_ShouldThrowException_WhenNotFound() {
        when(tenantRepository.findById(tenantId)).thenReturn(Optional.empty());

        DamilSoftException ex = assertThrows(DamilSoftException.class, () ->
                tenantService.getTenantById(tenantId));

        assertEquals(HttpStatus.NOT_FOUND, ex.getErrorCode());
    }


    @Test
    void getAllTenants_ShouldReturnMappedList() {
        when(tenantRepository.findAll()).thenReturn(List.of(tenant));
        when(tenantMapper.toResponse(tenant)).thenReturn(new TenantDto());

        List<TenantDto> result = tenantService.getAllTenants();

        assertEquals(1, result.size());
        verify(tenantMapper).toResponse(tenant);
    }

    @Test
    void getShortInfoForAllTenants_ShouldReturnLookUpList() {
        when(tenantRepository.findAll()).thenReturn(List.of(tenant));
        when(tenantMapper.lookUp(tenant)).thenReturn(new TenantLookUp(tenantId, "Gym", "Varna", "Neofit Rilski"));

        List<TenantLookUp> result = tenantService.getShortInfoForAllTenants();

        assertEquals(1, result.size());
        verify(tenantMapper).lookUp(tenant);
    }



    @Test
    void getTenantDtoByEmail_ShouldExtractEmailAndReturnDto() {

        String email = "admin@damilsoft.com";


        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(email);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);


        when(tenantRepository.findTenantByUserEmail(email)).thenReturn(tenant);
        TenantDto expectedDto = new TenantDto();
        when(tenantMapper.toResponse(tenant)).thenReturn(expectedDto);

        TenantDto result = tenantService.getTenantDtoByEmail();

        assertNotNull(result);
        assertEquals(expectedDto, result);
        verify(tenantRepository).findTenantByUserEmail(email);
    }


    @Test
    void createAbonnement_ShouldThrow_WhenTenantNotFound() {
        when(tenantRepository.findById(tenantId)).thenReturn(Optional.empty());

        assertThrows(DamilSoftException.class, () ->
                tenantService.createAbonnement(tenantId, Abonnement.STARTER, "MONTHLY"));
    }

    @Test
    void createAbonnement_ShouldThrow_WhenAlreadyHasSubscription() {
        tenant.setAbonnement(Abonnement.STARTER);
        when(tenantRepository.findById(tenantId)).thenReturn(Optional.of(tenant));

        DamilSoftException ex = assertThrows(DamilSoftException.class, () ->
                tenantService.createAbonnement(tenantId, Abonnement.GROWTH, "MONTHLY"));

        assertEquals(HttpStatus.CONFLICT, ex.getErrorCode());
        assertEquals("Tenant already has active subscription", ex.getMessage());
    }

    @Test
    void createAbonnement_ShouldSetMonthlySubscriptionCorrectly() {
        when(tenantRepository.findById(tenantId)).thenReturn(Optional.of(tenant));

        tenantService.createAbonnement(tenantId, Abonnement.STARTER, "MONTHLY");

        assertEquals(Abonnement.STARTER, tenant.getAbonnement());
        assertEquals(AbonnementDuration.MONTHLY, tenant.getAbonnementDuration());

        LocalDate expectedDate = LocalDate.now().plusMonths(1);
        assertEquals(expectedDate, tenant.getSubscriptionValidUntil());

        verify(tenantRepository).save(tenant);
    }

    @Test
    void createAbonnement_ShouldSetAnnuallySubscriptionCorrectly() {
        when(tenantRepository.findById(tenantId)).thenReturn(Optional.of(tenant));

        tenantService.createAbonnement(tenantId, Abonnement.PRO, "ANNUALLY");

        assertEquals(Abonnement.PRO, tenant.getAbonnement());
        assertEquals(AbonnementDuration.ANNUALLY, tenant.getAbonnementDuration());

        LocalDate expectedDate = LocalDate.now().plusYears(1);
        assertEquals(expectedDate, tenant.getSubscriptionValidUntil());

        verify(tenantRepository).save(tenant);
    }

    @Test
    void createAbonnement_ShouldThrowException_WhenDurationIsInvalid() {
        when(tenantRepository.findById(tenantId)).thenReturn(Optional.of(tenant));

        assertThrows(IllegalArgumentException.class, () ->
                tenantService.createAbonnement(tenantId, Abonnement.STARTER, "INVALID_DURATION"));

        verify(tenantRepository, never()).save(any());
    }
}
