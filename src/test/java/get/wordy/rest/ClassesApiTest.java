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

class ClassesApiTest extends BaseApiTest {

    private static final String CLASS_ID_PATTERN = "^[a-zA-Z0-9-]+-[a-zA-Z0-9]{5}$";

    @Test
    public void getClasses() throws URISyntaxException, IOException, InterruptedException {
        try (HttpClient httpClient = HttpClient.newBuilder().build()) {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .header("Cookie", jSessionIdHolder.get())
                    .uri(new URI("http://localhost:8080/api/v1/classes"))
                    .build();

            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            int statusCode = httpResponse.statusCode();
            String responseBody = httpResponse.body();
            prettyPrint(responseBody);

            // all minimal checks

            assertEquals(200, statusCode);

            var arrayNode = jsonMapper.readTree(responseBody);
            assertTrue(arrayNode.isArray(), "is not an array");
            assertFalse(arrayNode.isEmpty(), "no elements in array");

            JsonNode firstClass = null;
            for (JsonNode node : arrayNode) {
                if (node.has("classId")
                        && node.get("classId").asText().equals("desna-4xRg7")) { // predefined
                    firstClass = node;
                    break;
                }
            }
            assertNotNull(firstClass);
            assertEquals("10:30 - 11:30", firstClass.get("name").asText());
            assertEquals("Online VIP", firstClass.get("format").asText());
            assertEquals("TS-09", firstClass.get("material").asText());
            // optional
            assertTrue(firstClass.has("level"));
            assertTrue(firstClass.get("notes").isEmpty());
            // meta
            assertTrue(firstClass.get("attendees").isArray());
            assertEquals("Test", firstClass.get("attendees").get(0).asText());
        }
    }

    @Test
    void addClass() throws URISyntaxException, IOException, InterruptedException {
        try (HttpClient httpClient = HttpClient.newBuilder().build()) {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .header("Cookie", jSessionIdHolder.get())
                    .headers("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .uri(new URI("http://localhost:8080/api/v1/classes"))
                    .POST(HttpRequest.BodyPublishers.ofFile(Paths.get("src/test/resources/json/classes/addClass.json")))
                    .build();

            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            int statusCode = httpResponse.statusCode();
            assertEquals(201, statusCode);
            String responseBody = httpResponse.body();
            prettyPrint(responseBody);

            // all minimal checks
            var newClass = jsonMapper.readTree(responseBody);
            assertTrue(newClass.isObject(), "is not an object");
            assertTrue(newClass.get("classId").asText().matches(CLASS_ID_PATTERN));
            assertEquals("19:00 - 20:00", newClass.get("name").asText());
            assertEquals("Offline group", newClass.get("format").asText());
            assertEquals("BIS - 5", newClass.get("material").asText());
            assertEquals("Elementary", newClass.get("level").asText());
        }
    }

    @Test
    void deleteClass() throws URISyntaxException, IOException, InterruptedException {
        try (HttpClient httpClient = HttpClient.newBuilder().build()) {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .header("Cookie", jSessionIdHolder.get())
                    .uri(new URI("http://localhost:8080/api/v1/classes/desna-4xRg8"))
                    .DELETE()
                    .build();

            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            int statusCode = httpResponse.statusCode();
            assertEquals(204, statusCode);
        }
    }

}
