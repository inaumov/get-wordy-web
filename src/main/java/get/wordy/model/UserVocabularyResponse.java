package get.wordy.model;

import java.time.Instant;

public record UserVocabularyResponse(
        int vocabId,
        String name,
        String pictureUrl,
        VocabType accessType,
        int wordsTotal,
        int learningProgress,
        Instant updateTime
) {
}
