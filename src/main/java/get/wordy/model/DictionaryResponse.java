package get.wordy.model;

import java.time.LocalDateTime;

public record DictionaryResponse(
        int vocabId,
        String name,
        String pictureUrl,
        VocabType type,
        int wordsTotal,
        int learningProgress,
        LocalDateTime updateTime
) {
}
