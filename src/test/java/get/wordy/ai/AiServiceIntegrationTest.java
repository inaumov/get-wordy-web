package get.wordy.ai;

import com.fasterxml.jackson.databind.json.JsonMapper;
import get.wordy.ai.config.LoggingRestTemplateCustomizer;
import get.wordy.ai.config.RestTemplateConfiguration;
import get.wordy.ai.model.GetExplanationResult;
import get.wordy.config.JacksonConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = {AiService.class, RestTemplateConfiguration.class, LoggingRestTemplateCustomizer.class, JacksonConfiguration.class})
class AiServiceIntegrationTest {

    @Autowired
    private AiService aiService;

    @Autowired
    @Qualifier("openaiRestTemplate")
    private RestTemplate restTemplate;

    @Autowired
    private JsonMapper jsonMapper;

    private MockRestServiceServer mockServer;

    @Value("${openai.api.url}")
    private String apiUrl;

    @Value("classpath:json/ai/expected_content.json")
    private Resource mockFileResource;

    @BeforeEach
    void setup() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void requestWordSearch_shouldParseValidResponse() throws IOException {
        // given
        String searchRequest = "run";

        String innerJson = new String(mockFileResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        String mockResponse = """
                {
                  "choices": [
                    {
                      "message": {
                        "content": %s
                      }
                    }
                  ],
                  "usage": { "total_tokens": 200 }
                }
                """.formatted(jsonMapper.writeValueAsString(innerJson)); // <— escapes automatically
        System.out.println("Content from @Value: " + mockResponse);

        mockServer.expect(requestTo(apiUrl))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(mockResponse, MediaType.APPLICATION_JSON));

        // when
        GetExplanationResult result = aiService.search(searchRequest);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getLemma()).isEqualTo("run");
        assertThat(result.getTranscription()).isEqualTo("rʌn");
        assertThat(result.getExplanations()).hasSize(4);

        var explanation = result.getExplanations().get(1);
        assertThat(explanation.getPartOfSpeech()).isEqualTo("verb");
        assertThat(explanation.getMeaning()).contains("move at a speed");
        assertThat(explanation.getSentences()).contains("She runs every morning to stay fit.");

        mockServer.verify();
    }

    @Test
    void requestWordSearch_shouldHandleRefusalResponse() {
        String mockResponse = """
                    {
                      "choices": [
                        {
                          "message": {
                            "refusal": "Safety policy triggered",
                            "content": "Explanation refused."
                          }
                        }
                      ]
                    }
                """;

        mockServer.expect(requestTo(apiUrl))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(mockResponse, MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> aiService.search("bomb"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Safety policy triggered");

        mockServer.verify();
    }

    @Test
    void requestWordSearch_shouldThrowOnInvalidResponse() {
        String invalidResponse = "{}";

        mockServer.expect(requestTo(apiUrl))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(invalidResponse, MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> aiService.search("test"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No adequate response from AI");

        mockServer.verify();
    }

}
