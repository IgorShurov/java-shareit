package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static ru.practicum.shareit.item.ItemMapper.toItem;
import static ru.practicum.shareit.item.ItemMapper.toItemDto;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;

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
    public @Valid ItemDto create(ItemDto itemDto, Long ownerId) {
        return toItemDto(itemStorage.create(toItem(itemDto, ownerId)));
    }

    @Override
    public ItemDto update(ItemDto itemDto, Long ownerId, Long itemId) {
        if (itemDto.getId() == null) {
            itemDto.setId(itemId);
        }
        Item oldItem = itemStorage.getItemById(itemId);
        if (!oldItem.getOwnerId().equals(ownerId)) {
            throw new ItemNotFoundException("User were Id: " + ownerId + " does not have such a item?");
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
