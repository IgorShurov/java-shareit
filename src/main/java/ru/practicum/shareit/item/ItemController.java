package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;

import javax.validation.Valid;
import java.util.List;

import static ru.practicum.shareit.utilitary.Constants.OWNER_HEADER;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;
    private final UserService userService;

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable Long itemId) {
        log.info("GET request was received to the endpoint: '/items' to receive an item with ID={}", itemId);
        return itemService.getItemById(itemId);
    }

    @GetMapping
    public List<ItemDto> getItemsByOwner(@RequestHeader(OWNER_HEADER) Long ownerId) {
        log.info("GET request was received to the endpoint: '/items' to receive all the owner's items with ID={}", ownerId);
        return itemService.getItemsByOwner(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemsBySearchQuery(@RequestParam String text) {
        log.info("GET request was received to the endpoint: '/items/search' to search for an item with text={}", text);
        return itemService.getItemsBySearchQuery(text);
    }


    @PostMapping
    public @Valid ItemDto create(@RequestBody ItemDto itemDto, @RequestHeader(OWNER_HEADER) Long ownerId) {
        log.info("POST request was received to the endpoint: '/items' to add an item by the owner with ID={}", ownerId);
        return itemService.create(itemDto, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestBody ItemDto itemDto, @PathVariable Long itemId, @RequestHeader(OWNER_HEADER) Long ownerId) {
        log.info("PATCH request was received to the endpoint: '/items' to update the item with ID={}", itemId);
        return itemService.update(itemDto, ownerId, itemId);
    }


    @DeleteMapping("/{itemId}")
    public ItemDto delete(@PathVariable Long itemId, @RequestHeader(OWNER_HEADER) Long ownerId) {
        log.info("DELETE request was received to the endpoint: '/items' to delete an item with ID={}", itemId);
        return itemService.delete(itemId, ownerId);
    }
}
