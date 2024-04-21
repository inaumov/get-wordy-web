package get.wordy.model;

import java.util.List;

public record CardRequest(
        WordRequest word,
        List<String> sentences,
        List<String> collocations
) {
    public String getKeyword() {
        return this.word.value();
    }
}