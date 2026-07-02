package get.wordy.ai.schema.gemini;

import com.google.genai.types.Schema;
import com.google.genai.types.Type;

import java.util.List;
import java.util.Map;

public class GetExplanationSchema {

    public static Schema buildSingle() {
        return Schema.builder()
                .type(Type.Known.OBJECT)
                .properties(Map.of(
                        "lemma",
                        Schema.builder()
                                .type(Type.Known.STRING)
                                .description(
                                        "Requested word or phrase (lemma)"
                                )
                                .build(),
                        "transcription",
                        Schema.builder()
                                .type(Type.Known.STRING)
                                .description(
                                        "IPA phonetic transcription"
                                )
                                .build(),
                        "explanations",
                        explanationsSchema()
                ))
                .required(List.of(
                        "lemma",
                        "transcription",
                        "explanations"
                ))
                .build();
    }

    public static Schema buildMultiple() {
        return Schema.builder()
                .type(Type.Known.ARRAY)
                .items(buildSingle())
                .build();
    }

    private static Schema explanationsSchema() {
        Schema explanationItemSchema =
                Schema.builder()
                        .type(Type.Known.OBJECT)
                        .properties(Map.of(
                                "part_of_speech",
                                Schema.builder()
                                        .type(Type.Known.STRING)
                                        .description(
                                                "Part of speech of the lemma"
                                        )
                                        .enum_(List.of(
                                                "noun",
                                                "pronoun",
                                                "verb",
                                                "adjective",
                                                "adverb",
                                                "phrasal verb",
                                                "phrase",
                                                "idiom"
                                        ))
                                        .build(),
                                "meaning",
                                Schema.builder()
                                        .type(Type.Known.STRING)
                                        .description(
                                                "Dictionary meaning of the word or phrase"
                                        )
                                        .build(),
                                "register",
                                Schema.builder()
                                        .type(Type.Known.STRING)
                                        .description(
                                                "Language register, e.g. formal, informal, slang"
                                        )
                                        .build(),
                                "domain",
                                Schema.builder()
                                        .type(Type.Known.STRING)
                                        .description(
                                                "Usage domain, e.g. medicine, sports, business"
                                        )
                                        .build(),
                                "sentences",
                                Schema.builder()
                                        .type(Type.Known.ARRAY)
                                        .description(
                                                "Example sentences using the lemma"
                                        )
                                        .items(
                                                Schema.builder()
                                                        .type(Type.Known.STRING)
                                                        .build()
                                        )
                                        .build(),
                                "collocations",
                                Schema.builder()
                                        .type(Type.Known.ARRAY)
                                        .description(
                                                "Common collocations for the lemma"
                                        )
                                        .items(
                                                Schema.builder()
                                                        .type(Type.Known.STRING)
                                                        .build()
                                        )
                                        .build(),
                                "source",
                                Schema.builder()
                                        .type(Type.Known.STRING)
                                        .description(
                                                "Dictionary source, e.g. Oxford, Cambridge, Merriam-Webster"
                                        )
                                        .build(),
                                "level",
                                Schema.builder()
                                        .type(Type.Known.STRING)
                                        .description(
                                                "CEFR level"
                                        )
                                        .enum_("A1", "A2", "B1", "B2", "C1", "C2")
                                        .build()
                        ))
                        .required(List.of(
                                "part_of_speech",
                                "meaning",
                                "register",
                                "domain",
                                "sentences",
                                "collocations",
                                "source",
                                "level"
                        ))
                        .build();
        return Schema.builder()
                .type(Type.Known.ARRAY)
                .description(
                        "List of explanations for the lemma"
                )
                .items(explanationItemSchema)
                .build();
    }

}
