package get.wordy.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import get.wordy.rest.utils.JsonPrettyPrinter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.DisplayName.class)
public class BaseApiTest {

    final JsonMapper jsonMapper = new JsonMapper();
    final ThreadLocal<String> jSessionIdHolder = ThreadLocal.withInitial(this::login);

    JsonPrettyPrinter jsonPrettyPrinter;

    @BeforeEach
    void setUp() {
        jsonMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
        jsonPrettyPrinter = new JsonPrettyPrinter(jsonMapper);
    }

    public String login() {

        Map<String, String> formData = new HashMap<>();
        formData.put("username", "user");
        formData.put("password", "qwerty");

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/login"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(BaseApiTest.getFormDataAsString(formData)))
                .build();

        try (HttpClient httpClient = HttpClient.newBuilder().build()) {
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            int statusCode = httpResponse.statusCode();
            Optional<String> JSESSIONID = httpResponse.headers().firstValue("Set-Cookie");
            assertEquals(302, statusCode);
            assertTrue(JSESSIONID.isPresent());

            System.out.println("Login response status code: " + statusCode);
            String jSessionId = JSESSIONID.get();
            System.out.println("Login Cookie: " + jSessionId);
            return jSessionId;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getFormDataAsString(Map<String, String> formData) {
        return formData.entrySet()
                .stream()
                .map(singleEntry -> URLEncoder.encode(singleEntry.getKey(), StandardCharsets.UTF_8) + "=" + URLEncoder.encode(singleEntry.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));
    }

    void prettyPrint(String jsonBodyStr) throws JsonProcessingException {
        String prettyJson = jsonPrettyPrinter.prettyPrint(jsonBodyStr);
        System.out.println("Resource API response body received: \n" + prettyJson);
    }

}
