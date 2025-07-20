package get.wordy.rest;

import get.wordy.core.api.IVocabularyService;
import get.wordy.core.api.bean.Vocabulary;
import get.wordy.core.api.bean.Word;
import get.wordy.core.api.id.OwnerId;
import get.wordy.model.*;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@RestController
@PreAuthorize("hasAuthority('P_MANAGE_OWN_VOCAB')")
@RequestMapping(value = "/user/my-vocabularies")
public class UserVocabulariesController {
    private static final Logger LOG = LoggerFactory.getLogger(UserVocabulariesController.class);

    private final IVocabularyService vocabularyService;

    @Autowired
    public UserVocabulariesController(IVocabularyService vocabularyService) {
        this.vocabularyService = vocabularyService;
    }

    @GetMapping
    public ResponseEntity<List<UserVocabularyResponse>> getUserVocabularies(Principal user) {
        LOG.info("Getting vocabularies for the user = {}", user.getName());

        List<UserVocabularyResponse> vocabularies = vocabularyService.getVocabularies(createOwnerId(user))
                .stream()
                .map(this::toResponse)
                .toList();

        if (vocabularies.isEmpty()) {
            LOG.info("No vocabularies found for the user = {}", user.getName());
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
        }
        return new ResponseEntity<>(vocabularies, HttpStatus.OK);
    }

    @GetMapping("/{vocabId}")
    public ResponseEntity<UserVocabularyResponse> getVocabulary(Principal user,
                                                                @PathVariable("vocabId") int vocabId) {
        LOG.info("Getting vocabulary for the user = {} with id = {}", user.getName(), vocabId);

        Vocabulary vocabulary = vocabularyService.getVocabulary(createOwnerId(user), vocabId);
        return new ResponseEntity<>(toResponse(vocabulary), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<UserVocabularyResponse> createVocabulary(Principal user,
                                                                   @Valid @RequestBody DictionaryRequest dictionaryRequest) {
        LOG.info("Creating a new vocabulary = {} for the user = {}", dictionaryRequest.name(), user.getName());

        Vocabulary vocabulary = vocabularyService.createVocabulary(createOwnerId(user), dictionaryRequest.name(), dictionaryRequest.pictureUrl());
        UserVocabularyResponse response = toResponse(vocabulary);
        return ResponseEntity.created(URI.create("/vocabularies/" + response.vocabId()))
                .body(response);
    }

    @PatchMapping("/{vocabId}")
    public ResponseEntity<UserVocabularyResponse> partialUpdate(Principal user,
                                                                @PathVariable("vocabId") int vocabId,
                                                                @RequestBody DictionaryRequest partialUpdate,
                                                                @RequestParam(value = "forceRemovePicture", required = false) boolean forceRemovePicture) {
        LOG.info("Updating vocabulary for the user = {}, vocab id = {}", user.getName(), vocabId);

        // handle name change
        if (StringUtils.hasText(partialUpdate.name())) {
            vocabularyService.renameVocabulary(createOwnerId(user), vocabId, partialUpdate.name());
        }
        // handle pictureUrl change
        if (forceRemovePicture) {
            vocabularyService.changeVocabularyPicture(createOwnerId(user), vocabId, null);
        } else if (StringUtils.hasText(partialUpdate.pictureUrl())) {
            vocabularyService.changeVocabularyPicture(createOwnerId(user), vocabId, partialUpdate.pictureUrl());
        }
        return ResponseEntity
                .noContent()
                .build();
    }

    @DeleteMapping(value = "/{vocabId}")
    public ResponseEntity<Void> deleteVocabulary(Principal user,
                                                 @PathVariable("vocabId") int vocabId) {
        LOG.info("Deleting a vocabulary for the user = {}, by id = {}", user.getName(), vocabId);

        vocabularyService.deleteVocabulary(createOwnerId(user), vocabId);

        return ResponseEntity
                .noContent()
                .build();
    }

    @PostMapping(value = "/{vocabId}/words")
    public ResponseEntity<WordResponse> addToVocabulary(Principal user,
                                                        @PathVariable("vocabId") int vocabId,
                                                        @Valid @RequestBody WordIdRequest wordId, UriComponentsBuilder ucBuilder) {

        LOG.info("Adding new word to user vocabulary, id = {}, user = {}", vocabId, user.getName());

        Word addedToVocabulary = vocabularyService.addToVocabulary(createOwnerId(user), vocabId, wordId.wordId());

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder
                .path("/{vocabId}/words/{wordId}")
                .buildAndExpand(vocabId, addedToVocabulary.getId())
                .toUri()
        );
        WordResponse response = toWordResponse(addedToVocabulary);
        return new ResponseEntity<>(response, headers, HttpStatus.ACCEPTED);
    }

    @DeleteMapping(value = "/{vocabId}/words")
    public ResponseEntity<Void> removeFromVocabulary(Principal user,
                                                     @PathVariable("vocabId") int vocabId,
                                                     @Valid @RequestBody WordIdRequest wordId) {

        LOG.info("Deleting a word = {} from user vocabulary, id = {}, user = {}", wordId, vocabId, user.getName());

        vocabularyService.removeFromVocabulary(createOwnerId(user), vocabId, wordId.wordId());

        return ResponseEntity
                .noContent()
                .build();
    }

    private static OwnerId createOwnerId(Principal user) {
        return new OwnerId(user.getName(), "user");
    }

    private UserVocabularyResponse toResponse(Vocabulary vocabulary) {
        return new UserVocabularyResponse(
                vocabulary.getVocabId(),
                vocabulary.getName(),
                vocabulary.getPictureUrl(),
                getVocabType(vocabulary),
                vocabulary.getWordsTotal(),
                0, // todo
                vocabulary.getUpdateTime()
        );
    }

    private VocabType getVocabType(Vocabulary vocabulary) {
        boolean shared = vocabulary.isShared();
        if (shared) {
            return VocabType.SHARED;
        }
        return Objects.equals("Favorite Words", vocabulary.getName()) ? VocabType.FAV : VocabType.OWN;
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
