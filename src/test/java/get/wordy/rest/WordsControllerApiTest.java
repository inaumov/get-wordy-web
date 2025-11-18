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

import static org.junit.jupiter.api.Assertions.*;

class WordsControllerApiTest extends BaseApiTest {

    @Test
    void testSearchRequest() throws URISyntaxException, IOException, InterruptedException {
        try (HttpClient httpClient = HttpClient.newBuilder().build()) {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .header("Cookie", jSessionIdHolder.get())
                    .headers("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .uri(new URI("http://localhost:8080/api/v1/words?input=tomato"))
                    .build();

            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            int statusCode = httpResponse.statusCode();
            assertEquals(200, statusCode);
            String responseBody = httpResponse.body();
            prettyPrint(responseBody);

            var searchResponse = jsonMapper.readTree(responseBody);

            // all minimal checks
            assertTrue(searchResponse.isObject(), "is not an object");
            assertTrue(searchResponse.has("value"));
            assertTrue(searchResponse.has("transcription"));
            assertTrue(searchResponse.has("explanations"));

            // check nested object
            JsonNode explanationsArray = searchResponse.get("explanations");
            assertTrue(explanationsArray.isArray());
            JsonNode firstNode = explanationsArray.get(0);

            assertTrue(firstNode.isObject(), "is not an object");
            assertTrue(firstNode.has("partOfSpeech"));
            assertTrue(firstNode.has("meaning"));

            assertTrue(firstNode.has("inContext"));
            assertTrue(firstNode.get("inContext").isArray());

            assertTrue(firstNode.has("collocations"));
            assertTrue(firstNode.get("collocations").isArray());
        }
    }

}
