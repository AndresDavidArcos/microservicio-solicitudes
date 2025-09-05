package co.com.pragma.model.exception;

public class BusinessValidationException extends ApiException {
    public BusinessValidationException(String message) {
        super(message);
    }
}
