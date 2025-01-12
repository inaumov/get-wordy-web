package get.wordy.model;

import lombok.*;

import java.util.List;

@Getter
@Builder
public class WordSearchResponse {

    private String value;
    private String transcription;
    private List<Explanation> explanations;
}
