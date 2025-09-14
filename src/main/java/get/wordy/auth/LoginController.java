package get.wordy.auth;

import get.wordy.school.SchoolService;
import get.wordy.users.CustomUserDetails;
import get.wordy.users.UserDto;
import get.wordy.users.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.context.request.WebRequest;

@Slf4j
@Controller
public class LoginController {

    private final UserService userService;
    private final SchoolService schoolService;

    public LoginController(UserService userService, SchoolService schoolService) {
        this.userService = userService;
        this.schoolService = schoolService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/signup")
    public String signup(Authentication authentication,
                         WebRequest request,
                         Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/welcome";
        }
        UserDto userDto = new UserDto();
        model.addAttribute("user", userDto);
        return "signup";
    }

    @GetMapping("/reset_password")
    public String resetPassword() {
        return "reset_password";
    }

    @GetMapping("/welcome")
    public String loggedIn(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.debug("A user {} just have signed up", userDetails.getUsername());
        model.addAttribute("displayName", userDetails.getDisplayName());
        return "welcome";
    }

    @GetMapping("/account")
    public String account(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        model.addAttribute("userProfile", userDetails);
        Settings settings = userService.getSettings(userDetails.getUsername());
        if (settings == null) {
            settings = new Settings(5, true);  // ensure non-null
        }
        model.addAttribute("settings", settings);
        if (userDetails.hasPermissionOrRole("P_MANAGE_CLASSES")) {
            model.addAttribute("schoolInfo", schoolService.getSchoolInfoByOwner(userDetails.getUsername()));
        }
        if (userDetails.hasPermissionOrRole("P_SHARED_CLASS")) {
            model.addAttribute("schoolInfo", schoolService.getSchoolInfoByViewer(userDetails.getUsername()));
        }
        return "account";
    }

}
