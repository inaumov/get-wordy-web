package get.wordy.model;

import java.util.List;

public record CardRequest(
        WordRequest word,
        List<String> examples,
        List<String> collocations
) {
}