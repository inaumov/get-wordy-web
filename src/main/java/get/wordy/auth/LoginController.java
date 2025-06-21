package get.wordy.auth;

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

    public LoginController(UserService userService) {
        this.userService = userService;
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
        model.addAttribute("schoolInfo", new School("Desna Academy", null, "Steve Jobs"));
        return "account";
    }

}
