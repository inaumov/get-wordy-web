package get.wordy.rest;

import get.wordy.core.api.IDictionaryService;
import get.wordy.core.bean.Dictionary;
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
        LOG.info("Creating dictionary for the user = {}", user.getName());

        Dictionary dictionary = dictionaryService.createDictionary(dictionaryRequest.name(), dictionaryRequest.picture());
        DictionaryResponse response = toResponse(dictionary);
        return ResponseEntity.created(URI.create("/dictionaries/" + response.dictionaryId()))
                .body(response);
    }

    @PatchMapping("/dictionaries/{id}")
    public ResponseEntity<DictionaryResponse> partialUpdate(Principal user,
                                                            @RequestBody DictionaryRequest partialUpdate,
                                                            @PathVariable("id") int id) {
        LOG.info("Updating dictionary for the user = {}, id = {}", user.getName(), id);

        if (StringUtils.hasText(partialUpdate.name())) {
            dictionaryService.renameDictionary(id, partialUpdate.name());
        }
        if (StringUtils.hasText(partialUpdate.picture())) {
            dictionaryService.changeDictionaryPicture(id, partialUpdate.picture());
        }
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
