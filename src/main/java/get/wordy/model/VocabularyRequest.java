package get.wordy.model;

public record VocabularyRequest(
        String name,
        Boolean isShared
) {
}
