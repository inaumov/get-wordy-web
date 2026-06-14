package get.wordy.core;

import get.wordy.ai.model.WordDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ThemeDraftCacheService {

    private final Map<Integer, List<WordDto>> themeDraftCache = new HashMap<>();

    public List<WordDto> getDraft(int themeId) {
        return themeDraftCache.get(themeId);
    }

    public void putDraft(int themeId, List<WordDto> drafts) {
        themeDraftCache.put(themeId, new ArrayList<>(drafts));
    }

    public void removeDraft(int themeId) {
        themeDraftCache.remove(themeId);
    }

    public boolean containsDraft(int themeId) {
        return themeDraftCache.containsKey(themeId);
    }

}
