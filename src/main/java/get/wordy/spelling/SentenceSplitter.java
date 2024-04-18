package get.wordy.spelling;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;

import java.util.Arrays;
import java.util.Optional;

public class SentenceSplitter {

    private static final double MIN_VALUABLE_SIMILARITY = 0.75d;

    private static final JaroWinklerSimilarity JARO_WINKLER_SIMILARITY = new JaroWinklerSimilarity();

    public record Chunks(String beforeMatch, String matchedWords, String afterMatch) {
    }

    public static Optional<Chunks> splitByClosestMatch(String sentence, String keyword) {
        if (sentence == null || sentence.isBlank() || sentence.trim().length() < keyword.length()) {
            return Optional.empty();
        }
        String[] words = sentence.split(StringUtils.SPACE);
        int keywordLength = keyword.split(StringUtils.SPACE).length;

        double maxSimilarityFound = MIN_VALUABLE_SIMILARITY;
        int startIndex = -1;
        int endIndex = -1;

        // Iterate over words to find the sequence of words with the highest similarity to the keyword (or phrase)
        for (int i = 0; i <= words.length - keywordLength; i++) {
            // Combine current word and next word (if exists) to form a phrase
            StringBuilder phraseBuilder = new StringBuilder();
            for (int j = 0; j < keywordLength; j++) {
                if (j > 0) {
                    phraseBuilder.append(StringUtils.SPACE);
                }
                phraseBuilder.append(words[i + j].toLowerCase());
            }
            String potentiallyMatchingPhrase = phraseBuilder.toString();

            // Calculate similarity between phrase and keyword
            double similarity = findSimilarity(keyword.toLowerCase(), potentiallyMatchingPhrase);

            if (similarity >= maxSimilarityFound) {
                maxSimilarityFound = similarity;
                startIndex = i;
                endIndex = i + keywordLength - 1;
            }
        }

        if (startIndex == -1) {
            // If no match found, return nothing
            return Optional.empty();
        }

        // Split the sentence into chunks using the matched phrase
        String beforeMatch = String.join(" ", Arrays.copyOfRange(words, 0, startIndex));
        String matchedWords = String.join(" ", Arrays.copyOfRange(words, startIndex, endIndex + 1));
        String afterMatch = String.join(" ", Arrays.copyOfRange(words, endIndex + 1, words.length));

        return Optional.of(new Chunks(beforeMatch.trim(), matchedWords.trim(), afterMatch.trim()));
    }

    private static double findSimilarity(String s1, String s2) {
        return JARO_WINKLER_SIMILARITY.apply(s1, s2);
    }

}
