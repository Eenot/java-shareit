package ru.practicum.shareit.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.IncorrectDataException;
import ru.practicum.shareit.exception.UnsupportedStatusException;

@Component
@RequiredArgsConstructor
public class BookingValidator {

    private final BookingRepository repository;

    public void validateBookingState(String state) {
        String result = BookingState.checkState(state);
        if (result.isEmpty()) {
            throw new UnsupportedStatusException(state);
        }
    }

    public void validateBookingId(long bookingId) {
        if (bookingId < 0) {
            throw new IncorrectDataException("Бронирования с header-id " + bookingId + " не существует!");
        }
        repository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Бронирования с id " + bookingId + " не существует!"));
    }

    public Booking validateBookingIdAndReturnIt(long bookingId) {
        if (bookingId < 0) {
            throw new IncorrectDataException("Бронирования с header-id " + bookingId + " не существует!");
        }
        return repository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Бронирования с id " + bookingId + " не существует!"));
    }
}
