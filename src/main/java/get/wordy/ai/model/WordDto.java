package get.wordy.ai.model;

import get.wordy.core.api.bean.WordKey;

public record WordDto(
        String lemma,
        String partOfSpeech,
        String meaning,
        String level
) {
    public WordKey getKey() {
        return new WordKey(this.lemma, this.partOfSpeech);
    }
}