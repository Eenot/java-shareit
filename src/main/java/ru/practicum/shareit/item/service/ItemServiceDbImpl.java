package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.IncorrectDataException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.dto.comment.CommentMapper;
import ru.practicum.shareit.item.dto.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.validator.ItemValidator;
import ru.practicum.shareit.validator.UserValidator;

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
import static ru.practicum.shareit.item.dto.mapper.ItemMapper.toItemDbWithRequest;
import static ru.practicum.shareit.item.dto.mapper.ItemMapper.toItemDto;
import static ru.practicum.shareit.item.dto.mapper.ItemMapper.toItemDtoWithBookings;
import static ru.practicum.shareit.item.dto.mapper.ItemMapper.toItemDtoWithBookingsAndComments;
import static ru.practicum.shareit.item.dto.mapper.ItemMapper.toItemDtoWithComments;
import static ru.practicum.shareit.item.dto.mapper.ItemMapper.toItemDtoWithRequestId;
import static ru.practicum.shareit.item.dto.mapper.ItemMapper.toItemUpdate;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceDbImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final UserValidator userValidator;
    private final ItemValidator itemValidator;

    @Override
    public ItemDto createItem(ItemDto itemDto, long userId) {
        itemValidator.validateItemData(itemDto);
        User userFromDb = userValidator.validateUserIdAndReturnIt(userId);
        if (itemDto.getRequestId() != null) {
            ItemRequest request = itemRequestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new EntityNotFoundException("Запроса на вещь с таким ID не обнаружено! ID запроса: " + itemDto.getRequestId()));
            return toItemDtoWithRequestId(itemRepository.save(toItemDbWithRequest(itemDto, userFromDb, request)));
        }
        return toItemDto(itemRepository.save(toItemDb(itemDto, userFromDb)));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, long userId) {
        userValidator.validateUserId(userId);
        Item itemToUpdate = toItemUpdate(itemDto, itemValidator.validateItemIdAndReturnIt(itemDto.getId()));
        itemRepository.save(itemToUpdate);
        return toItemDto(itemToUpdate);
    }

    @Override
    public ItemDto getItemById(long itemId, long userId) {
        userValidator.validateUserId(userId);
        Item itemFromDb = itemValidator.validateItemIdAndReturnIt(itemId);

        List<CommentDto> commentsForItem = commentRepository.findAllByItem_Id(itemId)
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
        List<BookingDto> bookingsForItem = getOwnerBooking(userId)
                .stream()
                .filter(x -> x.getItem().getId().equals(itemId))
                .collect(Collectors.toList());

        if (!bookingsForItem.isEmpty() && !commentsForItem.isEmpty()) {
            return toItemDtoWithBookingsAndComments(itemFromDb, bookingsForItem, commentsForItem);
        } else if (!bookingsForItem.isEmpty()) {
            return toItemDtoWithBookings(itemFromDb, bookingsForItem);
        } else if (!commentsForItem.isEmpty()) {
            return toItemDtoWithComments(itemFromDb, commentsForItem);
        } else {
            return toItemDto(itemFromDb);
        }
    }

    @Override
    public Collection<ItemDto> getItemsByUserId(long userId, Pageable page) {
        userValidator.validateUserId(userId);
        Pageable pageForItems = PageRequest.of(page.getPageNumber(), page.getPageSize(), Sort.by(Sort.Direction.ASC, "id"));
        Pageable pageForComments = PageRequest.of(page.getPageNumber(), page.getPageSize(), Sort.by(Sort.Direction.DESC, "created"));

        List<Item> userItems = new ArrayList<>(itemRepository.findByOwner_Id(userId, pageForItems));
        List<CommentDto> commentsToUserItems = commentRepository.findAllItemByUserId(userId, pageForComments)
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
    public Collection<ItemDto> getItemsBySearching(String text, Pageable page) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        return itemRepository.search(text, page).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto checkItemOwner(Long itemId, Long ownerId) {
        Item itemFromDb = itemValidator.validateItemIdAndReturnIt(itemId);

        ItemDto itemDto = toItemDto(itemFromDb);
        if (!Objects.equals(itemDto.getOwnerId(), ownerId)) {
            throw new EntityNotFoundException("Пользователь с id " + ownerId + " не является владельцем");
        }
        return itemDto;
    }

    @Override
    public CommentDto addCommentToItem(Long userId, Long itemId, CommentDto commentDto) {
        User author = userValidator.validateUserIdAndReturnIt(userId);
        itemValidator.validateItemId(itemId);
        itemValidator.validateCommentData(commentDto);
        commentDto.setCreated(LocalDateTime.now());
        List<BookingDto> bookings = bookingRepository.findAllByUserIdAndItemIdAndEndDateIsPassed(userId, itemId, LocalDateTime.now())
                .stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
        if (bookings.isEmpty()) {
            throw new IncorrectDataException("У этого пользователя нет забронированных вещей.");
        }
        ItemDto item = getItemById(itemId, userId);
        commentDto = toCommentDto(commentRepository.save(CommentMapper.toCommentDb(commentDto, author, toItem(item))));
        return commentDto;
    }

    private List<BookingDto> getOwnerBooking(Long ownerId) {
        return bookingRepository.findAllByItem_Owner_Id(ownerId)
                .stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }
}
