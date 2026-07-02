package get.wordy.rest;

import get.wordy.ai.IVocabularyEnrichmentService;
import get.wordy.ai.model.GetExplanationResult;
import get.wordy.core.api.IWordExplanationService;
import get.wordy.core.api.bean.Word;
import get.wordy.model.Explanation;
import get.wordy.model.WordSearchResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/words")
public class WordsController {

    private final IVocabularyEnrichmentService vocabularyEnrichmentService;
    private final IWordExplanationService wordExplanationService;

    @Value("${ai.search.enabled}")
    private Boolean aiSearchEnabled;

    public WordsController(IVocabularyEnrichmentService vocabularyEnrichmentService, IWordExplanationService wordExplanationService) {
        this.vocabularyEnrichmentService = vocabularyEnrichmentService;
        this.wordExplanationService = wordExplanationService;
    }

    @GetMapping
    public ResponseEntity<?> search(Principal user, @RequestParam(value = "input") String input) {
        if (input == null || input.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        List<Word> explanations = wordExplanationService.findExplanations(input);
        if (!explanations.isEmpty()) {
            log.info("{} explanations found in DB for input '{}'", explanations.size(), input);
            return ResponseEntity.ok(toResponse(explanations));
        }

        if (!aiSearchEnabled) {
            log.info("AI search disabled. Returning feature-disabled message for '{}'", input);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("error", "AI search feature is disabled"));
        }

        log.info("Initiating AI search for user '{}', input '{}'", user.getName(), input);

        GetExplanationResult searchResult = vocabularyEnrichmentService.search(input);

        List<GetExplanationResult.Explanation> explanationsFromAi =
                Optional.ofNullable(searchResult.getExplanations()).orElse(List.of());

        if (explanationsFromAi.isEmpty()) {
            log.warn("AI returned no explanations for input '{}'", input);
            return ResponseEntity.noContent().build();
        }

        log.info("{} explanations returned from AI for input '{}'", explanationsFromAi.size(), input);
        WordSearchResponse wordSearchResponse = processAiResult(searchResult);

        return ResponseEntity.ok(wordSearchResponse);
    }

    private WordSearchResponse toResponse(List<Word> explanations) {
        Word first = explanations.getFirst();
        return WordSearchResponse.builder()
                .lemma(first.getLemma())
                .transcription(first.getTranscription())
                .explanations(explanations.stream()
                        .map(word -> Explanation.builder()
                                .wordId(word.getId())
                                .partOfSpeech(word.getPartOfSpeech())
                                .meaning(word.getMeaning())
                                .register(word.getRegister())
                                .domain(word.getDomain())
                                .inContext(word.getStrSentences())
                                .collocations(word.getCollocations())
                                .level(word.getLevel())
                                .build()
                        )
                        .toList()
                )
                .build();
    }

    private WordSearchResponse processAiResult(GetExplanationResult searchResult) {
        List<Word> words = searchResult.getExplanations().stream()
                .map(explanation -> {
                    Word entity = new Word(
                            searchResult.getLemma(),
                            explanation.getPartOfSpeech(),
                            searchResult.getTranscription(),
                            explanation.getMeaning(),
                            explanation.getLevel()
                    );
                    entity.setRegister(explanation.getRegister());
                    entity.setDomain(explanation.getDomain());
                    entity.setCollocations(explanation.getCollocations());
                    entity.setStrSentences(explanation.getSentences());
                    return wordExplanationService.addWordExplanation(entity);
                })
                .toList();

        log.debug("Saved {} new records from AI for lemma '{}'", words.size(), searchResult.getLemma());
        return toResponse(words);
    }

}
