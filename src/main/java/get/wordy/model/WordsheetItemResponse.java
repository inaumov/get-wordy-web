package get.wordy.model;

import java.util.List;

public record WordsheetItemResponse(
        int wordId,
        WordResponse word,
        List<String> sentences
) {

}