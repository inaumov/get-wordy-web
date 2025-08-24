package get.wordy.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
// optional for teachers/schools
public class OnboardingForm {

    @NotBlank(message = "School name cannot be empty")
    private String schoolName;

    private MultipartFile schoolLogo;

}
