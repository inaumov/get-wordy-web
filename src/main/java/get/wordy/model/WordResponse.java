package get.wordy.model;

public record WordResponse(
        int wordId,
        String value,
        String transcription,
        Explanation explanation
) {

}