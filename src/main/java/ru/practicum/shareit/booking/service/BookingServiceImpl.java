package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.IncorrectDataException;
import ru.practicum.shareit.exception.UnsupportedStatusException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.dto.mapper.BookingMapper.toBookingDb;
import static ru.practicum.shareit.booking.dto.mapper.BookingMapper.toBookingDto;
import static ru.practicum.shareit.booking.dto.mapper.BookingMapper.toBookingUpdate;
import static ru.practicum.shareit.user.dto.mapper.UserMapper.toUser;
import static ru.practicum.shareit.user.dto.mapper.UserMapper.toUserDto;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private static final Sort SORT_BY_START_DESC = Sort.by(Sort.Direction.DESC, "start");
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Override
    public BookingDto addBooking(BookingDto bookingDto, Long bookerId) {
        UserDto userFromDb = checkUserId(bookerId);
        Item itemFromDb = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new EntityNotFoundException("Вещи с таким id не существует! id: " + bookingDto.getItemId()));

        if (itemFromDb.getOwner().getId() == bookerId) {
            throw new EntityNotFoundException("Владелец не может забронировать свою же вещь!");
        }

        if (!itemFromDb.getAvailable()) {
            throw new IncorrectDataException("Вещь не доступна для бронирования!");
        }

        if (bookingDto.getStart() == null || bookingDto.getEnd() == null) {
            throw new IncorrectDataException("Даты бронирования не могут быть null!");
        }

        if (bookingDto.getEnd().isBefore(bookingDto.getStart()) || bookingDto.getStart().isEqual(bookingDto.getEnd())
            || bookingDto.getEnd().isBefore(LocalDateTime.now()) || bookingDto.getStart().isBefore(LocalDateTime.now())) {
            throw new IncorrectDataException("Некорректные даты бронирования!");
        }

        bookingDto.setStatus(BookingStatus.WAITING);
        return toBookingDto(bookingRepository.save(toBookingDb(bookingDto, itemFromDb, toUser(userFromDb))));
    }

    @Override
    public BookingDto approveBooking(Long bookingId, Long ownerId, String approve) {
        checkUserId(ownerId);
        BookingDto bookingDto = toBookingDto(bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Бронирования с таким id не существует! id: " + bookingId)));
        if (!Objects.equals(bookingDto.getItem().getOwnerId(), ownerId)) {
            throw new EntityNotFoundException("Пользователь с id " + ownerId + " не является владельцем!");
        }

        switch (approve.toLowerCase()) {
            case "true": {
                if (bookingDto.getStatus().equals(BookingStatus.APPROVED)) {
                    throw new IncorrectDataException("Статус уже подтверждён");
                }
                bookingDto.setStatus(BookingStatus.APPROVED);
                break;
            }
            case "false": {
                bookingDto.setStatus(BookingStatus.REJECTED);
                break;
            }
            default:
                throw new IncorrectDataException("Недопустимое значение для статуса!");
        }
        Booking bookingToUpdate = toBookingUpdate(bookingDto, bookingRepository.findById(bookingId).get());
        bookingRepository.save(bookingToUpdate);
        return toBookingDto(bookingToUpdate);
    }

    @Override
    public BookingDto getBookingInfo(Long bookingId, Long userId) {
        checkUserId(userId);
        BookingDto bookingDto = toBookingDto(bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Бронирования с id " + bookingId + "не существует!")));
        if (!Objects.equals(bookingDto.getItem().getOwnerId(), userId) && !Objects.equals(bookingDto.getBooker().getId(), userId)) {
            throw new EntityNotFoundException("Пользователь с id " + userId + " не является владельцем!");
        }
        return bookingDto;
    }

    @Override
    public List<BookingDto> getAllBookingsByUserId(Long userId, String state) {
        checkUserId(userId);
        checkBookingState(state);
        List<Booking> bookings;
        switch (state.toUpperCase()) {
            case "WAITING": {
                bookings = new ArrayList<>(bookingRepository.findAllByBookerIdAndWaitingStatus(userId, BookingStatus.WAITING, SORT_BY_START_DESC));
                break;
            }
            case "REJECTED": {
                bookings = new ArrayList<>(bookingRepository.findAllByBookerIdAndRejectedStatus(userId, List.of(BookingStatus.REJECTED, BookingStatus.CANCELED), SORT_BY_START_DESC));
                break;
            }
            case "CURRENT": {
                bookings = new ArrayList<>(bookingRepository.findAllByBookerIdAndCurrentStatus(userId, LocalDateTime.now(), SORT_BY_START_DESC));
                break;
            }
            case "FUTURE": {
                bookings = new ArrayList<>(bookingRepository.findAllByBookerIdAndFutureStatus(userId, LocalDateTime.now(), SORT_BY_START_DESC));
                break;
            }
            case "PAST": {
                bookings = new ArrayList<>(bookingRepository.findAllByBookerIdAndPastStatus(userId, LocalDateTime.now(), SORT_BY_START_DESC));
                break;
            }
            case "ALL": {
                bookings = new ArrayList<>(bookingRepository.findAllByBooker_Id(userId, SORT_BY_START_DESC));
                break;
            }
            default:
                bookings = new ArrayList<>();
        }
        return bookings.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getAllBookingsByOwnerId(Long ownerId, String state) {
        checkUserId(ownerId);
        checkBookingState(state);
        List<Long> userItemsIds = itemRepository.findByOwner_Id(ownerId, Sort.by(Sort.Direction.ASC, "id")).stream()
                .map(Item::getId)
                .collect(Collectors.toList());
        if (userItemsIds.isEmpty()) {
            throw new IncorrectDataException("Данная функция доступна только пользователям у которых >1 вещи");
        }
        List<Booking> bookings;
        switch (state.toUpperCase()) {
            case "WAITING": {
                bookings = new ArrayList<>(bookingRepository.findAllByOwnerItemsAndWaitingStatus(userItemsIds, BookingStatus.WAITING, SORT_BY_START_DESC));
                break;
            }
            case "REJECTED": {
                bookings = new ArrayList<>(bookingRepository.findAllByOwnerItemsAndRejectedStatus(userItemsIds, List.of(BookingStatus.REJECTED, BookingStatus.CANCELED), SORT_BY_START_DESC));
                break;
            }
            case "CURRENT": {
                bookings = new ArrayList<>(bookingRepository.findAllByOwnerItemsAndCurrentStatus(userItemsIds, LocalDateTime.now(), SORT_BY_START_DESC));
                break;
            }
            case "FUTURE": {
                bookings = new ArrayList<>(bookingRepository.findAllByOwnerItemsAndFutureStatus(userItemsIds, LocalDateTime.now(), SORT_BY_START_DESC));
                break;
            }
            case "PAST": {
                bookings = new ArrayList<>(bookingRepository.findAllByOwnerItemsAndPastStatus(userItemsIds, LocalDateTime.now(), SORT_BY_START_DESC));
                break;
            }
            case "ALL": {
                bookings = new ArrayList<>(bookingRepository.findAllByOwnerItems(userItemsIds, SORT_BY_START_DESC));
                break;
            }
            default:
                bookings = new ArrayList<>();
        }
        return bookings.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    private UserDto checkUserId(long userId) {
        if (userId == -1) {
            throw new IncorrectDataException("Пользователя с таким id не существует!");
        }
        return toUserDto(userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("Пользователя с таким id не существует! id"
                + userId)));
    }

    private void checkBookingState(String state) {
        try {
            BookingState.valueOf(state);
        } catch (Exception e) {
            throw new UnsupportedStatusException(state);
        }
    }
}
