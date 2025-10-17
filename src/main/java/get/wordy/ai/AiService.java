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

@Slf4j
@Service
public class AiService {

    @Value("${openai.model}")
    private String model;

    @Value("${openai.api.url}")
    private String apiUrl;

    @Value("${openai.model.temperature:0.2}")
    private double temperature;

    @Value("${openai.prompt_template}")
    private String systemPrompt;

    private final RestTemplate restTemplate;

    private final JsonMapper jsonMapper;

    public AiService(JsonMapper jsonMapper, @Qualifier("openaiRestTemplate") RestTemplate restTemplate) {
        this.jsonMapper = jsonMapper;
        this.restTemplate = restTemplate;
    }

    public GetExplanationResult search(String input) {

        // create a request
        ChatRequest chatRequest = new ChatRequest(model, systemPrompt, input);
        chatRequest.setTemperature(temperature);

        // call the API
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ChatRequest> request = new HttpEntity<>(chatRequest, headers);

        ResponseEntity<ChatResponse> response = restTemplate.postForEntity(apiUrl, request, ChatResponse.class);
        ChatResponse body = response.getBody();
        if (response.getStatusCode() == HttpStatus.OK) {
            if (body != null && body.getUsage() != null) {
                int total = body.getUsage().getTotalTokens();
                int prompt = body.getUsage().getPromptTokens();
                int completion = body.getUsage().getCompletionTokens();
                log.info("AI token usage — total: {}, prompt: {}, completion: {}", total, prompt, completion);
            }
            var content = extractContent(response);
            try {
                return jsonMapper.readValue(content, GetExplanationResult.class);
            } catch (JsonProcessingException e) {
                log.error("Failed to parse AI response: {}", content, e);
                throw new RuntimeException("Could not read message.content from AI");
            }
        }

        throw new RuntimeException("OK status from AI expected, but not received. Actual status = " + response.getStatusCode());
    }

    private String extractContent(ResponseEntity<ChatResponse> response) {
        ChatResponse chatResponse = response.getBody();
        if (chatResponse == null || chatResponse.getChoices() == null || chatResponse.getChoices().isEmpty()) {
            throw new RuntimeException("No adequate response from AI");
        }
        // return the first response
        ChatResponse.Choice first = chatResponse.getChoices().getFirst();
        if ("length".equals(first.getFinishReason())) {
            log.warn("Response truncated — consider increasing max_tokens.");
        }
        ChatResponse.Message message = first.getMessage();

        // check if the OpenAI safety system refused the request and generated a refusal instead
        if (message.getRefusal() != null) {
            // in this case, the .content field will contain the explanation (if any) that the model generated for why it is refusing
            log.error("Refusal: {}", message.getRefusal());
            log.error("Refused explanation: {}", message.getContent());
            throw new RuntimeException(message.getRefusal());
        }
        return message.getContent();
    }

}
