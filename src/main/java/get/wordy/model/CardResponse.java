package get.wordy.model;

import get.wordy.core.api.bean.CardStatus;

public record CardResponse(
        Integer wordId,
        CardStatus status,
        int score,
        String value,
        String transcription,
        Explanation explanation
) {

}