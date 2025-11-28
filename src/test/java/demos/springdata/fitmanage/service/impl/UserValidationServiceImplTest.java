package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.exception.MultipleValidationException;
import demos.springdata.fitmanage.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserValidationServiceImplTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserValidationServiceImpl validationService;

    private UUID tenantId;
    private String email;
    private String phone;

    @BeforeEach
    void setUp() {
        tenantId = UUID.randomUUID();
        email = "test@example.com";
        phone = "0888123456";
    }


    @Test
    void validateTenantScoped_ShouldPass_WhenNoDuplicatesExist() {

        when(userService.existsByEmailAndTenant(email, tenantId)).thenReturn(false);
        when(userService.existsByPhoneAndTenant(phone, tenantId)).thenReturn(false);

        assertDoesNotThrow(() ->
                validationService.validateTenantScopedCredentials(email, phone, tenantId));
    }

    @Test
    void validateTenantScoped_ShouldThrow_WhenEmailExistsInTenant() {

        when(userService.existsByEmailAndTenant(email, tenantId)).thenReturn(true);

        when(userService.existsByPhoneAndTenant(phone, tenantId)).thenReturn(false);

        MultipleValidationException ex = assertThrows(MultipleValidationException.class, () ->
                validationService.validateTenantScopedCredentials(email, phone, tenantId));

        assertTrue(ex.getErrors().containsKey("email"));
        assertEquals("Email is already registered in this tenant", ex.getErrors().get("email"));
        assertFalse(ex.getErrors().containsKey("phone")); // Уверяваме се, че няма грешка за телефона
    }

    @Test
    void validateTenantScoped_ShouldThrow_WhenPhoneExistsInTenant() {

        when(userService.existsByEmailAndTenant(email, tenantId)).thenReturn(false);
        when(userService.existsByPhoneAndTenant(phone, tenantId)).thenReturn(true);

        MultipleValidationException ex = assertThrows(MultipleValidationException.class, () ->
                validationService.validateTenantScopedCredentials(email, phone, tenantId));

        assertTrue(ex.getErrors().containsKey("phone"));
        assertEquals("Phone used from another user", ex.getErrors().get("phone"));
        assertFalse(ex.getErrors().containsKey("email"));
    }

    @Test
    void validateTenantScoped_ShouldThrowWithTwoErrors_WhenBothExist() {
        when(userService.existsByEmailAndTenant(email, tenantId)).thenReturn(true);
        when(userService.existsByPhoneAndTenant(phone, tenantId)).thenReturn(true);

        MultipleValidationException ex = assertThrows(MultipleValidationException.class, () ->
                validationService.validateTenantScopedCredentials(email, phone, tenantId));

        assertEquals(2, ex.getErrors().size());
        assertTrue(ex.getErrors().containsKey("email"));
        assertTrue(ex.getErrors().containsKey("phone"));
    }


    @Test
    void validateGlobalAndTenant_ShouldThrow_WhenEmailExistsGlobally() {

        when(userService.existsByEmail(email)).thenReturn(true);
        when(userService.existsByPhoneAndTenant(phone, tenantId)).thenReturn(false);


        MultipleValidationException ex = assertThrows(MultipleValidationException.class, () ->
                validationService.validateGlobalAndTenantScopedCredentials(email, phone, tenantId));

        assertTrue(ex.getErrors().containsKey("email"));
        assertEquals("Email is already registered", ex.getErrors().get("email"));

        verify(userService, never()).existsByEmailAndTenant(anyString(), any(UUID.class));
    }

    @Test
    void validateGlobalAndTenant_ShouldCheckTenantEmail_WhenGlobalEmailIsFree() {

        when(userService.existsByEmail(email)).thenReturn(false); // Глобално е свободен
        when(userService.existsByEmailAndTenant(email, tenantId)).thenReturn(true); // В тенанта е зает
        when(userService.existsByPhoneAndTenant(phone, tenantId)).thenReturn(false);


        MultipleValidationException ex = assertThrows(MultipleValidationException.class, () ->
                validationService.validateGlobalAndTenantScopedCredentials(email, phone, tenantId));

        assertTrue(ex.getErrors().containsKey("email"));
        assertEquals("Email is already registered in this tenant", ex.getErrors().get("email"));
    }

    @Test
    void validateGlobalAndTenant_ShouldThrowForPhone_WhenEmailIsOk() {

        when(userService.existsByEmail(email)).thenReturn(false);
        when(userService.existsByEmailAndTenant(email, tenantId)).thenReturn(false);
        when(userService.existsByPhoneAndTenant(phone, tenantId)).thenReturn(true);

        MultipleValidationException ex = assertThrows(MultipleValidationException.class, () ->
                validationService.validateGlobalAndTenantScopedCredentials(email, phone, tenantId));

        assertTrue(ex.getErrors().containsKey("phone"));
    }
}