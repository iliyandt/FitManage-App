package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.accountsettings.PreferencesResponse;
import demos.springdata.fitmanage.domain.entity.User;
import demos.springdata.fitmanage.domain.entity.UserPreferences;
import demos.springdata.fitmanage.repository.UserPreferencesRepository;
import demos.springdata.fitmanage.security.UserData;
import demos.springdata.fitmanage.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserPreferencesServiceImplTest {

    @Mock
    private UserPreferencesRepository userPreferencesRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserPreferencesServiceImpl userPreferencesService;

    private UserData userData;
    private User userEntity;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        userData = mock(UserData.class);
        lenient().when(userData.getId()).thenReturn(userId);

        userEntity = new User();
        userEntity.setId(userId);
    }


    @Test
    void getPreferences_ShouldReturnSettings_WhenPreferencesExist() {

        UserPreferences existingPrefs = new UserPreferences();
        Map<String, Object> settings = new HashMap<>();
        settings.put("theme", "dark");
        settings.put("notifications", true);
        existingPrefs.setSettings(settings);

        when(userPreferencesRepository.findByUserId(userId)).thenReturn(Optional.of(existingPrefs));

        PreferencesResponse response = userPreferencesService.getPreferences(userData);

        assertNotNull(response);
        assertEquals("dark", response.settings().get("theme"));
        assertEquals(true, response.settings().get("notifications"));
    }

    @Test
    void getPreferences_ShouldReturnEmptyMap_WhenNoPreferencesExist() {

        when(userPreferencesRepository.findByUserId(userId)).thenReturn(Optional.empty());

        PreferencesResponse response = userPreferencesService.getPreferences(userData);

        assertNotNull(response);
        assertNotNull(response.settings());
        assertTrue(response.settings().isEmpty(), "Should return empty map, not null");
    }


    @Test
    void upsert_ShouldCreateNewPreferences_WhenNoneExist() {
        Map<String, Object> newSettings = Map.of("lang", "bg", "newsletter", false);

        when(userService.findUserById(userId)).thenReturn(userEntity);
        when(userPreferencesRepository.findByUserId(userId)).thenReturn(Optional.empty());

        when(userPreferencesRepository.save(any(UserPreferences.class))).thenAnswer(invocation -> invocation.<UserPreferences>getArgument(0));

        PreferencesResponse response = userPreferencesService.upsert(userData, newSettings);

        assertNotNull(response);
        assertEquals("bg", response.settings().get("lang"));

        verify(userService).findUserById(userId);
        verify(userPreferencesRepository).save(argThat(prefs ->
                prefs.getUser().equals(userEntity) &&
                        prefs.getSettings().containsKey("lang")
        ));
    }

    @Test
    void upsert_ShouldUpdateExistingPreferences_AndMergeKeys() {

        UserPreferences existingPrefs = new UserPreferences();
        existingPrefs.setUser(userEntity);
        Map<String, Object> currentMap = new HashMap<>();
        currentMap.put("theme", "light");
        currentMap.put("volume", 50);
        existingPrefs.setSettings(currentMap);

        Map<String, Object> incomingSettings = new HashMap<>();
        incomingSettings.put("theme", "dark");
        incomingSettings.put("notifications", true);

        when(userService.findUserById(userId)).thenReturn(userEntity);
        when(userPreferencesRepository.findByUserId(userId)).thenReturn(Optional.of(existingPrefs));

        when(userPreferencesRepository.save(existingPrefs)).thenReturn(existingPrefs);

        PreferencesResponse response = userPreferencesService.upsert(userData, incomingSettings);

        Map<String, Object> resultSettings = response.settings();

        assertEquals("dark", resultSettings.get("theme"));

        assertEquals(50, resultSettings.get("volume"));

        assertEquals(true, resultSettings.get("notifications"));

        verify(userPreferencesRepository).save(existingPrefs);
    }
}