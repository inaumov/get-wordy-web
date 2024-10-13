package get.wordy.auth;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/users")
public class AuthController {

    @GetMapping("/auth/status")
    public ResponseEntity<Map<String, Object>> checkLoginStatus(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        HttpHeaders headers = new HttpHeaders();
        headers.setCacheControl(CacheControl.noStore());
        if (authentication != null && authentication.isAuthenticated()) {
            response.put("loggedIn", true);
            response.put("username", authentication.getName());
        } else {
            response.put("loggedIn", false);
        }
        return ResponseEntity.ok().headers(headers).body(response);
    }

}
