package get.wordy.rest;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class CardsApiTest extends BaseApiTest {

    @Test
    public void getCardsByDictionaryId() throws URISyntaxException, IOException, InterruptedException {
        try (HttpClient httpClient = HttpClient.newBuilder().build()) {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .header("Cookie", jSessionIdHolder.get())
                    .uri(new URI("http://localhost:8080/api/v1/dictionaries/1/cards"))
                    .build();

            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            int statusCode = httpResponse.statusCode();
            String responseBody = httpResponse.body();
            prettyPrint(responseBody);

            // all minimal checks

            assertEquals(200, statusCode);

            var jsonNode = jsonMapper.readTree(responseBody);
            assertTrue(jsonNode.isArray(), "is not an array");
            assertFalse(jsonNode.isEmpty(), "no elements in array");

            for (JsonNode card : jsonNode) {
                assertTrue(card.get("cardId").asInt() > 0, "cardId must be greater than 0");
                assertTrue(card.get("wordId").asInt() > 0, "wordId must be greater than 0");
                assertWordInCard(card);
                assertTrue(card.get("sentences").isArray());
                assertTrue(card.get("collocations").isArray());
                assertBasicCardInfo(card);
            }
        }
    }

    @Test
    public void getCardsForExerciseByDictionaryId() throws URISyntaxException, IOException, InterruptedException {
        try (HttpClient httpClient = HttpClient.newBuilder().build()) {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .header("Cookie", jSessionIdHolder.get())
                    .uri(new URI("http://localhost:8080/api/v1/dictionaries/1/exercise?limit=4"))
                    .build();

            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            int statusCode = httpResponse.statusCode();
            String responseBody = httpResponse.body();
            prettyPrint(responseBody);

            // all minimal checks

            assertEquals(200, statusCode);

            var jsonNode = jsonMapper.readTree(responseBody);
            assertTrue(jsonNode.isArray(), "is not an array");
            assertEquals(4, jsonNode.size(), "no elements in array");

            for (JsonNode card : jsonNode) {
                assertTrue(card.get("cardId").asInt() > 0, "cardId must be greater than 0");
                assertTrue(card.get("wordId").asInt() > 0, "wordId must be greater than 0");
                assertWordInCard(card);
                JsonNode sentences = card.get("sentences");
                assertTrue(sentences.isArray());
                assertFalse(sentences.isEmpty());
                assertFalse(sentences.get(0).get("fullSentence").asText().isBlank());
                assertFalse(sentences.get(0).get("matchedWord").asText().isBlank());
            }
        }
    }

    @Test
    public void getCardById() throws URISyntaxException, IOException, InterruptedException {
        try (HttpClient httpClient = HttpClient.newBuilder().build()) {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .header("Cookie", jSessionIdHolder.get())
                    .uri(new URI("http://localhost:8080/api/v1/dictionaries/1/cards/1"))
                    .build();

            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            int statusCode = httpResponse.statusCode();
            String responseBody = httpResponse.body();
            prettyPrint(responseBody);

            // all minimal checks

            assertEquals(200, statusCode);

            var card = jsonMapper.readTree(responseBody);
            assertTrue(card.isObject(), "is not an object");
            assertTrue(card.get("cardId").asInt() > 0, "cardId must be greater than 0");
            assertTrue(card.get("wordId").asInt() > 0, "wordId must be greater than 0");
            assertWordInCard(card);
            JsonNode sentences = card.get("sentences");
            assertTrue(sentences.isArray());
            assertFalse(sentences.isEmpty());
            assertFalse(sentences.get(0).get("fullSentence").asText().isBlank());
            assertFalse(sentences.get(0).get("matchedWord").asText().isBlank());
            assertTrue(card.get("collocations").isArray());
            assertBasicCardInfo(card);
        }
    }

    @Test
    void addCard() throws URISyntaxException, IOException, InterruptedException {
        try (HttpClient httpClient = HttpClient.newBuilder().build()) {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .header("Cookie", jSessionIdHolder.get())
                    .headers("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .uri(new URI("http://localhost:8080/api/v1/dictionaries/1/cards"))
                    .POST(HttpRequest.BodyPublishers.ofFile(Paths.get("src/test/resources/json/cards/addCard.json")))
                    .build();

            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            int statusCode = httpResponse.statusCode();
            String responseBody = httpResponse.body();
            prettyPrint(responseBody);

            // all minimal checks

            assertEquals(201, statusCode);

            var card = jsonMapper.readTree(responseBody);
            assertTrue(card.isObject(), "is not an object");
            assertTrue(card.get("cardId").asInt() > 0, "cardId must be greater than 0");
            assertTrue(card.get("wordId").asInt() > 0, "wordId must be greater than 0");
            assertWordInCard(card);
            assertTrue(card.get("sentences").isArray());
            assertEquals(2, card.get("sentences").size());
            assertBasicCardInfo(card);
            assertEquals("EDIT", card.get("status").asText());
        }
    }

    private static void assertWordInCard(JsonNode card) {
        assertTrue(card.has("word"));
        JsonNode word = card.get("word");
        assertTrue(word.isObject());
        assertTrue(word.get("wordId").asInt() > 0, "wordId must be greater than 0");
        assertTrue(word.has("value"));
        assertTrue(word.has("partOfSpeech"));
        assertTrue(word.has("transcription"));
        assertTrue(word.has("meaning"));
    }

    private static void assertBasicCardInfo(JsonNode card) {
        assertFalse(card.get("status").asText().isBlank());
        int score = card.get("score").asInt();
        assertTrue(score >= 0 && score <= 100);
        assertTrue(card.has("createdAt"));
    }

}
