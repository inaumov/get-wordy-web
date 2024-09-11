package get.wordy.model;

import jakarta.validation.constraints.NotBlank;

public record WordRequest(
        @NotBlank
        String value,
        @NotBlank
        String partOfSpeech,

        String transcription,
        @NotBlank
        String meaning
) {

}