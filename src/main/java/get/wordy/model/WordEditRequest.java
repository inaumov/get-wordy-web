package get.wordy.model;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WordEditRequest extends WordRequest {

    @Min(value = 1)
    private int wordId;

    public WordEditRequest(int wordId, String lemma, String transcription, Explanation explanation) {
        super(lemma, transcription, explanation);
        this.wordId = wordId;
    }

}
