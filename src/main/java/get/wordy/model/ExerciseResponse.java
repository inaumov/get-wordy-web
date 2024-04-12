package get.wordy.model;

import java.util.List;

public record ExerciseResponse(
        int cardId,
        int wordId,
        WordResponse word,
        List<SentenceResponse> sentences
) {

}
