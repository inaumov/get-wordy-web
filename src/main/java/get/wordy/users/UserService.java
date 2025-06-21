package get.wordy.users;

import get.wordy.auth.Settings;
import get.wordy.users.exception.UserAlreadyExistException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;

@Slf4j
@Service
@Transactional
public class UserService implements IUserService {

    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService userDetailsService;
    private final HashMap<String, Settings> userSettings = new HashMap<>();

    public UserService(PasswordEncoder passwordEncoder, CustomUserDetailsService userDetailsService) {
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public UserDto registerNewUserAccount(UserDto userDto) throws UserAlreadyExistException {
        if (emailExists(userDto.getEmail())) {
            throw new UserAlreadyExistException("There is an account with that email address: "
                    + userDto.getEmail());
        }

        // the rest of the registration operation
        UserDetails user = User.builder()
                .username(userDto.getFirstName() + userDto.getLastName() + "-temp")
                .password(userDto.getPassword())
                // encrypt the password using spring security
                .passwordEncoder(passwordEncoder::encode)
                .roles("USER")
                .build();

        userDetailsService.createUser(user);

        UserProfile userProfile = UserProfile.builder()
                .username(user.getUsername())
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .email(userDto.getEmail())
                .build();
        userDetailsService.createUserProfile(userProfile);
        userDetailsService.addUserToGroup(user.getUsername(), "individual_users");

        // auto-login logic after successful registration
        CustomUserDetails userDetails = this.userDetailsService.loadUserByUsername(userDto.getEmail());
        Authentication auth = new UsernamePasswordAuthenticationToken(
                userDetails, userDetails.getPassword(), userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        // verify what's stored in the context
        log.debug("User {} is now authenticated with authorities: {}", auth.getName(), auth.getAuthorities());
        log.info("A new account has been successfully created with email: {}", userDto.getEmail());
        return userDto;
    }

    @Override
    public UserDto saveUser(UserDto userDto) {
        return null;
    }

    @Override
    public UserDto findUserByEmail(String email) {
        return mapToUserDto(userDetailsService.loadUserByUsername(email));
    }

    @Override
    public List<UserDto> findAllUsers() {
        List<CustomUserDetails> users = List.of();
        return users.stream()
                .map(this::mapToUserDto)
                .toList();
    }

    @Override
    public Settings getSettings(String userId) {
        return userSettings.get(userId);
    }

    @Override
    public void updateSettings(String userId, Settings settings) {
        log.debug("Updating settings for the the user = {}. New settings: {}", userId, settings);
        userSettings.put(userId, settings);
    }

    private boolean emailExists(String email) {
        return userDetailsService.checkEmailExists(email);
    }

    private UserDto mapToUserDto(CustomUserDetails user) {
        UserDto userDto = new UserDto();
        userDto.setFirstName(userDto.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setEmail(user.getEmail());
        return userDto;
    }

}
