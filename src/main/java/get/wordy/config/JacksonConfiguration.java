package get.wordy.config;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class JacksonConfiguration {

    @Bean
    @Primary
    public JsonMapper jsonMapper() {
        JsonMapper jsonMapper = new JsonMapper();
        jsonMapper.registerModule(new JavaTimeModule());
        jsonMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        jsonMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        return jsonMapper;
    }

}