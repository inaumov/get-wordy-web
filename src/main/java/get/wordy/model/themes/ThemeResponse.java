package get.wordy.model.themes;

import get.wordy.core.api.bean.ThemeStatus;

import java.time.Instant;

public record ThemeResponse(
        int themeId,
        String name,
        String notes,
        ThemeStatus status,
        Instant createdAt,
        Instant lastModifiedAt,
        int wordsTotal
) {
}
