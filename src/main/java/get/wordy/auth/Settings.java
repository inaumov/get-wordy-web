package get.wordy.auth;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;

public record Settings(

        @Positive
        @Min(1) @Max(100)
        int cardsLimitExercise,

        Boolean schoolUpdatesEnabled
) {
}
