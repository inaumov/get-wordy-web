package get.wordy.model;

import jakarta.validation.constraints.NotBlank;

public record DictionaryRequest(
        int id,
        @NotBlank
        String name,
        String picture
) {

}