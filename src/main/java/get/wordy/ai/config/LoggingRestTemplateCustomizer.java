package get.wordy.ai.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "openai", name = "client.logging.http.enabled", havingValue = "true")
public class LoggingRestTemplateCustomizer implements RestTemplateCustomizer {

    public LoggingRestTemplateCustomizer() {
    }

    @Override
    public void customize(@Qualifier("restTemplateConfiguration") RestTemplate restTemplate) {
        BufferingClientHttpRequestFactory requestFactory = new BufferingClientHttpRequestFactory(clientHttpRequestFactory());
        restTemplate.setRequestFactory(requestFactory);
        restTemplate.getInterceptors().add(createClientHttpRequestInterceptor());
    }

    private ClientHttpRequestFactory clientHttpRequestFactory() {
        return new SimpleClientHttpRequestFactory();
    }

    private ClientHttpRequestInterceptor createClientHttpRequestInterceptor() {
        return (request, body, execution) -> {

            logRequest(request, body);
            var response = execution.execute(request, body);
            logResponse(response);
            return response;
        };
    }

    private void logRequest(HttpRequest request, byte[] body) {
        log.trace("Request URI: {}", request.getURI());
        log.trace("Request Method: {}", request.getMethod());
        log.trace("Request Headers: {}", request.getHeaders());
        log.trace("Request Body: {}", new String(body, StandardCharsets.UTF_8));
    }

    private void logResponse(ClientHttpResponse response) {

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.getBody(), StandardCharsets.UTF_8))) {
            String rawResponse = reader
                    .lines()
                    .reduce("", (accumulator, actual) -> accumulator + actual);

            log.trace("Response Status Code: {}", response.getStatusCode());
            log.trace("Response Headers: {}", response.getHeaders());
            log.trace("Response Body: {}", rawResponse);
        } catch (Exception e) {
            log.error("Error reading response body", e);
        }

    }

}
