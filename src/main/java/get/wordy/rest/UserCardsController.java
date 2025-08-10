package get.wordy.rest;

import get.wordy.core.api.IUserCardsService;
import get.wordy.core.api.id.OwnerId;
import get.wordy.model.Explanation;
import get.wordy.core.api.bean.*;
import get.wordy.model.*;
import jakarta.servlet.http.HttpServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;

@RestController
@PreAuthorize("hasAuthority('P_MANAGE_OWN_VOCAB')")
@RequestMapping(value = "/user/my-vocabularies")
public class UserCardsController extends HttpServlet {
    private static final Logger LOG = LoggerFactory.getLogger(UserCardsController.class);

    private final IUserCardsService userCardsService;

    public UserCardsController(IUserCardsService userCardsService) {
        this.userCardsService = userCardsService;
    }

    @GetMapping(value = "/{vocabId}/cards")
    public ResponseEntity<List<CardResponse>> getCards(Principal user, @PathVariable("vocabId") int vocabId) {

        LOG.info("Getting all cards for the user = {}, vocab id = {}", user.getName(), vocabId);

        OwnerId ownerId = createOwnerId(user);

        List<CardResponse> cards = userCardsService.getCards(ownerId, vocabId)
                .stream()
                .map(CardResponse::fromCard)
                .toList();

        return new ResponseEntity<>(cards, HttpStatus.OK);
    }

    @GetMapping(value = "/{vocabId}/exercise")
    public ResponseEntity<List<ExerciseResponse>> getCardsForExercise(Principal user,
                                                                      @PathVariable("vocabId") int vocabId,
                                                                      @RequestParam(value = "limit", required = false, defaultValue = "5") int limit) {

        LOG.info("Getting cards to exercise for the user = {}, vocab id = {}", user.getName(), vocabId);

        List<ExerciseResponse> cards = userCardsService.getCardsForExercise(createOwnerId(user), vocabId, limit)
                .stream()
                .map(this::toExerciseResponse)
                .toList();

        if (cards.isEmpty()) {
            LOG.info("No ready to exercise cards found for the user = {}", user.getName());
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
        }
        return new ResponseEntity<>(cards, HttpStatus.OK);
    }

    @PutMapping(value = "/{vocabId}/saveProgress")
    public ResponseEntity<Void> saveProgress(Principal user,
                                             @PathVariable("vocabId") int vocabId,
                                             @RequestBody int[] wordIds) {

        LOG.info("Submitting exercise result for the user = {}, received word refs: {}", user.getName(), Arrays.toString(wordIds));

        userCardsService.saveProgress(createOwnerId(user), vocabId, wordIds, 12);

        return ResponseEntity
                .accepted()
                .build();
    }

    @PutMapping(value = "/{vocabId}/cards/{wordId}/resetProgress")
    public ResponseEntity<Void> resetProgress(Principal user,
                                              @PathVariable("vocabId") int vocabId,
                                              @PathVariable("wordId") int wordId) {
        LOG.info("Resetting a card = {} for the user = {}, vocab id = {}", wordId, user.getName(), vocabId);

        userCardsService.resetProgress(createOwnerId(user), vocabId, wordId);

        return ResponseEntity
                .accepted()
                .build();
    }

    private ExerciseResponse toExerciseResponse(Exercise exercise) {
        return new ExerciseResponse(
                exercise.getWordId(),
                exercise.getWord().getValue(),
                Explanation.builder()
                        .partOfSpeech(exercise.getWord().getPartOfSpeech())
                        .meaning(exercise.getWord().getMeaning())
                        .build(),
                toSentencesResponse(exercise.getSentences())
        );
    }

    private List<SentenceResponse> toSentencesResponse(List<Sentence> sentences) {
        return sentences
                .stream()
                .map(sentence -> {
                    String originalSentenceStr = sentence.getExample();
                    String matchedWords = sentence.getMatchedWords();
                    if (StringUtils.hasText(matchedWords)) {
                        String replacedSentence = prepareReplacedSentence(originalSentenceStr, matchedWords);
                        return new SentenceResponse(originalSentenceStr, matchedWords, replacedSentence);
                    } else {
                        return new SentenceResponse(originalSentenceStr, null, null);
                    }
                })
                .toList();
    }

    private String prepareReplacedSentence(String originalSentence, String matchedWords) {
        String[] matchedWordsArr = matchedWords.split("\\s+");
        StringBuilder replacement = new StringBuilder();

        for (int i = 0; i < matchedWordsArr.length; i++) {
            String word = matchedWordsArr[i];
            String repeatedUnderscores = "_".repeat(word.length());
            replacement.append(repeatedUnderscores);
            if (i < matchedWordsArr.length - 1) {
                replacement.append(" "); // Append whitespace if it's not the last word
            }
        }

        return originalSentence.replaceAll("(?i)" + matchedWords, replacement.toString());
    }

    private static OwnerId createOwnerId(Principal user) {
        return new OwnerId(user.getName(), "user");
    }

}