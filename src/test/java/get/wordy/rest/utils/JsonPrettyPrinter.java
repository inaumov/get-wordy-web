package get.wordy.rest.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;

public class JsonPrettyPrinter {

    private final JsonMapper jsonMapper;

    public JsonPrettyPrinter(JsonMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
        this.jsonMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public String prettyPrint(String uglyJsonString) throws JsonProcessingException {
        Object jsonObject = jsonMapper.readValue(uglyJsonString, Object.class);
        return jsonMapper.writeValueAsString(jsonObject);
    }

    public String prettyPrint(JsonNode object) throws JsonProcessingException {
        return jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
    }

}
