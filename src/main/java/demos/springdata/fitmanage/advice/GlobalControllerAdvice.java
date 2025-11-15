package demos.springdata.fitmanage.advice;
import demos.springdata.fitmanage.domain.dto.error.ErrorResponse;
import demos.springdata.fitmanage.exception.DamilSoftException;
import demos.springdata.fitmanage.exception.MultipleValidationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleInvalidArgument(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorResponse body = ErrorResponse.of(
                status.value(),
                status.getReasonPhrase(),
                "Validation failed",
                request.getRequestURI(),
                errors
        );
        return ResponseEntity.status(status).body(body);
    }


    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();

        ex.getConstraintViolations().forEach(violation ->
                errors.put(violation.getPropertyPath().toString(), violation.getMessage())
        );

        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorResponse body = ErrorResponse.of(
                status.value(),
                status.getReasonPhrase(),
                "Validation failed",
                request.getRequestURI(),
                errors
        );

        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(DamilSoftException.class)
    public ResponseEntity<ErrorResponse> handleDamilSoftException(DamilSoftException ex, HttpServletRequest request) {
        HttpStatus status = ex.getErrorCode();

        ErrorResponse body = ErrorResponse.of(
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(MultipleValidationException.class)
    public ResponseEntity<ErrorResponse> handleMultipleValidationException(MultipleValidationException ex,
                                                                           HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorResponse body = ErrorResponse.of(
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI(),
                ex.getErrors()
        );
        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ErrorResponse body = ErrorResponse.of(
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(body);
    }


}
