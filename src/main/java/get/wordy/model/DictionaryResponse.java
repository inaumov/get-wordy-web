package get.wordy.model;

public record DictionaryResponse(
        int dictionaryId,
        String name,
        String picture,
        int cardsTotal
) {
}
