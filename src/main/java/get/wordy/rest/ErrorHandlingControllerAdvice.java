package get.wordy.rest;

import get.wordy.model.error.ValidationErrorResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
class ErrorHandlingControllerAdvice {

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    ValidationErrorResponse handleConstraintValidationException(ConstraintViolationException e) {
        ValidationErrorResponse errorResponse = new ValidationErrorResponse(HttpStatus.BAD_REQUEST.getReasonPhrase());
        for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
            errorResponse.getViolations().add(
                    new ValidationErrorResponse.Violation(violation.getPropertyPath().toString(), violation.getMessage()));
        }
        return errorResponse;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    ValidationErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        ValidationErrorResponse errorResponse = new ValidationErrorResponse(HttpStatus.BAD_REQUEST.getReasonPhrase());
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            ValidationErrorResponse.Violation error = new ValidationErrorResponse.Violation(fieldError.getField(), "Not valid due to validation error: " + fieldError.getDefaultMessage());
            errorResponse.getViolations().add(error);
        }
        return errorResponse;
    }

}
