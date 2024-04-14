package get.wordy.model;

import get.wordy.core.api.bean.CardStatus;

import java.time.Instant;
import java.util.List;

public record CardListResponse(
        int cardId,
        int wordId,
        WordResponse word,
        List<String> sentences,
        List<String> collocations,
        CardStatus status,
        int score,
        Instant createdAt
) {

}