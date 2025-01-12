package get.wordy.model;

import get.wordy.core.api.bean.CardStatus;

public record CardResponse(
        int cardId,
        CardStatus status,
        int score,
        String value,
        Explanation explanation
) {

}