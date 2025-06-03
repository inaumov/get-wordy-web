package get.wordy.model;

import java.time.LocalDateTime;

public record VocabularyResponse(
        int vocabId,
        String name,
        int wordsTotal,
        boolean isShared,
        LocalDateTime updateTime
) {
}
