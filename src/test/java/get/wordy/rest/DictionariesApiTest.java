package get.wordy.rest;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

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

}
