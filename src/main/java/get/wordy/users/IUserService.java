package get.wordy.users;

import java.util.List;

public interface IUserService {

    UserDto registerNewUserAccount(UserDto userDto);

    UserDto saveUser(UserDto userDto);

    UserDto findUserByEmail(String email);

    List<UserDto> findAllUsers();

}
