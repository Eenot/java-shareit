package shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import shareit.exception.IncorrectDataException;
import shareit.validator.BookingValidator;
import shareit.validator.PageableValidator;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import static shareit.constants.Headers.USER_ID;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {


    private final BookingClient bookingClient;
    private final PageableValidator pageableValidator;
    private final BookingValidator bookingValidator;

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestBody BookingDto bookingDto,
                                                @RequestHeader(USER_ID) @Positive long userId) {
        bookingValidator.validateBookingData(bookingDto);
        log.info("Gateway: Создание бронирования : {}", bookingDto.getId());
        return bookingClient.createBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@PathVariable @Positive Long bookingId,
                                                 @RequestParam @NotNull String approved,
                                                 @RequestHeader(USER_ID) @Positive long userId) {
        if (!approved.equals("true") && !approved.equals("false")) {
            throw new IncorrectDataException("Статус подтверждения может быть только TRUE или FALSE");
        }
        log.info("Gateway: Назначение статуса для бронирования: {}, статус: {}", bookingId, approved);
        return bookingClient.approveBooking(userId, bookingId, approved);
    }

    @GetMapping
    public ResponseEntity<Object> getAllBookingsForUser(@RequestParam(defaultValue = "ALL") String state,
                                                        @RequestParam(defaultValue = "0") Integer from,
                                                        @RequestParam(defaultValue = "10") Integer size,
                                                        @RequestHeader(USER_ID) @Positive long userId) {
        pageableValidator.checkingPageableParams(from, size);
        bookingValidator.validateBookingState(state);
        log.info("Gateway: Получение информации о бронированиях пользователя");
        return bookingClient.getAllBookingsForUser(userId, from, size, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllBookingsForOwner(@RequestParam(defaultValue = "ALL") String state,
                                                         @RequestParam(defaultValue = "0") Integer from,
                                                         @RequestParam(defaultValue = "10") Integer size,
                                                         @RequestHeader(USER_ID) @Positive long userId) {
        pageableValidator.checkingPageableParams(from, size);
        bookingValidator.validateBookingState(state);
        log.info("Gateway: Получение информации о забронированных вещах владельца");
        return bookingClient.getAllBookingsForOwner(userId, from, size, state);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getInfoForBooking(@PathVariable Long bookingId,
                                                    @RequestHeader(USER_ID) @Positive long userId) {
        log.info("Получение информации о бронировании: {}", bookingId);
        return bookingClient.getInfoForBooking(userId, bookingId);
    }
}
