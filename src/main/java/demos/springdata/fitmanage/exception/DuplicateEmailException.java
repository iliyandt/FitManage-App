package demos.springdata.fitmanage.exception;

import java.util.Map;

public class DuplicateEmailException extends RuntimeException{
    private final Map<String, String> errors;

    public DuplicateEmailException(Map<String, String> errors) {
        super("Validation failed");
        this.errors = errors;
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}
