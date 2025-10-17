package get.wordy.rest;

import get.wordy.core.api.IClassAccessService;
import get.wordy.core.api.IClassService;
import get.wordy.core.api.IVocabularyService;
import get.wordy.core.api.bean.ClassInfo;
import get.wordy.core.api.bean.ClassSchedule;
import get.wordy.core.api.bean.Word;
import get.wordy.core.api.bean.Vocabulary;
import get.wordy.core.api.bean.wrapper.VocabularySummary;
import get.wordy.core.api.id.OwnerId;
import get.wordy.core.api.id.OwnersId;
import get.wordy.model.*;
import get.wordy.model.WordResponse;
import jakarta.validation.Valid;
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
import java.util.function.Function;
import java.util.stream.Collectors;

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
    public ResponseEntity<List<ClassInfoResponse>> getClassesInfo(Principal user,
                                                                  @RequestParam(value = "filter", required = false) Optional<String> dayOfWeekFilter) {
        LOG.info("Getting {} classes info managed by the user = {}",
                dayOfWeekFilter.map(String::toUpperCase).orElse("ALL"),
                user.getName());

        // Step 1: Fetch classes
        List<ClassInfo> classInfos = classService.getClassesInfo(createUserOwnerId(user));

        // Step 2: Apply dayOfWeek filter early (if provided)
        if (dayOfWeekFilter.isPresent()) {
            String filter = dayOfWeekFilter.get().toUpperCase();
            classInfos = classInfos.stream()
                    .filter(c -> c.getTimeSlots().stream()
                            .anyMatch(slot -> slot.getDayOfWeek().equalsIgnoreCase(filter)))
                    .toList();
        }

        // Step 3: Convert to response DTOs and enrich with participants
        List<ClassInfoResponse> classResponses = classInfos.stream()
                .map(ClassInfoResponse::new)
                .map(this::enrichWithParticipants)
                .toList();

        // Step 4: Collect class IDs
        Set<String> classIds = classResponses.stream()
                .map(ClassInfoResponse::getClassId)
                .collect(Collectors.toSet());

        // Step 5: Fetch vocabulary summaries
        Map<String, VocabularySummary> summaryMap = vocabularyService
                .findVocabularySummaries(new OwnersId(classIds, "class"))
                .stream()
                .collect(Collectors.toMap(VocabularySummary::ownerId, Function.identity()));

        // Step 6: Enrich responses with vocab summary
        List<ClassInfoResponse> result = classResponses.stream()
                .map(classInfo -> {
                    VocabularySummary summary = summaryMap.get(classInfo.getClassId());
                    return summary != null
                            ? classInfo.withSharedSummary(summary.notSharedCount(), summary.lastUpdatedAt())
                            : classInfo;
                })
                .toList();

        return ResponseEntity.ok(result);
    }

    private ClassInfoResponse enrichWithParticipants(ClassInfoResponse response) {
        return response.withParticipants(accessService.getAssignedParticipants(response.getClassId()));
    }

    @PostMapping
    public ResponseEntity<ClassInfoResponse> addClassInfo(Principal user,
                                                          @Valid @RequestBody ClassInfoRequest classInfoRequest) {

        LOG.info("Add new class info = {} request for the user = {}", classInfoRequest.getName(), user.getName());

        var savedClass = classService.saveClassInfo(createUserOwnerId(user), copyClassInfo(classInfoRequest));
        return ResponseEntity.created(URI.create("/" + savedClass.getClassId()))
                .body(new ClassInfoResponse(savedClass));
    }

    @PutMapping
    public ResponseEntity<ClassInfoResponse> editClassInfo(Principal user,
                                                           @Valid @RequestBody ClassInfoEditRequest classInfoRequest) {

        LOG.info("Update class info = {} request for the user = {}", classInfoRequest.getName(), user.getName());

        var savedClass = classService.editClassInfo(createUserOwnerId(user), copyClassInfo(classInfoRequest));
        return ResponseEntity.accepted()
                .body(new ClassInfoResponse(savedClass));
    }

    @GetMapping(value = "/{classId}")
    public ResponseEntity<ClassInfoResponse> getClassInfo(Principal user,
                                                          @PathVariable("classId") String classId) {
        LOG.info("Getting a class info for the user = {}, class id = {}", user.getName(), classId);

        ClassInfo classInfo = classService.findClassInfo(createUserOwnerId(user), classId);
        return ResponseEntity.ok(enrichWithParticipants(new ClassInfoResponse(classInfo)));
    }

    @DeleteMapping(value = "/{classId}")
    public ResponseEntity<ClassInfo> deleteClass(Principal user, @PathVariable("classId") String classId) {
        LOG.info("Deleting a class by the user = {}, class id = {}", user.getName(), classId);

        classService.deleteClassInfo(createUserOwnerId(user), classId);

        return ResponseEntity
                .noContent()
                .build();
    }

    @PatchMapping(value = "/{classId}")
    public ResponseEntity<ClassInfo> classActivation(Principal user, @PathVariable("classId") String classId,
                                                     @RequestBody ClassActivation activationRequest) {
        LOG.info("Class activation request by the user = {}, class id = {}, set = {}", user.getName(), classId, activationRequest.isActive());

        if (activationRequest.isActive()) {
            accessService.activate(createUserOwnerId(user), classId);
        } else {
            accessService.deactivate(createUserOwnerId(user), classId);
        }

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

        Vocabulary vocabulary = vocabularyService.getVocabulary(createClassOwnerId(classId), vocabId);
        if (vocabulary.getWordsTotal() == 0) {
            Map<String, Object> empty = Map.of(
                    "name", vocabulary.getName(),
                    "vocabId", vocabId,
                    "isShared", vocabulary.isShared(),
                    "updateTime", vocabulary.getUpdateTime(),
                    "wordsTotal", 0,
                    "words", Collections.emptyList()
            );
            return new ResponseEntity<>(empty, HttpStatus.OK);
        }
        List<Word> vocabWords = vocabularyService.getWords(createClassOwnerId(classId), vocabId);

        Map<String, Object> vocabularyResponse = Map.of(
                "name", vocabulary.getName(),
                "vocabId", vocabId,
                "isShared", vocabulary.isShared(),
                "updateTime", vocabulary.getUpdateTime(),
                "wordsTotal", vocabulary.getWordsTotal(),
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
        if (Objects.nonNull(partialUpdate.isShared())) {
            vocabularyService.updateSharing(createClassOwnerId(classId), vocabId, partialUpdate.isShared());
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

        LOG.info("Adding new word = {} to class vocabulary, id = {}, user = {}, class id = {}", wordId, vocabId, user.getName(), classId);

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

    @DeleteMapping(value = "/{classId}/vocabularies/{vocabId}/words")
    public ResponseEntity<Void> removeFromVocabulary(Principal user,
                                                     @PathVariable("classId") String classId,
                                                     @PathVariable("vocabId") int vocabId,
                                                     @Valid @RequestBody WordIdRequest wordId) {

        LOG.info("Deleting a word = {} from class vocabulary, id = {}, user = {}, class id = {}", wordId, vocabId, user.getName(), classId);

        vocabularyService.removeFromVocabulary(createClassOwnerId(classId), vocabId, wordId.wordId());

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
        String classId;
        if (request instanceof ClassInfoEditRequest editRequest) {
            classId = editRequest.getClassId();
        } else {
            classId = "desna-" + RandomStringUtils.secure().nextAlphanumeric(5);
        }
        ClassInfo classInfo = new ClassInfo(
                classId,
                request.getName(),
                request.getFormat(),
                null,
                null,
                request.getNotes(),
                request.getScheduleType() == ScheduleType.REPEATABLE
        );
        if (request.getScheduleType() == ScheduleType.NONE) {
            return classInfo;
        }
        if (request.getScheduleType() == ScheduleType.ONE_TIME) {
            classInfo.setEndDate(request.getEndDate());
        }
        if (request.getScheduleType() == ScheduleType.REPEATABLE) {
            List<ClassSchedule> timeSlots = request.getTimeSlots()
                    .stream()
                    .map(timeSlot -> new ClassSchedule(timeSlot.dayOfWeek(), timeSlot.startTime(), timeSlot.endTime()))
                    .toList();
            classInfo.setTimeSlots(timeSlots);
        }
        return classInfo;
    }

    private WordResponse toWordResponse(Word word) {
        return new WordResponse(
                word.getId(),
                word.getLemma(),
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
                vocabHeader.isShared(),
                vocabHeader.getUpdateTime()
        );
    }

}
