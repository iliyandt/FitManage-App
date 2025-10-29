package demos.springdata.fitmanage.util;

import lombok.experimental.UtilityClass;

import java.util.Random;

@UtilityClass
public class SecurityCodeGenerator {
    private final Random random = new Random();

    public static String generateDefaultPassword() {
        return "InitialPass" + System.currentTimeMillis() + "!";
    }

    public static String generateVerificationCode() {
        int code = random.nextInt(900_000) + 100_000;
        return String.valueOf(code);
    }
}
