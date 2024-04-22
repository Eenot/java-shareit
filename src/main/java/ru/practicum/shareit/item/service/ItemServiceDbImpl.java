package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.EmptyFieldException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.IncorrectDataException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.dto.comment.CommentMapper;
import ru.practicum.shareit.item.dto.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.dto.comment.CommentMapper.toCommentDto;
import static ru.practicum.shareit.item.dto.mapper.ItemMapper.toItem;
import static ru.practicum.shareit.item.dto.mapper.ItemMapper.toItemDb;
import static ru.practicum.shareit.item.dto.mapper.ItemMapper.toItemDto;
import static ru.practicum.shareit.item.dto.mapper.ItemMapper.toItemDtoWithBookings;
import static ru.practicum.shareit.item.dto.mapper.ItemMapper.toItemDtoWithBookingsAndComments;
import static ru.practicum.shareit.item.dto.mapper.ItemMapper.toItemDtoWithComments;
import static ru.practicum.shareit.item.dto.mapper.ItemMapper.toItemUpdate;
import static ru.practicum.shareit.user.dto.mapper.UserMapper.toUser;
import static ru.practicum.shareit.user.dto.mapper.UserMapper.toUserDto;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceDbImpl implements ItemService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto createItem(ItemDto itemDto, long userId) {
        if (itemDto.getAvailable() == null || itemDto.getDescription() == null || itemDto.getName() == null) {
            throw new EmptyFieldException("Поля для ввода не должны быть NULL!");
        }
        if (itemDto.getName().isEmpty() || itemDto.getDescription().isEmpty()) {
            throw new EmptyFieldException("Поля для ввода не должны быть пусты!");
        }
        UserDto userFromDb = checkUserId(userId);
        return toItemDto(itemRepository.save(toItemDb(itemDto, toUser(userFromDb))));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, long userId) {
        checkUserId(userId);
        Item itemToUpdate = toItemUpdate(itemDto, itemRepository.findById(itemDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Предмета с таким Id не существует! Id: " + itemDto.getId())));
        itemRepository.save(itemToUpdate);
        return toItemDto(itemToUpdate);
    }

    @Override
    public ItemDto getItemById(long itemId, long userId) {
        checkUserId(userId);
        List<CommentDto> commentsForItem = commentRepository.findAllByItem_Id(itemId)
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
        List<BookingDto> bookingsForItem = getOwnerBooking(userId)
                .stream()
                .filter(x -> x.getItem().getId().equals(itemId))
                .collect(Collectors.toList());

        if (!bookingsForItem.isEmpty() && !commentsForItem.isEmpty()) {
            return toItemDtoWithBookingsAndComments(itemRepository.findById(itemId)
                    .orElseThrow(() -> new EntityNotFoundException("There is no Item with Id: " + itemId)), bookingsForItem, commentsForItem);
        } else if (!bookingsForItem.isEmpty()) {
            return toItemDtoWithBookings(itemRepository.findById(itemId)
                    .orElseThrow(() -> new EntityNotFoundException("There is no Item with Id: " + itemId)), bookingsForItem);
        } else if (!commentsForItem.isEmpty()) {
            return toItemDtoWithComments(itemRepository.findById(itemId)
                    .orElseThrow(() -> new EntityNotFoundException("There is no Item with Id: " + itemId)), commentsForItem);
        } else {
            return toItemDto(itemRepository.findById(itemId)
                    .orElseThrow(() -> new EntityNotFoundException("There is no Item with Id: " + itemId)));
        }
    }

    @Override
    public Collection<ItemDto> getItemsByUserId(long userId) {
        UserDto userFromDb = checkUserId(userId);

        List<Item> userItems = new ArrayList<>(itemRepository.findByOwner_Id(userFromDb.getId(), Sort.by(Sort.Direction.ASC, "id")));
        List<CommentDto> commentsToUserItems = commentRepository.findAllItemByUserId(userId, Sort.by(Sort.Direction.DESC, "created"))
                .stream().map(CommentMapper::toCommentDto).collect(Collectors.toList());
        List<BookingDto> bookingsToUserItems = getOwnerBooking(userId);

        Map<Item, List<BookingDto>> itemsWithBookingsMap = new HashMap<>();
        Map<Item, List<CommentDto>> itemsWithCommentsMap = new HashMap<>();

        for (Item i : userItems) {
            itemsWithCommentsMap.put(i, commentsToUserItems.stream()
                    .filter(c -> c.getItem().getId().equals(i.getId()))
                    .collect(Collectors.toList()));
            itemsWithBookingsMap.put(i, bookingsToUserItems.stream()
                    .filter(b -> b.getItem().getId().equals(i.getId()))
                    .collect(Collectors.toList()));
        }

        List<ItemDto> results = new ArrayList<>();
        for (Item i : userItems) {
            results.add(toItemDtoWithBookingsAndComments(i, itemsWithBookingsMap.get(i), itemsWithCommentsMap.get(i)));
        }

        return results;
    }

    @Override
    public Collection<ItemDto> getItemsBySearching(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        return itemRepository.search(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto checkItemOwner(Long itemId, Long ownerId) {
        ItemDto itemDto = toItemDto(itemRepository.findById(itemId).get());
        if (!Objects.equals(itemDto.getOwnerId(), ownerId)) {
            throw new EntityNotFoundException("Пользователь с id " + ownerId + " не является владельцем");
        }
        return itemDto;
    }

    @Override
    public CommentDto addCommentToItem(Long userId, Long itemId, CommentDto commentDto) {
        if (commentDto.getText().isEmpty()) {
            throw new IncorrectDataException("Комментарий не может быть пуст!");
        }
        UserDto author = checkUserId(userId);
        List<BookingDto> bookings = bookingRepository.findAllByUserIdAndItemIdAndEndDateIsPassed(userId, itemId, LocalDateTime.now())
                .stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
        if (bookings.isEmpty()) {
            throw new IncorrectDataException("У этого пользователя нет забронированных вещей.");
        }
        ItemDto item = getItemById(itemId, userId);
        commentDto = toCommentDto(commentRepository.save(CommentMapper.toCommentDb(commentDto, toUser(author), toItem(item))));
        return commentDto;
    }

    private UserDto checkUserId(long userId) {
        if (userId == -1) {
            throw new IncorrectDataException("Пользователя с таким id не существует!");
        }
        return toUserDto(userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("Пользователя с таким id не существует! id"
                + userId)));
    }

    private List<BookingDto> getOwnerBooking(Long ownerId) {
        return bookingRepository.findAllByItem_Owner_Id(ownerId)
                .stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }
}
