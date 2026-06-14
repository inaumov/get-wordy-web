package get.wordy.model.themes;

import get.wordy.core.api.bean.ThemeStatus;

public record ThemeResponse(int themeId, String name, String notes, ThemeStatus status, int wordsTotal) {
}
