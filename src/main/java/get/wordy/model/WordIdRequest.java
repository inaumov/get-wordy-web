package get.wordy.model;

import jakarta.validation.constraints.Positive;

public record WordIdRequest(
        @Positive
        int wordId
) {

}