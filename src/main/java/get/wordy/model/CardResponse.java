package get.wordy.model;

import get.wordy.core.api.bean.Card;
import get.wordy.core.api.bean.CardStatus;
import get.wordy.core.api.bean.Progress;
import get.wordy.core.api.bean.Word;

public record CardResponse(
        Integer wordId,
        CardStatus status,
        int score,
        String value,
        String transcription,
        Explanation explanation
) {

    public static CardResponse fromCard(Card card) {
        Word word = card.getWord();
        Progress progress = card.getProgress();
        return new CardResponse(
                card.getWordId(),
                progress == null ? CardStatus.UNSEEN : progress.getStatus(),
                progress == null ? 0 : progress.getScore(),
                word.getValue(),
                word.getTranscription(),
                Explanation.builder()
                        .partOfSpeech(word.getPartOfSpeech())
                        .meaning(word.getMeaning())
                        .inContext(word.getStrSentences())
                        .collocations(word.getCollocations())
                        .build()
        );
    }

}