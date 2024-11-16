package get.wordy.rest;

import get.wordy.core.api.IDictionaryService;
import get.wordy.core.api.bean.Card;
import get.wordy.core.api.bean.Dictionary;
import get.wordy.core.api.bean.Word;
import get.wordy.core.api.id.OwnerId;
import get.wordy.model.WordResponse;
import get.wordy.model.WordsheetItemResponse;
import get.wordy.model.WordsheetListItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/classes")
@PreAuthorize("hasAuthority('P_MANAGE_CLASSES')")
public class ClassController {
    private static final Logger LOG = LoggerFactory.getLogger(ClassController.class);

    private final IDictionaryService dictionaryService;

    public ClassController(IDictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getMockedClasses() {
        Map<String, Object> response = new HashMap<>();

        List<Map<String, Object>> classes = new ArrayList<>();
        // Add class data to the list
        classes.add(createClass("desna-4xRg5", "08:30 - 09:30", 1, "Online VIP", "", "LIS - 15"));
        classes.add(createClass("desna-4xRg6", "09:40 - 10:20", 1, "Online VIP", "", "Real Time 1"));
        classes.add(createClass("desna-4xRg7", "10:30 - 11:30", 1, "Online VIP", "Advanced", "TS-09"));
        classes.add(createClass("desna-4xRg8", "11:30 - 12:30", 1, "Offline VIP", "Intermediate", "IS-II"));
        classes.add(createClass("desna-4xRg9", "15:00 - 16:30", 7, "Offline group", "Beginner", "KB - 2"));
        classes.add(createClass("desna-Qsd33", "19:00 - 20:00", 3, "Offline group", "Elementary", "BIS - 5"));

        response.put("dayOfWeek", "Monday");
        response.put("classes", classes);

        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/{classId}")
    public ResponseEntity<List<WordsheetListItem>> getClassWordsheetList(@PathVariable("classId") String classId) {
        LOG.info("Getting wordsheet list for the class id = {}", classId);

        List<WordsheetListItem> dictionaries = dictionaryService.getDictionaries(createOwnerId(classId))
                .stream()
                .map(this::toResponse)
                .toList();

        if (dictionaries.isEmpty()) {
            LOG.info("No dictionaries found for the class = {}", classId);
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
        }
        return new ResponseEntity<>(dictionaries, HttpStatus.OK);
    }

    @GetMapping(value = "/{classId}/wordsheet/{wordsheetId}")
    public ResponseEntity<Map<String, Object>> getWordSheet(@PathVariable("classId") String classId,
                                                            @PathVariable("wordsheetId") int wordsheetId) {

        LOG.info("Getting a wordsheet for the class id = {}, and wordsheet id = {}", classId, wordsheetId);

        List<WordsheetItemResponse> wordSheetItems = dictionaryService.getCards(createOwnerId(classId), wordsheetId)
                .stream()
                .map(this::toWordSheetItem)
                .toList();

        if (wordSheetItems.isEmpty()) {
            LOG.info("No wordsheet found for the wordsheet id = {}", wordsheetId);
            return new ResponseEntity<>(Collections.emptyMap(), HttpStatus.OK);
        }
        Map<String, Object> wordsheetResponse = Map.of(
                "wordsheetId", wordsheetId,
                "name", "Test",
                "items", wordSheetItems
        );

        return new ResponseEntity<>(wordsheetResponse, HttpStatus.OK);
    }

    private OwnerId createOwnerId(String classId) {
        return new OwnerId(classId, "2");
    }

    private Map<String, Object> createClass(String classId, String name, int attendees, String classFormat, String classLevel, String notes) {
        Map<String, Object> classMap = new HashMap<>();
        classMap.put("classId", classId);
        classMap.put("name", name);
        classMap.put("attendees", attendees);
        classMap.put("format", classFormat);
        classMap.put("level", classLevel);
        classMap.put("notes", notes);
        return classMap;
    }

    private WordsheetItemResponse toWordSheetItem(Card card) {
        return new WordsheetItemResponse(
                card.getWordId(),
                toWordResponse(card.getWord()),
                card.getStrSentences()
        );
    }

    private WordResponse toWordResponse(Word word) {
        return new WordResponse(
                word.getId(),
                word.getValue(),
                word.getPartOfSpeech(),
                word.getTranscription(),
                word.getMeaning()
        );
    }

    private WordsheetListItem toResponse(Dictionary dictionary) {
        return new WordsheetListItem(
                dictionary.getId(),
                dictionary.getName(),
                dictionary.getCardsTotal(),
                false
        );
    }

}
