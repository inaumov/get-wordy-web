package get.wordy.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import get.wordy.core.api.bean.ClassInfo;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class ClassInfoResponse {

    private final String classId;
    private final String name;
    private final String format;
    private final ScheduleType scheduleType;
    private final List<TimeSlot> timeSlots;
    private final LocalDate endDate;
    private final String notes;
    @JsonProperty("isActive")
    private final boolean isActive;
    private int draftsCount;
    private LocalDateTime lastUpdatedAt;

    @Getter
    @JsonProperty("participants")
    private List<String> participants = new ArrayList<>();

    public ClassInfoResponse(ClassInfo classInfo) {
        this.classId = classInfo.getClassId();
        this.name = classInfo.getName();
        this.format = classInfo.getFormat();
        this.scheduleType = classInfo.getIsRepeatable() ? ScheduleType.REPEATABLE : classInfo.getEndDate() != null ? ScheduleType.ONE_TIME : ScheduleType.NONE;
        this.timeSlots = classInfo.getTimeSlots()
                .stream()
                .map(classSchedule -> new TimeSlot(classSchedule.getDayOfWeek(), classSchedule.getStartTime(), classSchedule.getEndTime()))
                .toList();
        this.endDate = classInfo.getEndDate();
        this.notes = classInfo.getNotes();
        this.isActive = classInfo.getIsActive();
    }

    public ClassInfoResponse withParticipants(List<String> participants) {
        this.participants.addAll(participants);
        return this;
    }

    public ClassInfoResponse withSharedSummary(int draftsCount, LocalDateTime lastUpdatedAt) {
        this.draftsCount = draftsCount;
        this.lastUpdatedAt = lastUpdatedAt;
        return this;
    }

}
