package get.wordy.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CardRequestTest {

    @Test
    void validCardRequest() {
        WordRequest word = new WordRequest("noun", "noun", null, "meaning");
        CardRequest validRequest = new CardRequest(word, null, null);
        Validator validator;
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
        Set<ConstraintViolation<CardRequest>> violations = validator.validate(validRequest);
        assertTrue(violations.isEmpty());
    }

    @Test
    void invalidCardRequest() {
        CardRequest invalidRequest = new CardRequest(null, null, null);
        Validator validator;
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
        Set<ConstraintViolation<CardRequest>> violations = validator.validate(invalidRequest);
        assertFalse(violations.isEmpty());
    }

    @ParameterizedTest
    @NullAndEmptySource
    void invalidWordValue(String wordValue) {
        WordRequest word = new WordRequest(wordValue, "noun", null, "meaning");
        CardRequest invalidRequest = new CardRequest(word, null, null);
        Validator validator;
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
        Set<ConstraintViolation<CardRequest>> violations = validator.validate(invalidRequest);
        assertFalse(violations.isEmpty());
    }

    @ParameterizedTest
    @NullAndEmptySource
    void invalidPartOfSpeech(String partOfSpeechValue) {
        WordRequest word = new WordRequest("noun", partOfSpeechValue, null, "meaning");
        CardRequest invalidRequest = new CardRequest(word, null, null);
        Validator validator;
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
        Set<ConstraintViolation<CardRequest>> violations = validator.validate(invalidRequest);
        assertFalse(violations.isEmpty());
    }

    @ParameterizedTest
    @NullAndEmptySource
    void invalidMeaning(String meaningValue) {
        WordRequest word = new WordRequest("noun", "noun", null, meaningValue);
        CardRequest invalidRequest = new CardRequest(word, null, null);
        Validator validator;
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
        Set<ConstraintViolation<CardRequest>> violations = validator.validate(invalidRequest);
        assertFalse(violations.isEmpty());
    }

}
