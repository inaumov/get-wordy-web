package get.wordy.rest;

import get.wordy.core.api.IDictionaryService;
import get.wordy.core.bean.Dictionary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

@RestController
public class DictionariesController {
    private static final Logger log = LoggerFactory.getLogger(DictionariesController.class);

    private final IDictionaryService dictionaryService;

    @Autowired
    public DictionariesController(IDictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    @RequestMapping(value = "/dictionaries", method = RequestMethod.GET)
    public ResponseEntity<List<Dictionary>> getUserDictionaries(Principal user) {
        log.info("Getting dictionary list for the user {}", user.getName());

        List<Dictionary> dictionaries = dictionaryService.getDictionaries();

        if (dictionaries.isEmpty()) {
            log.info("No dictionaries found for the user {}.", user.getName());
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
        }
        return new ResponseEntity<>(dictionaries, HttpStatus.OK);
    }

}
