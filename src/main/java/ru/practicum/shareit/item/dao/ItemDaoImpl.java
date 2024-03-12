package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.entity.Item;

import java.util.*;

@Repository
public class ItemDaoImpl implements ItemDao {
    private final Map<Long, Item> items = new HashMap<>();
    private Long id = 0L;

    private Long generateId() {
        return ++id;
    }

    @Override
    public Item create(Item item) {

        item.setId(generateId());
        items.put(item.getId(), item);
        return item;
    }
}
