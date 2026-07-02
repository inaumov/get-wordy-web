package get.wordy.ai.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetExplanationResult {

    private String lemma;
    private String transcription;
    private List<Explanation> explanations;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Explanation {
        @JsonProperty("part_of_speech")
        private String partOfSpeech;
        private String meaning;
        private String register;
        private String domain;
        private List<String> sentences;
        private List<String> collocations;
        private String source;
        private String level;
    }

}