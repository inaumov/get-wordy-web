package get.wordy.model;

import java.util.List;

public record WordsheetItemRequest(
        int wordId,
        WordRequest word,
        List<String> sentences,
        List<String> collocations
) {

}