package get.wordy.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/classes")
public class ClassController {

    @GetMapping
    @PreAuthorize("hasAuthority('P_MANAGE_CLASSES')")
    public ResponseEntity<List<Map<String, Object>>> getMockedClasses() {
        List<Map<String, Object>> classes = new ArrayList<>();
        
        return ResponseEntity.ok(classes);
    }

}
