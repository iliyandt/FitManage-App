package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.training.TrainingRequest;
import demos.springdata.fitmanage.domain.dto.training.TrainingResponse;
import demos.springdata.fitmanage.domain.entity.Training;
import demos.springdata.fitmanage.domain.entity.User;
import demos.springdata.fitmanage.repository.TrainingRepository;
import demos.springdata.fitmanage.security.UserData;
import demos.springdata.fitmanage.service.TrainingService;
import demos.springdata.fitmanage.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TrainingServiceImpl implements TrainingService {

    private final TrainingRepository trainingRepository;
    private final UserService userService;

    @Autowired
    public TrainingServiceImpl(TrainingRepository trainingRepository, UserService userService) {
        this.trainingRepository = trainingRepository;
        this.userService = userService;
    }

    @Override
    public TrainingResponse create(UserData user, TrainingRequest request) {

        User trainer = userService.findUserById(request.trainer());
        User currentUser = userService.findUserById(user.getId());

        Training training = new Training()
                .setTenant(currentUser.getTenant())
                .setTitle(request.title())
                .setLocation(request.location())
                .setCategory(request.category())
                .setDate(request.date())
                .setDuration(request.duration())
                .setTrainer(trainer)
                .setCapacity(request.capacity());

        trainingRepository.save(training);

        int spots = training.getCapacity() - training.getParticipants().size();

        boolean joined = training.getParticipants().stream()
                .anyMatch(participant -> participant.getId().equals(user.getId()));

        return new TrainingResponse
                (
                        training.getTitle(),
                        training.getCategory(),
                        training.getLocation(),
                        training.getDate(),
                        training.getDuration(),
                        training.getCapacity(),
                        spots,
                        String.format("%s %s",trainer.getFirstName(), trainer.getLastName()),
                        joined
                );
    }

    @Override
    @Transactional
    public List<TrainingResponse> getTrainings(UserData user) {
        User currentUser = userService.findUserById(user.getId());
        List<Training> trainings = trainingRepository.findAllByTenant(currentUser.getTenant());

        return trainings.stream()
                .map(training -> {
                    boolean joined = training.getParticipants().stream()
                            .anyMatch(participant -> participant.getId().equals(user.getId()));

                    return new TrainingResponse(
                            training.getTitle(),
                            training.getCategory(),
                            training.getLocation(),
                            training.getDate(),
                            training.getDuration(),
                            training.getCapacity(),
                            training.getCapacity() - training.getParticipants().size(),
                            String.format("%s %s", training.getTrainer().getFirstName(), training.getTrainer().getLastName()),
                            joined
                    );
                })
                .toList();
    }


}
