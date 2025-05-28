package demos.springdata.fitmanage.util;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import java.util.Set;

public class ValidationUtilImpl implements ValidationUtil{
    private final Validator validator;

    public ValidationUtilImpl() {
        this.validator = Validation
                .buildDefaultValidatorFactory()
                .getValidator();
    }

    @Override
    public <T> boolean isValid(T entity) {
        return this.validator.validate(entity).isEmpty();
    }

    @Override
    public <T> Set<ConstraintViolation<T>> violations(T entity) {
        return this.validator.validate(entity);
    }
}
