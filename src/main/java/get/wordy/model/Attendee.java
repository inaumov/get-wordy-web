package get.wordy.model;

import jakarta.validation.constraints.NotBlank;

public record Attendee(
        @NotBlank
        String userIdentity
) {
}
