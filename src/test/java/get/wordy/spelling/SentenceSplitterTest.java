package get.wordy.spelling;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SentenceSplitterTest {

    @ParameterizedTest
    @CsvSource(
            delimiter = '|',
            textBlock = """
                    snuggle up|Past tense|The couple snuggled up on the couch.|snuggled up
                    hug|Present simple tense|She hugs her teddy bear every night.|hugs
                    embracing|Present participle|Embracing each other is a sign of love.|Embracing
                    fetch|Past tense|The dogs fetched the ball from the yard.|fetched
                    fetch|Present continuous tense|He is fetching water from the well.|fetching
                    fetch|Present simple tense|She fetches her mail every morning.|fetches
                    fetch|Present participle|Fetching the newspaper is part of his daily routine.|Fetching
                    throw|Future tense|They will throw the ball across the field.|throw
                    throw|Present perfect tense|The children have thrown snowballs all day.|thrown
                    toss|Past perfect tense|By the time we arrived, they had tossed their hats in the air.|tossed
                    toss|Future perfect tense|They will have tossed a coin to make a decision.|tossed
                    cuddle up|Past tense|The children cuddled up to each other for warmth.|cuddled up
                    cuddle together|Present continuous tense|They are cuddling together by the fireplace.|cuddling together
                    cuddle up|Present simple tense|She cuddles up to her teddy bear every night.|cuddles up
                    cuddle up|Present participle|Cuddling up to each other is their favorite activity.|Cuddling up
                    look forward to|Past tense|I looked forward to our meeting all week.|looked forward to
                    look forward to|Present continuous tense|She is looking forward to the concert tomorrow.|looking forward to
                    look forward to|Present simple tense|He looks forward to his vacation every year.|looks forward to
                    look forward to|Present participle|Looking forward to your response.|Looking forward to
                    look forward to|Future tense|They will look forward to the new movie release.|look forward to
                    look forward to|Present perfect tense|She has looked forward to this day for so long.|looked forward to
                    look forward to|Past perfect tense|By that time, they had been looking forward to the event.|looking forward to
                    look forward to|Future perfect tense|They will have looked forward to the reunion for months.|looked forward to
                    look forward to|Past continuous tense|They were always looking forward to family gatherings.|looking forward to
                    look forward to|Past habit|She used to look forward to visiting her grandparents.|look forward to
                    look forward to|Present perfect continuous tense|They have been looking forward to this celebration.|looking forward to
                    look forward to|Future participle|Looking forward to the upcoming holidays.|Looking forward to
                    come up with|Past tense|She came up with a brilliant idea for the project.|came up with
                    come up with|Present continuous tense|We are coming up with new strategies for growth.|coming up with
                    come up with|Present simple tense|He comes up with creative solutions to problems.|comes up with
                    come up with|Present participle|Coming up with innovative products is their specialty.|Coming up with
                    come up with|Future tense|They will come up with a plan for the event.|come up with
                    come up with|Present perfect tense|She has come up with several proposals.|come up with
                    come up with|Past perfect tense|By that time, they had already come up with a backup plan.|come up with
                    come up with|Future perfect tense|They will have come up with a decision by tomorrow.|come up with
                    come up with|Past continuous tense|We were always coming up with new ideas during brainstorming sessions.|coming up with
                    come up with|Past habit|He used to come up with interesting stories as a child.|come up with
                    come up with|Present perfect continuous tense|They have been coming up with innovative solutions for years.|coming up with
                    come up with|Future participle|Coming up with creative concepts is challenging but rewarding.|Coming up with
                    get used to|Past tense|I got used to the new routine after a few days.|got used to
                    get used to|Present continuous tense|She is getting used to the colder weather.|getting used to
                    get used to|Present simple tense|He gets used to early mornings quickly.|gets used to
                    get used to|Present participle|Getting used to change takes time.|Getting used to
                    get used to|Future tense|They will get used to the new city soon.|get used to
                    get used to|Present perfect tense|She has gotten used to working from home.|gotten used to
                    get used to|Past perfect tense|By that time, they had gotten used to the noise.|gotten used to
                    get used to|Future perfect tense|They will have gotten used to the new schedule by next month.|gotten used to
                    get used to|Past continuous tense|We were getting used to the new surroundings.|getting used to
                    get used to|Past habit|He used to get used to challenges easily.|get used to
                    get used to|Present perfect continuous tense|They have been getting used to the new software for weeks.|getting used to
                    get used to|Future participle|Getting used to a different culture can be an enriching experience.|Getting used to
                    """
    )
    void testSplitByClosestMatch(String keyword, String tense, String sentence, String expectedMatchedWords) {
        Optional<SentenceSplitter.Chunks> result = SentenceSplitter.splitByClosestMatch(sentence, keyword);
        assertTrue(result.isPresent());
        SentenceSplitter.Chunks chunks = result.get();
        assertEquals(expectedMatchedWords, chunks.matchedWords(), "Matched words mismatch");
        printResult(keyword, tense, sentence, chunks);
    }

    @ParameterizedTest
    @CsvSource(
            delimiter = '|',
            textBlock = """
                    test|
                    cuddle up|The couple snuggled up on the couch.
                    bug|She hugs her teddy bear every night.
                    """
    )
    void testNoClosestMatch(String keyword, String sentence) {
        Optional<SentenceSplitter.Chunks> result = SentenceSplitter.splitByClosestMatch(sentence, keyword);
        assertTrue(result.isEmpty());
    }

    private static void printResult(String keyword, String tense, String sentence, SentenceSplitter.Chunks chunks) {
        String chunksStr = chunks.beforeMatch() + " | " + chunks.matchedWords() + " | " + chunks.afterMatch();
        System.out.printf("Keyword: \"%s\", Tense: %s, Sentence: \"%s\", Chunks: \"%s\"\n", keyword, tense, sentence, chunksStr);
    }

}
