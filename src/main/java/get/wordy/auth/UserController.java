package get.wordy.auth;

import get.wordy.core.api.IClassAccessService;
import get.wordy.core.api.bean.ClassViewerInfo;
import get.wordy.users.CustomUserDetails;
import get.wordy.users.UserDto;
import get.wordy.users.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final UserService userService;
    private final IClassAccessService classAccessService;

    public UserController(UserService userService, IClassAccessService classAccessService) {
        this.userService = userService;
        this.classAccessService = classAccessService;
    }

    @PostMapping(value = "/profile", consumes = "application/x-www-form-urlencoded")
    public String updateProfile(@Valid @ModelAttribute("userProfile") UserDto userDto,
                                @AuthenticationPrincipal CustomUserDetails userDetails,
                                BindingResult result,
                                Model model) {

        log.info("Attempt to update account for user: {}", userDetails.getUsername());
        UserDto savedUser = userService.saveUser(userDto);

        return "account";
    }

    @GetMapping("/my-classes")
    public ResponseEntity<List<ClassViewerInfo>> getParticipantClasses(Principal user) {
        List<ClassViewerInfo> classes = classAccessService.getParticipantClasses(user.getName());
        return ResponseEntity.ok(classes);
    }

    @PostMapping(value = "/settings", consumes = "application/x-www-form-urlencoded")
    public String updateSettings(@ModelAttribute("settings") Settings settings,
                                 @AuthenticationPrincipal CustomUserDetails userDetails) {
        userService.updateSettings(userDetails.getUsername(), settings);
        return "redirect:/account";  // redirect to refresh page
    }

}
