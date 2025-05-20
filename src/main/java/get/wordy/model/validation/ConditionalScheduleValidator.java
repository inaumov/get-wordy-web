package get.wordy.model.validation;

import get.wordy.model.ClassInfoRequest;
import get.wordy.model.ScheduleType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.CollectionUtils;

public class ConditionalScheduleValidator implements ConstraintValidator<ConditionalScheduleValidation, ClassInfoRequest> {

    @Override
    public boolean isValid(ClassInfoRequest request, ConstraintValidatorContext context) {
        if (request == null) {
            return true; // Handled by @NotNull elsewhere if needed
        }

        boolean valid = true;
        context.disableDefaultConstraintViolation();

        if (request.getScheduleType() == ScheduleType.REPEATABLE) {
            if (CollectionUtils.isEmpty(request.getTimeSlots())) {
                context.buildConstraintViolationWithTemplate("Time slots are required for REPEATABLE type")
                        .addPropertyNode("timeSlots")
                        .addConstraintViolation();
                valid = false;
            }
        }

        if (request.getScheduleType() == ScheduleType.ONE_TIME) {
            if (request.getEndDate() == null) {
                context.buildConstraintViolationWithTemplate("End date is required for ONE_TIME type")
                        .addPropertyNode("endDate")
                        .addConstraintViolation();
                valid = false;
            }
        }

        return valid;
    }
}
