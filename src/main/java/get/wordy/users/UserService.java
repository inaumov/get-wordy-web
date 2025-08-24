package get.wordy.users;

import get.wordy.auth.Settings;
import get.wordy.users.exception.UserAlreadyExistException;
import lombok.extern.slf4j.Slf4j;
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
    @Transactional
    public UserDto registerUser(UserDto userDto, String userGroup)
            throws UserAlreadyExistException {

        if (emailExists(userDto.getEmail())) {
            throw new UserAlreadyExistException("Account already exists with email: " + userDto.getEmail());
        }

        // the rest of the registration operation
        UserDetails user = User.builder()
                .username(userDto.getFirstName() + userDto.getLastName() + "-temp")
                .password(userDto.getPassword())
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
        userDetailsService.addUserToGroup(user.getUsername(), userGroup);

        log.info("New account in '{}' group has been created for email: {}", userGroup, userDto.getEmail());
        return userDto.withUsername(user.getUsername());
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
