package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.training.TrainingRequest;
import demos.springdata.fitmanage.domain.dto.training.TrainingResponse;
import demos.springdata.fitmanage.security.UserData;

import java.util.List;

public interface TrainingService {
    TrainingResponse create(UserData user, TrainingRequest request);
    List<TrainingResponse> getTrainings(UserData user);
}
