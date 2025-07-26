package demos.springdata.fitmanage.advice;


import demos.springdata.fitmanage.domain.dto.auth.response.ApiResponse;
import demos.springdata.fitmanage.exception.FitManageAppException;
import demos.springdata.fitmanage.exception.MultipleValidationException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;


@RestControllerAdvice
public class ApplicationExceptionHandler {

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<?> handleInvalidArgument(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->{
            errors.put(error.getField(), error.getDefaultMessage());
        });
        return ApiResponse.failure(ex.getMessage(), "BAD_REQUEST", errors);
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(ConstraintViolationException.class)
    public ApiResponse<?> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            String field = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            errors.put(field, message);
        });
        return ApiResponse.failure(ex.getMessage(), "BAD_REQUEST", errors);
    }




    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(FitManageAppException.class)
    public ApiResponse<?> handleFitManageAppException(FitManageAppException ex) {
        return ApiResponse.failure(ex.getMessage(), ex.getErrorCode().name());
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(MultipleValidationException.class)
    public ApiResponse<?> handleMultipleValidationException(MultipleValidationException ex) {
        return ApiResponse.failure(ex.getMessage(), "BAD_REQUEST", ex.getErrors());
    }


}
