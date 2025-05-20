package get.wordy.model.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ConditionalScheduleValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ConditionalScheduleValidation {
    String message() default "Invalid request data for given schedule type";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
