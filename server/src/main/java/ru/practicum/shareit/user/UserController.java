package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

/**
 * Класс описывающий Controller для модели User
 */

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserDto> getUsers() {
        log.info("GET: request to the endpoint was received: '/users' to receive users");
        return userService.getUsers();
    }

    @GetMapping("{userId}")
    public UserDto getUserById(@PathVariable Long userId) {
        log.info("GET: request to the endpoint was received: '/users' to receive the user with ID={}", userId);
        return userService.getUserById(userId);
    }

    @PostMapping
    public UserDto createUser(@RequestBody UserDto userDto) {
        log.info("POST: request to the endpoint was received: '/users' to add a user");
        return userService.create(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@RequestBody UserDto userDto, @PathVariable Long userId) {
        log.info("PATCH: request to the endpoint was received: '/users' to update a user with ID={}", userId);
        return userService.update(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long userId) {
        log.info("DELETE: request to the endpoint was received: '/users' to delete a user with ID={}", userId);
        userService.delete(userId);
        return ResponseEntity.noContent().build();
    }
}
