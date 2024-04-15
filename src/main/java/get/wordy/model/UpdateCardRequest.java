package get.wordy.model;

import jakarta.validation.constraints.Min;

import java.util.List;

public record UpdateCardRequest(
        @Min(value = 1, message = "cardId: required and must be greater then 0")
        int cardId,
        @Min(value = 1, message = "wordId: required and must be greater then 0")
        int wordId,
        WordRequest word,
        List<String> sentences,
        List<String> collocations
) {
}