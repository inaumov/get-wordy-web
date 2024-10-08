package get.wordy.users;

import lombok.*;

@Getter
@Setter
@Builder
public class UserProfile {

    private String username;
    private String firstName;
    private String lastName;
    private String email;

}
