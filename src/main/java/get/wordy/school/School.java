package get.wordy.school;

public record School(
        Long id,
        String name,
        String logo,
        String teacherName
) {
    School withTeacherDisplayName(String teacherDisplayName) {
        return new School(id, name, logo, teacherDisplayName);
    }

}
