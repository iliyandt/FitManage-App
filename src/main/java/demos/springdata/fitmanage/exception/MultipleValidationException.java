package demos.springdata.fitmanage.exception;

import java.util.Map;

public class MultipleValidationException extends RuntimeException{
    private final Map<String, String> errors;

    public MultipleValidationException(Map<String, String> errors) {
        super("Validation failed");
        this.errors = errors;
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}
