package get.wordy.model;

import get.wordy.core.bean.wrapper.CardStatus;

import java.time.Instant;
import java.util.List;

public record CardResponse(
        int cardId,
        WordResponse word,
        List<String> contexts,
        List<String> collocations,
        CardStatus status,
        int score,
        Instant createdAt
) {

}