package get.wordy.model;

public record SentenceResponse(
        String originalSentence,
        String matchedWords,
        String replacedSentence
) {

}
