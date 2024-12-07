package get.wordy.ai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import get.wordy.ai.model.ChatRequest;
import get.wordy.ai.model.ChatResponse;
import get.wordy.ai.model.GetExplanationResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Slf4j
@Service
public class AiService {

    @Value("${openai.model}")
    private String model;

    @Value("${openai.api.url}")
    private String apiUrl;

    @Value("${openai.prompt_template}")
    private String systemPrompt;

    private final RestTemplate restTemplate;

    private final JsonMapper jsonMapper;

    public AiService(JsonMapper jsonMapper, @Qualifier("openaiRestTemplate") RestTemplate restTemplate) {
        this.jsonMapper = jsonMapper;
        this.restTemplate = restTemplate;
    }

    public GetExplanationResult requestWordSearch(String input) {

        // create a request
        ChatRequest chatRequest = new ChatRequest(model, systemPrompt, searchRequest);

        // call the API
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<ChatRequest> request = new HttpEntity<>(chatRequest, headers);

        ResponseEntity<ChatResponse> response = restTemplate.postForEntity(apiUrl, request, ChatResponse.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            var content = extractContent(response);
            try {
                return jsonMapper.readValue(content, GetExplanationResult.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Could not read message.content from AI");
            }
        }

        throw new RuntimeException("OK status from AI expected, but not received. Actual status = " + response.getStatusCode());
    }

    private static String extractContent(ResponseEntity<ChatResponse> response) {
        ChatResponse chatResponse = response.getBody();
        if (chatResponse == null || chatResponse.getChoices() == null || chatResponse.getChoices().isEmpty()) {
            throw new RuntimeException("No adequate response from AI");
        }
        // return the first response
        ChatResponse.Choice first = chatResponse.getChoices().getFirst();
        ChatResponse.Message message = first.getMessage();

        // check if the OpenAI safety system refused the request and generated a refusal instead
        Optional<String> refusal = Optional.ofNullable(message.getRefusal());
        if (refusal.isPresent()) {
            // in this case, the .content field will contain the explanation (if any) that the model generated for why it is refusing
            log.error("Refusal: {}", refusal.get());
            log.error("Refused explanation: {}", message.getContent());
            throw new RuntimeException(refusal.get());
        }
        return message.getContent();
    }

}
