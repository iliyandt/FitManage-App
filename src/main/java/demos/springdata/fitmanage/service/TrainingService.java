package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.training.TrainingRequest;
import demos.springdata.fitmanage.domain.dto.training.TrainingResponse;
import demos.springdata.fitmanage.domain.entity.Training;
import demos.springdata.fitmanage.domain.entity.User;
import demos.springdata.fitmanage.security.UserData;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface TrainingService {
    TrainingResponse create(UserData user, TrainingRequest request);
    TrainingResponse update(UUID id, TrainingRequest update);
    void delete(UUID id);
    void joinTraining(UserData user, UUID trainingId);
    void cancelTraining(UserData user, UUID trainingId);
    List<TrainingResponse> getTrainings(UserData user);
    Training save(Training training);

    Set<Training> findAllByTrainer(User foundUser);
}
