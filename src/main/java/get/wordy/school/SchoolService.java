package get.wordy.school;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
public class SchoolService {

    public static final School EMPTY_SCHOOL = new School(null, null, null, null);

    private final JdbcTemplate jdbcTemplate;

    public SchoolService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public void registerSchool(String username, String schoolName, String logoFilename) {
        String sql = "INSERT INTO schools (school_name, logo_filename, owner_id) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, schoolName, logoFilename, username);

        log.info("School '{}' registered by {}", schoolName, username);
    }

    public School getSchoolInfoByOwner(String username) {
        String sql = "SELECT id, school_name, logo_filename FROM schools WHERE owner_id = ?";
        String teacherDisplayNameSql = """
                SELECT CONCAT(p.first_name, ' ', p.last_name) AS display_name
                FROM user_profiles p
                WHERE p.username = ?
                """;
        return querySchool(username, sql, teacherDisplayNameSql);
    }

    @Transactional
    public School getSchoolInfoByViewer(String viewerId) {
        String sql = """
                SELECT s.id, s.school_name, s.logo_filename, s.owner_id
                FROM schools s
                WHERE s.owner_id = (
                    SELECT i.owner_id
                    FROM class_info i
                    LEFT JOIN class_access a ON i.class_id = a.class_id
                    WHERE a.viewer_id = ? AND a.is_active = TRUE
                    LIMIT 1
                )
                """;
        String teacherDisplayNameSql = """
                SELECT CONCAT(p.first_name, ' ', p.last_name) AS display_name
                FROM user_profiles p
                WHERE p.username = (
                    SELECT i.owner_id
                    FROM class_info i
                             LEFT JOIN class_access a ON i.class_id = a.class_id
                    WHERE a.viewer_id = ? AND a.is_active = TRUE
                    LIMIT 1
                )
                """;
        return querySchool(viewerId, sql, teacherDisplayNameSql);
    }

    private School querySchool(String username, String sql, String teacherDisplayNameSql) {
        try {
            School school = jdbcTemplate.queryForObject(sql, schoolRowMapper(), username);
            // note: teacher's display name cannot be empty
            String teacherDisplayName = jdbcTemplate.queryForObject(teacherDisplayNameSql, String.class, username);
            return Optional.ofNullable(school)
                    .map(s -> s.withTeacherDisplayName(teacherDisplayName))
                    .orElse(EMPTY_SCHOOL
                            .withTeacherDisplayName(teacherDisplayName));
        } catch (EmptyResultDataAccessException e) {
            // fallback in case of no school result
            String teacherDisplayName = jdbcTemplate.queryForObject(teacherDisplayNameSql, String.class, username);
            return EMPTY_SCHOOL.withTeacherDisplayName(teacherDisplayName);
        }
    }

    private static RowMapper<School> schoolRowMapper() {
        return (rs, rowNum) -> new School(
                rs.getLong("id"),
                rs.getString("school_name"),
                rs.getString("logo_filename"),
                null);
    }

}
