package demos.springdata.fitmanage.validation;

import demos.springdata.fitmanage.exception.MultipleValidationException;
import demos.springdata.fitmanage.repository.GymMemberRepository;
import demos.springdata.fitmanage.repository.GymRepository;
import demos.springdata.fitmanage.repository.SuperAdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserValidationService {
    private final SuperAdminRepository superAdminRepository;
    private final GymRepository gymRepository;
    private final GymMemberRepository gymMemberRepository;

    @Autowired
    public UserValidationService(SuperAdminRepository superAdminRepo, GymRepository gymRepo, GymMemberRepository gymMemberRepo) {
        this.superAdminRepository = superAdminRepo;
        this.gymRepository = gymRepo;
        this.gymMemberRepository = gymMemberRepo;
    }

    public void checkDuplicateEmailOrThrow(String email) {
        Map<String, String> errors = new HashMap<>();
        boolean exists = false;

        if (superAdminRepository.findByEmail(email).isPresent()) {
            errors.put("message", "Admin with this email exists");
            exists = true;
        }

        if (gymRepository.findByEmail(email).isPresent()) {
            errors.put("message", "Gym with this email exists");
            exists = true;
        }

        if (gymMemberRepository.findByEmail(email).isPresent()) {
            errors.put("message", "Gym member with this email exists");
            exists = true;
        }

        if (exists) {
            throw new MultipleValidationException(errors);
        }
    }
}
