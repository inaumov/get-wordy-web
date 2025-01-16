package get.wordy.model;

public record DictionaryResponse(
        int vocabId,
        String name,
        String pictureUrl,
        int wordsTotal
) {
}
