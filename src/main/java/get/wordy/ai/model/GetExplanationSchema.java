package get.wordy.ai.model;

import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
class GetExplanationSchema {

    private String type = "object";
    private LinkedHashMap<String, Object> properties;
    private Boolean additionalProperties = false;
    private String[] required = {"value", "transcription", "explanations"};

    GetExplanationSchema() {
        this.properties = LinkedHashMap.newLinkedHashMap(3);
        this.properties.put("value", getValueDefinition());
        this.properties.put("transcription", getTranscriptionDefinition());
        this.properties.put("explanations", getExplanationsDefinition()
        );
    }

    private static Map<String, String> getValueDefinition() {
        return Map.of(
                "type", "string",
                "description", "A requested word or phrase"
        );
    }

    private static Map<String, String> getTranscriptionDefinition() {
        return Map.of(
                "type", "string"
        );
    }

    private static Map<String, Object> getExplanationsDefinition() {
        return Map.of(
                "type", "array",
                "items", Map.of(
                        "type", "object",
                        "properties", Map.of(
                                "part_of_speech", Map.of(
                                        "type", "string",
                                        "enum", new String[]{"noun", "pronoun", "verb", "adjective", "adverb", "phrasal verb", "phrase"}
                                ),
                                "meaning", Map.of("type", "string"),
                                "sentences", Map.of(
                                        "type", "array",
                                        "items", Map.of("type", "string"),
                                        "additionalProperties", false
                                ),
                                "collocations", Map.of(
                                        "type", "array",
                                        "items", Map.of("type", "string"),
                                        "additionalProperties", false
                                )
                        ),
                        "additionalProperties", false,
                        "required", new String[]{"part_of_speech", "meaning", "sentences", "collocations"}
                )
        );
    }

}
