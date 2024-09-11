package get.wordy.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CardRequest(
        @Valid
        @NotNull
        WordRequest word,
        List<String> sentences,
        List<String> collocations
) {
    public String getKeyword() {
        return this.word.value();
    }
}