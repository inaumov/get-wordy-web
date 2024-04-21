package get.wordy.rest;

import get.wordy.spelling.SentenceSplitter;
import get.wordy.core.api.IDictionaryService;
import get.wordy.core.api.bean.*;
import get.wordy.core.api.exception.DictionaryNotFoundException;
import get.wordy.model.*;
import jakarta.servlet.http.HttpServlet;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

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
    public ResponseEntity<List<CardListResponse>> getCards(Principal user, @PathVariable("dictionaryId") int dictionaryId) {

        LOG.info("Getting all cards for the user = {}, dictionary id = {}", user.getName(), dictionaryId);

        List<CardListResponse> cards = dictionaryService.getCards(dictionaryId)
                .stream()
                .map(this::toCardListResponse)
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

        List<ExerciseResponse> cards = dictionaryService.getCardsForExercise(dictionaryId, limit)
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

    @GetMapping(value = "/{dictionaryId}/cards/{cardId}")
    public ResponseEntity<CardResponse> getCard(Principal user,
                                                @PathVariable("dictionaryId") int dictionaryId,
                                                @PathVariable("cardId") int cardId) {

        LOG.info("Getting card = {} for the user = {}, dictionary id = {}", cardId, user.getName(), dictionaryId);
        Card card = dictionaryService.loadCard(cardId);
        CardResponse cardResponse = toCardResponse(card);
        return new ResponseEntity<>(cardResponse, HttpStatus.OK);
    }

    @PostMapping(value = "/{dictionaryId}/cards")
    public ResponseEntity<CardResponse> addCard(Principal user,
                                                @PathVariable("dictionaryId") int dictionaryId,
                                                @RequestBody CardRequest cardRequest, UriComponentsBuilder ucBuilder) {

        LOG.info("Adding a new card for the user = {}, dictionary id = {}", user.getName(), dictionaryId);

        Card card = new Card();
        WordRequest word = cardRequest.word();
        card.setWord(toWordEntity(word));
        card.setSentences(cardRequest.sentences()
                .stream()
                .map(strSentence -> withClosestMatch(strSentence, cardRequest.getKeyword()))
                .toList());
        card.setCollocations(cardRequest.collocations());
        Card addedCard = dictionaryService.addCard(dictionaryId, card);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder
                .path("/dictionaries/{dictionaryId}/cards/{cardId}")
                .buildAndExpand(dictionaryId, card.getId())
                .toUri()
        );
        CardResponse cardResponse = toCardResponse(addedCard);
        return new ResponseEntity<>(cardResponse, headers, HttpStatus.CREATED);
    }

    @PutMapping(value = "/{dictionaryId}/cards")
    public ResponseEntity<CardResponse> updateCard(Principal user,
                                                   UriComponentsBuilder ucBuilder,
                                                   @PathVariable("dictionaryId") int dictionaryId,
                                                   @Valid @RequestBody UpdateCardRequest cardRequest) {

        LOG.info("Update a card for the user = {}, dictionary id = {}", user.getName(), dictionaryId);

        Card card = new Card();
        card.setId(cardRequest.cardId());
        card.setWordId(cardRequest.wordId());
        Word wordEntity = toWordEntity(cardRequest.word());
        card.setWord(wordEntity.withId(cardRequest.wordId()));
        card.setSentences(cardRequest.sentences()
                .stream()
                .map(strSentence -> withClosestMatch(strSentence, cardRequest.getKeyword()))
                .toList());
        card.setCollocations(cardRequest.collocations());
        Card addedCard = dictionaryService.updateCard(dictionaryId, card);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder
                .path("/dictionaries/{dictionaryId}/cards/{cardId}")
                .buildAndExpand(dictionaryId, card.getId())
                .toUri()
        );
        CardResponse cardResponse = toCardResponse(addedCard);
        return new ResponseEntity<>(cardResponse, headers, HttpStatus.ACCEPTED);
    }

    @PutMapping(value = "/{dictionaryId}/cards/{cardId}/resetScore")
    public ResponseEntity<CardResponse> resetCard(Principal user,
                                                  @PathVariable("dictionaryId") int dictionaryId,
                                                  @PathVariable("cardId") int cardId) {
        LOG.info("Resetting a card = {} for the user = {}, dictionary id = {}", cardId, user.getName(), dictionaryId);

        dictionaryService.getDictionaries()
                .stream()
                .filter(dictionary -> dictionary.getId() == dictionaryId)
                .findAny()
                .orElseThrow(DictionaryNotFoundException::new);

        dictionaryService.resetScore(cardId);

        return ResponseEntity
                .accepted()
                .build();
    }

    @DeleteMapping(value = "/{dictionaryId}/cards/{cardId}")
    public ResponseEntity<CardResponse> deleteCard(Principal user,
                                                   @PathVariable("dictionaryId") int dictionaryId,
                                                   @PathVariable("cardId") int cardId) {
        LOG.info("Deleting a card = {} for the user = {}, dictionary id = {}", cardId, user.getName(), dictionaryId);

        dictionaryService.getDictionaries()
                .stream()
                .filter(dictionary -> dictionary.getId() == dictionaryId)
                .findAny()
                .orElseThrow(DictionaryNotFoundException::new);

        dictionaryService.deleteCard(cardId);

        return ResponseEntity
                .noContent()
                .build();
    }

    @PostMapping(value = "/{dictionaryId}/generate")
    public ResponseEntity<List<CardResponse>> generate(Principal user,
                                                       @PathVariable("dictionaryId") int dictionaryId,
                                                       @RequestBody String[] wordsArray) {

        Set<String> uniqueWords = new LinkedHashSet<>(wordsArray.length);
        Collections.addAll(uniqueWords, wordsArray);

        LOG.info("Generating cards for the user = {} and new words: {}", user.getName(), uniqueWords);

        List<CardResponse> cards = dictionaryService.generateCards(dictionaryId, uniqueWords)
                .stream()
                .map(this::toCardResponse)
                .toList();

        return new ResponseEntity<>(cards, HttpStatus.ACCEPTED);
    }

    private Word toWordEntity(WordRequest wordRequest) {
        return new Word(0,
                wordRequest.value(),
                wordRequest.partOfSpeech(),
                wordRequest.transcription(),
                wordRequest.meaning()
        );
    }

    private CardListResponse toCardListResponse(Card card) {
        return new CardListResponse(
                card.getId(),
                card.getWordId(),
                toWordResponse(card.getWord()),
                card.getStrSentences(),
                card.getCollocations(),
                card.getStatus(),
                card.getScore(),
                card.getInsertedAt()
        );
    }

    private CardResponse toCardResponse(Card card) {
        return new CardResponse(
                card.getId(),
                card.getWordId(),
                toWordResponse(card.getWord()),
                toSentencesResponse(card.getSentences()),
                card.getCollocations(),
                card.getStatus(),
                card.getScore(),
                card.getInsertedAt()
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

    private ExerciseResponse toExerciseResponse(Exercise exercise) {
        return new ExerciseResponse(
                exercise.getCardId(),
                exercise.getWordId(),
                toWordResponse(exercise.getWord()),
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

    private static Sentence withClosestMatch(String strSentence, String keyword) {
        Sentence sentence = new Sentence(strSentence);
        Optional<SentenceSplitter.Chunks> sentenceChunks = SentenceSplitter.splitByClosestMatch(strSentence, keyword);
        return sentenceChunks
                .map(chunks -> {
                    LOG.debug("Sentence split for \"{}\", with keyword \"{}\" closest match: {}", strSentence, keyword, chunks);
                    return sentence.withMatchedWords(chunks.matchedWords());
                })
                .orElse(sentence);
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

}