package get.wordy.model;

public record WordResponse(
        int wordId,
        String value,
        String partOfSpeech,
        String transcription,
        String meaning
) {

}