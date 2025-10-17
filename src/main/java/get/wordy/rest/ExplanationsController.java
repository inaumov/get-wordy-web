package get.wordy.rest;

import get.wordy.core.ClassVocabularyService;
import get.wordy.core.api.bean.Word;
import get.wordy.core.api.id.OwnerId;
import get.wordy.model.Explanation;
import get.wordy.model.WordEditRequest;
import get.wordy.model.WordRequest;
import get.wordy.model.WordResponse;
import jakarta.servlet.http.HttpServlet;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.security.Principal;

@RestController
@RequestMapping(value = "/vocabularies")
@PreAuthorize("hasAuthority('P_MANAGE_CLASSES')")
public class ExplanationsController extends HttpServlet {
    private static final Logger LOG = LoggerFactory.getLogger(ExplanationsController.class);

    private final ClassVocabularyService vocabularyService;

    public ExplanationsController(ClassVocabularyService classVocabularyService) {
        this.vocabularyService = classVocabularyService;
    }

    @GetMapping(value = "/{vocabId}/explanations/{wordId}")
    public ResponseEntity<WordResponse> getExplanation(Principal user,
                                                       @PathVariable("vocabId") int vocabId,
                                                       @PathVariable("wordId") int wordId) {

        LOG.info("Getting word explanation = {} for the user = {}, vocabulary id = {}", wordId, user.getName(), vocabId);
        Word word = vocabularyService.getWordExplanation(createOwnerId(user), vocabId, wordId);
        WordResponse wordResponse = toWordResponse(word);
        return new ResponseEntity<>(wordResponse, HttpStatus.OK);
    }

    @PostMapping(value = "/{vocabId}/explanations")
    public ResponseEntity<WordResponse> addExplanation(Principal user,
                                                       @PathVariable("vocabId") int vocabId,
                                                       @Valid @RequestBody WordRequest wordRequest, UriComponentsBuilder ucBuilder) {

        LOG.info("Adding a new word explanation for the user = {}, vocabulary id = {}", user.getName(), vocabId);

        Word entity = toEntity(wordRequest);
        Word wordAdded = vocabularyService.addWordExplanation(createOwnerId(user), vocabId, entity);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder
                .path("/vocabularies/{vocabId}/explanations/{wordId}")
                .buildAndExpand(vocabId, wordAdded.getId())
                .toUri()
        );
        WordResponse wordResponse = toWordResponse(wordAdded);
        return new ResponseEntity<>(wordResponse, headers, HttpStatus.CREATED);
    }

    @PutMapping(value = "/{vocabId}/explanations")
    public ResponseEntity<WordResponse> updateExplanation(Principal user,
                                                          UriComponentsBuilder ucBuilder,
                                                          @PathVariable("vocabId") int vocabId,
                                                          @Valid @RequestBody WordEditRequest wordRequest) {

        LOG.info("Update a word explanation for the user = {}, vocabulary id = {}", user.getName(), vocabId);

        Word entity = toEntity(wordRequest)
                .withId(wordRequest.getWordId());

        Word wordUpdated = vocabularyService.updateWordExplanation(createOwnerId(user), vocabId, entity);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder
                .path("/vocabularies/{vocabId}/explanations/{wordId}")
                .buildAndExpand(vocabId, entity.getId())
                .toUri()
        );
        WordResponse wordResponse = toWordResponse(wordUpdated);
        return new ResponseEntity<>(wordResponse, headers, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{vocabId}/explanations/{wordId}")
    public ResponseEntity<Void> deleteExplanationPermanently(Principal user,
                                                             @PathVariable("vocabId") int vocabId,
                                                             @PathVariable("wordId") int wordId) {
        LOG.info("Removing a word explanation = {} for the user = {}, vocabulary id = {}", wordId, user.getName(), vocabId);

        vocabularyService.deleteWordExplanationPermanently(createOwnerId(user), vocabId, wordId);

        return ResponseEntity
                .noContent()
                .build();
    }

    private Word toEntity(WordRequest wordRequest) {
        Word word = new Word(
                wordRequest.getLemma(),
                wordRequest.getExplanation().getPartOfSpeech(),
                wordRequest.getTranscription(),
                wordRequest.getExplanation().getMeaning()
        );
        word.setStrSentences(wordRequest.getExplanation().getInContext());
        word.setCollocations(wordRequest.getExplanation().getCollocations());
        return word;
    }

    private WordResponse toWordResponse(Word word) {
        return new WordResponse(
                word.getId(),
                word.getLemma(),
                word.getTranscription(),
                Explanation.builder()
                        .wordId(word.getId())
                        .partOfSpeech(word.getPartOfSpeech())
                        .meaning(word.getMeaning())
                        .collocations(word.getCollocations())
                        .inContext(word.getStrSentences())
                        .build()
        );
    }

    private static OwnerId createOwnerId(Principal user) {
        return new OwnerId(user.getName(), "class");
    }

}
