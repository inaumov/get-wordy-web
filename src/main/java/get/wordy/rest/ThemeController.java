package get.wordy.rest;

import get.wordy.ai.model.WordDto;
import get.wordy.core.ThemeServiceWrapper;
import get.wordy.core.ThemeService;
import get.wordy.core.api.bean.Theme;
import get.wordy.core.api.bean.Word;
import get.wordy.core.api.bean.WordKey;
import get.wordy.core.api.id.OwnerId;
import get.wordy.model.Explanation;
import get.wordy.model.WordIdRequest;
import get.wordy.model.WordResponse;
import get.wordy.model.themes.ThemeResponse;
import get.wordy.model.themes.ThemeRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.security.Principal;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/themes")
@PreAuthorize("hasAuthority('P_MANAGE_THEMES')")
public class ThemeController {

    private final ThemeService themeService;
    private final ThemeServiceWrapper themeServiceWrapper;

    public ThemeController(ThemeService themeService, ThemeServiceWrapper themeServiceWrapper) {
        this.themeService = themeService;
        this.themeServiceWrapper = themeServiceWrapper;
    }

    @GetMapping
    public ResponseEntity<List<ThemeResponse>> getAllThemes(Principal principal) {

        OwnerId ownerId = createUserOwnerId(principal);
        return ResponseEntity.ok(
                themeService.getAllThemes(ownerId)
                        .stream()
                        .map(this::toResponse)
                        .toList()
        );
    }

    @GetMapping("/{themeId}")
    public ResponseEntity<ThemeResponse> getTheme(Principal principal,
                                                  @PathVariable int themeId) {

        OwnerId ownerId = createUserOwnerId(principal);
        var theme = themeService.getTheme(ownerId, themeId);
        if (theme == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toResponse(theme));
    }

    @PostMapping
    public ResponseEntity<ThemeResponse> createTheme(Principal principal,
                                                     @RequestBody ThemeRequest request) {

        OwnerId ownerId = createUserOwnerId(principal);
        var created = themeService.createTheme(ownerId, request.name());
        return ResponseEntity.ok(toResponse(created));
    }

    @PatchMapping("/{themeId}")
    public ResponseEntity<ThemeResponse> updateThemeName(Principal principal,
                                                         @PathVariable int themeId,
                                                         @RequestBody ThemeRequest request) {

        OwnerId ownerId = createUserOwnerId(principal);
        var updated = themeService.updateThemeName(ownerId, themeId, request.name());
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toResponse(updated));
    }

    @DeleteMapping("/{themeId}")
    public ResponseEntity<Void> deleteTheme(Principal principal,
                                            @PathVariable int themeId) {

        OwnerId ownerId = createUserOwnerId(principal);
        boolean deleted = themeServiceWrapper.deleteTheme(ownerId, themeId);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/{themeId}/words")
    public ResponseEntity<List<WordResponse>> getWords(Principal user,
                                                       @PathVariable("themeId") int themeId) {

        log.info("Getting all words for the user = {}, theme id = {}", user.getName(), themeId);

        OwnerId ownerId = createUserOwnerId(user);

        List<WordResponse> words = themeService.getWords(ownerId, themeId)
                .stream()
                .map(this::toWordResponse)
                .toList();

        return new ResponseEntity<>(words, HttpStatus.OK);
    }

    @PostMapping(value = "/{themeId}/words")
    public ResponseEntity<WordResponse> addToTheme(Principal user,
                                                   @PathVariable("themeId") int themeId,
                                                   @Valid @RequestBody WordIdRequest wordId, UriComponentsBuilder ucBuilder) {

        log.info("Adding new word id = {} to themeId = {}, user = {}", wordId.wordId(), themeId, user.getName());

        Word addedToTheme = themeServiceWrapper.addWordToTheme(createUserOwnerId(user), themeId, wordId.wordId());

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder
                .path("/{themeId}/words/{wordId}")
                .buildAndExpand(themeId, addedToTheme.getId())
                .toUri()
        );
        WordResponse response = toWordResponse(addedToTheme);
        return new ResponseEntity<>(response, headers, HttpStatus.ACCEPTED);
    }

    @DeleteMapping(value = "/{themeId}/words")
    public ResponseEntity<Void> removeFromTheme(Principal user,
                                                @PathVariable("themeId") int themeId,
                                                @Valid @RequestBody WordIdRequest wordId) {

        log.info("Deleting a word = {} from user theme, id = {}, user = {}", wordId, themeId, user.getName());

        themeServiceWrapper.removeWordFromTheme(createUserOwnerId(user), themeId, wordId.wordId());

        return ResponseEntity
                .noContent()
                .build();
    }

    @PostMapping("/{themeId}/generate-draft")
    public ResponseEntity<ThemeResponse> generateDraft(Principal user,
                                                       @PathVariable int themeId,
                                                       @RequestParam(value = "candidatesLimit", required = false, defaultValue = "5") int candidatesLimit) {
        OwnerId ownerId = createUserOwnerId(user);
        log.info("Requested to generate {} words draft for themeId='{}', user='{}'", candidatesLimit, themeId, user.getName());
        themeServiceWrapper.generateDraft(ownerId, themeId, candidatesLimit);
        return ResponseEntity.accepted().build();
    }

    @PutMapping("/{themeId}/confirm")
    public ResponseEntity<ThemeResponse> confirmTheme(Principal user,
                                                      @PathVariable int themeId) {
        OwnerId ownerId = createUserOwnerId(user);
        log.info("Confirming words for themeId='{}', user='{}'", themeId, user.getName());
        themeServiceWrapper.confirmTheme(ownerId, themeId);
        return ResponseEntity.accepted().build();
    }

    @GetMapping(value = "/{themeId}/candidate-words")
    public ResponseEntity<List<WordDto>> getDraft(Principal user,
                                                  @PathVariable("themeId") int themeId) {

        log.info("Getting candidate words (draft) for the user = {}, theme id = {}", user.getName(), themeId);

        OwnerId ownerId = createUserOwnerId(user);
        List<WordDto> wordCandidates = themeServiceWrapper.getCandidateWords(ownerId, themeId);
        return new ResponseEntity<>(wordCandidates, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{themeId}/candidate-words")
    public ResponseEntity<Void> removeFromTheme(Principal user,
                                                @PathVariable("themeId") int themeId,
                                                @Valid @RequestBody WordKey wordKey) {

        log.info("Deleting a candidate word = [{}:{}] from user theme, id = {}, user = {}", wordKey.partOfSpeech(), wordKey.partOfSpeech(), themeId, user.getName());

        themeServiceWrapper.removeCandidateWordsFromTheme(createUserOwnerId(user), themeId, wordKey);
        return ResponseEntity
                .noContent()
                .build();
    }

    private static OwnerId createUserOwnerId(Principal user) {
        return new OwnerId(user.getName(), "user");
    }

    private ThemeResponse toResponse(Theme entity) {
        return new ThemeResponse(
                entity.themeId(),
                entity.name(),
                entity.notes(),
                entity.status(),
                entity.createdAt(),
                entity.lastUpdatedAt(),
                entity.wordsTotal());
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

}
