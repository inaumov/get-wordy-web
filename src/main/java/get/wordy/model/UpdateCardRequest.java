package get.wordy.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record UpdateCardRequest(
        @Min(value = 1)
        int cardId,
        @Min(value = 1)
        int wordId,
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