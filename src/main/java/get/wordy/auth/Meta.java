package get.wordy.auth;

import lombok.Builder;

import java.util.List;

@Builder
public record Meta(List<String> permissions, School school) {
}
