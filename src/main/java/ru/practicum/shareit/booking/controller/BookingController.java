package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static ru.practicum.shareit.constants.Headers.USER_ID;

@RestController
@RequestMapping(path = "/bookings")
@Slf4j
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(@RequestBody BookingDto bookingDto, HttpServletRequest request) {
        Long userId = Long.valueOf(request.getHeader(USER_ID));
        log.info("Создание бронирования : {}", bookingDto.getId());
        return bookingService.addBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@PathVariable Long bookingId, @RequestParam Boolean approved, HttpServletRequest request) {
        Long userId = Long.valueOf(request.getHeader(USER_ID));
        log.info("Назначение статуса для бронирования: {}, статус: {}", bookingId, approved);
        return bookingService.approveBooking(bookingId, userId, approved);
    }

    @GetMapping
    public List<BookingDto> getAllBookingsForUser(@RequestParam(defaultValue = "ALL") String state, HttpServletRequest request) {
        Long userId = Long.valueOf(request.getHeader(USER_ID));
        log.info("Получение информации о бронированиях пользователя.");
        return bookingService.getAllBookingsByUserId(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingsForOwner(@RequestParam(defaultValue = "ALL") String state, HttpServletRequest request) {
        Long userId = Long.valueOf(request.getHeader(USER_ID));
        log.info("Получение информации о забронированных вещах владельца");
        return bookingService.getAllBookingsByOwnerId(userId, state);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getInfoForBooking(@PathVariable Long bookingId, HttpServletRequest request) {
        Long userId = Long.valueOf(request.getHeader(USER_ID));
        log.info("Получение информации о бронировании: {}", bookingId);
        return bookingService.getBookingInfo(bookingId, userId);
    }
}
