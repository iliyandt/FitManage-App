package demos.springdata.fitmanage.service;
import demos.springdata.fitmanage.domain.entity.User;

public interface EmailService {
    void sendInitialPassword(User user, String initialPassword);
    void sendVerificationEmail(User user);
}
