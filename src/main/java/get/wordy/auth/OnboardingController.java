package get.wordy.auth;

import get.wordy.school.SchoolService;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Slf4j
@Controller
@RequestMapping("/onboarding")
public class OnboardingController {

    private final Path rootLocation = Paths.get("uploads/school-logos");

    private final SchoolService schoolService;
    private final AuthService authService;

    public OnboardingController(SchoolService schoolService, AuthService authService) {
        this.schoolService = schoolService;
        this.authService = authService;
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage", e);
        }
    }

    @GetMapping
    public String showOnboardingForm(Model model, HttpSession session) {
        String email = (String) session.getAttribute("pendingEmail");
        String username = (String) session.getAttribute("pendingUsername");

        if (email == null) {
            return "redirect:/signup";
        }

        // Only include fields the user edits
        model.addAttribute("onboardingForm", new OnboardingForm());
        return "onboarding";
    }

    @PostMapping
    public String completeOnboarding(@Valid @ModelAttribute("onboardingForm") OnboardingForm form,
                                     BindingResult bindingResult,
                                     HttpServletRequest request,
                                     HttpSession session) {

        if (bindingResult.hasErrors()) {
            return "onboarding";
        }

        return finalizeOnboarding(request, session, form);
    }

    @PostMapping("/skip")
    public String skipOnboarding(HttpServletRequest request, HttpSession session) {
        // just authenticate, no school created
        String username = (String) session.getAttribute("pendingUsername");
        log.info("Skip registering a school for the user '{}'", username);
        return finalizeOnboarding(request, session, null);
    }

    /** Shared logic: authenticate user and clear session. */
    private String finalizeOnboarding(HttpServletRequest request, HttpSession session, OnboardingForm registerSchool) {
        String email = (String) session.getAttribute("pendingEmail");
        String username = (String) session.getAttribute("pendingUsername");
        if (!StringUtils.hasText(email) || !StringUtils.hasText(username)) {
            return "redirect:/signup";
        }

        // Only onboarding-specific data comes from wizard
        if (registerSchool != null && StringUtils.hasText(registerSchool.getSchoolName())) {
            String logoFilename = saveToFileStorageService(registerSchool.getSchoolLogo());
            schoolService.registerSchool(username, registerSchool.getSchoolName(), logoFilename);
        }

        authService.authenticateUser(email, request);

        session.removeAttribute("pendingEmail");
        session.removeAttribute("pendingUsername");

        return "redirect:/welcome";
    }

    private String saveToFileStorageService(MultipartFile schoolLogo) {
        if (schoolLogo != null && !schoolLogo.isEmpty() && StringUtils.hasText(schoolLogo.getOriginalFilename())) {
            String cleanName = StringUtils.cleanPath(schoolLogo.getOriginalFilename());
            String filename = UUID.randomUUID() + "_" + cleanName;
            try {
                Path destination = rootLocation.resolve(filename).normalize();
                Files.copy(schoolLogo.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
                return filename; // store only filename in DB
            } catch (IOException e) {
                throw new RuntimeException("Failed to store file " + filename, e);
            }
        }
        return null;
    }

    @ModelAttribute("fileError")
    public String fileError() {
        return null; // default, overridden in handler
    }

}
