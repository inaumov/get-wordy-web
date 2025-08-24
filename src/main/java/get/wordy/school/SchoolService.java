package get.wordy.school;

import get.wordy.school.entity.School;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class SchoolService {

    private final SchoolRepository schoolRepository;

    public SchoolService(SchoolRepository schoolRepository) {
        this.schoolRepository = schoolRepository;
    }

    @Transactional
    public void registerSchool(String username, String schoolName, String logoPath) {

        School school = new School();
        school.setName(schoolName);
        // persist school entity with logoPath instead of blob
        school.setLogoPath(logoPath);
        // assign teacher, save etc.
        school.setOwnerId(username);
        schoolRepository.save(school);

        log.info("School '{}' registered by {}", schoolName, username);
    }

}
