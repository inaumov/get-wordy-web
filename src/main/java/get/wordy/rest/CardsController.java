package get.wordy.rest;

import get.wordy.core.api.IDictionaryService;
import get.wordy.core.bean.Card;
import get.wordy.core.bean.Collocation;
import get.wordy.core.bean.Context;
import get.wordy.core.bean.Word;
import get.wordy.model.CardRequest;
import get.wordy.model.CardResponse;
import get.wordy.model.WordRequest;
import get.wordy.model.WordResponse;
import jakarta.servlet.http.HttpServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@RestController
public class CardsController extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(CardsController.class);

    private final IDictionaryService dictionaryService;

    public CardsController(IDictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    @RequestMapping(value = "/dictionaries/{dictionaryId}/cards", method = RequestMethod.GET)
    public ResponseEntity<List<CardResponse>> getCards(Principal user, @PathVariable("dictionaryId") int dictionaryId) {

        log.info("Getting all cards for the user = {}, dictionary id = {}", user.getName(), dictionaryId);

        List<CardResponse> cards = dictionaryService.getCards(dictionaryId)
                .stream()
                .map(this::toCardResponse)
                .toList();

        if (cards.isEmpty()) {
            log.info("No cards found for the user = {}", user.getName());
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
        }
        return new ResponseEntity<>(cards, HttpStatus.OK);
    }

    @RequestMapping(value = "/dictionaries/{dictionaryId}/exercise", method = RequestMethod.GET)
    public ResponseEntity<List<CardResponse>> getCardsForExercise(Principal user,
                                                                  @PathVariable("dictionaryId") int dictionaryId,
                                                                  @RequestParam(value = "limit", required = false, defaultValue = "5") int limit) {

        log.info("Getting cards to exercise for the user = {}, dictionary id = {}", user.getName(), dictionaryId);

        List<CardResponse> cards = dictionaryService.getCardsForExercise(dictionaryId, limit)
                .stream()
                .map(this::toCardResponse)
                .toList();

        if (cards.isEmpty()) {
            log.info("No ready to exercise cards found for the user = {}", user.getName());
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
        }
        return new ResponseEntity<>(cards, HttpStatus.OK);
    }

    @RequestMapping(value = "/dictionaries/{dictionaryId}/cards/{cardId}", method = RequestMethod.GET)
    public ResponseEntity<CardResponse> getCard(Principal user,
                                                @PathVariable("dictionaryId") int dictionaryId,
                                                @PathVariable("cardId") int cardId) {

        log.info("Getting card = {} for the user = {}, dictionary id = {}", cardId, user.getName(), dictionaryId);
        Card card = dictionaryService.loadCard(cardId);
        CardResponse cardResponse = toCardResponse(card);
        return new ResponseEntity<>(cardResponse, HttpStatus.OK);
    }

    @RequestMapping(value = "/dictionaries/{dictionaryId}/cards", method = RequestMethod.POST)
    public ResponseEntity<CardResponse> addCard(Principal user,
                                                @PathVariable("dictionaryId") int dictionaryId,
                                                @RequestBody CardRequest cardRequest, UriComponentsBuilder ucBuilder) {

        log.info("Adding a new card for the user = {}, dictionary id = {}", user.getName(), dictionaryId);

        Card card = new Card();
        card.setWord(toWordEntity(cardRequest.word()));
        card.addContexts(toContextEntityList(cardRequest.contexts()));
        card.addCollocations(toCollocationEntityList(cardRequest.collocations()));
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

    @RequestMapping(value = "/dictionaries/{dictionaryId}/generate", method = RequestMethod.POST)
    public ResponseEntity<List<CardResponse>> generate(Principal user,
                                                       @PathVariable("dictionaryId") int dictionaryId,
                                                       @RequestBody String[] wordsArray) {

        log.info("Generating cards for the user = {} and new words: {}", user.getName(), Arrays.toString(wordsArray));

        Set<String> setOfWords = Set.of(wordsArray);
        List<CardResponse> cards = dictionaryService.generateCards(dictionaryId, setOfWords)
                .stream()
                .map(this::toCardResponse)
                .toList();

        return new ResponseEntity<>(cards, HttpStatus.CREATED);
    }

    private List<Context> toContextEntityList(List<String> contexts) {
        return contexts.stream().map(v -> {
            Context context = new Context();
            context.setExample(v);
            return context;
        }).toList();
    }

    private List<Collocation> toCollocationEntityList(List<String> contexts) {
        return contexts.stream().map(v -> {
            Collocation context = new Collocation();
            context.setExample(v);
            return context;
        }).toList();
    }

    private Word toWordEntity(WordRequest wordRequest) {
        return new Word(0,
                wordRequest.value(),
                wordRequest.partOfSpeech(),
                wordRequest.transcription(),
                wordRequest.meaning()
        );
    }

    private CardResponse toCardResponse(Card card) {
        return new CardResponse(
                card.getId(),
                toWordResponse(card.getWord()),
                card.getContexts().stream().map(Context::getExample).toList(),
                card.getCollocations().stream().map(Collocation::getExample).toList(),
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

}