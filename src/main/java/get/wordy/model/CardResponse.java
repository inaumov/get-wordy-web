package get.wordy.model;

import get.wordy.core.api.bean.CardStatus;

import java.time.Instant;
import java.util.List;

public record CardResponse(
        int cardId,
        int wordId,
        WordResponse word,
        List<SentenceResponse> sentences,
        List<String> collocations,
        CardStatus status,
        int score,
        Instant createdAt
) {

}