package get.wordy.core;

import get.wordy.core.api.IWordExplanationService;
import get.wordy.core.api.bean.Sentence;
import get.wordy.core.api.bean.Word;
import get.wordy.core.api.id.OwnerId;
import get.wordy.spelling.SentenceSplitter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class VocabularyServiceWrapper implements IWordExplanationService {

    @Override
    public Word getWordExplanation(OwnerId ownerId, int wordId) {
        return null;
    }

    @Override
    public Word addWordExplanation(OwnerId ownerId, int vocabId, Word entity) {
        List<Sentence> exerciseSentences = entity.getSentences()
                .stream()
                .map(strSentence -> withClosestMatch(strSentence, entity.getValue()))
                .toList();
        return null;
    }

    @Override
    public Word updateWordExplanation(OwnerId ownerId, int vocabId, Word entity) {
        List<Sentence> exerciseSentences = entity.getSentences()
                .stream()
                .map(strSentence -> withClosestMatch(strSentence, entity.getValue()))
                .toList();
        return null;
    }

    @Override
    public void deleteWordExplanationPermanently(OwnerId ownerId, int vocabId, int wordId) {

    }

    private static Sentence withClosestMatch(String strSentence, String keyword) {
        Sentence sentence = new Sentence(strSentence);
        Optional<SentenceSplitter.Chunks> sentenceChunks = SentenceSplitter.splitByClosestMatch(strSentence, keyword);
        return sentenceChunks
                .map(chunks -> {
                    log.debug("Sentence split for \"{}\", with keyword \"{}\" closest match: {}", strSentence, keyword, chunks);
                    return sentence.withMatchedWords(chunks.matchedWords());
                })
                .orElse(sentence);
    }

}
