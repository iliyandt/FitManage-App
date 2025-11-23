package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.training.TrainingRequest;
import demos.springdata.fitmanage.domain.dto.training.TrainingResponse;
import demos.springdata.fitmanage.domain.entity.Training;
import demos.springdata.fitmanage.domain.entity.User;
import demos.springdata.fitmanage.exception.DamilSoftException;
import demos.springdata.fitmanage.repository.TrainingRepository;
import demos.springdata.fitmanage.security.UserData;
import demos.springdata.fitmanage.service.TrainingService;
import demos.springdata.fitmanage.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

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

        return createTrainingResponse(training, false);
    }



    @Override
    @Transactional
    public TrainingResponse update(UUID id, TrainingRequest update) {
        Training training = trainingRepository.findById(id).orElseThrow(() -> new DamilSoftException("Training not found.", HttpStatus.NOT_FOUND));

        updateTrainingEntity(update, training);

        Training updatedTraining = trainingRepository.save(training);

        return createTrainingResponse(updatedTraining, false);
    }


    @Override
    @Transactional
    public void delete(UUID id) {
        Training training = trainingRepository.findById(id)
                .orElseThrow(() -> new DamilSoftException("Training not found.", HttpStatus.NOT_FOUND));
        trainingRepository.delete(training);
    }

    @Override
    @Transactional
    public void joinTraining(UserData user, UUID trainingId) {

        User currentUser = userService.findUserById(user.getId());
        Training training = trainingRepository.findByIdAndTenant(trainingId, currentUser.getTenant())
                .orElseThrow(() -> new DamilSoftException("Training not found.", HttpStatus.NOT_FOUND));

        training.getParticipants().add(currentUser);
    }

    @Override
    @Transactional
    public void cancelTraining(UserData user, UUID trainingId) {
        User currentUser = userService.findUserById(user.getId());
        Training training = trainingRepository.findByIdAndTenant(trainingId, currentUser.getTenant())
                .orElseThrow(() -> new DamilSoftException("Training not found.", HttpStatus.NOT_FOUND));

        training.getParticipants().remove(currentUser);
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

                    return createTrainingResponse(training, joined);
                })
                .toList();
    }

    @Override
    public Training save(Training training) {
        return trainingRepository.save(training);
    }

    @Override
    public Set<Training> findAllByTrainer(User foundUser) {
        return trainingRepository.findAllByTrainer(foundUser);
    }


    private static TrainingResponse createTrainingResponse(Training training, boolean joined) {
        return new TrainingResponse
                (
                        training.getId(),
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
    }

    private void updateTrainingEntity(TrainingRequest update, Training training) {
        if (update.trainer() != null) {
            User trainer = userService.findUserById(update.trainer());
            training.setTrainer(trainer);
        }

        training.setTitle(update.title() != null ? update.title() : training.getTitle());
        training.setCategory(update.category() != null ? update.category() : training.getCategory());
        training.setLocation(update.location() != null ? update.location() : training.getLocation());
        training.setDate(update.date() != null ? update.date() : training.getDate());
        training.setDuration(update.duration() != null ? update.duration() : training.getDuration());
        training.setCapacity(update.capacity() != null ? update.capacity() : training.getCapacity());
    }


}
