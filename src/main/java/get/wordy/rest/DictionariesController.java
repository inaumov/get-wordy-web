package get.wordy.rest;

import get.wordy.core.api.IDictionaryService;
import get.wordy.core.api.exception.DictionaryNotFoundException;
import get.wordy.core.api.bean.Dictionary;
import get.wordy.model.DictionaryRequest;
import get.wordy.model.DictionaryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.Principal;
import java.util.Collections;
import java.util.List;

@RestController
public class DictionariesController {
    private static final Logger LOG = LoggerFactory.getLogger(DictionariesController.class);

    private final IDictionaryService dictionaryService;

    @Autowired
    public DictionariesController(IDictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    @GetMapping(value = "/dictionaries")
    public ResponseEntity<List<DictionaryResponse>> getUserDictionaries(Principal user) {
        LOG.info("Getting dictionary list for the user = {}", user.getName());

        List<DictionaryResponse> dictionaries = dictionaryService.getDictionaries()
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
                                                               @RequestBody DictionaryRequest dictionaryRequest) {
        LOG.info("Creating a new dictionary = {} for the user = {}", dictionaryRequest.name(), user.getName());

        Dictionary dictionary = dictionaryService.createDictionary(dictionaryRequest.name(), dictionaryRequest.picture());
        DictionaryResponse response = toResponse(dictionary);
        return ResponseEntity.created(URI.create("/dictionaries/" + response.dictionaryId()))
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
            dictionaryService.renameDictionary(id, partialUpdate.name());
        }
        // handle picture change
        if (forceRemovePicture) {
            dictionaryService.changeDictionaryPicture(id, null);
        } else if (StringUtils.hasText(partialUpdate.picture())) {
            dictionaryService.changeDictionaryPicture(id, partialUpdate.picture());
        }
        return ResponseEntity
                .noContent()
                .build();
    }

    @DeleteMapping(value = "/dictionaries/{dictionaryId}")
    public ResponseEntity<DictionaryResponse> deleteDictionary(Principal user,
                                                               @PathVariable("dictionaryId") int dictionaryId) {
        LOG.info("Deleting a dictionary for the user = {}, dictionary id = {}", user.getName(), dictionaryId);

        Dictionary found = dictionaryService.getDictionaries()
                .stream()
                .filter(dictionary -> dictionary.getId() == dictionaryId)
                .findAny()
                .orElseThrow(DictionaryNotFoundException::new);
        if (found.getCardsTotal() > 0) {
            throw new IllegalStateException("Cannot delete dictionary with cards");
        }

        dictionaryService.deleteDictionary(dictionaryId);

        return ResponseEntity
                .noContent()
                .build();
    }

    private DictionaryResponse toResponse(Dictionary dictionary) {
        return new DictionaryResponse(
                dictionary.getId(),
                dictionary.getName(),
                dictionary.getPicture(),
                dictionary.getCardsTotal()
        );
    }

}
