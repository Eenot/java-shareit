package ru.practicum.shareit.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.IncorrectDataException;
import ru.practicum.shareit.exception.UnsupportedStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingValidatorTest {

    @Mock
    BookingRepository bookingRepository;

    @InjectMocks
    BookingValidator bookingValidator;

    @Test
    void validateBookingState_whenStateIsIncorrect_thenThrowUnsupportedStatusException() {
        UnsupportedStatusException exception = assertThrows(UnsupportedStatusException.class,
                () -> bookingValidator.validateBookingState("NOT"));

        assertEquals(exception.getMessage(), "NOT");
    }

    @Test
    void validateBookingId_whenBookingNotExists_thenThrowEntityNotFoundException() {
        when(bookingRepository.findById(anyLong())).thenThrow(new EntityNotFoundException("Бронирование не найдено!"));

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> bookingValidator.validateBookingId(1L));

        assertEquals(exception.getMessage(), "Бронирование не найдено!");
    }

    @Test
    void validateBookingId_whenBookingIdLessThanZero_thenThrowIncorrectDataException() {
        IncorrectDataException exception = assertThrows(IncorrectDataException.class,
                () -> bookingValidator.validateBookingId(-1L));

        assertEquals(exception.getMessage(), "Бронирования с header-id -1 не существует!");
    }

    @Test
    void validateBookingIdAndReturns_whenBookingNotFound_thenThrowEntityNotFoundException() {
        when(bookingRepository.findById(anyLong())).thenThrow(new EntityNotFoundException("Бронирование не найдено!"));

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> bookingValidator.validateBookingIdAndReturnIt(1L));

        assertEquals(exception.getMessage(), "Бронирование не найдено!");
    }

    @Test
    void validateBookingIdAndReturns_whenBookingExists_thenReturnBooking() {
        Booking booking = new Booking();
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        Booking actual = bookingValidator.validateBookingIdAndReturnIt(1L);

        assertEquals(actual, booking);
    }

}
