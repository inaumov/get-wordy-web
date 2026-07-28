package get.wordy.core;

import get.wordy.ai.IVocabularyEnrichmentService;
import get.wordy.ai.model.GetExplanationResult;
import get.wordy.ai.model.WordDto;
import get.wordy.core.api.IWordExplanationService;
import get.wordy.core.api.bean.ExistingWordLookup;
import get.wordy.core.api.bean.ThemeStatus;
import get.wordy.core.api.bean.Word;
import get.wordy.core.api.bean.WordKey;
import get.wordy.core.api.id.OwnerId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerErrorException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ThemeDraftConfirmationService {

    private final ThemeService themeService;
    private final ThemeServiceWrapper themeServiceWrapper;
    private final IVocabularyEnrichmentService enrichmentService;
    private final IWordExplanationService wordExplanationService;

    public void confirmDraft(OwnerId ownerId, int themeId) {
        try {
            themeService.updateThemeStatus(ownerId, themeId, ThemeStatus.PROCESSING);
            List<WordDto> draftWords = themeServiceWrapper.getCandidateWords(ownerId, themeId);
            log.info("Vocabulary enrichment for theme={} has been requested", themeId);

            List<WordKey> candidateKeys = draftWords.stream()
                    .map(WordDto::getKey)
                    .toList();

            List<ExistingWordLookup> lookup = wordExplanationService.lookupWords(candidateKeys);

            List<WordKey> missingKeys = lookup.stream()
                    .filter(l -> !l.exists())
                    .map(ExistingWordLookup::key)
                    .toList();

            if (!missingKeys.isEmpty()) {
                List<GetExplanationResult> results = enrichmentService.multisearch(missingKeys);
                results.forEach(this::saveExplanations);
            }
            log.info("AI multisearch has been finished for theme={}", themeId);

            List<Integer> wordIds = wordExplanationService.findExistingWords(candidateKeys)
                    .stream()
                    .map(ExistingWordLookup::id)
                    .toList();

            themeService.addWordsToTheme(ownerId, themeId, wordIds);
            themeService.updateThemeStatus(ownerId, themeId, ThemeStatus.READY);
            log.info("Theme={} has been confirmed", themeId);
        } catch (Exception ex) {
            log.error("Theme confirmation failed for theme={}", themeId, ex);
            throw new ServerErrorException("Theme confirmation failed for theme", ex);
        }
    }

    private void saveExplanations(GetExplanationResult result) {
        result.getExplanations().forEach(explanation -> {
            Word entity = new Word(
                    result.getLemma(),
                    explanation.getPartOfSpeech(),
                    result.getTranscription(),
                    explanation.getMeaning(),
                    explanation.getLevel()
            );
            entity.setRegister(explanation.getRegister());
            entity.setDomain(explanation.getDomain());
            entity.setCollocations(explanation.getCollocations());
            entity.setStrSentences(explanation.getSentences());
            wordExplanationService.addWordExplanation(entity);
        });
        log.info("Saved {} explanations for '{}'", result.getExplanations().size(), result.getLemma());
    }

}
