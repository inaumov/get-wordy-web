package get.wordy.model.themes;

public record ThemeResponse(int themeId, String name, int wordsTotal) {
    public ThemeResponse withName(String name) {
        return new ThemeResponse(themeId, name, wordsTotal);
    }
}
