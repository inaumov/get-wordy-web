package get.wordy.model;

public record VocabularyResponse(
        int vocabId,
        String name,
        int wordsTotal,
        boolean isShared
) {
}
