package get.wordy.model;

public record WordResponse(
        int wordId,
        String lemma,
        String transcription,
        Explanation explanation
) {

}