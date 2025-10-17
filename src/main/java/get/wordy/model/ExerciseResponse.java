package get.wordy.model;

import java.util.List;

public record ExerciseResponse(
        Integer wordId,
        String lemma,
        Explanation explanation,
        List<SentenceResponse> exerciseSentences
) {

}
