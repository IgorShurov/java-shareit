package ru.practicum.shareit.user;

import org.apache.commons.lang3.StringUtils;
import ru.practicum.shareit.exception.UserValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import static ru.practicum.shareit.utilitary.Constants.EMAIL_REGEX_PATTERN;
import static ru.practicum.shareit.utilitary.PatternValidator.isPatternMatches;

/**
 * Класс содержащий методы валидации для моделей User и UserDto
 */

public class UserValidator {
    public static boolean isUserDtoValid(UserDto userDto) {

        if (StringUtils.isBlank(userDto.getName())) {
            throw new UserValidationException("Name validation error.");
        } else if (!isPatternMatches(userDto.getEmail(), EMAIL_REGEX_PATTERN)) {
            throw new UserValidationException("Email validation error.");
        } else {
            return true;
        }
    }
}