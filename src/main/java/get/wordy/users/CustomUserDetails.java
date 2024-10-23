package get.wordy.users;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomUserDetails implements UserDetails {

    private final UserDetails userDetails;
    @Getter
    private final String email;
    @Getter
    private final String firstName;
    @Getter
    private final String lastName;
    @Getter
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(UserDetails userDetails, String email, String firstName, String lastName, Collection<? extends GrantedAuthority> authorities) {
        this.userDetails = userDetails;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.authorities = authorities;
    }

    @Override
    public String getPassword() {
        return userDetails.getPassword();
    }

    @Override
    public String getUsername() {
        return userDetails.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return userDetails.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return userDetails.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return userDetails.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return userDetails.isEnabled();
    }

    public String getDisplayName() {
        return firstName + StringUtils.SPACE + lastName;
    }

    public boolean hasPermissionOrRole(String authority) {
        return getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(authority));
    }

}
