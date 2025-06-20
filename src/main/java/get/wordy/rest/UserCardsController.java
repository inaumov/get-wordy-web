package get.wordy.rest;

import get.wordy.core.api.id.OwnerId;
import get.wordy.model.Explanation;
import get.wordy.core.api.IUserCardsService;
import get.wordy.core.api.bean.*;
import get.wordy.model.*;
import jakarta.servlet.http.HttpServlet;
import jakarta.validation.Valid;
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

        List<CardResponse> cards = userCardsService.getCards(createOwnerId(user), vocabId)
                .stream()
                .map(this::toCardResponse)
                .toList();

        if (cards.isEmpty()) {
            LOG.info("No cards found for the user = {}", user.getName());
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
        }
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

    @PutMapping(value = "/{vocabId}/exercise")
    public ResponseEntity<Void> submitExerciseResult(Principal user,
                                                     @PathVariable("vocabId") int vocabId,
                                                     @RequestBody int[] cardIds) {

        LOG.info("Submitting exercise result for the user = {} and cards: {}", user.getName(), Arrays.toString(cardIds));

        userCardsService.increaseScoreUp(createOwnerId(user), vocabId, cardIds, 12);

        return ResponseEntity
                .accepted()
                .build();
    }

    @DeleteMapping(value = "/{vocabId}/cards")
    public ResponseEntity<Void> deleteCard(Principal user,
                                           @PathVariable("vocabId") int vocabId,
                                           @Valid @RequestBody CardIdRequest cardId) {
        LOG.info("Deleting a card = {} for the user = {}, vocab id = {}", cardId, user.getName(), vocabId);

        userCardsService.deleteCard(createOwnerId(user), cardId.cardId());

        return ResponseEntity
                .noContent()
                .build();
    }

    @PutMapping(value = "/{vocabId}/cards/{cardId}/resetScore")
    public ResponseEntity<Void> resetCard(Principal user,
                                          @PathVariable("vocabId") int vocabId,
                                          @PathVariable("cardId") int cardId) {
        LOG.info("Resetting a card = {} for the user = {}, vocab id = {}", cardId, user.getName(), vocabId);

        userCardsService.resetScore(createOwnerId(user), cardId);

        return ResponseEntity
                .accepted()
                .build();
    }

/*
    @PostMapping(value = "/{dictionaryId}/generate")
    public ResponseEntity<List<CardResponse>> generate(Principal user,
                                                       @PathVariable("dictionaryId") int dictionaryId,
                                                       @RequestBody String[] wordsArray) {

        Set<String> uniqueWords = new LinkedHashSet<>(wordsArray.length);
        Collections.addAll(uniqueWords, wordsArray);

        LOG.info("Generating cards for the user = {} and new words: {}", user.getName(), uniqueWords);

        List<CardResponse> cards = dictionaryService.generateCards(createOwnerId(user), dictionaryId, uniqueWords)
                .stream()
                .map(this::toCardResponse)
                .toList();

        return new ResponseEntity<>(cards, HttpStatus.ACCEPTED);
    }
*/

    private CardResponse toCardResponse(Card card) {
        return new CardResponse(
                card.getId(),
                card.getStatus(),
                card.getScore(),
                card.getWord().getValue(),
                Explanation.builder()
                        .partOfSpeech(card.getWord().getPartOfSpeech())
                        .meaning(card.getWord().getMeaning())
                        .collocations(card.getWord().getCollocations())
                        .inContext(card.getWord().getStrSentences())
                        .build()
        );
    }

    private ExerciseResponse toExerciseResponse(Exercise exercise) {
        return new ExerciseResponse(
                exercise.getCardId(),
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