package get.wordy.rest;

import get.wordy.core.api.IClassAccessService;
import get.wordy.core.api.IClassService;
import get.wordy.core.api.IVocabularyService;
import get.wordy.core.api.bean.ClassInfo;
import get.wordy.core.api.bean.ClassSchedule;
import get.wordy.core.api.bean.Word;
import get.wordy.core.api.bean.Vocabulary;
import get.wordy.core.api.id.OwnerId;
import get.wordy.model.*;
import get.wordy.model.WordResponse;
import jakarta.validation.Valid;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.RandomStringUtils;
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
    private final IVocabularyService vocabularyService;
    private final IClassAccessService accessService;

    public ClassController(IClassService classService, IVocabularyService vocabularyService, IClassAccessService accessService) {
        this.classService = classService;
        this.vocabularyService = vocabularyService;
        this.accessService = accessService;
    }

    @GetMapping
    public ResponseEntity<Map<String, List<ClassInfoResponse>>> getClassesInfo(Principal user,
                                                                               @RequestParam(value = "filter", required = false) Optional<String> dayOfWeekFilter) {
        LOG.info("Getting {} classes info managed by the user = {}", dayOfWeekFilter.isEmpty() ? "all" : dayOfWeekFilter, user.getName());

        // fetch classes and filter by dayOfWeek at the data source level if a filter is provided
        List<ClassInfoResponse> classes = classService.getClassesInfo(createUserOwnerId(user))
                .stream()
                .map(ClassInfoResponse::new)
                .map(this::enrichWithAttendees)
                .filter(classInfo ->
                        dayOfWeekFilter.isEmpty() ||
                                classInfo.getSchedules().stream()
                                        .anyMatch(schedule -> schedule.getDayOfWeek().equalsIgnoreCase(dayOfWeekFilter.get())))
                .toList();

        // group classes by day, while maintaining original order
        Map<String, List<ClassInfoResponse>> groupedClasses = new TreeMap<>();
        for (ClassInfoResponse classInfo : classes) {
            for (ClassSchedule schedule : classInfo.getSchedules()) {
                String day = schedule.getDayOfWeek().toLowerCase(); // ensure uniformity
                if (dayOfWeekFilter.isEmpty() || day.equalsIgnoreCase(dayOfWeekFilter.get())) {
                    groupedClasses.computeIfAbsent(day, k -> new ArrayList<>())
                            .add(classInfo);
                }
            }
        }

        return ResponseEntity.ok(groupedClasses);
    }

    private ClassInfoResponse enrichWithAttendees(ClassInfoResponse response) {
        return response.withAttendees(accessService.getAssignedAttendees(response.getClassId()));
    }

    @PostMapping
    public ResponseEntity<ClassInfoResponse> addClassInfo(Principal user,
                                                          @Valid @RequestBody ClassInfoRequest classInfoRequest) {

        LOG.info("Add new class info = {} request for the user = {}", classInfoRequest.getName(), user.getName());

        var savedClass = classService.saveClassInfo(createUserOwnerId(user), copyClassInfo(classInfoRequest));
        return ResponseEntity.created(URI.create("/" + savedClass.getClassId()))
                .body(new ClassInfoResponse(savedClass));
    }

    @GetMapping(value = "/{classId}")
    public ResponseEntity<ClassInfoResponse> getClassInfo(Principal user,
                                                          @PathVariable("classId") String classId) {
        LOG.info("Getting a class info for the user = {}, class id = {}", user.getName(), classId);

        Optional<ClassInfoResponse> response = Optional.of(classService.findClassInfo(createUserOwnerId(user), classId))
                .map(ClassInfoResponse::new)
                .map(this::enrichWithAttendees);

        return ResponseEntity.ok(response.orElseThrow());
    }

    @DeleteMapping(value = "/{classId}")
    public ResponseEntity<ClassInfo> deleteClassInfo(Principal user, @PathVariable("classId") String classId) {
        LOG.info("Deleting a class info for the user = {}, class id = {}", user.getName(), classId);

        classService.deleteClassInfo(createUserOwnerId(user), classId);

        return ResponseEntity
                .noContent()
                .build();
    }

    @GetMapping(value = "/{classId}/vocabularies")
    public ResponseEntity<List<VocabularyResponse>> getVocabularies(Principal user,
                                                                    @PathVariable("classId") String classId) {
        LOG.info("Getting vocabularies for the class id = {}", classId);

        List<VocabularyResponse> vocabulariesResponse = vocabularyService.getVocabularies(createClassOwnerId(classId))
                .stream()
                .map(this::toResponse)
                .toList();

        if (vocabulariesResponse.isEmpty()) {
            LOG.info("No vocabularies found for the class = {}", classId);
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
        }
        return new ResponseEntity<>(vocabulariesResponse, HttpStatus.OK);
    }

    @PostMapping("/{classId}/vocabularies")
    public ResponseEntity<VocabularyResponse> addVocabulary(Principal user,
                                                            @PathVariable("classId") String classId,
                                                            @Valid @RequestBody VocabularyRequest vocabularyRequest) {
        LOG.info("Adding a new vocabulary = {} for the class = {}", vocabularyRequest.name(), classId);

        var vocabulary = vocabularyService.createVocabulary(createClassOwnerId(classId), vocabularyRequest.name(), null);
        VocabularyResponse response = toResponse(vocabulary);
        return ResponseEntity.created(URI.create("/{classId}/vocabulary/" + vocabulary.getVocabId()))
                .body(response);
    }

    @GetMapping(value = "/{classId}/vocabularies/{vocabId}")
    public ResponseEntity<Map<String, Object>> getVocabulary(Principal user,
                                                             @PathVariable("classId") String classId,
                                                             @PathVariable("vocabId") int vocabId) {

        LOG.info("Getting a vocabulary for the class id = {}, and vocabulary id = {}", classId, vocabId);

        List<Word> vocabWords = vocabularyService.getWords(createClassOwnerId(classId), vocabId);

        if (vocabWords.isEmpty()) {
            LOG.info("No vocabulary found by id = {}", vocabId);
            return new ResponseEntity<>(Collections.emptyMap(), HttpStatus.OK);
        }
        Map<String, Object> vocabularyResponse = Map.of(
                "vocabId", vocabId,
                "name", "Test",
                "words", vocabWords
                        .stream()
                        .map(this::toWordResponse)
                        .toList()
        );

        return new ResponseEntity<>(vocabularyResponse, HttpStatus.OK);
    }

    @PatchMapping("/{classId}/vocabularies/{vocabId}")
    public ResponseEntity<VocabularyResponse> partialUpdate(Principal user,
                                                            @PathVariable("classId") String classId,
                                                            @PathVariable("vocabId") int vocabId,
                                                            @RequestBody VocabularyRequest partialUpdate) {
        LOG.info("Updating vocabulary id = {} for the class = {}", vocabId, classId);

        // handle name change
        if (StringUtils.hasText(partialUpdate.name())) {
            vocabularyService.renameVocabulary(createClassOwnerId(classId), vocabId, partialUpdate.name());
        }
        // handle availability change
        if (BooleanUtils.isTrue(partialUpdate.isShared())) {
            vocabularyService.makeVocabularyIsShared(createClassOwnerId(classId), vocabId, true);
        }
        return ResponseEntity
                .noContent()
                .build();
    }

    @PostMapping(value = "/{classId}/vocabularies/{vocabId}/words")
    public ResponseEntity<WordResponse> addToVocabulary(Principal user,
                                                        @PathVariable("classId") String classId,
                                                        @PathVariable("vocabId") int vocabId,
                                                        @Valid @RequestBody WordIdRequest wordId, UriComponentsBuilder ucBuilder) {

        LOG.info("Receiving a new word to add request to vocabulary id = {}. User = {}, class id = {}", vocabId, user.getName(), classId);

        Word addedToVocabulary = vocabularyService.addToVocabulary(createClassOwnerId(classId), vocabId, wordId.wordId());

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder
                .path("/{classId}/vocabularies/{vocabId}/words/{wordId}")
                .buildAndExpand(classId, vocabId, addedToVocabulary.getId())
                .toUri()
        );
        WordResponse response = toWordResponse(addedToVocabulary);
        return new ResponseEntity<>(response, headers, HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/{classId}/vocabularies/{vocabId}/words/{wordId}")
    public ResponseEntity<Void> removeFromVocabulary(Principal user,
                                                     @PathVariable("classId") String classId,
                                                     @PathVariable("vocabId") int vocabId,
                                                     @PathVariable("wordId") int wordId) {

        LOG.info("Deleting a word = {} from vocabulary id = {} for the user = {}, classId id = {}", wordId, vocabId, user.getName(), classId);

        vocabularyService.removeFromVocabulary(createClassOwnerId(classId), vocabId, wordId);

        return ResponseEntity
                .noContent()
                .build();
    }

    private OwnerId createUserOwnerId(Principal user) {
        return new OwnerId(user.getName(), "user");
    }

    private OwnerId createClassOwnerId(String classId) {
        return new OwnerId(classId, "class");
    }

    public ClassInfo copyClassInfo(ClassInfoRequest request) {
        return new ClassInfo(
                "desna-" + RandomStringUtils.secure().nextAlphanumeric(5),
                request.getName(),
                request.getFormat(),
                request.getLevel(),
                request.getMaterial(),
                request.getNotes(),
                null
        );
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

    private VocabularyResponse toResponse(Vocabulary vocabHeader) {
        return new VocabularyResponse(
                vocabHeader.getVocabId(),
                vocabHeader.getName(),
                vocabHeader.getWordsTotal(),
                vocabHeader.isShared()
        );
    }

}
