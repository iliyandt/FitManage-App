package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.training.TrainingRequest;
import demos.springdata.fitmanage.domain.dto.training.TrainingResponse;
import demos.springdata.fitmanage.domain.entity.Tenant;
import demos.springdata.fitmanage.domain.entity.Training;
import demos.springdata.fitmanage.domain.entity.User;
import demos.springdata.fitmanage.exception.DamilSoftException;
import demos.springdata.fitmanage.repository.TrainingRepository;
import demos.springdata.fitmanage.security.UserData;
import demos.springdata.fitmanage.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
class TrainingServiceImplTest {

    @Mock
    private TrainingRepository trainingRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private TrainingServiceImpl trainingService;


    private UserData currentUserData;
    private User currentUserEntity;
    private User trainerEntity;
    private Tenant tenant;
    private UUID trainingId;
    private UUID trainerId;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        trainerId = UUID.randomUUID();
        trainingId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();

        tenant = new Tenant();
        tenant.setId(tenantId);
        tenant.setName("Damilsoft Gym");

        currentUserEntity = new User();
        currentUserEntity.setId(userId);
        currentUserEntity.setFirstName("Iliyan");
        currentUserEntity.setLastName("K");
        currentUserEntity.setTenant(tenant);

        currentUserData = mock(UserData.class);
        lenient().when(currentUserData.getId()).thenReturn(userId);

        trainerEntity = new User();
        trainerEntity.setId(trainerId);
        trainerEntity.setFirstName("Damyan");
        trainerEntity.setLastName("K");
    }


    @Test
    void update_ShouldThrow_WhenTrainingNotFound() {
        TrainingRequest request = new TrainingRequest(trainerId,"Title", "Loc", "Cat", LocalDateTime.now(), 60, 10);
        when(trainingRepository.findById(trainingId)).thenReturn(Optional.empty());

        DamilSoftException ex = assertThrows(DamilSoftException.class, () ->
                trainingService.update(trainingId, request));

        assertEquals(HttpStatus.NOT_FOUND, ex.getErrorCode());
    }


    @Test
    void delete_ShouldCallRepositoryDelete() {
        Training training = new Training();
        when(trainingRepository.findById(trainingId)).thenReturn(Optional.of(training));

        trainingService.delete(trainingId);

        verify(trainingRepository).delete(training);
    }


    @Test
    void joinTraining_ShouldAddUserToParticipants() {
        Training training = new Training();
        training.setId(trainingId);
        training.setParticipants(new HashSet<>());
        when(userService.findUserById(userId)).thenReturn(currentUserEntity);
        when(trainingRepository.findByIdAndTenant(trainingId, tenant)).thenReturn(Optional.of(training));

        trainingService.joinTraining(currentUserData, trainingId);

        assertTrue(training.getParticipants().contains(currentUserEntity));
    }

    @Test
    void cancelTraining_ShouldRemoveUserFromParticipants() {
        Training training = new Training();
        training.setId(trainingId);
        training.setParticipants(new HashSet<>());
        training.getParticipants().add(currentUserEntity);

        when(userService.findUserById(userId)).thenReturn(currentUserEntity);
        when(trainingRepository.findByIdAndTenant(trainingId, tenant)).thenReturn(Optional.of(training));

        trainingService.cancelTraining(currentUserData, trainingId);

        assertFalse(training.getParticipants().contains(currentUserEntity));
    }

    @Test
    void joinTraining_ShouldThrow_WhenTrainingFromDifferentTenant() {
        when(userService.findUserById(userId)).thenReturn(currentUserEntity);
        when(trainingRepository.findByIdAndTenant(trainingId, tenant)).thenReturn(Optional.empty());

        assertThrows(DamilSoftException.class, () ->
                trainingService.joinTraining(currentUserData, trainingId));
    }

    @Test
    void findAllByTrainer_ShouldDelegateToRepo() {
        trainingService.findAllByTrainer(trainerEntity);
        verify(trainingRepository).findAllByTrainer(trainerEntity);
    }
}