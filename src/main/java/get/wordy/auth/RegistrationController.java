package get.wordy.auth;

import get.wordy.users.IUserService;
import get.wordy.users.UserDto;
import get.wordy.users.exception.UserAlreadyExistException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/users")
@Slf4j
public class RegistrationController {

    private final HttpServletRequest request;
    private final IUserService userService;
    private final AuthService authService;

    public RegistrationController(HttpServletRequest request, IUserService userService, AuthService authService) {
        this.request = request;
        this.userService = userService;
        this.authService = authService;
    }

    // handler method to handle user registration form submit request
    @PostMapping(value = "/registration", consumes = "application/x-www-form-urlencoded")
    public String registration(@Valid @ModelAttribute("user") UserDto userDto,
                               @RequestParam(value = "isTeacher", required = false) boolean isTeacher,
                               BindingResult result,
                               HttpSession session,
                               Model model) {

        log.info("Attempt to register new account for email: {}", userDto.getEmail());

        if (result.hasErrors()) {
            model.addAttribute("user", userDto);
            return "signup"; // back to signup with errors
        }

        UserDto registered;
        try {
            if (isTeacher) {
                // teacher → go to onboarding, don’t register yet
                registered = userService.registerUser(userDto, "teachers");

                // store pending user’s email in session
                session.setAttribute("pendingEmail", registered.getEmail());
                session.setAttribute("pendingUsername", registered.getUsername());
                return "redirect:/onboarding";
            }

            registered = userService.registerUser(userDto, "individual_users");
        } catch (UserAlreadyExistException uaeEx) {
            result.rejectValue("email", "invalid_email", "Account already exists with that email");
            return "signup";
        }

        authService.authenticateUser(registered.getEmail(), request); // login immediately
        return "redirect:/welcome";
    }

}
