package get.wordy.ai.config;

import com.google.genai.Client;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeminiClientConfig {

    @Bean
    public Client geminiClient(GeminiProperties properties) {
        return Client.builder()
                .apiKey(properties.apiKey())
                .build();
    }

}
