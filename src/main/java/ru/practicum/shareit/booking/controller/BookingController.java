package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.validator.PageableValidator;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static ru.practicum.shareit.constants.Headers.USER_ID;

@RestController
@RequestMapping(path = "/bookings")
@Slf4j
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final PageableValidator pageableValidator;

    @PostMapping
    public BookingDto createBooking(@RequestBody BookingDto bookingDto, HttpServletRequest request) {
        log.info("Создание бронирования : {}", bookingDto.getId());
        return bookingService.addBooking(bookingDto, (long) (request.getIntHeader(USER_ID)));
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@PathVariable Long bookingId, @RequestParam String approved, HttpServletRequest request) {
        log.info("Назначение статуса для бронирования: {}, статус: {}", bookingId, approved);
        return bookingService.approveBooking(bookingId, (long) (request.getIntHeader(USER_ID)), approved);
    }

    @GetMapping
    public List<BookingDto> getAllBookingsForUser(@RequestParam(defaultValue = "ALL") String state,
                                                  @RequestParam(defaultValue = "0") Integer from,
                                                  @RequestParam(defaultValue = "10") Integer size,
                                                  @RequestHeader(USER_ID) Long userId) {
        pageableValidator.checkingPageableParams(from, size);
        log.info("Получение информации о бронированиях пользователя");
        Pageable page = PageRequest.of(from / size, size);
        return bookingService.getAllBookingsByUserId(userId, state, page);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingsForOwner(@RequestParam(defaultValue = "ALL") String state,
                                                   @RequestParam(defaultValue = "0") Integer from,
                                                   @RequestParam(defaultValue = "10") Integer size,
                                                   HttpServletRequest request) {
        pageableValidator.checkingPageableParams(from, size);
        Long userId = Long.valueOf(request.getHeader(USER_ID));
        log.info("Получение информации о забронированных вещах владельца");
        Pageable page = PageRequest.of(from / size, size);
        return bookingService.getAllBookingsByOwnerId(userId, state, page);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getInfoForBooking(@PathVariable Long bookingId, HttpServletRequest request) {
        log.info("Получение информации о бронировании: {}", bookingId);
        return bookingService.getBookingInfo(bookingId, (long) request.getIntHeader(USER_ID));
    }
}
