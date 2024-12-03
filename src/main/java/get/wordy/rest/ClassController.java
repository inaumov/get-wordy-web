package get.wordy.rest;

import get.wordy.core.ClassDetails;
import get.wordy.core.IClassService;
import get.wordy.core.WordsheetItem;
import get.wordy.core.WordsheetListItem;
import get.wordy.core.api.bean.Word;
import get.wordy.core.api.id.OwnerId;
import get.wordy.model.*;
import jakarta.validation.Valid;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.*;

@RestController
@RequestMapping("/classes")
@PreAuthorize("hasAuthority('P_MANAGE_CLASSES')")
public class ClassController {
    private static final Logger LOG = LoggerFactory.getLogger(ClassController.class);

    private final IClassService classService;

    public ClassController(IClassService classService) {
        this.classService = classService;
    }

    @GetMapping
    public ResponseEntity<List<ClassDetails>> getClasses(Principal user,
                                                         @RequestParam(value = "filter", required = false) String dayOfWeek) {
        LOG.info("Getting {} classes list for the user = {}", StringUtils.hasText(dayOfWeek) ? dayOfWeek : "all", user.getName());

        List<ClassDetails> classes = classService.getClasses(user.getName(), dayOfWeek);

        List<ClassDetails> response = new ArrayList<>();
        // Add class data to the list
        response.add(createClass("desna-4xRg5", "08:30 - 09:30", 1, "Online VIP", "", "LIS - 15"));
        response.add(createClass("desna-4xRg6", "09:40 - 10:20", 1, "Online VIP", "", "Real Time 1"));
        response.add(createClass("desna-4xRg7", "10:30 - 11:30", 0, "Online VIP", "Advanced", "TS-09"));
        response.add(createClass("desna-4xRg8", "11:30 - 12:30", 1, "Offline VIP", "Intermediate", "IS-II"));
        response.add(createClass("desna-4xRg9", "15:00 - 16:30", 7, "Offline group", "Beginner", "KB - 2"));
        response.add(createClass("desna-Qsd33", "19:00 - 20:00", 3, "Offline group", "Elementary", "BIS - 5"));

        response.addAll(classes);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ClassDetails> addClass(Principal user,
                                                 @Valid @RequestBody ClassDetails classDetails) {

        LOG.info("Add new class = {} request for the user = {}", classDetails.getName(), user.getName());

        ClassDetails savedClass = classService.saveClass(user.getName(), classDetails);
        return ResponseEntity.created(URI.create("/" + savedClass.getClassId()))
                .body(savedClass);
    }

    @DeleteMapping(value = "/{classId}")
    public ResponseEntity<ClassDetails> deleteClass(Principal user,
                                                    @PathVariable("classId") String classId) {
        LOG.info("Deleting a class for the user = {}, class id = {}", user.getName(), classId);

        classService.deleteClass(createUserOwnerId(user.getName()), classId);

        return ResponseEntity
                .noContent()
                .build();
    }

    @GetMapping(value = "/{classId}/wordsheets")
    public ResponseEntity<List<WordsheetResponse>> getClassWordsheetList(Principal user,
                                                                         @PathVariable("classId") String classId) {
        LOG.info("Getting wordsheet list for the class id = {}", classId);

        List<WordsheetResponse> wordsheetsList = classService.getWordsheetList(createUserOwnerId(user.getName()), classId)
                .stream()
                .map(this::toResponse)
                .toList();

        if (wordsheetsList.isEmpty()) {
            LOG.info("No wordsheets found for the class = {}", classId);
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
        }
        return new ResponseEntity<>(wordsheetsList, HttpStatus.OK);
    }

    @PostMapping("/{classId}/wordsheets")
    public ResponseEntity<WordsheetResponse> createNewWordsheet(Principal user,
                                                                @PathVariable("classId") String classId,
                                                                @Valid @RequestBody WordsheetRequest wordsheetRequest) {
        LOG.info("Creating a new wordsheet = {} for the class = {}", wordsheetRequest.name(), classId);

        WordsheetListItem wordsheet = classService.createWordsheet(createClassOwnerId(classId), wordsheetRequest.name());
        WordsheetResponse response = toResponse(wordsheet);
        return ResponseEntity.created(URI.create("/{classId}/wordsheet/" + wordsheet.wordsheetId()))
                .body(response);
    }

    @GetMapping(value = "/{classId}/wordsheets/{wordsheetId}")
    public ResponseEntity<Map<String, Object>> getWordSheet(Principal user,
                                                            @PathVariable("classId") String classId,
                                                            @PathVariable("wordsheetId") int wordsheetId) {

        LOG.info("Getting a wordsheet for the class id = {}, and wordsheet id = {}", classId, wordsheetId);

        List<WordsheetItemResponse> wordsheetItems = classService.getWordsheetItems(createClassOwnerId(classId), wordsheetId)
                .stream()
                .map(this::toWordSheetItem)
                .toList();

        if (wordsheetItems.isEmpty()) {
            LOG.info("No wordsheet found for the wordsheet id = {}", wordsheetId);
            return new ResponseEntity<>(Collections.emptyMap(), HttpStatus.OK);
        }
        Map<String, Object> wordsheetResponse = Map.of(
                "wordsheetId", wordsheetId,
                "name", "Test",
                "items", wordsheetItems
        );

        return new ResponseEntity<>(wordsheetResponse, HttpStatus.OK);
    }

    @PatchMapping("/{classId}/wordsheets/{wordsheetId}")
    public ResponseEntity<WordsheetResponse> partialUpdate(Principal user,
                                                           @PathVariable("classId") String classId,
                                                           @PathVariable("wordsheetId") int wordsheetId,
                                                           @RequestBody WordsheetRequest partialUpdate) {
        LOG.info("Updating wordsheet id = {} for the class = {}", wordsheetId, classId);

        // handle name change
        if (StringUtils.hasText(partialUpdate.name())) {
            classService.renameWordsheet(createClassOwnerId(classId), wordsheetId, partialUpdate.name());
        }
        // handle availability change
        if (BooleanUtils.isTrue(partialUpdate.isShared())) {
            classService.makeWorksheetIsShared(createClassOwnerId(classId), wordsheetId, true);
        }
        return ResponseEntity
                .noContent()
                .build();
    }

    @PostMapping(value = "/{classId}/wordsheets/{wordsheetId}/words")
    public ResponseEntity<WordsheetItemResponse> addToWordsheet(Principal user,
                                                                @PathVariable("classId") String classId,
                                                                @PathVariable("wordsheetId") int wordsheetId,
                                                                @Valid @RequestBody WordsheetItemRequest request, UriComponentsBuilder ucBuilder) {

        LOG.info("Receiving a new word to add request to wordsheet id = {}. User = {}, class id = {}", wordsheetId, user.getName(), classId);

        WordsheetItem item = new WordsheetItem();
        WordRequest word = request.word();
        item.setWord(toWordEntity(word));
        item.setSentences(request.sentences());
        item.setCollocations(request.collocations());

        WordsheetItem addedItem = classService.addToWordsheet(createClassOwnerId(classId), wordsheetId, item);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder
                .path("/{classId}/wordsheets/{wordsheetId}/words/{wordId}")
                .buildAndExpand(classId, wordsheetId, addedItem.getId())
                .toUri()
        );
        WordsheetItemResponse response = toWordSheetItem(addedItem);
        return new ResponseEntity<>(response, headers, HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/{classId}/wordsheets/{wordsheetId}/words/{wordId}")
    public ResponseEntity<WordsheetItemResponse> deleteWordsheetItem(Principal user,
                                                                     @PathVariable("classId") String classId,
                                                                     @PathVariable("wordsheetId") int wordsheetId,
                                                                     @PathVariable("wordId") int wordId) {

        LOG.info("Deleting a word = {} from wordsheet id = {} for the user = {}, classId id = {}", wordId, wordsheetId, user.getName(), classId);

        classService.deleteFromWordsheet(createClassOwnerId(classId), wordsheetId, wordId);

        return ResponseEntity
                .noContent()
                .build();
    }

    private OwnerId createUserOwnerId(String user) {
        return new OwnerId(user, "1");
    }

    private OwnerId createClassOwnerId(String classId) {
        return new OwnerId(classId, "2");
    }

    public ClassDetails createClass(String classId, String name, int attendees, String classFormat,
                                    String classLevel, String material) {
        return new ClassDetails(classId, name, attendees, classFormat, classLevel, material, "notes");
    }

    private WordsheetItemResponse toWordSheetItem(WordsheetItem wordsheet) {
        return new WordsheetItemResponse(
                wordsheet.getWord().getId(),
                toWordResponse(wordsheet.getWord()),
                wordsheet.getSentences()
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

    private WordsheetResponse toResponse(WordsheetListItem wordsheetListItem) {
        return new WordsheetResponse(
                wordsheetListItem.wordsheetId(),
                wordsheetListItem.name(),
                wordsheetListItem.wordsTotal(),
                wordsheetListItem.isShared()
        );
    }

    private Word toWordEntity(WordRequest wordRequest) {
        return new Word(0,
                wordRequest.value(),
                wordRequest.partOfSpeech(),
                wordRequest.transcription(),
                wordRequest.meaning()
        );
    }

}
