package get.wordy.webapp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import get.wordy.core.api.IDictionaryService;
import get.wordy.core.bean.Card;
import get.wordy.core.bean.Collocation;
import get.wordy.core.bean.Context;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@WebServlet(urlPatterns = {"/api/v1/cards", "/api/v1/cards/exercise"})
public class CardsServlet extends HttpServlet {

    private IDictionaryService dictionaryService;
    private JsonMapper jsonMapper;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        System.out.println("CardsServlet \"Init\" method called");

        dictionaryService = ServiceHolder.getDictionaryService();

        jsonMapper = new JsonMapper();
        jsonMapper.registerModule(new JavaTimeModule());
        jsonMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        jsonMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse resp) throws IOException {

        String dictionaryIdParam = request.getParameter("dictionaryId");
        String cardIdParam = request.getParameter("cardId");

        String responseStr = "{}";

        if (request.getRequestURI().contains("exercise") && dictionaryIdParam != null) {
            String cardsNumberParam = request.getParameter("limit");
            int dictionaryId = Integer.parseInt(dictionaryIdParam);
            int cardsNumber = Integer.parseInt(cardsNumberParam);
            System.out.println("POST: Exercise action has been received");
            List<Card> cards = dictionaryService.getCardsForExercise(dictionaryId, cardsNumber);
            responseStr = jsonMapper.writeValueAsString(cards);
            // write to response
            resp.setContentType("application/json");
            resp.getWriter().write(responseStr);
            return;
        }

        if (dictionaryIdParam != null) {
            int dictionaryId = Integer.parseInt(dictionaryIdParam);
            Set<Card> cards = dictionaryService.getCards(dictionaryId);
            responseStr = jsonMapper.writeValueAsString(cards);
        } else if (cardIdParam != null) {
            int cardId = Integer.parseInt(cardIdParam);
            Card card = dictionaryService.loadCard(cardId);
            responseStr = jsonMapper.writeValueAsString(card);
        }

        // write to response
        resp.setContentType("application/json");
        resp.getWriter().write(responseStr);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse resp) throws IOException {
        String dictionaryIdParam = request.getParameter("dictionaryId");
        String actionParam = request.getParameter("action");

        int dictionaryId = Integer.parseInt(dictionaryIdParam);

        Object result = switch (actionParam) {
            case "add-card" -> {
                JsonNode jsonNode = jsonMapper.readTree(request.getReader());
                ObjectReader objectReader = jsonMapper.readerFor(CardRequest.class);
                CardRequest cardRequest = objectReader.readValue(jsonNode);
                System.out.println("POST: add-card has been received: " + jsonMapper.writeValueAsString(cardRequest));
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
                yield dictionaryService.addCard(dictionaryId, card);
            }
            case "generate" -> {
                JsonNode jsonNode = jsonMapper.readTree(request.getReader());
                ObjectReader objectReader = jsonMapper.readerForArrayOf(String.class);
                String[] wordsArray = objectReader.readValue(jsonNode);
                System.out.println("POST: generate has been received: " + jsonMapper.writeValueAsString(wordsArray));
                Set<String> setOfWords = Set.of(wordsArray);
                yield dictionaryService.generateCards(dictionaryId, setOfWords);
            }
            default -> throw new IllegalStateException("Not supported action: " + actionParam);
        };

        resp.setContentType("application/json");
        String valueAsString = jsonMapper.writeValueAsString(result);
        resp.getWriter().write(valueAsString);
    }

}