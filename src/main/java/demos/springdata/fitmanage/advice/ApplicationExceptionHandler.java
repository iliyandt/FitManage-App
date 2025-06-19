package demos.springdata.fitmanage.advice;


import demos.springdata.fitmanage.exception.FitManageAppException;
import demos.springdata.fitmanage.exception.MultipleValidationException;
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
    public Map<String, String> handleInvalidArgument(MethodArgumentNotValidException ex) {
        Map<String, String> errorMap = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->{
            errorMap.put(error.getField(), error.getDefaultMessage());
        });
        return errorMap;
    }

    @ExceptionHandler(FitManageAppException.class)
    @ResponseStatus(HttpStatus.OK)
    public Map<String, String> handleFitManageAppException(FitManageAppException ex) {
        return Map.of("error", ex.getMessage());
    }

    @ExceptionHandler(MultipleValidationException.class)
    @ResponseStatus(HttpStatus.OK)
    public Map<String, String> handleMultipleValidationException(MultipleValidationException ex) {
        return ex.getErrors();
    }
}
