package get.wordy.model;

import get.wordy.core.api.bean.CardStatus;

import java.time.Instant;
import java.util.List;

public record CardResponse(
        int cardId,
        WordResponse word,
        List<String> examples,
        List<String> collocations,
        CardStatus status,
        int score,
        Instant createdAt
) {

}