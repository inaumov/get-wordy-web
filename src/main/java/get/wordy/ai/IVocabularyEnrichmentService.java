package get.wordy.ai;

import get.wordy.ai.model.GetExplanationResult;
import get.wordy.ai.model.ThemeResult;
import get.wordy.core.api.bean.WordKey;

import java.util.List;

public interface IVocabularyEnrichmentService {

    GetExplanationResult search(String input);

    ThemeResult generate(String theme);

    List<GetExplanationResult> enrich(List<WordKey> words);

}
