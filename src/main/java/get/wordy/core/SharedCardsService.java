package get.wordy.core;

import get.wordy.core.api.IUserCardsService;
import get.wordy.core.api.IVocabularyService;
import get.wordy.core.api.bean.Card;
import get.wordy.core.api.bean.Progress;
import get.wordy.core.api.bean.Word;
import get.wordy.core.api.id.OwnerId;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SharedCardsService {

    private final IVocabularyService vocabularyService;
    private final IUserCardsService progressService;

    public SharedCardsService(IVocabularyService vocabularyService,
                              @Qualifier("userCardsService") IUserCardsService progressService) {
        this.vocabularyService = vocabularyService;
        this.progressService = progressService;
    }

    @Transactional
    public List<Card> getCards(OwnerId ownerId, OwnerId userId, int vocabId) {
        List<Word> vocabWords = vocabularyService.getWords(ownerId, vocabId);
        Map<Integer, Progress> userProgress = progressService.getProgress(userId, vocabId)
                .stream()
                .collect(Collectors.toMap(Progress::getWordId, Function.identity()));

        return vocabWords
                .stream()
                .map(word -> {
                    Progress progress = userProgress.get(word.getId());
                    Card card = new Card();
                    card.setVocabId(vocabId);
                    card.setWord(word);
                    if (progress != null) {
                        card.setProgress(progress);
                    }
                    return card;
                })
                .toList();
    }

}
