package get.wordy.rest;

import get.wordy.core.SharedCardsService;
import get.wordy.core.api.IClassAccessService;
import get.wordy.core.api.IVocabularyService;
import get.wordy.core.api.bean.*;
import get.wordy.core.api.id.OwnerId;
import get.wordy.model.*;
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

@RestController
@PreAuthorize("hasAuthority('P_SHARED_CLASS')")
@RequestMapping(value = "/user/my-classes")
public class SharedVocabulariesController {

    private static final Logger LOG = LoggerFactory.getLogger(SharedVocabulariesController.class);

    private final IVocabularyService vocabularyService;
    private final IClassAccessService accessService;
    private final SharedCardsService sharedCardsService;

    public SharedVocabulariesController(IVocabularyService vocabularyService,
                                        IClassAccessService accessService,
                                        SharedCardsService sharedCardsService
    ) {
        this.vocabularyService = vocabularyService;
        this.accessService = accessService;
        this.sharedCardsService = sharedCardsService;
    }

    @GetMapping(value = "/{classId}/vocabularies")
    public ResponseEntity<List<UserVocabularyResponse>> getVocabularies(Principal user,
                                                                        @PathVariable("classId") String classId) {
        LOG.info("Getting shared vocabularies for the user = {}, and class id = {}", user.getName(), classId);

        accessService.hasAccess(classId, user.getName());

        List<UserVocabularyResponse> vocabulariesResponse = vocabularyService.getVocabularies(createClassOwnerId(classId))
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

    @GetMapping(value = "/{classId}/vocabularies/{vocabId}")
    public ResponseEntity<UserVocabularyResponse> getVocabulary(Principal user,
                                                                @PathVariable("classId") String classId,
                                                                @PathVariable("vocabId") int vocabId) {

        LOG.info("Getting shared vocabulary = {} for the user = {}, and class id = {}", vocabId, user.getName(), classId);

        accessService.hasAccess(classId, user.getName());
        Vocabulary vocabulary = vocabularyService.getVocabulary(createClassOwnerId(classId), vocabId);
        if (!vocabulary.isShared()) {
            throw new AccessDeniedException("Permission denied: no access to this vocabulary = " + vocabId);
        }
        return new ResponseEntity<>(toResponse(vocabulary), HttpStatus.OK);
    }

    @GetMapping(value = "/{classId}/vocabularies/{vocabId}/cards")
    public ResponseEntity<List<CardResponse>> getCards(Principal user,
                                                       @PathVariable("classId") String classId,
                                                       @PathVariable("vocabId") int vocabId) {

        LOG.info("Getting cards from shared vocabulary = {} for the user = {}, and class id = {}", vocabId, user.getName(), classId);

        accessService.hasAccess(classId, user.getName());
        Vocabulary vocabulary = vocabularyService.getVocabulary(createClassOwnerId(classId), vocabId);
        if (!vocabulary.isShared()) {
            throw new AccessDeniedException("Permission denied: no access to this vocabulary = " + vocabId);
        }

        if (vocabulary.getWordsTotal() == 0) {
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
        }
        List<CardResponse> cards = sharedCardsService.getCards(createClassOwnerId(classId), createOwnerId(user), vocabId)
                .stream()
                .map(CardResponse::fromCard)
                .toList();
        return new ResponseEntity<>(cards, HttpStatus.OK);
    }

    private static OwnerId createClassOwnerId(String classId) {
        return new OwnerId(classId, "class");
    }

    private static OwnerId createOwnerId(Principal user) {
        return new OwnerId(user.getName(), "user");
    }

    private UserVocabularyResponse toResponse(Vocabulary vocabulary) {
        return new UserVocabularyResponse(
                vocabulary.getVocabId(),
                vocabulary.getName(),
                null,
                VocabType.SHARED,
                vocabulary.getWordsTotal(),
                0,
                vocabulary.getUpdateTime()
        );
    }

}
