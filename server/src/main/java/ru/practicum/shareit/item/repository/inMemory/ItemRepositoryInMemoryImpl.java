package ru.practicum.shareit.item.repository.inMemory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.repository.inMemory.UserRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@Slf4j
@RequiredArgsConstructor
public class ItemRepositoryInMemoryImpl implements ItemRepository {

    private final UserRepository userRepository;
    private final Map<Long, List<Item>> items = new HashMap<>();
    private int itemId = 0;

    @Override
    public Item createItem(Item item, long userId) {
        item.setId(++itemId);
        item.setOwner(userRepository.getUserById(userId));
        items.compute(userId, (owner, userItems) -> {
            if (userItems == null) {
                userItems = new ArrayList<>();
            }
            userItems.add(item);
            return userItems;
        });
        int index = findItemIndexInList(itemId, userId);
        return items.get(userId).get(index);
    }

    @Override
    public Item updateItem(Item item, long userId) {
        if (userId != item.getOwner().getId()) {
            throw new EntityNotFoundException("Некорректный id владельца!");
        }

        int index = findItemIndexInList(itemId, userId);
        items.get(userId).set(index, item);
        return items.get(userId).get(index);
    }

    @Override
    public Item getItemById(long itemId) {
        Item item = null;
        for (long userId : items.keySet()) {
            item = items.get(userId).stream().filter(x -> x.getId() == itemId).findFirst().orElse(null);
        }

        return item;
    }

    @Override
    public Collection<Item> getItemsByUserId(long userId) {
        return items.get(userId);
    }

    @Override
    public Collection<Item> getItemsBySearching(String text) {
        Collection<Item> availableItems = new ArrayList<>();

        for (long userId : items.keySet()) {
            availableItems.addAll(items.get(userId).stream()
                    .filter(x -> x.getAvailable().equals(true))
                    .filter(x -> x.getDescription().toLowerCase().contains(text))
                    .collect(Collectors.toList()));
        }

        return availableItems;
    }

    private int findItemIndexInList(long itemId, long userId) {
        return IntStream.range(0, items.get(userId).size())
                .filter(i -> items.get(userId).get(i).getId() == itemId)
                .findFirst()
                .orElse(-1);
    }
}
