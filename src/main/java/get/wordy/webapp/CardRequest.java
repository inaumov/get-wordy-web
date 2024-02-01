package get.wordy.webapp;

import get.wordy.core.bean.Word;

import java.util.ArrayList;
import java.util.List;

public class CardRequest {

    private Word word;
    private final List<String> contexts = new ArrayList<>();
    private final List<String> collocations = new ArrayList<>();

    public Word getWord() {
        return word;
    }

    public void setWord(Word word) {
        this.word = word;
    }

    public List<String> getContexts() {
        return contexts;
    }

    public List<String> getCollocations() {
        return collocations;
    }

}