package get.wordy.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import get.wordy.ai.IVocabularyEnrichmentService;
import get.wordy.ai.model.WordDto;
import get.wordy.core.api.bean.Theme;
import get.wordy.core.api.bean.ThemeStatus;
import get.wordy.core.api.id.OwnerId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ThemeDraftGenerationService {

    private final ThemeService themeService;
    private final IVocabularyEnrichmentService enrichmentService;
    private final ThemeDraftCacheService themeDraftCacheService;
    private final JsonMapper jsonMapper;

    public void generateDraft(OwnerId ownerId, int themeId, int candidatesLimit) {
        try {
            Theme theme = themeService.updateThemeStatus(ownerId, themeId, ThemeStatus.GENERATING);
            log.info("Generating candidate words for theme={}", themeId);
            List<WordDto> candidateWords = enrichmentService
                    .generateTheme(theme.name(), candidatesLimit)
                    .words();
            log.info("AI generated {} candidate words for theme={}", candidateWords.size(), themeId);
            saveDraft(ownerId, themeId, candidateWords);
        } catch (Exception ex) {
            log.error("Draft generation failed for theme={}", themeId, ex);
            themeService.updateThemeStatus(ownerId, themeId, ThemeStatus.FAILED);
        }
    }

    private void saveDraft(OwnerId ownerId, int themeId, List<WordDto> candidateWords) throws JsonProcessingException {
        log.info("Saving a draft generated for theme={}", themeId);
        String json = jsonMapper.writeValueAsString(candidateWords);
        themeService.saveCandidateWords(ownerId, themeId, json);
        themeService.updateThemeStatus(ownerId, themeId, ThemeStatus.DRAFT);
        themeDraftCacheService.putDraft(themeId, candidateWords);
    }

}
