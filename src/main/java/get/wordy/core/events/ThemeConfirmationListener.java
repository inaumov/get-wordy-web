package get.wordy.core.events;

import get.wordy.core.ThemeDraftConfirmationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class ThemeConfirmationListener {

    private final ThemeDraftConfirmationService service;

    @Async("themeExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onThemeDraftConfirmed(ThemeDraftConfirmedEvent event) {
        service.confirmDraft(event.ownerId(), event.themeId());
    }

}
