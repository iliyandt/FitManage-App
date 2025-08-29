package demos.springdata.fitmanage.util;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class UserSecurityUtils {
    private final Random random = new Random();

    public String generateDefaultPassword() {
        return "InitialPass" + System.currentTimeMillis() + "!";
    }

    public String generateVerificationCode() {
        int code = random.nextInt(900_000) + 100_000;
        return String.valueOf(code);
    }
}
