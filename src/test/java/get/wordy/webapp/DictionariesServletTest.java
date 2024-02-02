package get.wordy.webapp;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DictionariesServletTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    }

    @Test
    public void getDictionariesRequestUsingHttpClient() throws URISyntaxException, IOException, InterruptedException {
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8080/GetWordyAdmin/api/v1/dictionaries"))
                    .GET()
                    .build();

            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            int statusCode = httpResponse.statusCode();
            String responseBody = httpResponse.body();

            // all minimal checks

            assertEquals(200, statusCode);

            objectMapper.readTree(responseBody);

            System.out.println("Dictionaries response status code: " + statusCode);
            System.out.println("Dictionaries response headers: " + httpResponse.headers());
            System.out.println("Dictionaries response body: \n" + responseBody);
        }
    }

}
