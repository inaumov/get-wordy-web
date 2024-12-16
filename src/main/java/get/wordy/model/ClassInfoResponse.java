package get.wordy.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import get.wordy.core.api.bean.ClassInfo;
import lombok.Getter;
import lombok.experimental.Delegate;

import java.util.List;

public class ClassInfoResponse {

    @Delegate
    private final ClassInfo classInfo;

    @Getter
    @JsonProperty("attendees")
    private List<String> attendees;

    public ClassInfoResponse(ClassInfo classInfo) {
        this.classInfo = classInfo;
    }

    public ClassInfoResponse withAttendees(List<String> attendees) {
        this.attendees = attendees;
        return this;
    }

}
