package get.wordy.core;

import get.wordy.core.api.IVocabularyService;
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
public class ClassVocabularyService {

    private final IWordExplanationService explanationsService;
    private final IVocabularyService vocabularyService;

    public ClassVocabularyService(IWordExplanationService explanationsService, IVocabularyService vocabularyService) {
        this.explanationsService = explanationsService;
        this.vocabularyService = vocabularyService;
    }

    public Word getWordExplanation(OwnerId ownerId, int vocabId, int wordId) {
        vocabularyService.hasVocabulary(ownerId, vocabId);
        return explanationsService.getWordExplanation(wordId);
    }

    public Word addWordExplanation(OwnerId ownerId, int vocabId, Word entity) {
        vocabularyService.hasVocabulary(ownerId, vocabId);

        List<Sentence> exerciseSentences = entity.getStrSentences()
                .stream()
                .map(strSentence -> withClosestMatch(strSentence, entity.getLemma()))
                .toList();

        // custom word
        Word wordAdded = explanationsService.addWordExplanation(entity);
        wordAdded.setSentences(exerciseSentences);
        vocabularyService.addToVocabulary(ownerId, vocabId, wordAdded.getId());
        return wordAdded;
    }

    public Word updateWordExplanation(OwnerId ownerId, int vocabId, Word entity) {
        vocabularyService.hasVocabulary(ownerId, vocabId);

        List<Sentence> exerciseSentences = entity.getStrSentences()
                .stream()
                .map(strSentence -> withClosestMatch(strSentence, entity.getLemma()))
                .toList();

        return explanationsService.updateWordExplanation(entity);
    }

    public void deleteWordExplanationPermanently(OwnerId ownerId, int vocabId, int wordId) {
        vocabularyService.hasVocabulary(ownerId, vocabId);
        boolean deleted = explanationsService.deleteWordExplanationPermanently(wordId);
        if (deleted) {
            log.info("A custom word explanation with id {} successfully deleted from vocab {}", wordId, vocabId);
        }
    }

    private static Sentence withClosestMatch(String strSentence, String keyword) {
        Sentence sentence = Sentence.of(strSentence);
        Optional<SentenceSplitter.Chunks> sentenceChunks = SentenceSplitter.splitByClosestMatch(strSentence, keyword);
        return sentenceChunks
                .map(chunks -> {
                    log.debug("Sentence split for \"{}\", with keyword \"{}\" closest match: {}", strSentence, keyword, chunks);
                    return sentence.withMatchedWords(chunks.matchedWords());
                })
                .orElse(sentence);
    }

}
