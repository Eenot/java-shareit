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
        log.info("Создание бронирования : {}", bookingDto);
        return bookingService.addBooking(bookingDto, Long.valueOf(request.getHeader(USER_ID)));
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@PathVariable Long bookingId, @RequestParam String approved, HttpServletRequest request) {
        log.info("Назначение статуса для бронирования: {}, статус: {}", bookingId, approved);
        return bookingService.approveBooking(bookingId, Long.valueOf(request.getHeader(USER_ID)), approved);
    }

    @GetMapping
    public List<BookingDto> getAllBookingsForUser(@RequestParam(defaultValue = "ALL") String state, HttpServletRequest request) {
        log.info("Получение информации о бронированиях пользователя.");
        return bookingService.getAllBookingsByUserId(Long.valueOf(request.getHeader(USER_ID)), state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingsForOwner(@RequestParam(defaultValue = "ALL") String state, HttpServletRequest request) {
        log.info("Получение информации о забронированных вещах владельца");
        return bookingService.getAllBookingsByOwnerId(Long.valueOf(request.getHeader(USER_ID)), state);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getInfoForBooking(@PathVariable Long bookingId, HttpServletRequest request) {
        log.info("Получение информации о бронировании: {}", bookingId);
        return bookingService.getBookingInfo(bookingId, Long.valueOf(request.getHeader(USER_ID)));
    }
}
