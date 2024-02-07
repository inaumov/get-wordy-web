package get.wordy.rest;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CardsApiTest {

    private final JsonMapper jsonMapper = new JsonMapper();

    @BeforeEach
    void setUp() {
        jsonMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    }

    @Test
    public void getCardsByDictionaryIdRequestUsingHttpClient() throws URISyntaxException, IOException, InterruptedException {
        try (HttpClient httpClient = HttpClient.newBuilder()
                .authenticator(new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(
                                "admin",
                                "admin".toCharArray());
                    }
                }).build()
        ) {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8080/GetWordyApp/api/v1/dictionaries/1/cards"))
                    .build();

            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            int statusCode = httpResponse.statusCode();
            String responseBody = httpResponse.body();

            // all minimal checks

            assertEquals(200, statusCode);

            var jsonNode = jsonMapper.readTree(responseBody);
            assertTrue(jsonNode.isArray(), "is not an array");
            System.out.println("Dictionaries response status code: " + statusCode);
            System.out.println("Dictionaries response headers: " + httpResponse.headers());
            System.out.println("Dictionaries response body: \n" + responseBody);
        }
    }

}
