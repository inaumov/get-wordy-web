package get.wordy.rest;

import get.wordy.core.api.IVocabularyService;
import get.wordy.core.api.bean.Vocabulary;
import get.wordy.core.api.id.OwnerId;
import get.wordy.model.DictionaryRequest;
import get.wordy.model.DictionaryResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.Principal;
import java.util.Collections;
import java.util.List;

@RestController
@PreAuthorize("hasAuthority('P_MANAGE_OWN_DICTIONARIES')")
public class DictionariesController {
    private static final Logger LOG = LoggerFactory.getLogger(DictionariesController.class);

    private final IVocabularyService vocabularyService;

    @Autowired
    public DictionariesController(IVocabularyService vocabularyService) {
        this.vocabularyService = vocabularyService;
    }

    @GetMapping(value = "/dictionaries")
    public ResponseEntity<List<DictionaryResponse>> getUserDictionaries(Principal user) {
        LOG.info("Getting dictionary list for the user = {}", user.getName());

        List<DictionaryResponse> dictionaries = vocabularyService.getVocabularies(createOwnerId(user))
                .stream()
                .map(this::toResponse)
                .toList();

        if (dictionaries.isEmpty()) {
            LOG.info("No dictionaries found for the user = {}", user.getName());
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
        }
        return new ResponseEntity<>(dictionaries, HttpStatus.OK);
    }

    @PostMapping("/dictionaries")
    public ResponseEntity<DictionaryResponse> createDictionary(Principal user,
                                                               @Valid @RequestBody DictionaryRequest dictionaryRequest) {
        LOG.info("Creating a new dictionary = {} for the user = {}", dictionaryRequest.name(), user.getName());

        Vocabulary dictionary = vocabularyService.createVocabulary(createOwnerId(user), dictionaryRequest.name(), dictionaryRequest.pictureUrl());
        DictionaryResponse response = toResponse(dictionary);
        return ResponseEntity.created(URI.create("/dictionaries/" + response.vocabId()))
                .body(response);
    }

    @PatchMapping("/dictionaries/{id}")
    public ResponseEntity<DictionaryResponse> partialUpdate(Principal user,
                                                            @PathVariable("id") int id,
                                                            @RequestBody DictionaryRequest partialUpdate,
                                                            @RequestParam(value = "forceRemovePicture", required = false) boolean forceRemovePicture) {
        LOG.info("Updating dictionary for the user = {}, id = {}", user.getName(), id);

        // handle name change
        if (StringUtils.hasText(partialUpdate.name())) {
            vocabularyService.renameVocabulary(createOwnerId(user), id, partialUpdate.name());
        }
        // handle pictureUrl change
        if (forceRemovePicture) {
            vocabularyService.changeVocabularyPicture(createOwnerId(user), id, null);
        } else if (StringUtils.hasText(partialUpdate.pictureUrl())) {
            vocabularyService.changeVocabularyPicture(createOwnerId(user), id, partialUpdate.pictureUrl());
        }
        return ResponseEntity
                .noContent()
                .build();
    }

    @DeleteMapping(value = "/dictionaries/{dictionaryId}")
    public ResponseEntity<DictionaryResponse> deleteDictionary(Principal user,
                                                               @PathVariable("dictionaryId") int dictionaryId) {
        LOG.info("Deleting a dictionary for the user = {}, dictionary id = {}", user.getName(), dictionaryId);

        vocabularyService.deleteVocabulary(createOwnerId(user), dictionaryId);

        return ResponseEntity
                .noContent()
                .build();
    }

    private static OwnerId createOwnerId(Principal user) {
        return new OwnerId(user.getName(), "1");
    }

    private DictionaryResponse toResponse(Vocabulary vocabulary) {
        return new DictionaryResponse(
                vocabulary.getVocabId(),
                vocabulary.getName(),
                vocabulary.getPictureUrl(),
                vocabulary.getWordsTotal()
        );
    }

}
