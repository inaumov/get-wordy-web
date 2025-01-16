package get.wordy.rest;

import get.wordy.core.api.id.OwnerId;
import get.wordy.model.Explanation;
import get.wordy.core.api.IDictionaryService;
import get.wordy.core.api.bean.*;
import get.wordy.model.*;
import jakarta.servlet.http.HttpServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;

@RestController
@RequestMapping(value = "/dictionaries")
public class CardsController extends HttpServlet {
    private static final Logger LOG = LoggerFactory.getLogger(CardsController.class);

    private final IDictionaryService dictionaryService;

    public CardsController(IDictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    @GetMapping(value = "/{dictionaryId}/cards")
    public ResponseEntity<List<CardResponse>> getCards(Principal user, @PathVariable("dictionaryId") int dictionaryId) {

        LOG.info("Getting all cards for the user = {}, dictionary id = {}", user.getName(), dictionaryId);

        List<CardResponse> cards = dictionaryService.getCards(createOwnerId(user), dictionaryId)
                .stream()
                .map(this::toCardResponse)
                .toList();

        if (cards.isEmpty()) {
            LOG.info("No cards found for the user = {}", user.getName());
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
        }
        return new ResponseEntity<>(cards, HttpStatus.OK);
    }

    @GetMapping(value = "/{dictionaryId}/exercise")
    public ResponseEntity<List<ExerciseResponse>> getCardsForExercise(Principal user,
                                                                      @PathVariable("dictionaryId") int dictionaryId,
                                                                      @RequestParam(value = "limit", required = false, defaultValue = "5") int limit) {

        LOG.info("Getting cards to exercise for the user = {}, dictionary id = {}", user.getName(), dictionaryId);

        List<ExerciseResponse> cards = dictionaryService.getCardsForExercise(createOwnerId(user), dictionaryId, limit)
                .stream()
                .map(this::toExerciseResponse)
                .toList();

        if (cards.isEmpty()) {
            LOG.info("No ready to exercise cards found for the user = {}", user.getName());
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
        }
        return new ResponseEntity<>(cards, HttpStatus.OK);
    }

    @PutMapping(value = "/{dictionaryId}/exercise")
    public ResponseEntity<Void> submitExerciseResult(Principal user,
                                                     @PathVariable("dictionaryId") int dictionaryId,
                                                     @RequestBody int[] cardIds) {

        LOG.info("Submitting exercise result for the user = {} and cards: {}", user.getName(), Arrays.toString(cardIds));

        dictionaryService.increaseScoreUp(dictionaryId, cardIds, 25);

        return ResponseEntity
                .accepted()
                .build();
    }

    @PutMapping(value = "/{dictionaryId}/cards/{cardId}/resetScore")
    public ResponseEntity<Void> resetCard(Principal user,
                                          @PathVariable("dictionaryId") int dictionaryId,
                                          @PathVariable("cardId") int cardId) {
        LOG.info("Resetting a card = {} for the user = {}, dictionary id = {}", cardId, user.getName(), dictionaryId);

        dictionaryService.resetScore(cardId);

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
                        .inContext(card.getStrSentences())
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
        return new OwnerId(user.getName(), "1");
    }

}