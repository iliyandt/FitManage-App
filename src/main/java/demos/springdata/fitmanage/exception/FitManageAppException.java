package demos.springdata.fitmanage.exception;

public class FitManageAppException extends RuntimeException {
    private final ApiErrorCode errorCode;


    public FitManageAppException(String message, ApiErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public FitManageAppException(String message, Throwable cause, ApiErrorCode errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public ApiErrorCode getErrorCode() {
        return errorCode;
    }

    public int getStatusCode() {
        return errorCode.getStatusCode();
    }
}

