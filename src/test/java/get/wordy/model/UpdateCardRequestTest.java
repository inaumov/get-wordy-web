package get.wordy.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UpdateCardRequestTest {

    @Test
    void validCardRequest() {
        WordRequest word = new WordRequest("noun", "noun", null, "meaning");
        UpdateCardRequest invalidRequest = new UpdateCardRequest(1, 2, word, null, null);
        Validator validator;
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
        Set<ConstraintViolation<UpdateCardRequest>> violations = validator.validate(invalidRequest);
        assertTrue(violations.isEmpty());
    }

    @Test
    void invalidIds() {
        WordRequest word = new WordRequest("noun", "noun", null, "meaning");
        UpdateCardRequest invalidRequest = new UpdateCardRequest(0, 0, word, null, null);
        Validator validator;
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
        Set<ConstraintViolation<UpdateCardRequest>> violations = validator.validate(invalidRequest);
        assertFalse(violations.isEmpty());
        assertEquals(2, violations.size());
    }

    @Test
    void invalidCardRequest() {
        UpdateCardRequest invalidRequest = new UpdateCardRequest(1, 2, null, null, null);
        Validator validator;
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
        Set<ConstraintViolation<UpdateCardRequest>> violations = validator.validate(invalidRequest);
        assertFalse(violations.isEmpty());
    }

    @ParameterizedTest
    @NullAndEmptySource
    void invalidWordValue(String wordValue) {
        WordRequest word = new WordRequest(wordValue, "noun", null, "meaning");
        UpdateCardRequest invalidRequest = new UpdateCardRequest(1, 2, word, null, null);
        Validator validator;
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
        Set<ConstraintViolation<UpdateCardRequest>> violations = validator.validate(invalidRequest);
        assertFalse(violations.isEmpty());
    }

    @ParameterizedTest
    @NullAndEmptySource
    void invalidPartOfSpeech(String partOfSpeechValue) {
        WordRequest word = new WordRequest("noun", partOfSpeechValue, null, "meaning");
        UpdateCardRequest invalidRequest = new UpdateCardRequest(1, 2, word, null, null);
        Validator validator;
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
        Set<ConstraintViolation<UpdateCardRequest>> violations = validator.validate(invalidRequest);
        assertFalse(violations.isEmpty());
    }

    @ParameterizedTest
    @NullAndEmptySource
    void invalidMeaning(String meaningValue) {
        WordRequest word = new WordRequest("noun", "noun", null, meaningValue);
        UpdateCardRequest invalidRequest = new UpdateCardRequest(1, 2, word, null, null);
        Validator validator;
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
        Set<ConstraintViolation<UpdateCardRequest>> violations = validator.validate(invalidRequest);
        assertFalse(violations.isEmpty());
    }

}
