package get.wordy.rest;

import get.wordy.core.api.IClassAccessService;
import get.wordy.core.api.id.OwnerId;
import get.wordy.model.Attendee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/classes")
@PreAuthorize("hasAuthority('P_MANAGE_CLASSES')")
public class ParticipantsController {

    private static final Logger LOG = LoggerFactory.getLogger(ParticipantsController.class);

    private final IClassAccessService classService;

    public ParticipantsController(IClassAccessService classAccessService) {
        this.classService = classAccessService;
    }

    @PostMapping("/{classId}/assign")
    public ResponseEntity<Void> assignUser(Principal adminUser,
                                           @PathVariable String classId,
                                           @RequestBody Attendee targetUser) {

        LOG.info("Assigning user = {} to class id = {}", targetUser.userIdentity(), classId);

        classService.assignUserToClass(new OwnerId(adminUser.getName(), "user"), classId, targetUser.userIdentity());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{classId}/revoke")
    public ResponseEntity<Void> revokeUser(Principal adminUser,
                                           @PathVariable String classId,
                                           @RequestBody Attendee targetUser) {

        LOG.info("Revoking user = {} from class id = {}", targetUser.userIdentity(), classId);

        classService.removeUserFromClass(new OwnerId(adminUser.getName(), "user"), classId, targetUser.userIdentity());
        return ResponseEntity.ok().build();
    }

}
