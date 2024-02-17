package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static ru.practicum.shareit.item.ItemMapper.toItem;
import static ru.practicum.shareit.item.ItemMapper.toItemDto;
import static ru.practicum.shareit.item.ItemValidator.isItemDtoValid;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserService userService;

    @Override
    public ItemDto getItemById(Long id) {
        return toItemDto(itemStorage.getItemById(id));
    }

    @Override
    public List<ItemDto> getItemsByOwner(Long ownerId) {
        return itemStorage.getItemsByOwner(ownerId).stream()
                .map(ItemMapper::toItemDto)
                .collect(toList());
    }

    @Override
    public List<ItemDto> getItemsBySearchQuery(String text) {
        return itemStorage.getItemsBySearchQuery(text.toLowerCase()).stream()
                .map(ItemMapper::toItemDto)
                .collect(toList());
    }

    @Override
    public ItemDto create(ItemDto itemDto, Long ownerId) {
        ItemDto newItemDto = null;
        if (userService.getUserById(ownerId) != null) {
            newItemDto = toItemDto(itemStorage.create(toItem(itemDto, ownerId)));
            isItemDtoValid(newItemDto);
        }
        return newItemDto;
    }

    @Override
    public ItemDto update(ItemDto itemDto, Long ownerId, Long itemId) {
        if (userService.getUserById(ownerId) != null) {
            if (itemDto.getId() == null) {
                itemDto.setId(itemId);
            }
            Item oldItem = itemStorage.getItemById(itemId);
            if (!oldItem.getOwnerId().equals(ownerId)) {
                throw new ItemNotFoundException("User does not have such a item!");
            }
        }
        return toItemDto(itemStorage.update(toItem(itemDto, ownerId)));
    }

    @Override
    public ItemDto delete(Long itemId, Long ownerId) {
        Item item = itemStorage.getItemById(itemId);
        if (!item.getOwnerId().equals(ownerId)) {
            throw new ItemNotFoundException("User were Id: " + ownerId + "does not have such a item!");
        }
        return toItemDto(itemStorage.delete(itemId));
    }

    @Override
    public void deleteItemsByOwner(Long ownerId) {
        itemStorage.deleteItemsByOwner(ownerId);
    }
}
