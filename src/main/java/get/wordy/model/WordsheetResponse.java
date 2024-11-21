package get.wordy.model;

public record WordsheetResponse(
        int wordsheetId,
        String name,
        int wordsTotal,
        boolean isShared
) {
}
