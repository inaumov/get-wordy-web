package get.wordy.model.themes;

import jakarta.validation.constraints.NotBlank;

public record ThemeRequest(
        @NotBlank
        String name
) {
}
