package get.wordy.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.json.JsonMapper;
import get.wordy.ai.model.WordDto;
import get.wordy.core.api.IWordExplanationService;
import get.wordy.core.api.bean.*;
import get.wordy.core.api.id.OwnerId;
import get.wordy.core.events.ThemeDraftConfirmedEvent;
import get.wordy.core.events.ThemePopulationRequestedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static get.wordy.core.api.bean.ThemeStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ThemeServiceWrapper {

    private final static Set<ThemeStatus> finalStatuses = EnumSet.of(CONFIRMED, PROCESSING, READY);

    private final ThemeService themeService;
    private final IWordExplanationService wordExplanationService;
    private final ThemeDraftCacheService draftCacheService;
    private final JsonMapper jsonMapper;
    private final ApplicationEventPublisher publisher;

    @Transactional
    public void generateDraft(OwnerId ownerId, int themeId, int candidatesLimit) {
        try {
            Theme theme = themeService.getTheme(ownerId, themeId);
            if (theme.status() == ThemeStatus.GENERATING) {
                throw new IllegalStateException("Theme draft is already being generated");
            }
            if (theme.status() != ThemeStatus.NEW) {
                throw new IllegalStateException("Theme draft has been already generated");
            }
            themeService.updateThemeStatus(ownerId, themeId, ThemeStatus.GENERATING);
            publisher.publishEvent(new ThemePopulationRequestedEvent(ownerId, themeId, candidatesLimit));
        } catch (Exception e) {
            log.error("Failed populating words for theme '{}'", themeId, e);
            themeService.updateThemeStatus(ownerId, themeId, ThemeStatus.FAILED);
            throw new IllegalStateException("Theme draft generation failed");
        }
    }

    @Transactional
    public void confirmTheme(OwnerId ownerId, int themeId) {
        try {
            Theme theme = themeService.getTheme(ownerId, themeId);
            if (finalStatuses.contains(theme.status())) {
                throw new IllegalStateException("Theme draft has been already confirmed");
            }
            if (theme.status() != ThemeStatus.DRAFT) {
                throw new IllegalStateException("Theme draft is not generated yet");
            }
            themeService.updateThemeStatus(ownerId, themeId, CONFIRMED);
            publisher.publishEvent(new ThemeDraftConfirmedEvent(ownerId, themeId));
        } catch (Exception e) {
            log.error("Failed populating words for theme '{}'", themeId, e);
            themeService.updateThemeStatus(ownerId, themeId, ThemeStatus.FAILED);
            throw new IllegalStateException("Theme completion failed");
        }
    }

    public List<WordDto> getCandidateWords(OwnerId ownerId, int themeId) {
        if (draftCacheService.containsDraft(themeId)) {
            List<WordDto> cached = draftCacheService.getDraft(themeId);
            if (!cached.isEmpty()) {
                return cached;
            }
        }
        try {
            String candidateWordsJson = themeService.getCandidateWordsJson(ownerId, themeId);
            return jsonMapper.readValue(
                    candidateWordsJson,
                    new TypeReference<>() {
                    }
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public Word addWordToTheme(OwnerId userOwnerId, int themeId, int wordId) {
        themeService.addWordsToTheme(userOwnerId, themeId, List.of(wordId));
        return wordExplanationService.getWordExplanation(wordId);
    }

    @Transactional
    public void removeWordFromTheme(OwnerId userOwnerId, int themeId, int wordId) {
        themeService.removeWordsFromTheme(userOwnerId, themeId, List.of(wordId));
    }

    @Transactional
    public void removeCandidateWordsFromTheme(OwnerId userOwnerId, int themeId, WordKey wordKey) {
        themeService.removeCandidateWordsFromTheme(userOwnerId, themeId, wordKey.lemma(), wordKey.partOfSpeech());
        if (draftCacheService.containsDraft(themeId)) {
            List<WordDto> draft = draftCacheService.getDraft(themeId);
            draft.removeIf(item -> item.getKey().equals(wordKey));
        }
    }

    @Transactional
    public boolean deleteTheme(OwnerId ownerId, int themeId) {
        boolean removed = themeService.deleteTheme(ownerId, themeId);
        if (removed) {
            draftCacheService.removeDraft(themeId);
        }
        return removed;
    }

}
