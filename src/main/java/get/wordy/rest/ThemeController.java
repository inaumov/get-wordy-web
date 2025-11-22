package get.wordy.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/themes")
public class ThemeController {

    private final Map<Integer, Map<String, Object>> mockThemes = new HashMap<>();

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllThemes() {
        return ResponseEntity.ok(new ArrayList<>(mockThemes.values()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getTheme(@PathVariable int id) {
        Map<String, Object> theme = mockThemes.get(id);
        if (theme == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(theme);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createTheme(@RequestBody Map<String, Object> theme) {
        int id = mockThemes.size() + 1;
        theme.put("id", id);
        mockThemes.put(id, theme);
        return ResponseEntity.ok(theme);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateThemeName(
            @PathVariable int id,
            @RequestBody Map<String, String> update) {

        Map<String, Object> theme = mockThemes.get(id);
        if (theme == null) return ResponseEntity.notFound().build();

        theme.put("name", update.get("name"));
        return ResponseEntity.ok(theme);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTheme(@PathVariable int id) {
        mockThemes.remove(id);
        return ResponseEntity.noContent().build();
    }

}
