package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static ru.practicum.shareit.user.UserMapper.toUser;
import static ru.practicum.shareit.user.UserMapper.toUserDto;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    public List<UserDto> getUsers() {
        return userStorage.getUsers().stream()
                .map(UserMapper::toUserDto)
                .collect(toList());
    }

    @Override
    public UserDto getUserById(Long id) {
        return toUserDto(userStorage.getUserById(id));
    }

    @Override
    public UserDto create(UserDto userDto) {
        return toUserDto(userStorage.create(toUser(userDto)));
    }

    @Override
    public UserDto update(UserDto userDto, Long id) {
        if (userDto.getId() == null) {
            userDto.setId(id);
        }
        return toUserDto(userStorage.update(toUser(userDto)));
    }

    @Override
    public UserDto delete(Long userId) {
        return toUserDto(userStorage.delete(userId));
    }
}
