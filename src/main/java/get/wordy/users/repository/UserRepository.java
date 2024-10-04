package get.wordy.users.repository;

import get.wordy.users.entity.GwUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<GwUser, String> {

    GwUser findByEmail(String email);

}
