package get.wordy.model;

import java.util.List;

public record ExerciseResponse(
        Integer wordId,
        String value,
        Explanation explanation,
        List<SentenceResponse> exerciseSentences
) {

}
