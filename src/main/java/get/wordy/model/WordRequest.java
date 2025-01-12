package get.wordy.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class WordRequest {
    @NotBlank
    private String value;

    private String transcription;

    @Valid
    @NotNull
    private Explanation explanation;
}