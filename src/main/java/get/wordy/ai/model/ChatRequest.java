package get.wordy.ai.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ChatRequest {

    private static final Object GET_EXPLANATION_SCHEMA = new GetExplanationSchema();

    private String model;
    private List<Message> messages;
    private int n = 1;
    private double temperature;
    @JsonProperty("response_format")
    private Map<String, Object> responseFormat;

    public ChatRequest(String model, String systemPrompt, String searchRequest) {
        this.model = model;
        // add system prompt
        this.messages = new ArrayList<>();
        this.messages.add(new Message("system", systemPrompt));
        // add the user message
        this.messages.add(new Message("user", searchRequest));
        // instructions to get structured output
        this.responseFormat = Map.of(
                "type", "json_schema",
                "json_schema", Map.of(
                        "name", "get_explanation",
                        "schema", GET_EXPLANATION_SCHEMA,
                        "strict", true
                )
        );
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Message {
        private String role;
        private String content;
    }

}
