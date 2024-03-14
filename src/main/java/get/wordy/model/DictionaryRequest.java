package get.wordy.model;

import jakarta.validation.constraints.NotBlank;

public record DictionaryRequest(
        int id,
        @NotBlank(message = "Name: mandatory")
        String name,
        String picture
) {

}