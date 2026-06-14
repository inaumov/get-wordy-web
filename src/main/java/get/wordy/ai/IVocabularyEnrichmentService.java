package get.wordy.ai;

import get.wordy.ai.model.GetExplanationResult;
import get.wordy.ai.model.ThemeResult;

import java.util.List;

public interface IVocabularyEnrichmentService {

    GetExplanationResult search(String input);

    List<GetExplanationResult> multisearch(List<String> input);

    ThemeResult generateTheme(String theme, int candidatesLimit);

}
