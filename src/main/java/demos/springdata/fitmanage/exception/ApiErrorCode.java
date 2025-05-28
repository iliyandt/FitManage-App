package demos.springdata.fitmanage.exception;

import org.apache.http.HttpStatus;

public enum ApiErrorCode {
    BAD_REQUEST(HttpStatus.SC_BAD_REQUEST), INTERNAL_ERROR(HttpStatus.SC_INTERNAL_SERVER_ERROR), NOT_FOUND(HttpStatus.SC_NOT_FOUND), CONFLICT(HttpStatus.SC_CONFLICT);

    private final int statusCode;

    ApiErrorCode(int errorCode) {
        statusCode = errorCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}

