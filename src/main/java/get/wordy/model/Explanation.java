package get.wordy.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class Explanation {
    @NotBlank
    private String partOfSpeech;
    @NotBlank
    private String meaning;
    private List<String> inContext;
    private List<String> collocations;
}
