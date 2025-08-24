package get.wordy.auth;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxUploadSize(MaxUploadSizeExceededException ex,
                                      RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("fileError", "File too large! Max allowed size is 1MB.");
        return "redirect:/onboarding";
    }

}
