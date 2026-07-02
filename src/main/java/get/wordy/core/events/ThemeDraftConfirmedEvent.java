package get.wordy.core.events;

import get.wordy.core.api.id.OwnerId;

public record ThemeDraftConfirmedEvent(OwnerId ownerId, int themeId) {
}
