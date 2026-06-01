package get.wordy.ai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import get.wordy.ai.logger.gemini.UsageLogger;
import get.wordy.ai.schema.gemini.GetExplanationSchema;
import get.wordy.ai.model.GetExplanationResult;
import get.wordy.ai.schema.gemini.ThemeGenerateSchema;
import get.wordy.ai.model.ThemeResult;
import get.wordy.core.api.bean.WordKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.util.List;

@Slf4j
@Service
@ConditionalOnProperty(name = "ai.provider", havingValue = "gemini")
@RequiredArgsConstructor
public class GeminiService implements IVocabularyEnrichmentService {

    @Value("${gemini.model}")
    private String model;

    @Value("${gemini.get_explanation_prompt}")
    private String getExplanationPrompt;

    private final Client client;
    private final JsonMapper jsonMapper;

    @Override
    public GetExplanationResult search(String input) {
        String prompt = """
                %s
                
                Requested lemma:
                %s
                """
                .formatted(getExplanationPrompt, input);

        GenerateContentConfig config =
                GenerateContentConfig.builder()
                        .responseMimeType("application/json")
                        .responseSchema(GetExplanationSchema.build())
                        .build();

        StopWatch watch = new StopWatch();
        GenerateContentResponse response;
        try {
            watch.start();
            response = client.models.generateContent(model, prompt, config);
            watch.stop();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate AI response");
        }

        UsageLogger.log(response, watch);
        String jsonContent = response.text();
        try {
            return jsonMapper.readValue(jsonContent, GetExplanationResult.class);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse gemini response: {}", jsonContent, e);
            throw new RuntimeException("Could not read json content from gemini response");
        }
    }

    @Override
    public ThemeResult generate(String theme) {

        String prompt = """
                Generate exactly 15 English vocabulary words for theme:
                "%s".
                """
                .formatted(theme);

        GenerateContentConfig config =
                GenerateContentConfig.builder()
                        .responseMimeType("application/json")
                        .responseSchema(ThemeGenerateSchema.build())
                        .build();

        StopWatch watch = new StopWatch();
        GenerateContentResponse response;
        try {
            response = client.models.generateContent(model, prompt, config);
            watch.stop();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate AI response");
        }

        UsageLogger.log(response, watch);
        String jsonContent = response.text();
        try {
            return jsonMapper.readValue(jsonContent, ThemeResult.class);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse gemini response: {}", jsonContent, e);
            throw new RuntimeException("Could not read json content from gemini response");
        }

    }

    @Override
    public List<GetExplanationResult> enrich(List<WordKey> words) {
        return List.of();
    }

}
