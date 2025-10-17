package get.wordy.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Explanation {
    private Integer wordId;
    @NotBlank
    private String partOfSpeech;
    @NotBlank
    private String meaning;
    private String register;
    private String domain;
    private List<String> inContext;
    private List<String> collocations;
}
