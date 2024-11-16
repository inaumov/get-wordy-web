package get.wordy.model;

public record WordsheetListItem(
        int wordsheetId,
        String name,
        int wordsTotal,
        boolean isShared
) {
}
