package demos.springdata.fitmanage.domain.dto.auth.response;

import java.util.Map;

public class ApiResponse<T> {
    private boolean success;
    private String message;
    private String errorCode;
    private T data;
    private Map<String, String> validationErrors;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Map<String, String> getValidationErrors() {
        return validationErrors;
    }

    public void setValidationErrors(Map<String, String> validationErrors) {
        this.validationErrors = validationErrors;
    }


    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setData(data);
        response.setMessage("Success");
        response.setErrorCode(null);
        response.setValidationErrors(null);
        return response;
    }

    
    public static <T> ApiResponse<T> failure(String message, String errorCode) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setMessage(message);
        response.setErrorCode(errorCode);
        response.setData(null);
        response.setValidationErrors(null);
        return response;
    }

    public static <T> ApiResponse<T> failure(String message, String errorCode, Map<String, String> errors) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setMessage(message);
        response.setErrorCode(errorCode);
        response.setData(null);
        response.setValidationErrors(errors);
        return response;
    }


}
