package get.wordy.users;

import get.wordy.users.exception.UserAlreadyExistException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
public class UserService implements IUserService {

    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService userDetailsService;

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

        log.info("A new account successfully created for the email: {}", userDto.getEmail());
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
