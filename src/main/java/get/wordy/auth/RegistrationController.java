package get.wordy.auth;

import get.wordy.users.IUserService;
import get.wordy.users.UserDto;
import get.wordy.users.exception.UserAlreadyExistException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/users")
@Slf4j
public class RegistrationController {

    private final HttpServletRequest request;
    private final IUserService userService;

    public RegistrationController(HttpServletRequest request, IUserService userService) {
        this.request = request;
        this.userService = userService;
    }

    // handler method to handle user registration form submit request
    @PostMapping(value = "/registration", consumes = "application/x-www-form-urlencoded")
    public String registration(@Valid @ModelAttribute("user") UserDto userDto,
                               BindingResult result,
                               Model model) {

        log.info("Attempt to register a new account for email: {}", userDto.getEmail());

        try {
            UserDto registered = userService.registerNewUserAccount(userDto);
        } catch (UserAlreadyExistException uaeEx) {
            result.rejectValue("email", "invalid_email",
                    "There is already an account registered with the same email");
        }

        if (result.hasErrors()) {
            model.addAttribute("user", userDto);
            return "redirect:/signup?errors";
        }

        SecurityContext context = SecurityContextHolder.getContext();
        // Save to HTTP session:
        HttpSession session = request.getSession(true); // inject HttpServletRequest
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);

        return "redirect:/welcome";
    }

}
