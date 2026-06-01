package get.wordy.ai.logger.openai;

import get.wordy.ai.schema.openai.ChatResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;

@Slf4j
public final class UsageLogger {

    private UsageLogger() {
    }

    public static void log(ResponseEntity<ChatResponse> response, StopWatch watch) {
        log.debug("Request completed in={} ms", watch.getTotalTimeMillis());

        if (response == null || response.getStatusCode() != HttpStatus.OK) {
            return;
        }

        ChatResponse body = response.getBody();

        if (body == null || body.getUsage() == null) {
            return;
        }

        ChatResponse.Usage usage = body.getUsage();
        Integer total = usage.getTotalTokens();
        Integer prompt = usage.getPromptTokens();
        Integer completion = usage.getCompletionTokens();

        log.debug("Token usage: total={}, prompt={}, completion={}",
                total,
                prompt,
                completion
        );

        if (body.getChoices() != null && !body.getChoices().isEmpty()) {
            String payload = body.getChoices()
                    .getFirst()
                    .getMessage()
                    .getContent();

            log.debug("OpenAI raw response:\n{}", payload);
        }
    }

}
