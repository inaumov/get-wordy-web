package get.wordy.rest;

import get.wordy.core.api.IClassService;
import get.wordy.core.api.bean.ClassInfo;
import get.wordy.core.api.bean.Word;
import get.wordy.core.api.bean.VocabHeader;
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

    public ClassController(IClassService classService) {
        this.classService = classService;
    }

    @GetMapping
    public ResponseEntity<List<ClassInfoResponse>> getClasses(Principal user,
                                                              @RequestParam(value = "filter", required = false) String dayOfWeek) {
        LOG.info("Getting {} classes managed by the user = {}", StringUtils.hasText(dayOfWeek) ? dayOfWeek : "all", user.getName());

        List<ClassInfoResponse> response = classService.getClasses(createUserOwnerId(user), dayOfWeek)
                .stream()
                .map(ClassInfoResponse::new)
                .map(this::enrichWithAttendees)
                .toList();

        return ResponseEntity.ok(response);
    }

    private ClassInfoResponse enrichWithAttendees(ClassInfoResponse response) {
        return response.withAttendees(List.of("Test"));
    }

    @PostMapping
    public ResponseEntity<ClassInfoResponse> addClass(Principal user, @Valid @RequestBody ClassInfoRequest classInfoRequest) {

        LOG.info("Add new class = {} request for the user = {}", classInfoRequest.getName(), user.getName());

        var savedClass = classService.saveClass(createUserOwnerId(user), copyClassInfo(classInfoRequest));
        return ResponseEntity.created(URI.create("/" + savedClass.getClassId()))
                .body(new ClassInfoResponse(savedClass));
    }

    @DeleteMapping(value = "/{classId}")
    public ResponseEntity<ClassInfo> deleteClass(Principal user, @PathVariable("classId") String classId) {
        LOG.info("Deleting a class for the user = {}, class id = {}", user.getName(), classId);

        classService.deleteClass(createUserOwnerId(user), classId);

        return ResponseEntity
                .noContent()
                .build();
    }

    @GetMapping(value = "/{classId}/vocabularies")
    public ResponseEntity<List<VocabularyResponse>> getVocabularies(Principal user,
                                                                    @PathVariable("classId") String classId) {
        LOG.info("Getting vocabularies for the class id = {}", classId);

        List<VocabularyResponse> vocabulariesResponse = classService.getVocabularies(createUserOwnerId(user), classId)
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

        var vocabulary = classService.createVocabulary(createUserOwnerId(user), classId, vocabularyRequest.name());
        VocabularyResponse response = toResponse(vocabulary);
        return ResponseEntity.created(URI.create("/{classId}/vocabulary/" + vocabulary.vocabId()))
                .body(response);
    }

    @GetMapping(value = "/{classId}/vocabularies/{vocabId}")
    public ResponseEntity<Map<String, Object>> getVocabulary(Principal user,
                                                             @PathVariable("classId") String classId,
                                                             @PathVariable("vocabId") int vocabId) {

        LOG.info("Getting a vocabulary for the class id = {}, and vocabulary id = {}", classId, vocabId);

        List<Word> vocabWords = classService.getWords(createUserOwnerId(user), classId, vocabId);

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
            classService.renameVocabulary(createUserOwnerId(user), classId, vocabId, partialUpdate.name());
        }
        // handle availability change
        if (BooleanUtils.isTrue(partialUpdate.isShared())) {
            classService.makeVocabularyIsShared(createUserOwnerId(user), classId, vocabId, true);
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

        Word addedToVocabulary = classService.addToVocabulary(createUserOwnerId(user), classId, vocabId, wordId.wordId());

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

        classService.removeFromVocabulary(createUserOwnerId(user), classId, vocabId, wordId);

        return ResponseEntity
                .noContent()
                .build();
    }

    private OwnerId createUserOwnerId(Principal user) {
        return new OwnerId(user.getName(), "user");
    }

    public ClassInfo copyClassInfo(ClassInfoRequest request) {
        return new ClassInfo(
                "desna-" + RandomStringUtils.secure().nextAlphanumeric(5),
                request.getName(),
                request.getFormat(),
                request.getLevel(),
                request.getMaterial(),
                request.getNotes()
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
                        .inContext(word.getSentences())
                        .collocations(word.getCollocations())
                        .build()
        );
    }

    private VocabularyResponse toResponse(VocabHeader vocabHeader) {
        return new VocabularyResponse(
                vocabHeader.vocabId(),
                vocabHeader.name(),
                vocabHeader.wordsTotal(),
                vocabHeader.isShared()
        );
    }

}
