package get.wordy.model;

import java.time.Instant;

public record VocabularyResponse(
        int vocabId,
        String name,
        int wordsTotal,
        boolean isShared,
        Instant updateTime
) {
}
