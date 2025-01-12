package get.wordy.model;

import java.util.List;

public record ExerciseResponse(
        int cardId,
        String value,
        Explanation explanation,
        List<SentenceResponse> exerciseSentences
) {

}
