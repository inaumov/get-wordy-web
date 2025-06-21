package get.wordy.users;

import get.wordy.auth.Settings;

import java.util.List;

public interface IUserService {

    UserDto registerNewUserAccount(UserDto userDto);

    UserDto saveUser(UserDto userDto);

    UserDto findUserByEmail(String email);

    List<UserDto> findAllUsers();

    void updateSettings(String userId, Settings settings);

    Settings getSettings(String userId);

}
