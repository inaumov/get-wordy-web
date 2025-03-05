package get.wordy.rest;

import get.wordy.core.api.IClassAccessService;
import get.wordy.core.api.IVocabularyService;
import get.wordy.core.api.bean.Vocabulary;
import get.wordy.core.api.bean.Word;
import get.wordy.core.api.id.OwnerId;
import get.wordy.model.Explanation;
import get.wordy.model.WordResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@PreAuthorize("hasAuthority('P_SHARED_CLASS')")
@RequestMapping(value = "/user/my-classes")
public class SharedVocabulariesController {

    private static final Logger LOG = LoggerFactory.getLogger(SharedVocabulariesController.class);

    private final IVocabularyService vocabularyService;
    private final IClassAccessService accessService;

    public SharedVocabulariesController(IVocabularyService vocabularyService,
                                        IClassAccessService accessService) {
        this.vocabularyService = vocabularyService;
        this.accessService = accessService;
    }

    @GetMapping(value = "/{classId}/vocabularies")
    public ResponseEntity<List<Map<String, Object>>> getVocabularies(Principal user,
                                                                     @PathVariable("classId") String classId) {
        LOG.info("Getting shared vocabularies for the user = {}, and class id = {}", user.getName(), classId);

        accessService.hasAccess(classId, user.getName());

        List<Map<String, Object>> vocabulariesResponse = vocabularyService.getVocabularies(createClassOwnerId(classId))
                .stream()
                .filter(Vocabulary::isShared) // !important
                .map(this::toResponse)
                .toList();

        if (vocabulariesResponse.isEmpty()) {
            LOG.info("No available vocabularies found for the class = {}", classId);
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
        }
        return new ResponseEntity<>(vocabulariesResponse, HttpStatus.OK);
    }

    private Map<String, Object> toResponse(Vocabulary vocabulary) {
        return Map.of(
                "vocabId", vocabulary.getVocabId(),
                "name", vocabulary.getName(),
                "wordsTotal", vocabulary.getWordsTotal());
    }

    @GetMapping(value = "/{classId}/vocabularies/{vocabId}")
    public ResponseEntity<Map<String, Object>> getVocabulary(Principal user,
                                                             @PathVariable("classId") String classId,
                                                             @PathVariable("vocabId") int vocabId) {

        LOG.info("Getting shared vocabulary = {} for the user = {}, and class id = {}", vocabId, user.getName(), classId);

        accessService.hasAccess(classId, user.getName());
        Vocabulary vocabulary = vocabularyService.getVocabulary(createClassOwnerId(classId), vocabId);
        if (!vocabulary.isShared()) {
            throw new AccessDeniedException("Permission denied: no access to this vocabulary = " + vocabId);
        }

        if (vocabulary.getWordsTotal() == 0) {
            Map<String, Object> empty = Map.of(
                    "vocabId", vocabId,
                    "name", vocabulary.getName(),
                    "wordsTotal", 0,
                    "words", Collections.emptyList());
            return new ResponseEntity<>(empty, HttpStatus.OK);
        }

        List<Word> vocabWords = vocabularyService.getWords(createClassOwnerId(classId), vocabId);
        Map<String, Object> vocabularyResponse = Map.of(
                "vocabId", vocabId,
                "name", vocabulary.getName(),
                "wordsTotal", vocabulary.getWordsTotal(),
                "words", vocabWords
                        .stream()
                        .map(this::toWordResponse)
                        .toList()
        );

        return new ResponseEntity<>(vocabularyResponse, HttpStatus.OK);
    }

    private OwnerId createClassOwnerId(String classId) {
        return new OwnerId(classId, "class");
    }

    private WordResponse toWordResponse(Word word) {
        return new WordResponse(
                word.getId(),
                word.getValue(),
                word.getTranscription(),
                Explanation.builder()
                        .partOfSpeech(word.getPartOfSpeech())
                        .meaning(word.getMeaning())
                        .inContext(word.getStrSentences())
                        .collocations(word.getCollocations())
                        .build()
        );
    }

}
