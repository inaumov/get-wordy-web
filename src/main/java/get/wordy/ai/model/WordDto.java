package get.wordy.ai.model;

public record WordDto(
        String lemma,
        String partOfSpeech,
        String meaning,
        String level
) {
}