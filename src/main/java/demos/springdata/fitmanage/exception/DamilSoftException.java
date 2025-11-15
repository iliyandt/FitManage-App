package demos.springdata.fitmanage.exception;


import org.springframework.http.HttpStatus;

public class DamilSoftException extends RuntimeException {
    private final HttpStatus errorCode;

    public DamilSoftException(String message, HttpStatus errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public DamilSoftException(String message, Throwable cause, HttpStatus errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public HttpStatus getErrorCode() {
        return errorCode;
    }
}

