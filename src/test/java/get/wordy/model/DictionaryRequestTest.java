package get.wordy.model;

import jakarta.validation.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;

class DictionaryRequestTest {

    @ParameterizedTest
    @NullAndEmptySource
    void validateName(String name) {
        DictionaryRequest invalidRequest = new DictionaryRequest(0, name, null);
        Validator validator;
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
        Set<ConstraintViolation<DictionaryRequest>> violations = validator.validate(invalidRequest);
        assertFalse(violations.isEmpty());
    }

}
