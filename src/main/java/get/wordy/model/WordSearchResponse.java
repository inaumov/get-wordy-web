package get.wordy.model;

import lombok.*;

import java.util.List;

@Getter
@Builder
public class WordSearchResponse {

    private String value;
    private String transcription;
    private List<ExplanationResponse> explanations;

    @Getter
    @Builder
    public static class ExplanationResponse {
        private String partOfSpeech;
        private String meaning;
        private List<String> inContext;
        private List<String> collocations;
    }

}
