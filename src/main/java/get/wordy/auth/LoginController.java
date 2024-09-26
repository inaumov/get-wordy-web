package get.wordy.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    private static final Logger LOG = LoggerFactory.getLogger(LoginController.class);

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/signup")
    public String signup() {
        return "signup";
    }

    @GetMapping("/reset_password")
    public String resetPassword() {
        return "reset_password";
    }

    @GetMapping("/account")
    public String account(@AuthenticationPrincipal UserDetails userDetails) {
        LOG.debug("Logged in user: {}", userDetails.getUsername());
        return "account";
    }

}
