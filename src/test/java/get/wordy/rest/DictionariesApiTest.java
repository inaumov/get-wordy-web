package get.wordy.rest;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Paths;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class DictionariesApiTest extends BaseApiTest {

    private static final String IMAGE_URL_PATTERN = "(http(s?):/)(/[^/]+)+\\.(?:jpg|jpeg|png)";

    @Test
    public void getDictionaries() throws URISyntaxException, IOException, InterruptedException {
        try (HttpClient httpClient = HttpClient.newBuilder().build()) {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .header("Cookie", jSessionIdHolder.get())
                    .uri(new URI("http://localhost:8080/api/v1/dictionaries"))
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

            boolean foundOneExpected = false;
            for (JsonNode dictionary : jsonNode) {
                assertTrue(dictionary.get("dictionaryId").asInt() > 0);
                assertFalse(dictionary.get("name").asText().isBlank());
                if ("Random words".equals(dictionary.get("name").asText())) {
                    assertTrue(dictionary.get("cardsTotal").asInt() > 0);
                    assertTrue(dictionary.get("picture").asText().matches(IMAGE_URL_PATTERN));
                    foundOneExpected = true;
                }
                assertTrue(dictionary.has("picture"));
                assertTrue(dictionary.has("cardsTotal"));
            }
            if (!foundOneExpected) {
                fail("no expected dictionary");
            }
        }
    }

    @Test
    void submitExerciseResult() throws URISyntaxException, IOException, InterruptedException {
        int[] cardIds = IntStream.of(1, 2, 3, 77, 3, 77, 77)
                .toArray();
        try (HttpClient httpClient = HttpClient.newBuilder().build()) {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .header("Cookie", jSessionIdHolder.get())
                    .headers("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .uri(new URI("http://localhost:8080/api/v1/dictionaries/1/exercise"))
                    .PUT(HttpRequest.BodyPublishers.ofString(jsonMapper.writeValueAsString(cardIds)))
                    .build();

            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            int statusCode = httpResponse.statusCode();
            assertEquals(202, statusCode);
        }
    }

    @Test
    void createDictionary() throws URISyntaxException, IOException, InterruptedException {
        try (HttpClient httpClient = HttpClient.newBuilder().build()) {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .header("Cookie", jSessionIdHolder.get())
                    .headers("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .uri(new URI("http://localhost:8080/api/v1/dictionaries"))
                    .POST(HttpRequest.BodyPublishers.ofFile(Paths.get("src/test/resources/json/dictionaries/createDictionary.json")))
                    .build();

            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            int statusCode = httpResponse.statusCode();
            assertEquals(201, statusCode);
            String responseBody = httpResponse.body();
            prettyPrint(responseBody);

            // all minimal checks
            var dictionary = jsonMapper.readTree(responseBody);
            assertTrue(dictionary.isObject(), "is not an object");
            assertTrue(dictionary.get("dictionaryId").asInt() > 0, "cardId must be greater than 0");
            assertEquals("New dictionary. Test", dictionary.get("name").asText());
            assertTrue(dictionary.get("picture").asText().matches(IMAGE_URL_PATTERN));
            assertEquals(0, dictionary.get("cardsTotal").asInt());
        }
    }

    @Test
    void deleteDictionary() throws URISyntaxException, IOException, InterruptedException {
        try (HttpClient httpClient = HttpClient.newBuilder().build()) {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .header("Cookie", jSessionIdHolder.get())
                    .uri(new URI("http://localhost:8080/api/v1/dictionaries/8"))
                    .DELETE()
                    .build();

            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            int statusCode = httpResponse.statusCode();
            assertEquals(204, statusCode);
        }
    }

}
