package get.wordy.core;

import get.wordy.ai.IVocabularyEnrichmentService;
import get.wordy.ai.model.GetExplanationResult;
import get.wordy.ai.model.WordDto;
import get.wordy.core.api.IWordExplanationService;
import get.wordy.core.api.bean.ThemeStatus;
import get.wordy.core.api.bean.Word;
import get.wordy.core.api.bean.WordKey;
import get.wordy.core.api.id.OwnerId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerErrorException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ThemeDraftConfirmationService {

    private final ThemeService themeService;
    private final ThemeServiceWrapper themeServiceWrapper;
    private final IVocabularyEnrichmentService enrichmentService;
    private final IWordExplanationService wordExplanationService;

    public void confirmDraft(OwnerId ownerId, int themeId) {
        themeService.updateThemeStatus(ownerId, themeId, ThemeStatus.PROCESSING);
        try {
            List<WordDto> draftWords = themeServiceWrapper.getCandidateWords(ownerId, themeId);
            log.info("Vocabulary enrichment for theme={} has been requested", themeId);

            List<WordKey> candidateKeys = draftWords.stream()
                    .map(w -> new WordKey(w.lemma(), w.partOfSpeech()))
                    .toList();

            List<WordKey> missingKeys = findMissingWords(candidateKeys);
            if (!missingKeys.isEmpty()) {
                List<String> missingLemmas = missingKeys
                        .stream()
                        .map(WordKey::lemma)
                        .toList();
                List<GetExplanationResult> results = enrichmentService.multisearch(missingLemmas);
                results.forEach(this::saveExplanations);
            }
            log.info("AI multisearch has been finished for theme={}", themeId);

            List<Integer> wordIds = themeService.findExistingWords(candidateKeys)
                    .stream()
                    .map(Word::getId)
                    .toList();

            themeService.addWordsToTheme(ownerId, themeId, wordIds);
            themeService.updateThemeStatus(ownerId, themeId, ThemeStatus.READY);
            log.info("Theme={} has been confirmed", themeId);
        } catch (Exception ex) {
            log.error("Theme confirmation failed for theme={}", themeId, ex);
            throw new ServerErrorException("Theme confirmation failed for theme", ex);
        }
    }

    private List<WordKey> findMissingWords(List<WordKey> candidateKeys) {
        Set<WordKey> existing = themeService.findExistingWords(candidateKeys)
                .stream()
                .map(word -> new WordKey(word.getLemma(), word.getPartOfSpeech()))
                .collect(Collectors.toSet());

        return candidateKeys
                .stream()
                .filter(key -> !existing.contains(key))
                .toList();
    }

    private void saveExplanations(GetExplanationResult result) {
        result.getExplanations().forEach(explanation -> {
            Word entity = new Word(
                    result.getLemma(),
                    explanation.getPartOfSpeech(),
                    result.getTranscription(),
                    explanation.getMeaning()
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
