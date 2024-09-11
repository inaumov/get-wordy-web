package get.wordy.model.error;

import java.util.ArrayList;
import java.util.List;

public class ValidationErrorResponse {

    private final String errorCode;
    private final List<Violation> violations = new ArrayList<>();

    public ValidationErrorResponse(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public List<Violation> getViolations() {
        return violations;
    }

    public record Violation(String fieldName, String errorMessage) {
    }

}
