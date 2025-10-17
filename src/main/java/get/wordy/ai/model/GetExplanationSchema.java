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
    private String[] required = {"lemma", "transcription", "explanations"};

    GetExplanationSchema() {
        this.properties = LinkedHashMap.newLinkedHashMap(3);
        this.properties.put("lemma", getLemmaDefinition());
        this.properties.put("transcription", getTranscriptionDefinition());
        this.properties.put("explanations", getExplanationsDefinition()
        );
    }

    private static Map<String, String> getLemmaDefinition() {
        return Map.of(
                "type", "string",
                "description", "Requested word or phrase (lemma)"
        );
    }

    private static Map<String, String> getRegisterDefinition() {
        return Map.of(
                "type", "string",
                "description", "-- e.g. formal, slang"
        );
    }

    private static Map<String, String> getDomainDefinition() {
        return Map.of(
                "type", "string",
                "description", "-- e.g. medicine, sports"
        );
    }

    private static Map<String, String> getSourceDefinition() {
        return Map.of(
                "type", "string",
                "description", "source (e.g., Oxford, Cambridge, Merriam-Webster)"
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
                                        "enum", new String[]{"noun", "pronoun", "verb", "adjective", "adverb", "phrasal verb", "phrase", "idiom"}
                                ),
                                "meaning", Map.of("type", "string"),
                                "register", getRegisterDefinition(),
                                "domain", getDomainDefinition(),
                                "sentences", Map.of(
                                        "type", "array",
                                        "items", Map.of("type", "string"),
                                        "additionalProperties", false
                                ),
                                "collocations", Map.of(
                                        "type", "array",
                                        "items", Map.of("type", "string"),
                                        "additionalProperties", false
                                ),
                                "source", getSourceDefinition()
                        ),
                        "additionalProperties", false,
                        "required", new String[]{"part_of_speech", "meaning", "register", "domain", "sentences", "collocations", "source"}
                )
        );
    }

}
