package demos.springdata.fitmanage.exception;

public class FitManageAppException extends RuntimeException{
    private final ApiErrorCode errorCode;

    public FitManageAppException(String message, ApiErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
    public FitManageAppException(ApiErrorCode errorCode, String message) {
        this(errorCode, message, null);
    }

    public FitManageAppException(ApiErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public int getStatusCode() {
        return errorCode.getStatusCode();
    }
}

