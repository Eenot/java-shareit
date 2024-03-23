package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemRepository {

    Item createItem(Item item, long userId);

    Item updateItem(Item item, long userId);

    Item getItemById(long itemId);

    Collection<Item> getItemsByUserId(long userId);

    Collection<Item> getItemsBySearching(String text);
}
