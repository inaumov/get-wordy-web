package get.wordy.users;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CustomUserDetailsService extends JdbcUserDetailsManager {

    private final String profileByUsernameQuery = "SELECT first_name,last_name,email FROM user_profiles WHERE username = ?";
    private final String createProfileSql = "INSERT INTO user_profiles VALUES (:username,:firstName,:lastName,:email)";
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public CustomUserDetailsService(DataSource dataSource) {
        super.setDataSource(dataSource);
        this.setUsersByUsernameQuery("""
                select u.username, u.password, u.enabled
                from users u
                         join user_profiles p on u.username = p.username
                where email = ?
                """);
        namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public CustomUserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Load user from the users table
        UserDetails userDetails = super.loadUserByUsername(email);
        // Load user profile from user_profiles table
        UserProfile profile = loadUserProfile(userDetails.getUsername());
        return new CustomUserDetails(userDetails, profile.getEmail(), profile.getFirstName(), profile.getLastName());
    }

    public UserProfile loadUserProfile(String username) {
        RowMapper<UserProfile> mapper = (rs, rowNum) -> {
            String firstName = rs.getString(1);
            String lastName = rs.getString(2);
            String email = rs.getString(3);
            return new UserProfile(username, firstName, lastName, email);
        };
        return Objects.requireNonNull(getJdbcTemplate()).queryForObject(this.profileByUsernameQuery, mapper, username);
    }

    public void createUserProfile(UserProfile userProfile) {
        validateUserProfile(userProfile);
        Objects.requireNonNull(namedParameterJdbcTemplate)
                .update(this.createProfileSql,
                        Map.of("username", userProfile.getUsername(),
                                "firstName", userProfile.getFirstName(),
                                "lastName", userProfile.getLastName(),
                                "email", userProfile.getEmail()
                        )
                );
    }

    private void validateUserProfile(UserProfile user) {
        Assert.hasText(user.getUsername(), "Username must not be empty or null");
        Assert.hasText(user.getEmail(), "Email must not be empty or null");
    }

    public boolean checkEmailExists(String email) {
        List<UserDetails> userDetails = super.loadUsersByUsername(email);
        return !userDetails.isEmpty();
    }

}
