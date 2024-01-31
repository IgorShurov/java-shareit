package ru.practicum.shareit.item;

import org.apache.commons.lang3.StringUtils;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.ValidationException;

/**
 * Класс содержащий методы валидации для моделей Item и ItemDto
 */

public class ItemValidator {

    public static boolean isItemValid(Item item) {
        if (StringUtils.isBlank(item.getName())) {
            throw new ValidationException("Error in class" + item.getClass() + " Item Name validation error.");
        } else if (StringUtils.isBlank(item.getDescription())) {
            throw new ValidationException("Error in class" + item.getClass() + " Item Description validation error.");
        } else if (item.getAvailable() == null) {
            throw new ValidationException("Error in class" + item.getClass() + " Item Availability validation error.");
        } else {
            return true;
        }
    }

    public static boolean isItemDtoValid(ItemDto itemDto) {
        if (StringUtils.isBlank(itemDto.getName())) {
            throw new ValidationException("Error in class" + itemDto.getClass() + " Item Name validation error.");
        } else if (StringUtils.isBlank(itemDto.getDescription())) {
            throw new ValidationException("Error in class" + itemDto.getClass() + " Item Description validation error.");
        } else if (itemDto.getAvailable() == null) {
            throw new ValidationException("Error in class" + itemDto.getClass() + " Item Availability validation error.");
        } else {
            return true;
        }
    }
}
