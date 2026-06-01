package get.wordy.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WordEditRequest {

    @Min(value = 1)
    private int wordId;
    @NotBlank
    private String lemma;

    private String transcription;

    @Valid
    @NotNull
    private Explanation explanation;

}
