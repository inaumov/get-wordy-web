package get.wordy.ai.logger.gemini;

import com.google.genai.types.GenerateContentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;

@Slf4j
public final class UsageLogger {

    public static void log(GenerateContentResponse response, StopWatch watch) {

        log.debug("Request completed in={} ms", watch.getTotalTimeMillis());

        if (response == null || response.usageMetadata().isEmpty()) {
            return;
        }

        var usage = response.usageMetadata()
                .get();

        log.debug("Token usage: total={}, prompt={}, completion={}",
                usage.totalTokenCount(),
                usage.promptTokenCount(),
                usage.candidatesTokenCount()
        );

        String payload = response.text();
        log.debug("Gemini raw response:\n{}", payload);
    }

}
