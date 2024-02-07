package get.wordy.rest;

import get.wordy.core.api.IDictionaryService;
import get.wordy.core.bean.Card;
import get.wordy.core.bean.Collocation;
import get.wordy.core.bean.Context;
import get.wordy.model.CardRequest;
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
import java.util.stream.Collectors;

@RestController
public class CardsController extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(CardsController.class);

    private final IDictionaryService dictionaryService;

    public CardsController(IDictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    @RequestMapping(value = "/dictionaries/{dictionaryId}/cards", method = RequestMethod.GET)
    public ResponseEntity<Set<Card>> getCards(Principal user,
                                              @PathVariable("dictionaryId") int dictionaryId) {

        log.info("Getting all cards for the user = {}, dictionary Id = {}", user.getName(), dictionaryId);

        Set<Card> cards = dictionaryService.getCards(dictionaryId);

        if (cards.isEmpty()) {
            log.info("No cards found for the user {}.", user.getName());
            return new ResponseEntity<>(Collections.emptySet(), HttpStatus.OK);
        }
        return new ResponseEntity<>(cards, HttpStatus.OK);
    }

    @RequestMapping(value = "/dictionaries/{dictionaryId}/exercise", method = RequestMethod.GET)
    public ResponseEntity<List<Card>> getCardsForExercise(Principal user,
                                                          @PathVariable("dictionaryId") int dictionaryId,
                                                          @RequestParam(value = "limit", required = false, defaultValue = "5") int limit) {

        log.info("Getting cards to exercise for the user = {}, dictionary Id = {}", user.getName(), dictionaryId);

        List<Card> cards = dictionaryService.getCardsForExercise(dictionaryId, limit);

        if (cards.isEmpty()) {
            log.info("No ready to exercise cards found for the user {}.", user.getName());
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
        }
        return new ResponseEntity<>(cards, HttpStatus.OK);
    }

    @RequestMapping(value = "/dictionaries/{dictionaryId}/cards/{cardId}", method = RequestMethod.GET)
    public ResponseEntity<Card> getCard(Principal user,
                                        @PathVariable("dictionaryId") int dictionaryId,
                                        @PathVariable("cardId") int cardId) {

        log.info("Getting all cards for the user = {}, dictionary Id = {}", user.getName(), dictionaryId);
        Card card = dictionaryService.loadCard(cardId);
        return new ResponseEntity<>(card, HttpStatus.OK);
    }

    @RequestMapping(value = "/dictionaries/{dictionaryId}/cards", method = RequestMethod.POST)
    public ResponseEntity<Card> addCard(Principal user,
                                        @PathVariable("dictionaryId") int dictionaryId,
                                        @RequestBody CardRequest cardRequest, UriComponentsBuilder ucBuilder) {

        log.info("Adding a new card for the user = {}, dictionary Id = {}", user.getName(), dictionaryId);

        Card card = new Card();
        card.setWord(cardRequest.getWord());
        card.addContexts(cardRequest.getContexts().stream().map(v -> {
            Context context = new Context();
            context.setExample(v);
            return context;
        }).collect(Collectors.toList()));
        card.addCollocations(cardRequest.getCollocations().stream().map(v -> {
            Collocation context = new Collocation();
            context.setExample(v);
            return context;
        }).collect(Collectors.toList()));
        dictionaryService.addCard(dictionaryId, card);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/dictionaries/{dictionaryId}/cards/{id}").buildAndExpand(card.getId()).toUri());

        return new ResponseEntity<>(card, headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/dictionaries/{dictionaryId}/generate", method = RequestMethod.POST)
    public ResponseEntity<List<Card>> generate(Principal user,
                                               @PathVariable("dictionaryId") int dictionaryId,
                                               @RequestBody String[] wordsArray) {

        log.info("Try generating cards for new words: " + Arrays.toString(wordsArray));

        Set<String> setOfWords = Set.of(wordsArray);
        List<Card> cards = dictionaryService.generateCards(dictionaryId, setOfWords);

        return new ResponseEntity<>(cards, HttpStatus.CREATED);
    }

}