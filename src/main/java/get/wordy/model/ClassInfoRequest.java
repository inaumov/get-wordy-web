package get.wordy.model;

import get.wordy.model.validation.ConditionalScheduleValidation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ConditionalScheduleValidation
public class ClassInfoRequest {

    @NotBlank
    private String name;
    private String format;
    private String notes;
    @NotNull
    private ScheduleType scheduleType;
    private LocalDate endDate;
    @Valid
    private List<TimeSlot> timeSlots;
}
