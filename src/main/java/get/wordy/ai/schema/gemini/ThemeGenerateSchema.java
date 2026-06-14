package get.wordy.ai.schema.gemini;

import com.google.genai.types.Schema;
import com.google.genai.types.Type;

import java.util.List;
import java.util.Map;

public class ThemeGenerateSchema {

    public static Schema build() {

        Schema wordSchema =
                Schema.builder()
                        .type(Type.Known.OBJECT)
                        .required(List.of("lemma", "partOfSpeech"))
                        .properties(
                                Map.of(
                                        "lemma", Schema.builder()
                                                .type(Type.Known.STRING)
                                                .description(
                                                        "English vocabulary word or phrase (lemma)"
                                                )
                                                .build(),
                                        "partOfSpeech", Schema.builder()
                                                .type(Type.Known.STRING)
                                                .description(
                                                        "Grammatical category of the word"
                                                ).enum_("noun", "pronoun", "verb", "adjective", "adverb", "phrasal verb", "phrase", "idiom")
                                                .build(),
                                        "meaning", Schema.builder()
                                                .type(Type.Known.STRING)
                                                .description(
                                                        "Short learner-friendly English dictionary definition"
                                                )
                                                .build(),
                                        "level", Schema.builder()
                                                .type(Type.Known.STRING)
                                                .description(
                                                        "CEFR level"
                                                )
                                                .enum_("A1", "A2", "B1", "B2", "C1", "C2")
                                                .build()
                                ))
                        .build();

        return Schema.builder()
                .type(Type.Known.OBJECT)
                .properties(Map.of(
                        "words",
                        Schema.builder()
                                .type(Type.Known.ARRAY)
                                .items(wordSchema)
                                .build()
                ))
                .required(List.of("words"))
                .build();
    }

}
