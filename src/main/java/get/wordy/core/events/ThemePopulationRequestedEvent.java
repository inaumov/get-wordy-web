package get.wordy.core.events;

import get.wordy.core.api.id.OwnerId;

public record ThemePopulationRequestedEvent(OwnerId ownerId, int themeId, int candidatesLimit) {
}