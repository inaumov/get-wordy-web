package get.wordy.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public record TimeSlot(
        @NotBlank
        String dayOfWeek,
        @JsonFormat(pattern = "HH:mm")
        @NotNull
        LocalTime startTime,
        @JsonFormat(pattern = "HH:mm")
        @NotNull
        LocalTime endTime
) {

}
